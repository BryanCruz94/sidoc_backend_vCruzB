package ec.mil.ejercito.cedmt.sidoc.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;


@Component
public class PdfToTextUtil {
    /**
     * Extrae el texto de un archivo PDF.
     *
     * @param pdfFile Archivo PDF del cual se extraerá el texto.
     * @return El contenido del PDF como un String.
     * @throws IOException Si ocurre un error al procesar el archivo PDF.
     */
    public static String extractTextFromPdf(File pdfFile) throws IOException {
        // Validar que el archivo no sea null y que exista
        if (pdfFile == null || !pdfFile.exists() || !pdfFile.isFile()) {
            throw new IllegalArgumentException("El archivo proporcionado no es válido o no existe.");
        }

        // Usar PDFBox para leer el archivo PDF y extraer el texto
        try (PDDocument document = PDDocument.load(pdfFile)) {
            if (document.isEncrypted()) {
                throw new IOException("El archivo PDF está encriptado y no se puede procesar.");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

}
