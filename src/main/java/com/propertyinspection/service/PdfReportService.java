package com.propertyinspection.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.propertyinspection.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfReportService {

    private static final int PROBLEMS_PER_PAGE = 5;

    public byte[] generate(ReportRequest r, List<MultipartFile> photos) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 30, 30, 34, 34);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 19);
            Font section = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font label = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f);
            Font normal = FontFactory.getFont(FontFactory.HELVETICA, 8.5f);
            Font small = FontFactory.getFont(FontFactory.HELVETICA, 7.5f);
            Font problemFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9.5f);

            addHeader(doc, title, r);
            addPropertyDetails(doc, r, label, normal);

            List<ProblemDto> list = r.getProblems();
            if (list == null || list.isEmpty()) {
                Paragraph noProblems = new Paragraph("No problems were reported during this inspection.", normal);
                noProblems.setSpacingBefore(15);
                doc.add(noProblems);
            } else {
                Paragraph findings = new Paragraph("INSPECTION FINDINGS   |   TOTAL PROBLEMS: " + list.size(), section);
                findings.setSpacingBefore(12);
                findings.setSpacingAfter(7);
                doc.add(findings);

                int photoCursor = 0;

                for (int start = 0; start < list.size(); start += PROBLEMS_PER_PAGE) {
                    int end = Math.min(start + PROBLEMS_PER_PAGE, list.size());
                    PdfPTable table = new PdfPTable(3);
                    table.setWidthPercentage(100);
                    table.setWidths(new float[]{0.45f, 4.1f, 1.75f});
                    table.setHeaderRows(1);
                    table.setSplitRows(false);
                    table.setKeepTogether(true);

                    addHeaderCell(table, "#");
                    addHeaderCell(table, "PROBLEM INFORMATION");
                    addHeaderCell(table, "PHOTO");

                    for (int i = start; i < end; i++) {
                        ProblemDto p = list.get(i);
                        int count = p.getPhotoCount() == null ? 0 : p.getPhotoCount();

                        PdfPCell number = new PdfPCell(new Phrase(String.valueOf(i + 1), problemFont));
                        styleCell(number, 6, Element.ALIGN_CENTER, Element.ALIGN_TOP);
                        table.addCell(number);

                        PdfPCell info = new PdfPCell();
                        info.setPadding(7);
                        info.setVerticalAlignment(Element.ALIGN_TOP);

                        Paragraph area = new Paragraph(val(p.getArea()), label);
                        area.setSpacingAfter(2);
                        info.addElement(area);

                        Paragraph problem = new Paragraph(val(p.getProblemName()), problemFont);
                        problem.setSpacingAfter(4);
                        info.addElement(problem);

                        Paragraph severity = new Paragraph("Severity: " + val(p.getSeverity()), label);
                        severity.setSpacingAfter(4);
                        info.addElement(severity);

                        Paragraph description = new Paragraph("Description: " + val(p.getDescription()), normal);
                        description.setLeading(10);
                        info.addElement(description);
                        table.addCell(info);

                        PdfPCell photoCell = new PdfPCell();
                        photoCell.setPadding(5);
                        photoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        photoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

                        boolean addedPhoto = false;
                        int photosForProblem = 0;
                        while (photosForProblem < count && photoCursor < photos.size()) {
                            MultipartFile f = photos.get(photoCursor++);
                            if (f == null || f.isEmpty() || f.getContentType() == null
                                    || !f.getContentType().startsWith("image/")) {
                                continue;
                            }

                            // Keep the report compact: show the first two photos for each problem.
                            if (photosForProblem < 2) {
                                Image img = Image.getInstance(f.getBytes());
                                img.scaleToFit(125, 92);
                                img.setAlignment(Element.ALIGN_CENTER);
                                photoCell.addElement(img);
                                Paragraph cap = new Paragraph("Photo " + (photosForProblem + 1), small);
                                cap.setAlignment(Element.ALIGN_CENTER);
                                photoCell.addElement(cap);
                                addedPhoto = true;
                            }
                            photosForProblem++;
                        }

                        if (!addedPhoto) {
                            Paragraph noPhoto = new Paragraph("No photo", small);
                            noPhoto.setAlignment(Element.ALIGN_CENTER);
                            photoCell.addElement(noPhoto);
                        }

                        table.addCell(photoCell);
                    }

                    doc.add(table);

                    if (end < list.size()) {
                        doc.newPage();
                        Paragraph continued = new Paragraph(
                                "PROPERTY INSPECTION REPORT — Continued",
                                section);
                        continued.setSpacingAfter(7);
                        doc.add(continued);
                    }
                }
            }

            Paragraph footer = new Paragraph("Generated by Property Inspection System.", small);
            footer.setSpacingBefore(12);
            doc.add(footer);
            doc.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new IllegalStateException("PDF generation failed.", e);
        }
    }

    private void addHeader(Document doc, Font title, ReportRequest r) throws DocumentException {
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{2.5f, 7.5f});
        header.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        PdfPCell brand = new PdfPCell();
        brand.setBorder(Rectangle.NO_BORDER);
        Paragraph brandName = new Paragraph("PROPERTY\nINSPECTION", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10));
        brand.addElement(brandName);
        header.addCell(brand);

        PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(Rectangle.NO_BORDER);
        Paragraph t = new Paragraph("PROPERTY INSPECTION REPORT", title);
        t.setAlignment(Element.ALIGN_RIGHT);
        titleCell.addElement(t);
        Paragraph sub = new Paragraph("Design and developed by Vaibhav", FontFactory.getFont(FontFactory.HELVETICA, 8.5f));
        sub.setAlignment(Element.ALIGN_RIGHT);
        titleCell.addElement(sub);
        header.addCell(titleCell);

        doc.add(header);
        LineSeparator line = new LineSeparator();
        line.setLineWidth(1.2f);
        doc.add(new Chunk(line));
        doc.add(Chunk.NEWLINE);
    }

    private void addPropertyDetails(Document doc, ReportRequest r, Font label, Font normal) throws DocumentException {
        PdfPTable details = new PdfPTable(4);
        details.setWidthPercentage(100);
        details.setWidths(new float[]{1.25f, 3.0f, 1.25f, 3.0f});

        detail(details, "Property", val(r.getPropertyName()), label, normal);
        detail(details, "Inspection Date", val(r.getInspectionDate()), label, normal);
        detail(details, "Address", val(r.getAddress()), label, normal);
        detail(details, "Inspector", val(r.getInspectorName()), label, normal);
        detail(details, "Unit / Flat", val(r.getUnitNumber()), label, normal);
        detail(details, "Owner / Customer", val(r.getOwnerName()), label, normal);

        // Fill the final cell pair so the table remains rectangular.
        detail(details, "", "", label, normal);

        doc.add(details);
    }

    private void detail(PdfPTable t, String l, String v, Font lf, Font vf) {
        PdfPCell a = new PdfPCell(new Phrase(l, lf));
        PdfPCell b = new PdfPCell(new Phrase(v, vf));
        a.setPadding(5);
        b.setPadding(5);
        a.setVerticalAlignment(Element.ALIGN_TOP);
        b.setVerticalAlignment(Element.ALIGN_TOP);
        t.addCell(a);
        t.addCell(b);
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f)));
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private void styleCell(PdfPCell cell, float padding, int horizontal, int vertical) {
        cell.setPadding(padding);
        cell.setHorizontalAlignment(horizontal);
        cell.setVerticalAlignment(vertical);
    }

    private String val(String v) {
        return v == null || v.isBlank() ? "-" : v;
    }
}
