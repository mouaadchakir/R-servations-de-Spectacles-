package com.ticketing.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Component
public class QRCodeGenerator {

    /**
     * Generate a QR code as a byte array
     *
     * @param content The content to encode in the QR code
     * @param width   Width of the QR code
     * @param height  Height of the QR code
     * @return Byte array representing the QR code image
     */
    public byte[] generateQRCodeImage(String content, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        
        return outputStream.toByteArray();
    }

    /**
     * Generate a QR code and return as a Base64 encoded string
     *
     * @param content The content to encode in the QR code
     * @param width   Width of the QR code
     * @param height  Height of the QR code
     * @return Base64 encoded string of the QR code image
     */
    public String generateQRCodeBase64(String content, int width, int height) throws WriterException, IOException {
        byte[] qrCodeBytes = generateQRCodeImage(content, width, height);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(qrCodeBytes);
    }
}
