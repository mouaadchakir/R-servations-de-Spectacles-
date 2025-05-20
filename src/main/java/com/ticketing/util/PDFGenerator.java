package com.ticketing.util;

import com.lowagie.text.DocumentException;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Component
public class PDFGenerator {

    private final TemplateEngine templateEngine;

    public PDFGenerator(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * Generate a PDF from a Thymeleaf template
     *
     * @param templateName Name of the Thymeleaf template
     * @param variables    Map of variables to be used in the template
     * @return ByteArrayOutputStream containing the PDF
     */
    public ByteArrayOutputStream generatePDF(String templateName, Map<String, Object> variables) throws DocumentException {
        // Prepare Thymeleaf context
        Context context = new Context();
        if (variables != null) {
            variables.forEach(context::setVariable);
        }

        // Process template to HTML
        String htmlContent = templateEngine.process(templateName, context);

        // Convert HTML to PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);

        return outputStream;
    }
}
