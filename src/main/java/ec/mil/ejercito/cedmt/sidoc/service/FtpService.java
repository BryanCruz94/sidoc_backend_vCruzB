package ec.mil.ejercito.cedmt.sidoc.service;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Base64;
import java.util.function.Function;

@Service
public class FtpService {

    @Value("${sidoc.ftp.host}")
    private String ftpServer;

    @Value("${sidoc.ftp.port:21}")
    private int ftpPort;

    @Value("${sidoc.ftp.user}")
    private String ftpUser;

    @Value("${sidoc.ftp.password}")
    private String ftpPassword;

    @Value("${sidoc.ftp.passive:true}")
    private boolean passive;

    // Ejecuta una acción usando una sola sesión FTP
    private <T> T withFtpSession(Function<FTPClient, T> action) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(ftpServer, ftpPort);

            boolean loginOk = ftpClient.login(ftpUser, ftpPassword);
            if (!loginOk) throw new RuntimeException("No se pudo iniciar sesión en el servidor FTP.");

            if (passive) ftpClient.enterLocalPassiveMode();
            else ftpClient.enterLocalActiveMode();

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setBufferSize(64 * 1024);
            ftpClient.setControlKeepAliveTimeout(15);

            return action.apply(ftpClient);

        } catch (Exception ex) {
            throw new RuntimeException("Error en sesión FTP: " + ex.getMessage(), ex);
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (Exception ignore) {}
        }
    }

    // Aquí se carga el archivo al FTP
    public boolean uploadFile(String remoteFilePath, File localFile) {
        return withFtpSession(ftpClient -> {
            try (InputStream inputStream = new BufferedInputStream(new FileInputStream(localFile))) {
                boolean ok = ftpClient.storeFile(remoteFilePath, inputStream);
                if (!ok) {
                    System.out.println("No se pudo subir el archivo: " + remoteFilePath);
                    System.out.println("Respuesta FTP: " + ftpClient.getReplyString());
                }
                return ok;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        });
    }

    // Aquí se descarga el archivo como bytes desde el FTP
    public byte[] downloadFileAsBytes(String remoteFilePath) {
        return withFtpSession(ftpClient -> {
            try (InputStream inputStream = ftpClient.retrieveFileStream(remoteFilePath);
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                if (inputStream == null) {
                    System.out.println("Archivo no encontrado en el FTP: " + remoteFilePath);
                    System.out.println("Respuesta FTP: " + ftpClient.getReplyString());
                    return null;
                }

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                if (!ftpClient.completePendingCommand()) {
                    System.out.println("No se pudo completar la descarga: " + remoteFilePath);
                    System.out.println("Respuesta FTP: " + ftpClient.getReplyString());
                    return null;
                }

                return outputStream.toByteArray();

            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        });
    }

    // Aquí se descarga el archivo y se convierte a Base64
    public String getFileAsBase64(String remoteFilePath) {
        byte[] data = downloadFileAsBytes(remoteFilePath);
        return (data == null) ? null : Base64.getEncoder().encodeToString(data);
    }

    // Variante para cuando ya tienes una sesión abierta
    public String getFileAsBase64(FTPClient ftpClient, String remoteFilePath) {
        try (InputStream inputStream = ftpClient.retrieveFileStream(remoteFilePath);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            if (inputStream == null) {
                System.out.println("Archivo no encontrado en el FTP: " + remoteFilePath);
                System.out.println("Respuesta FTP: " + ftpClient.getReplyString());
                return null;
            }

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            if (!ftpClient.completePendingCommand()) {
                System.out.println("No se pudo completar la transferencia: " + remoteFilePath);
                System.out.println("Respuesta FTP: " + ftpClient.getReplyString());
                return null;
            }

            return Base64.getEncoder().encodeToString(outputStream.toByteArray());

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
