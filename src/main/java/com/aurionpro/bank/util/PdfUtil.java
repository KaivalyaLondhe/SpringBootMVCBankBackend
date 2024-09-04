package com.aurionpro.bank.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class PdfUtil {

    public static void convertCsvToPdf(String csvFilePath, String pdfFilePath) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
            document.open();

            try (Scanner scanner = new Scanner(new FileInputStream(csvFilePath))) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    document.add(new Paragraph(line));
                }
            }

            document.close();
        } catch (IOException | DocumentException e) {
            throw new RuntimeException("Failed to convert CSV to PDF", e);
        }
    }
}
