package com.ticketing.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.DocumentException;
import com.ticketing.model.Reservation;
import com.ticketing.model.Show;
import com.ticketing.service.ReservationService;
import com.ticketing.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TemplateEngine templateEngine;
    private final ReservationService reservationService;

    @Override
    public byte[] generateQRCode(Reservation reservation) {
        try {
            // Generate QR code content with reservation details
            String qrContent = String.format("RESERVATION:%s;SHOW:%s;SEAT:%s;USER:%s",
                    reservation.getConfirmationCode(),
                    reservation.getShow().getId(),
                    reservation.getSeat().getSeatNumber(),
                    reservation.getUser().getId());

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 200, 200);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating QR code", e);
        }
    }

    @Override
    public ByteArrayOutputStream generateTicketPDF(Reservation reservation) {
        try {
            // Format the show date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedShowDate = reservation.getShow().getDate().format(formatter);
            
            // Create context for Thymeleaf template
            Context context = new Context();
            context.setVariable("reservation", reservation);
            context.setVariable("showDate", formattedShowDate);
            context.setVariable("qrCode", getQRCodeBase64(reservation));
            
            // Process the Thymeleaf template
            String ticketHtml = templateEngine.process("ticket/pdf-ticket", context);
            
            // Generate PDF from HTML
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(ticketHtml);
            renderer.layout();
            renderer.createPDF(outputStream);
            
            return outputStream;
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF ticket", e);
        }
    }

    @Override
    public ByteArrayOutputStream generateDemoTicket() {
        try {
            // Demo data
            Map<String, Object> demoData = new HashMap<>();
            demoData.put("showTitle", "Demo Show: Phantom of the Opera");
            demoData.put("showDate", "25/05/2025 19:30");
            demoData.put("location", "Grand Theatre");
            demoData.put("seatNumber", "A-12");
            demoData.put("userName", "Demo User");
            demoData.put("confirmationCode", "DEMO" + UUID.randomUUID().toString().substring(0, 4));
            
            // Generate a simple QR code for demo
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode("DEMO TICKET", BarcodeFormat.QR_CODE, 200, 200);
            ByteArrayOutputStream qrStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", qrStream);
            String qrBase64 = Base64.getEncoder().encodeToString(qrStream.toByteArray());
            
            // Create context for Thymeleaf template
            Context context = new Context();
            context.setVariable("demo", demoData);
            context.setVariable("qrCode", "data:image/png;base64," + qrBase64);
            
            // Process the Thymeleaf template
            String ticketHtml = templateEngine.process("ticket/demo-ticket", context);
            
            // Generate PDF from HTML
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(ticketHtml);
            renderer.layout();
            renderer.createPDF(outputStream);
            
            return outputStream;
        } catch (Exception e) {
            throw new RuntimeException("Error generating demo ticket", e);
        }
    }

    @Override
    public String getQRCodeBase64(Reservation reservation) {
        byte[] qrCodeBytes = generateQRCode(reservation);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(qrCodeBytes);
    }
}
