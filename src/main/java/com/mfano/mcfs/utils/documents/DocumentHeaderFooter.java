package com.mfano.mcfs.utils.documents;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;

import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class DocumentHeaderFooter extends PdfPageEventHelper {

    private final String filter;

    public DocumentHeaderFooter(String filter) {
        this.filter = filter;
    }

    @Override
    public void onEndPage(
            PdfWriter writer,
            Document document) {

        PdfContentByte canvas =
                writer.getDirectContent();

        // ==========================================
        // LOGO
        // ==========================================
        try {
            InputStream logoStream =
                    getClass()
                            .getClassLoader()
                            .getResourceAsStream(
                                    "static/images/mfano.png"
                            );

            if (logoStream != null) {

                byte[] logoBytes =
                        logoStream.readAllBytes();

                Image logo =
                        Image.getInstance(logoBytes);

                // Size
                logo.scaleToFit(65, 65);

                // Position
                float logoX =
                        document.left();

                float logoY =
                        document.top() + 15;

                logo.setAbsolutePosition(
                        logoX,
                        logoY
                );

                canvas.addImage(logo);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // ==========================================
        // COMPANY NAME
        // ==========================================
        Font companyFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        18
                );

        Font subtitleFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        9
                );

        float centerX =
                (document.left()
                        + document.right()) / 2;

        ColumnText.showTextAligned(
                canvas,
                Element.ALIGN_CENTER,
                new Phrase(
                        "MCFS",
                        companyFont
                ),
                centerX,
                document.top() + 40,
                0
        );

        ColumnText.showTextAligned(
                canvas,
                Element.ALIGN_CENTER,
                new Phrase(
                        "DOCUMENT MANAGEMENT SYSTEM",
                        subtitleFont
                ),
                centerX,
                document.top() + 27,
                0
        );

        // ==========================================
        // HEADER LINE
        // ==========================================
        canvas.moveTo(
                document.left(),
                document.top() + 15
        );

        canvas.lineTo(
                document.right(),
                document.top() + 15
        );

        canvas.stroke();

        // ==========================================
        // FOOTER
        // ==========================================
        Font footerFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        8
                );

        String generated =
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "dd MMM yyyy HH:mm"
                                )
                        );

        ColumnText.showTextAligned(
                canvas,
                Element.ALIGN_LEFT,
                new Phrase(
                        "MCFS - Confidential",
                        footerFont
                ),
                document.left(),
                document.bottom() - 20,
                0
        );

        ColumnText.showTextAligned(
                canvas,
                Element.ALIGN_CENTER,
                new Phrase(
                        "Generated: " + generated,
                        footerFont
                ),
                centerX,
                document.bottom() - 20,
                0
        );

        ColumnText.showTextAligned(
                canvas,
                Element.ALIGN_RIGHT,
                new Phrase(
                        "Page " + writer.getPageNumber(),
                        footerFont
                ),
                document.right(),
                document.bottom() - 20,
                0
        );
    }
}