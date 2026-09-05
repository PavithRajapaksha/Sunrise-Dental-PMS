package com.sunrise.sunrisedentalpms.util;

import com.sunrise.sunrisedentalpms.model.Appointment;
import com.sunrise.sunrisedentalpms.model.Bill;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class BillGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final float MARGIN = 50;
    private static final float LINE_HEIGHT = 20;

    private BillGenerator() {
    }

    public static byte[] generate(Bill bill) throws IOException {
        Appointment appointment = bill.getAppointment();

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                float y = page.getMediaBox().getHeight() - MARGIN;

                content.setFont(boldFont, 18);
                y = writeLine(content, "Sunrise Dental Clinic", MARGIN, y);
                y -= LINE_HEIGHT;

                content.setFont(boldFont, 12);
                y = writeLine(content, "Bill #" + bill.getBillId(), MARGIN, y);

                content.setFont(regularFont, 11);
                y = writeLine(content, "Date: " + bill.getGeneratedDate().format(DATE_FORMAT), MARGIN, y);
                y = writeLine(content, "Appointment #: " + appointment.getAppointmentNumber(), MARGIN, y);
                y = writeLine(content, "Patient: " + appointment.getPatient().getName(), MARGIN, y);
                y = writeLine(content, "Dentist: " + appointment.getDentist().getName(), MARGIN, y);
                y = writeLine(content, "Treatment: " + appointment.getTreatmentType().getName(), MARGIN, y);
                y -= LINE_HEIGHT / 2;
                y = writeLine(content, "Total amount: Rs. " + bill.getTotalAmount(), MARGIN, y);
                y = writeLine(content, "Status: " + bill.getStatus(), MARGIN, y);
                String paymentTypeText = bill.getPaymentType() == null ? "Not yet recorded" : bill.getPaymentType().name();
                y = writeLine(content, "Payment type: " + paymentTypeText, MARGIN, y);
                y -= LINE_HEIGHT;
                writeLine(content, "Thank you for choosing Sunrise Dental Clinic.", MARGIN, y);
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            document.save(output);
            return output.toByteArray();
        }
    }

    private static float writeLine(PDPageContentStream content, String text, float x, float y) throws IOException {
        content.beginText();
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
        return y - LINE_HEIGHT;
    }
}