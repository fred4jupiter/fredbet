package de.fred4jupiter.fredbet.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;

public class PdfCreationUT {

    @Test
    public void createPdfTable() throws IOException {
        System.out.println("My first table");
        // step 1: creation of a document-object
        Document document = new Document();
        try {
            // step 2:
            // we create a writer that listens to the document
            // and directs a PDF-stream to a file
            PdfWriter.getInstance(document, new FileOutputStream("MyFirstTable.pdf"));
            // step 3: we open the document
            document.open();
            // step 4: we create a table and add it to the document
            Table table = new Table(2, 2); // 2 rows, 2 columns
            table.addCell("0.0");
            table.addCell("0.1");
            table.addCell("1.0");
            table.addCell("1.1");
            document.add(table);
            document.add(new Paragraph("converted to PdfPTable:"));
            table.setConvert2pdfptable(true);
            document.add(table);
        } catch (DocumentException | IOException de) {
            System.err.println(de.getMessage());
        }
        // step 5: we close the document
        document.close();
    }
}
