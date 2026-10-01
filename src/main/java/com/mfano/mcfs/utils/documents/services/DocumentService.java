package com.mfano.mcfs.utils.documents.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mfano.mcfs.auth.services.AuditService;
import com.mfano.mcfs.config.CustomUserDetails;
import com.mfano.mcfs.utils.documents.DocumentHeaderFooter;
import com.mfano.mcfs.utils.documents.models.Documents;
import com.mfano.mcfs.utils.documents.repositories.DocumentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final AuditService auditService;
    @Value("${app.upload-dir}")
    private String uploadDir;
    private final DocumentRepository documentRepository;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    // Get Role By Id
    public Documents findById(Long id) {
        return documentRepository.findById(id).orElse(null);
    }

    // Delete Role
    public void delete(Long id) {
        documentRepository.deleteById(id);
    }

    // Update Role
    public void save(Documents doc) {
        documentRepository.save(doc);
    }

    // Get All documents
    public List<Documents> findAll() {
        return documentRepository.findAll();
    }

    // filter today's
    public List<Documents> findToday() {

        LocalDate today = LocalDate.now();

        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        return documentRepository.findByCreatedAtBetween(start, end);
    }

    // filter this month's
    public List<Documents> findThisMonth() {

        YearMonth month = YearMonth.now();

        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end = month.plusMonths(1).atDay(1).atStartOfDay();

        return documentRepository.findByCreatedAtBetween(start, end);
    }

    // filter this year's
    public List<Documents> findThisYear() {

        int year = LocalDate.now().getYear();

        LocalDateTime start = LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime end = LocalDate.of(year + 1, 1, 1).atStartOfDay();

        return documentRepository.findByCreatedAtBetween(start, end);
    }

    // Get documents by filter
    public List<Documents> getDocumentsByFilter(String filter) {

        return switch (filter.toLowerCase()) {

            case "today" -> findToday();

            case "month" -> {
                yield findThisMonth();
            }

            case "year" -> {
                yield findThisYear();
            }

            default -> findAll();
        };
    }

    // Saving Documents with file upload
    public void save(MultipartFile file, Documents doc, CustomUserDetails auth) throws IOException {
        Documents docu = new Documents();
        docu.setName(doc.getName());
        docu.setSender(doc.getSender());
        docu.setRecipient(doc.getRecipient());
        docu.setFileNumber(doc.getFileNumber());
        docu.setFileSize(file.getSize());
        docu.setContentType(file.getContentType());
        docu.setDescription(doc.getDescription());
        docu.setDote(doc.getDote());
        docu.setDosa(doc.getDosa());
        docu.setDoca(doc.getDoca());

        if (file != null && !file.isEmpty()) {
            docu.setFileName(uploadFile(file));
        }

        docu.setCreatedBy(auth.getEmail());
        documentRepository.save(docu);
    }

    // Update Document
    public void update(MultipartFile file, Documents doc, CustomUserDetails auth, Long id) throws IOException {

        Documents docu = findById(id);
        docu.setName(doc.getName());
        docu.setSender(doc.getSender());
        docu.setRecipient(doc.getRecipient());
        docu.setFileNumber(doc.getFileNumber());
        docu.setDescription(doc.getDescription());
        docu.setDote(doc.getDote());
        docu.setDosa(doc.getDosa());
        docu.setDoca(doc.getDoca());

        if (file != null && !file.isEmpty()) {
            docu.setFileName(uploadFile(file));

            docu.setContentType(file.getContentType());
            docu.setFileSize(file.getSize());
        }

        docu.setCreatedBy(auth.getEmail());
        documentRepository.save(docu);
    }

    public void toggleActive(Long id) {
        Documents existing = findById(id);
        existing.setActive(!Boolean.TRUE.equals(existing.isActive()));
        save(existing);
    }

    public Documents findByName(String name) {
        return documentRepository.findByName(name).orElse(null);
    }

    public Documents findBySender(String sender) {
        return documentRepository.findBySender(sender);
    }

    public Documents findByReference(String reference) {
        return documentRepository.findByReferenceNumber(reference);
    }

    public List<Documents> findByNameContaining(String keyword) {
        return documentRepository.findAll().stream()
                .filter(entry -> entry.getName() != null && entry.getName().contains(keyword))
                .toList();
    }

    public List<Documents> findByNameAndCreatedBy(String action, String performedBy) {
        return documentRepository.findAll().stream()
                .filter(entry -> entry.getName().equals(action) && entry.getCreatedBy().equals(performedBy))
                .toList();
    }

    /// uploads2
    @Transactional
    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file.");
        }
        Set<String> ALLOWED_DOCUMENT_TYPES = Set.of(
                "application/pdf",

                // Images
                "image/jpeg",
                "image/png",
                "image/webp"

        );
        if (file.getContentType() == null ||
                !ALLOWED_DOCUMENT_TYPES.contains(file.getContentType())) {

            throw new IllegalArgumentException(
                    "Unsupported document type.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "File size must not exceed 10 MB.");
        }

        // Create profile directory
        Path profileDirectory = Paths.get(uploadDir, "documents");
        Files.createDirectories(profileDirectory);

        // Get extension
        String originalName = file.getOriginalFilename();

        String extension = "";

        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(
                    originalName.lastIndexOf(".")).toLowerCase();
        }

        // Generate safe unique filename
        String filename = UUID.randomUUID() + extension;
        Path destination = profileDirectory.resolve(filename)
                .normalize();

        // Make sure destination remains inside upload directory
        if (!destination.startsWith(profileDirectory.normalize())) {
            throw new IOException("Invalid file path.");
        }

        Files.copy(file.getInputStream(),
                destination,
                StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }

    // Delete Document
    @Transactional
    public void deleteDocument(Long id, RedirectAttributes red) throws IOException {
        Documents existing = findById(id);
        if (existing == null) {
            red.addFlashAttribute("error", "Document not found.");
            return;
        }

        try {
            String doc = existing.getFileName();
            Path filePath = Paths.get(uploadDir, "documents", doc)
                    .toAbsolutePath()
                    .normalize();

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            } else {
                red.addFlashAttribute("error", "File does not exist: " + filePath);
            }

            delete(id);
            red.addFlashAttribute("message", "Document deleted successfully");
        } catch (Exception e) {
            red.addFlashAttribute("error", "Failed to delete document: " + e.getMessage());
        }

    }

    public Resource loadFile(Documents document) {
        if (document == null ||
                document.getFileName() == null ||
                document.getFileName().isBlank()) {

            throw new IllegalArgumentException(
                    "Document file is missing");
        }

        try {
            Path documentDirectory = Paths
                    .get(uploadDir, "documents")
                    .toAbsolutePath()
                    .normalize();

            Path filePath = documentDirectory
                    .resolve(document.getFileName())
                    .normalize();

            if (!filePath.startsWith(documentDirectory)) {
                throw new IllegalArgumentException(
                        "Invalid document path");
            }

            Resource resource = new UrlResource(
                    filePath.toUri());

            if (!resource.exists()) {
                throw new RuntimeException(
                        "Document file not found");
            }

            if (!resource.isReadable()) {
                throw new RuntimeException(
                        "Document file is not readable");
            }

            return resource;

        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not load document", e);
        }
    }

    // PDF Export document by filter
    public ResponseEntity<byte[]> exportDocuments(CustomUserDetails auth, String filter, String format) {
        ResponseEntity<byte[]> res = null;
        List<Documents> documents = this.getDocumentsByFilter(filter);
        switch (format) {
            case "pdf":
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    Document pdfDocument = new Document(PageSize.A4.rotate(), 36, 36, 70, 50);
                    PdfWriter.getInstance(pdfDocument, outputStream);
                    // Custom header/footer
                    PdfWriter writer = PdfWriter.getInstance(pdfDocument, outputStream);
                    writer.setPageEvent(new DocumentHeaderFooter(filter));
                    pdfDocument.open();

                    // =========================
                    // Title
                    // =========================
                    Font titleFont = FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            16);

                    Paragraph title = new Paragraph(
                            "DOCUMENT RECORDS REPORT",
                            titleFont);

                    title.setAlignment(Element.ALIGN_CENTER);
                    title.setSpacingBefore(10);
                    title.setSpacingAfter(8);
                    pdfDocument.add(title);

                    // =========================
                    // Filter
                    // =========================
                    Font filterFont = FontFactory.getFont(
                            FontFactory.HELVETICA,
                            9);

                    Paragraph filterText = new Paragraph(
                            "Filter: " + formatFilter(filter),
                            filterFont);

                    filterText.setAlignment(Element.ALIGN_CENTER);
                    pdfDocument.add(filterText);

                    pdfDocument.add(
                            new Paragraph(" "));

                    // =========================
                    // Table
                    // =========================
                    PdfPTable table = new PdfPTable(6);
                    table.setWidthPercentage(100);
                    table.setWidths(new float[] {
                            5f,
                            20f,
                            20f,
                            20f,
                            15f,
                            20f
                    });

                    addHeader(table, "#");
                    addHeader(table, "Document Name");
                    addHeader(table, "Type");
                    addHeader(table, "Classification");
                    addHeader(table, "Status");
                    addHeader(table, "Received Date");

                    int count = 1;

                    for (Documents document : documents) {

                        table.addCell(String.valueOf(count++));

                        table.addCell(
                                safe(document.getName()));

                        table.addCell(
                                document.getDote() != null
                                        ? safe(document.getDote().getName())
                                        : "");

                        table.addCell(
                                document.getDoca() != null
                                        ? safe(document.getDoca().getName())
                                        : "");

                        table.addCell(
                                document.getDosa() != null
                                        ? safe(document.getDosa().getName())
                                        : "");

                        table.addCell(
                                document.getCreatedAt() != null
                                        ? document.getCreatedAt().toString()
                                        : "");
                    }

                    pdfDocument.add(table);

                    // =========================
                    // Footer
                    // =========================
                    // ============================
                    // TOTAL RECORDS
                    // ============================
                    Paragraph total = new Paragraph(
                            "Total Records: " + documents.size(),
                            FontFactory.getFont(
                                    FontFactory.HELVETICA_BOLD,
                                    10));

                    total.setAlignment(Element.ALIGN_RIGHT);
                    total.setSpacingBefore(8);
                    pdfDocument.add(total);

                    // ============================
                    // SIGNATURE SECTION
                    // ============================
                    pdfDocument.add(new Paragraph(" "));

                    PdfPTable signatureTable = new PdfPTable(2);

                    signatureTable.setWidthPercentage(100);

                    signatureTable.setWidths(
                            new float[] { 50f, 50f });

                    // Prepared By
                    PdfPCell prepared = new PdfPCell();
                    prepared.setBorder(PdfPCell.NO_BORDER);
                    prepared.addElement(
                            new Paragraph(
                                    "Prepared By:",
                                    FontFactory.getFont(
                                            FontFactory.HELVETICA_BOLD,
                                            10)));

                    prepared.addElement(
                            new Paragraph(
                                    "\n\nName: ______________________________"));

                    prepared.addElement(
                            new Paragraph(
                                    "Signature: __________________________"));

                    prepared.addElement(
                            new Paragraph(
                                    "Date: _______________________________"));

                    signatureTable.addCell(prepared);

                    // Approved By
                    PdfPCell approved = new PdfPCell();
                    approved.setBorder(PdfPCell.NO_BORDER);

                    approved.addElement(
                            new Paragraph(
                                    "Approved By:",
                                    FontFactory.getFont(
                                            FontFactory.HELVETICA_BOLD,
                                            10)));

                    approved.addElement(
                            new Paragraph(
                                    "\n\nName: ______________________________"));

                    approved.addElement(
                            new Paragraph(
                                    "Signature: __________________________"));

                    approved.addElement(
                            new Paragraph(
                                    "Date: _______________________________"));

                    signatureTable.addCell(approved);
                    pdfDocument.add(signatureTable);
                    pdfDocument.close();
                    byte[] pdfBytes = outputStream.toByteArray();

                    // =========================
                    // Response
                    // =========================
                    auditService.record("EXPORT_DOCUMENT", "SUCCESS",
                            auth.getEmail() + " Exported documents  filtered = " + filter);
                    res = ResponseEntity.ok()
                            .header(
                                    HttpHeaders.CONTENT_DISPOSITION,
                                    "attachment; filename=\"mcfs-document-records.pdf\"")
                            .contentType(MediaType.APPLICATION_PDF)
                            .body(pdfBytes);

                } catch (Exception e) {

                    auditService.record("EXPORT_DOCUMENT", "FAIL",
                            auth.getEmail() + " Failed export documents filtered = " + filter);
                    res = ResponseEntity.internalServerError()
                            .build();
                }
                break;

            case "excel":

                break;

            case "word":

                break;

            default:
                break;
        }
        return res;
    }

    // Helpers
    private void addHeader(
            PdfPTable table,
            String text) {

        Font font = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                10);

        PdfPCell cell = new PdfPCell(
                new Phrase(text, font));

        cell.setHorizontalAlignment(
                Element.ALIGN_CENTER);

        cell.setPadding(6);

        table.addCell(cell);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String formatFilter(String filter) {

        return switch (filter.toLowerCase()) {

            case "today" -> "Today";

            case "month" -> "This Month";

            case "year" -> "This Year";

            default -> "All Entries";
        };
    }
}
