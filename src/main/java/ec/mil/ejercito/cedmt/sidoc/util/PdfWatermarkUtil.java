package ec.mil.ejercito.cedmt.sidoc.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class PdfWatermarkUtil {
    public byte[] addWatermarkToPdf(byte[] pdfBytes, String watermarkText) {
        try (ByteArrayInputStream pdfInputStream = new ByteArrayInputStream(pdfBytes);
             PDDocument document = PDDocument.load(pdfInputStream);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // Eliminar todas las restricciones de seguridad
            document.setAllSecurityToBeRemoved(true);

            int totalPages = document.getNumberOfPages();

            if(totalPages <= 20){
                for (int i = 0; i < totalPages; i++) {
                    addWatermarkToPage(document, document.getPage(i), watermarkText);
                }
                document.save(outputStream);
                return outputStream.toByteArray();
            }else {
                int firstPagesToWatermark = Math.min(10, totalPages);
                int lastPagesToWatermarkStart = Math.max(0, totalPages - 10);

                for (int i = 0; i < firstPagesToWatermark; i++) {
                    addWatermarkToPage(document, document.getPage(i), watermarkText);
                }

                for (int i = lastPagesToWatermarkStart; i < totalPages; i++) {
                    addWatermarkToPage(document, document.getPage(i), watermarkText);
                }
            }
            // Guardar el documento con las marcas de agua aplicadas
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al agregar la marca de agua al PDF.", e);
        }
    }

    // Método auxiliar para agregar la marca de agua a una página específica
    private void addWatermarkToPage(PDDocument document, PDPage page, String watermarkText) throws IOException {
        // Obtener dimensiones de la página
        float pageWidth = page.getMediaBox().getWidth();
        //float pageHeight = page.getMediaBox().getHeight();

        // Configurar fuente y tamaño de texto
        float fontSize = 22;
        PDType1Font font = PDType1Font.HELVETICA_BOLD;

        // Calcular el ancho del texto
        float textWidth = (font.getStringWidth(watermarkText) / 1000) * fontSize;

        // Coordenadas centradas
        float centerX = ((pageWidth - textWidth) / 2)+100;
        float centerY = 80;

        // Crear flujo de contenido en modo "PREPEND" para que la marca quede detrás del contenido
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.PREPEND, true)) {
            // Configurar color y fuente
            contentStream.setNonStrokingColor(213, 216, 220); // Gris claro
            contentStream.setFont(font, fontSize);

            contentStream.beginText();
            contentStream.setTextRotation(Math.toRadians(45), centerX, centerY);
            contentStream.showText(watermarkText);
            contentStream.endText();
        }
    }

}
