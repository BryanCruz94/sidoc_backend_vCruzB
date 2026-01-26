package ec.mil.ejercito.cedmt.sidoc.service;

import ec.mil.ejercito.cedmt.sidoc.dto.DocEjercitoResponseDTO;
import ec.mil.ejercito.cedmt.sidoc.dto.*;
import ec.mil.ejercito.cedmt.sidoc.model.*;
import ec.mil.ejercito.cedmt.sidoc.repository.*;
import ec.mil.ejercito.cedmt.sidoc.util.PdfWatermarkUtil;
import ec.mil.ejercito.cedmt.sidoc.util.PdfToTextUtil;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

@Service
public class ManualService {

    @Autowired
    private FtpService ftpService;

    @Autowired
    private ManualRepository manualRepository;

    @Autowired
    private TipoRepository tipoRepository;

    @Autowired
    private SubcategoriaRepository subcategoriaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private DescargaRepository descargaRepository;

    @Autowired
    private PdfWatermarkUtil pdfWatermarkUtil;

    //RUTAS DE FTP
    @Value("${sidoc.ruta.archivos}")
    private String baseRutaArchivos;

    @Value("${sidoc.ruta.portadas}")
    private String baseRutaPortadas;


    public java.util.List<DocEjercitoResponseDTO> getActiveManuals() {
        List<DocEjercitoResponseDTO> manuals = manualRepository.findActiveManuals();

        for (DocEjercitoResponseDTO m : manuals) {
            // Solo si tienes la ruta FTP registrada
            if (m.getUrlImagen() != null && !m.getUrlImagen().isBlank() && m.getId() != null) {
                // Requiere campo imageUrl en el DTO (recomendado)
                try {
                    m.getClass().getMethod("setImageUrl", String.class)
                            .invoke(m, "/api/manuals/" + m.getId() + "/image");
                } catch (Exception ignore) {
                    // Si aún no agregas imageUrl al DTO, puedes construir la URL en Angular:
                    // [src]="'/api/manuals/' + manual.id + '/image'"
                }
            }
        }
        return manuals;
    }

    public ManualNewResponseDTO subirManual(ManualRequestNewDTO manualRequest) {
        File tempArchivo = null;
        File tempPortada = null;
        File resizedPortada = null;

        try {
            // 1) Subir PDF al FTP
            tempArchivo = convertToTempFile(manualRequest.getArchivo());

            String nombreArchivo = getTimestamp() + safeFileName(manualRequest.getArchivo().getOriginalFilename());
            String rutaArchivo = baseRutaArchivos + nombreArchivo;

            boolean pdfOk = ftpService.uploadFile(rutaArchivo, tempArchivo);
            if (!pdfOk) {
                throw new RuntimeException("No se pudo subir el PDF al FTP.");
            }

            // 2) Subir portada
            String rutaPortada = null;
            if (manualRequest.getPortada() != null && !manualRequest.getPortada().isEmpty()) {
                tempPortada = convertToTempFile(manualRequest.getPortada());
                resizedPortada = resizeImage(tempPortada, 315, 444);

                String nombrePortada = getTimestamp() + safeFileName(manualRequest.getPortada().getOriginalFilename());
                rutaPortada = baseRutaPortadas + nombrePortada;

                boolean portadaOk = ftpService.uploadFile(rutaPortada, resizedPortada);
                if (!portadaOk) {
                    throw new RuntimeException("No se pudo subir la portada al FTP.");
                }
            }

            // 3) Crear entidad Manual
            Manual manual = new Manual();
            manual.setNombre(manualRequest.getNombre());
            manual.setCodigo(manualRequest.getCodigo());
            manual.setDescripcion(manualRequest.getDescripcion());
            manual.setAnioPublicacion(LocalDate.parse(manualRequest.getAnioPublicacion()));
            manual.setEstado('1');
            manual.setPublicado('1');
            manual.setUrlImagen(rutaPortada);
            manual.setEnlaceDescarga(rutaArchivo);

            // 4) Asignar relaciones
            if (manualRequest.getSubcategoriaId() != null) {
                manual.setSubcategoria(subcategoriaRepository.findById(manualRequest.getSubcategoriaId())
                        .orElseThrow(() -> new RuntimeException("Subcategoría no encontrada con ID: " + manualRequest.getSubcategoriaId())));
            }

            manual.setTipo(tipoRepository.findById(manualRequest.getTipoId())
                    .orElseThrow(() -> new RuntimeException("Tipo no encontrado con ID: " + manualRequest.getTipoId())));

            if (manualRequest.getCategoriaId() != null) {
                manual.setCategoria(categoriaRepository.findById(manualRequest.getCategoriaId())
                        .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + manualRequest.getCategoriaId())));
            }

            // 5) Guardar en BD
            Manual manualGuardado = manualRepository.save(manual);
            return new ManualNewResponseDTO(manualGuardado);

        } catch (Exception e) {
            throw new RuntimeException("Error al crear el manual.", e);
        } finally {
            deleteFileSafe(tempArchivo);
            deleteFileSafe(tempPortada);
            deleteFileSafe(resizedPortada);
        }
    }


    //Metodo para descargar el PDF del manual, incluye marcas de agua de usuario y fecha
    public byte[] obtenerArchivoPdf(Long manualId, String usuario, String ip, String cedula) {
        Manual manual = manualRepository.findByIdDescarga(manualId)
                .orElseThrow(() -> new RuntimeException("Manual no encontrado con ID: " + manualId));

        String rutaArchivo = manual.getEnlaceDescarga();
        byte[] pdfBytes = ftpService.downloadFileAsBytes(rutaArchivo);

        if (pdfBytes == null) {
            throw new RuntimeException("No se pudo descargar el archivo desde el FTP.");
        }

        //LLENAR LA TABLA DESCARGAS
        Descarga descarga = new Descarga();
        descarga.setManual(manual);
        descarga.setFecha(LocalDateTime.now());
        descarga.setIp(ip);
        descarga.setUsuarioId(usuario);
        descargaRepository.save(descarga);

        return pdfWatermarkUtil.addWatermarkToPdf(pdfBytes, "Desc. por " + usuario + " - " + cedula + LocalDate.now());
    }

   public void eliminarManual(Long manualId) {
        Manual manual = manual = manualRepository.findByIdDescarga(manualId)
                .orElseThrow(() -> new RuntimeException("Manual no encontrado con ID: " + manualId));
        manual.setEstado('0');
        manualRepository.save(manual);
    }

    public ManualResponseToEditDTO manualParaEditar(Long idManual) {
       Manual manual = manualRepository.findByIdDescarga(idManual)
                .orElseThrow(() -> new RuntimeException("Manual no encontrado con ID: " + idManual));
        ManualResponseToEditDTO manualResponseDTO = manualRepository.findManualById(idManual);
        if (manualResponseDTO.getUrlImagen() != null) {
            String base64Image = ftpService.getFileAsBase64(manualResponseDTO.getUrlImagen());
            manualResponseDTO.setUrlImagen(base64Image);
        }
        return manualResponseDTO;
    }

    public ManualNewResponseDTO editarManual(ManualRequestEditDTO manualRequest) throws IOException {
        Manual manual = manualRepository.findByIdDescarga(manualRequest.getId())
                .orElseThrow(() -> new RuntimeException("Manual no encontrado con ID: " + manualRequest.getId()));

        manual.setNombre(manualRequest.getNombre());
        manual.setCodigo(manualRequest.getCodigo());
        manual.setAnioPublicacion(LocalDate.parse(manualRequest.getAnioPublicacion()));
        manual.setDescripcion(manualRequest.getDescripcion());
        manual.setPublicado(manualRequest.getPublicado());

        if (manualRequest.getTipoId() != null) {
            manual.setTipo(tipoRepository.findById(manualRequest.getTipoId())
                    .orElseThrow(() -> new RuntimeException("Tipo no encontrado con ID: " + manualRequest.getTipoId())));
        }

        if (manualRequest.getSubcategoriaId() != null) {
            manual.setSubcategoria(subcategoriaRepository.findById(manualRequest.getSubcategoriaId())
                    .orElseThrow(() -> new RuntimeException("Subcategoría no encontrada con ID: "
                            + manualRequest.getSubcategoriaId())));
        }

        if (manualRequest.getArchivo() != null) {
            File tempArchivo = convertToTempFile(manualRequest.getArchivo());
            String rutaArchivo = baseRutaArchivos + getHoraFecha() + manualRequest.getArchivo().getOriginalFilename();
            ftpService.uploadFile(rutaArchivo, tempArchivo);
            manual.setEnlaceDescarga(rutaArchivo);
            tempArchivo.delete();
        }

        if (manualRequest.getPortada() != null) {
            File tempPortada = convertToTempFile(manualRequest.getPortada());
            File resizedPortada = resizeImage(tempPortada, 450, 640);
            String rutaPortada = baseRutaPortadas + getHoraFecha() + manualRequest.getPortada().getOriginalFilename();
            ftpService.uploadFile(rutaPortada, resizedPortada);
            manual.setUrlImagen(rutaPortada);
            tempPortada.delete();
            resizedPortada.delete();
        }

        Manual updatedManual = manualRepository.save(manual);

        return new ManualNewResponseDTO(updatedManual);
    }


    /* MÉTODOS UTILITARIOS */

    private void deleteFileSafe(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    private File convertToTempFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("upload_", file.getOriginalFilename());
        file.transferTo(tempFile);
        return tempFile;
    }

    private File resizeImage(File originalImage, int width, int height) throws IOException {
        BufferedImage inputImage = ImageIO.read(originalImage);

        // Enfoque escalonado para mejor calidad
        BufferedImage resizedImage = resizeImageStepwise(inputImage, width, height);

        // Determinar el formato del archivo original
        String originalFileName = originalImage.getName().toLowerCase();
        String formatName = originalFileName.endsWith(".png") ? "png" : "jpg";

        // Guardar la imagen redimensionada
        File outputImage = File.createTempFile("resized_", "." + formatName);
        if (formatName.equals("jpg")) {
            saveAsJpegWithQuality(resizedImage, 0.9f, outputImage); // Ajusta la calidad al 90%
        } else {
            ImageIO.write(resizedImage, formatName, outputImage);
        }

        return outputImage;
    }

    private void saveAsJpegWithQuality(BufferedImage image, float quality, File outputFile) throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality); // Calidad entre 0.0f (baja) y 1.0f (alta)

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputFile)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), param);
        }
        writer.dispose();
    }

    private BufferedImage resizeImageStepwise(BufferedImage originalImage, int width, int height) {
        int currentWidth = originalImage.getWidth();
        int currentHeight = originalImage.getHeight();

        BufferedImage tempImage = originalImage;

        while (currentWidth > width || currentHeight > height) {
            currentWidth = Math.max(width, currentWidth / 2);
            currentHeight = Math.max(height, currentHeight / 2);

            BufferedImage resizedImage = new BufferedImage(currentWidth, currentHeight, tempImage.getType());
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.drawImage(tempImage, 0, 0, currentWidth, currentHeight, null);
            g2d.dispose();

            tempImage = resizedImage;
        }
        return tempImage;
    }



    public String getHoraFecha() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd HH:MM"));
    }

    // *** MÉTODOS PARA GENERAR RESUMEN DEL MANUAL CON IA
    // Convierte pdf a texto
    private final ChatAIService chatAIService;

    // Constructor con inyección de dependencias
    public ManualService(ChatAIService chatAIService) {
        this.chatAIService = chatAIService;
    }

    public String convertirPDFaText(File tempArchivo) {
        String textoPDF;
        try {
            textoPDF = PdfToTextUtil.extractTextFromPdf(tempArchivo);
            return textoPDF;
        } catch (IOException e) {
            throw new RuntimeException("Error", e);
        }
    }

    // Timestamp seguro para nombres de archivo (sin espacios ni ':' )
    private String getTimestamp() {
        return java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_"));
    }

    // Limpia el nombre del archivo para evitar caracteres raros en FTP
    private String safeFileName(String name) {
        if (name == null) return "archivo";
        // deja letras/números/punto/guion/guion bajo; lo demás lo convierte a "_"
        return name.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
    }

    // Genera resumen del texto
    public String generarResumen(String textoPDF) {
        return chatAIService.getManualAbstract(textoPDF);
    }

}
