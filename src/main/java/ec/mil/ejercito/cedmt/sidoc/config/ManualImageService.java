package ec.mil.ejercito.cedmt.sidoc.config;

import ec.mil.ejercito.cedmt.sidoc.service.FtpService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ManualImageService {

    private final FtpService ftpService;

    public ManualImageService(FtpService ftpService) {
        this.ftpService = ftpService;
    }

    /**
     * Devuelve los bytes de la imagen desde FTP. La primera vez la descarga,
     * las siguientes salen de caché (RAM) gracias a Caffeine.
     */
    @Cacheable(value = "manualImages", key = "#remoteFilePath", unless = "#result == null")
    public byte[] getImageBytes(String remoteFilePath) {
        return ftpService.downloadFileAsBytes(remoteFilePath);
    }
}
