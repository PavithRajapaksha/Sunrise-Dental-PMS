package com.sunrise.sunrisedentalpms.util;

import com.sunrise.sunrisedentalpms.model.DentistWorkloadReport;
import com.sunrise.sunrisedentalpms.model.TreatmentRevenueReport;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public final class ReportPdfGenerator {

    private static final PDFont REGULAR_FONT =
            new PDType1Font(
                    Standard14Fonts.FontName.HELVETICA
            );

    private static final PDFont BOLD_FONT =
            new PDType1Font(
                    Standard14Fonts.FontName.HELVETICA_BOLD
            );

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy"
            );

    private static final int MAX_ROWS_PER_PAGE =
            26;

    private ReportPdfGenerator() {
    }

    public static byte[] generateRevenueReport(
            List<TreatmentRevenueReport> report,
            String filterLabel)
            throws IOException {

        List<TreatmentRevenueReport> rows =
                report == null
                        ? List.of()
                        : report;

        try (PDDocument document =
                     new PDDocument();

             ByteArrayOutputStream output =
                     new ByteArrayOutputStream()) {

            int index = 0;
            int pageNumber = 1;

            do {

                PDPage page =
                        new PDPage(
                                PDRectangle.A4
                        );

                document.addPage(
                        page
                );

                try (PDPageContentStream content =
                             new PDPageContentStream(
                                     document,
                                     page
                             )) {

                    float y =
                            drawPageHeader(
                                    content,
                                    "Treatment Revenue Report",
                                    filterLabel,
                                    pageNumber
                            );

                    drawRevenueHeader(
                            content,
                            y
                    );

                    y -= 25;

                    int rowsWritten = 0;

                    while (index < rows.size()
                            && rowsWritten
                            < MAX_ROWS_PER_PAGE) {

                        TreatmentRevenueReport row =
                                rows.get(
                                        index
                                );

                        drawText(
                                content,
                                row.getTreatmentTypeId(),
                                50,
                                y,
                                REGULAR_FONT,
                                10
                        );

                        drawText(
                                content,
                                truncate(
                                        row.getTreatmentName(),
                                        32
                                ),
                                130,
                                y,
                                REGULAR_FONT,
                                10
                        );

                        drawText(
                                content,
                                String.valueOf(
                                        row.getBillCount()
                                ),
                                365,
                                y,
                                REGULAR_FONT,
                                10
                        );

                        drawText(
                                content,
                                "Rs. "
                                        + formatAmount(
                                        row.getTotalRevenue()
                                ),
                                430,
                                y,
                                REGULAR_FONT,
                                10
                        );

                        y -= 22;
                        index++;
                        rowsWritten++;
                    }

                    if (rows.isEmpty()) {

                        drawText(
                                content,
                                "No report data found.",
                                50,
                                y,
                                REGULAR_FONT,
                                10
                        );

                    } else if (index >= rows.size()) {

                        BigDecimal totalRevenue =
                                rows.stream()
                                        .map(
                                                TreatmentRevenueReport
                                                        ::getTotalRevenue
                                        )
                                        .filter(
                                                Objects::nonNull
                                        )
                                        .reduce(
                                                BigDecimal.ZERO,
                                                BigDecimal::add
                                        );

                        int totalBills =
                                rows.stream()
                                        .mapToInt(
                                                TreatmentRevenueReport
                                                        ::getBillCount
                                        )
                                        .sum();

                        y -= 10;

                        drawText(
                                content,
                                "Total Bills:",
                                365,
                                y,
                                BOLD_FONT,
                                10
                        );

                        drawText(
                                content,
                                String.valueOf(
                                        totalBills
                                ),
                                430,
                                y,
                                BOLD_FONT,
                                10
                        );

                        y -= 20;

                        drawText(
                                content,
                                "Total Revenue:",
                                365,
                                y,
                                BOLD_FONT,
                                10
                        );

                        drawText(
                                content,
                                "Rs. "
                                        + formatAmount(
                                        totalRevenue
                                ),
                                450,
                                y,
                                BOLD_FONT,
                                10
                        );
                    }
                }

                pageNumber++;

            } while (index < rows.size());

            document.save(
                    output
            );

            return output.toByteArray();
        }
    }

    public static byte[] generateDentistWorkloadReport(
            List<DentistWorkloadReport> report,
            String filterLabel)
            throws IOException {

        List<DentistWorkloadReport> rows =
                report == null
                        ? List.of()
                        : report;

        try (PDDocument document =
                     new PDDocument();

             ByteArrayOutputStream output =
                     new ByteArrayOutputStream()) {

            int index = 0;
            int pageNumber = 1;

            do {

                PDPage page =
                        new PDPage(
                                PDRectangle.A4
                        );

                document.addPage(
                        page
                );

                try (PDPageContentStream content =
                             new PDPageContentStream(
                                     document,
                                     page
                             )) {

                    float y =
                            drawPageHeader(
                                    content,
                                    "Dentist Workload Report",
                                    filterLabel,
                                    pageNumber
                            );

                    drawWorkloadHeader(
                            content,
                            y
                    );

                    y -= 25;

                    int rowsWritten = 0;

                    while (index < rows.size()
                            && rowsWritten
                            < MAX_ROWS_PER_PAGE) {

                        DentistWorkloadReport row =
                                rows.get(
                                        index
                                );

                        drawText(
                                content,
                                row.getDentistId(),
                                50,
                                y,
                                REGULAR_FONT,
                                10
                        );

                        drawText(
                                content,
                                truncate(
                                        row.getDentistName(),
                                        40
                                ),
                                150,
                                y,
                                REGULAR_FONT,
                                10
                        );

                        drawText(
                                content,
                                String.valueOf(
                                        row.getAppointmentCount()
                                ),
                                430,
                                y,
                                REGULAR_FONT,
                                10
                        );

                        y -= 22;
                        index++;
                        rowsWritten++;
                    }

                    if (rows.isEmpty()) {

                        drawText(
                                content,
                                "No report data found.",
                                50,
                                y,
                                REGULAR_FONT,
                                10
                        );

                    } else if (index >= rows.size()) {

                        int totalAppointments =
                                rows.stream()
                                        .mapToInt(
                                                DentistWorkloadReport
                                                        ::getAppointmentCount
                                        )
                                        .sum();

                        y -= 10;

                        drawText(
                                content,
                                "Total Appointments:",
                                330,
                                y,
                                BOLD_FONT,
                                10
                        );

                        drawText(
                                content,
                                String.valueOf(
                                        totalAppointments
                                ),
                                450,
                                y,
                                BOLD_FONT,
                                10
                        );
                    }
                }

                pageNumber++;

            } while (index < rows.size());

            document.save(
                    output
            );

            return output.toByteArray();
        }
    }

    private static float drawPageHeader(
            PDPageContentStream content,
            String title,
            String filterLabel,
            int pageNumber)
            throws IOException {

        drawText(
                content,
                "Sunrise Dental Clinic",
                50,
                790,
                BOLD_FONT,
                18
        );

        drawText(
                content,
                title,
                50,
                760,
                BOLD_FONT,
                15
        );

        drawText(
                content,
                "Generated: "
                        + LocalDate.now()
                        .format(
                                DATE_FORMAT
                        ),
                50,
                735,
                REGULAR_FONT,
                10
        );

        drawText(
                content,
                "Filter: "
                        + safe(
                        filterLabel
                ),
                50,
                718,
                REGULAR_FONT,
                10
        );

        drawText(
                content,
                "Page "
                        + pageNumber,
                500,
                735,
                REGULAR_FONT,
                9
        );

        return 680;
    }

    private static void drawRevenueHeader(
            PDPageContentStream content,
            float y)
            throws IOException {

        drawText(
                content,
                "ID",
                50,
                y,
                BOLD_FONT,
                10
        );

        drawText(
                content,
                "Treatment",
                130,
                y,
                BOLD_FONT,
                10
        );

        drawText(
                content,
                "Bills",
                365,
                y,
                BOLD_FONT,
                10
        );

        drawText(
                content,
                "Revenue",
                430,
                y,
                BOLD_FONT,
                10
        );
    }

    private static void drawWorkloadHeader(
            PDPageContentStream content,
            float y)
            throws IOException {

        drawText(
                content,
                "Dentist ID",
                50,
                y,
                BOLD_FONT,
                10
        );

        drawText(
                content,
                "Dentist",
                150,
                y,
                BOLD_FONT,
                10
        );

        drawText(
                content,
                "Appointments",
                430,
                y,
                BOLD_FONT,
                10
        );
    }

    private static void drawText(
            PDPageContentStream content,
            String text,
            float x,
            float y,
            PDFont font,
            float fontSize)
            throws IOException {

        content.beginText();

        content.setFont(
                font,
                fontSize
        );

        content.newLineAtOffset(
                x,
                y
        );

        content.showText(
                safe(
                        text
                )
        );

        content.endText();
    }

    private static String formatAmount(
            BigDecimal value) {

        if (value == null) {
            return "0.00";
        }

        return value
                .setScale(
                        2,
                        java.math.RoundingMode.HALF_UP
                )
                .toPlainString();
    }

    private static String truncate(
            String value,
            int maxLength) {

        String text =
                safe(
                        value
                );

        if (text.length()
                <= maxLength) {

            return text;
        }

        return text.substring(
                0,
                maxLength - 3
        ) + "...";
    }

    private static String safe(
            String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace(
                        '\n',
                        ' '
                )
                .replace(
                        '\r',
                        ' '
                )
                .replaceAll(
                        "[^\\x20-\\x7E]",
                        "?"
                );
    }
}