package com.mfano.mcfs.utils.documents.models;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import com.mfano.mcfs.auth.models.CommonObject;
import com.mfano.mcfs.config.CustomUserDetails;
import com.mfano.mcfs.auth.models.User;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document extends CommonObject {
    /**
     * Unique reference number assigned to the document.
     * Example: DOC/2026/00001
     */
    @Column(name = "reference_number", unique = true, nullable = false, length = 50)
    private String referenceNumber;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_type")
    private DocumentType dt;

    /**
     * Current lifecycle status.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_class")
    private DocumentClass dc;
     /**
     * Current lifecycle status.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_status")
    private DocumentStatus ds;

    /**
     * Date the document was registered in the system.
     */
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    /**
     * Date the document was completed.
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * Date the document was archived.
     */
    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    /**
     * Person/organization that sent the document.
     */
    @Column(name = "sender", length = 255)
    private String sender;

    /**
     * Person/organization receiving the document.
     */
    @Column(name = "recipient", length = 255)
    private String recipient;

    /**
     * Physical or logical file reference.
     */
    @Column(name = "file_number", length = 100)
    private String fileNumber;

    /**
     * MIME type of the uploaded document.
     */
    @Column(name = "content_type", length = 100)
    private String contentType;

    /**
     * Size of the uploaded document in bytes.
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * User who last modified the document.
     */
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (registeredAt == null) {
            registeredAt = now;
        }

        if (referenceNumber == null) {
            referenceNumber = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase();
        }
    
    }

    @PreUpdate
    protected void onUpdate() {
          Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            this.updatedBy = userDetails.getEmail();
        }
    }
}