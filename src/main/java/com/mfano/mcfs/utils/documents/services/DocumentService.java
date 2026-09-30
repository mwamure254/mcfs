package com.mfano.mcfs.utils.documents.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mcfs.utils.documents.models.Document;
import com.mfano.mcfs.utils.documents.repositories.DocumentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentService {
    @Value("${app.upload-dir}")
    private String uploadDir;
    private final DocumentRepository documentRepository;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    // Get All documents
    public List<Document> findAll() {
        return documentRepository.findAll();
    }

    // Get Role By Id
    public Document findById(Long id) {
        return documentRepository.findById(id).orElse(null);
    }

    // Delete Role
    public void delete(Long id) {
        documentRepository.deleteById(id);
    }

    // Update Role
    public void save(Document doc) {
        documentRepository.save(doc);
    }

    // Update Role
    public void save(MultipartFile file, Document doc) throws IOException {
        Document docu = new Document();
        docu.setName(doc.getName());
        docu.setSender(doc.getSender());
        docu.setRecipient(doc.getRecipient());
        docu.setFileNumber(doc.getFileNumber());
        docu.setFileSize(file.getSize());
        docu.setContentType(file.getContentType());
        docu.setNote(doc.getNote());
        docu.setDote(doc.getDote());
        docu.setDosa(doc.getDosa());
        docu.setDoca(doc.getDoca());
        docu.setFileName(uploadFile(file));
        documentRepository.save(docu);
    }

    public void toggleActive(Long id) {
        Document existing = findById(id);
        existing.setActive(!Boolean.TRUE.equals(existing.isActive()));
        save(existing);
    }

    public Document findByName(String name) {
        return documentRepository.findByName(name).orElse(null);
    }

    public Document findBySender(String sender) {
        return documentRepository.findBySender(sender);
    }

    public Document findByReference(String reference) {
        return documentRepository.findByReferenceNumber(reference);
    }

    public List<Document> findByNameContaining(String keyword) {
        return documentRepository.findAll().stream()
                .filter(entry -> entry.getName() != null && entry.getName().contains(keyword))
                .toList();
    }

    public List<Document> findByNameAndCreatedBy(String action, String performedBy) {
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

        Files.copy(
                file.getInputStream(),
                destination,
                StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }

    // Delete Document
    @Transactional
    public void deleteDocument(Long id, RedirectAttributes red) throws IOException {
        Document existing = findById(id);
        try {
            String doc = existing.getFileName();
            Path path = Path.of(uploadDir + "/documents/" + doc);
            Files.delete(path);

            existing.setFileName(null);
            save(existing);
            red.addFlashAttribute("message", "Document deleted successfully");
        } catch (Exception e) {
            red.addFlashAttribute("error", e.getMessage());
        }

    }

    public Resource loadFile(Document document) {
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

}
