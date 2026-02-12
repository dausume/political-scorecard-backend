package com.asc.politicalscorecard.services.documentservices;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class DocumentConversionService {

    private static final Logger logger = Logger.getLogger(DocumentConversionService.class.getName());

    public byte[] htmlToPdf(String html) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            String wrappedHtml = wrapHtml(html);
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(wrappedHtml, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error converting HTML to PDF: ", e);
            throw new RuntimeException("Failed to convert HTML to PDF", e);
        }
    }

    public byte[] htmlToDocx(String html) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            String wrappedHtml = wrapXhtml(html);
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
            XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);
            wordMLPackage.getMainDocumentPart().getContent().addAll(
                    xhtmlImporter.convert(wrappedHtml, null)
            );
            wordMLPackage.save(os);
            return os.toByteArray();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error converting HTML to DOCX: ", e);
            throw new RuntimeException("Failed to convert HTML to DOCX", e);
        }
    }

    public String docxToHtml(InputStream docxInputStream) {
        try {
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(docxInputStream);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Docx4J.toHTML(wordMLPackage, null, null, os);
            return os.toString("UTF-8");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error converting DOCX to HTML: ", e);
            throw new RuntimeException("Failed to convert DOCX to HTML", e);
        }
    }

    private String wrapHtml(String bodyContent) {
        return "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"/>" +
               "<style>body { font-family: serif; font-size: 12pt; margin: 1in; }</style>" +
               "</head><body>" + bodyContent + "</body></html>";
    }

    private String wrapXhtml(String bodyContent) {
        return "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta charset=\"UTF-8\"/></head><body>" +
               bodyContent + "</body></html>";
    }
}
