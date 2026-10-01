package com.mfano.mcfs.records;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Element;

import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mcfs.auth.services.AuditService;
import com.mfano.mcfs.auth.services.BranchService;
import com.mfano.mcfs.auth.services.ProfileService;
import com.mfano.mcfs.auth.services.RoleService;
import com.mfano.mcfs.auth.services.UserService;
import com.mfano.mcfs.config.CustomUserDetails;
import com.mfano.mcfs.dtos.UserDto;
import com.mfano.mcfs.utils.documents.services.DocumentClassService;
import com.mfano.mcfs.utils.documents.services.DocumentStatusService;
import com.mfano.mcfs.utils.documents.services.DocumentTypeService;
import com.mfano.mcfs.utils.documents.services.DocumentService;
import com.mfano.mcfs.utils.documents.models.Documents;
import com.mfano.mcfs.utils.documents.models.DocumentClass;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/records")
@PreAuthorize("hasAuthority('RMO')")
@RequiredArgsConstructor
public class RecordsController {
    private final ProfileService profileService;
    private final AuditService auditService;

    private final RoleService roleService;
    private final BranchService storeService;
    private final UserService userService;

    private final DocumentService documentService;
    private final DocumentTypeService typeService;
    private final DocumentClassService classService;
    private final DocumentStatusService statusService;

    // manage /GET/* module
    @GetMapping("/{option}")
    public String getAll(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable String option, Model red,
            @RequestParam(required = false, defaultValue = "all") String filter) {
        red.addAttribute("profile", profileService.checkProfile(auth.getId()));
        red.addAttribute("user", userService.findById(auth.getId()));

        String dir = "redirect";
        switch (option) {
            case "dashboard":
                red.addAttribute("users", userService.findAll());
                red.addAttribute("stores", storeService.findAll());
                red.addAttribute("roles", roleService.findAll());
                red.addAttribute("audits", auditService.findAll());
                dir = "admin/index";
                break;

            case "users":
                red.addAttribute("userDto", new UserDto());
                Long storeId = auth.getBranch().getId();

                if (storeId != null) {
                    // red.addAttribute("users", userService.findByBranch_Id(storeId));
                    red.addAttribute("users", userService.findAll());

                } else {
                    red.addAttribute("users", userService.findAll());
                }
                red.addAttribute("stores", storeService.findAll());
                red.addAttribute("roles", roleService.findAll());
                dir = "admin/users";
                break;

            case "branches":
                red.addAttribute("users", userService.findAll());
                red.addAttribute("branches", storeService.findAll());
                dir = "admin/branches";
                break;

            case "documents":

                List<Documents> documents;
                switch (filter) {
                    case "today" -> documents = documentService.findToday();
                    case "month" -> documents = documentService.findThisMonth();
                    case "year" -> documents = documentService.findThisYear();
                    default -> documents = documentService.findAll();
                }

                red.addAttribute("filter", filter);
                red.addAttribute("documents", documents);
                red.addAttribute("types", typeService.findAll());
                red.addAttribute("statuses", statusService.findAll());
                red.addAttribute("classes", classService.findAll());
                red.addAttribute("branches", storeService.findAll());
                red.addAttribute("stores", storeService.findAll());
                dir = "records/documents";
                break;

            case "add-document":
                red.addAttribute("types", typeService.findAll());
                dir = "admin/types";
                break;

            case "classes":
                red.addAttribute("classes", classService.findAll());
                dir = "admin/classes";
                break;

            case "statuses":
                red.addAttribute("statuses", statusService.findAll());
                dir = "admin/statuses";
                break;
        }

        return dir;
    }

    // Document saving
    @PostMapping("/documents/save")
    public String saveDocument(@AuthenticationPrincipal CustomUserDetails auth,
            @RequestParam("file") MultipartFile file, @ModelAttribute Documents doc, RedirectAttributes red) {
        try {
            documentService.save(file, doc, auth);
            auditService.record("CREATE_DOCUMENT", "SUCCESS", auth.getEmail() + " Recorded document: " + doc.getName());
            red.addFlashAttribute("message", "Document recorded successfully");
        } catch (Exception e) {
            auditService.record("CREATE_DOCUMENT", "FAIL",
                    auth.getEmail() + " Fail to record document: " + doc.getName());
            red.addAttribute("error", e.getMessage());
        }
        return "redirect:/records/documents";
    }

    // Document updating
    @PostMapping("/documents/update/{id}")
    public String updateDocument(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable Long id,
            @RequestParam("file") MultipartFile file, @ModelAttribute Documents doc, RedirectAttributes red) {

        if (doc == null) {
            red.addFlashAttribute("error", "Document not found.");
            return "redirect:/records/documents";
        }
        try {
            documentService.update(file, doc, auth, id);
            auditService.record("UPDATE_DOCUMENT", "SUCCESS", auth.getEmail() + " Updated document: " + doc.getName());
            red.addFlashAttribute("message", "Document updated successfully");
        } catch (Exception e) {
            auditService.record("UPDATE_DOCUMENT", "FAIL",
                    auth.getEmail() + " Failed to update document: " + doc.getName());
            red.addAttribute("error", e.getMessage());
        }
        return "redirect:/records/documents/edit/" + id;
    }

    // Delete document
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/documents/{option}/{id}")
    public String imageDelete(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable String option,
            @PathVariable Long id, RedirectAttributes red) {

        Documents doc = documentService.findById(id);
        red.addAttribute("profile", profileService.checkProfile(auth.getId()));
        switch (option) {

            // delete document
            case "delete":
                try {
                    documentService.deleteDocument(id, red);
                    auditService.record("DELETE_DOCUMENT", "SUCCESS",
                            auth.getEmail() + " Deleted document id = " + doc.getName());
                } catch (IOException e) {
                    auditService.record("DELETE_DOCUMENT", "FAIL",
                            auth.getEmail() + " Failed teleted document id = " + doc.getName());
                }
                break;

            case "toggle":
                try {
                    documentService.toggleActive(id);
                    auditService.record("TOGGLE_Document", "SUCCESS",
                            auth.getEmail() + " Toggled document " + doc.getName());
                    red.addFlashAttribute("message", "Document toggled successfully.");

                } catch (Exception e) {
                    auditService.record("TOGGLE_DOCUMENT", "FAIL",
                            auth.getEmail() + " Fail to toggle document " + doc.getName());
                    red.addFlashAttribute("error", "Sorry! Failed to toggle document");
                }
                break;
        }
        return "redirect:/records/documents";
    }

    // View document by reference
    @GetMapping("/documents/{id}")
    public ResponseEntity<Resource> openDocument(@PathVariable Long id) {

        Documents document = documentService.findById(id);
        if (document == null) {
            return ResponseEntity.notFound().build();
        }

        String contentType = document.getContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }

        Resource resource = documentService.loadFile(document);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + document.getFileName() + "\"")
                .body(resource);
    }

    // Edit document by reference
    @GetMapping("/documents/edit/{id}")
    public String editDocument(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable Long id,
            Model model, RedirectAttributes red) {

        model.addAttribute("profile", profileService.checkProfile(auth.getId()));
        model.addAttribute("user", userService.findById(auth.getId()));
        Documents document = documentService.findById(id);
        if (document == null) {
            red.addFlashAttribute("error", "Document not found.");
            return "redirect:/records/documents";
        }

        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("types", typeService.findAll());
        model.addAttribute("classes", classService.findAll());
        model.addAttribute("statuses", statusService.findAll());
        model.addAttribute("document", document);
        return "records/document-edit";
    }

    // Export document by filter
    @GetMapping("/documents/export/{format}")
    public ResponseEntity<byte[]> exportDocument(@AuthenticationPrincipal CustomUserDetails auth,
            @RequestParam(defaultValue = "all") String filter, @PathVariable String format) {
        return documentService.exportDocuments(auth, filter, format);
    }

}
