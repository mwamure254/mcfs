package com.mfano.mcfs.utils.documents.models;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import jakarta.persistence.PrePersist;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import com.mfano.mcfs.auth.models.CommonObject;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
public class Document extends CommonObject {
    /**
     * Unique reference number assigned to the document.
     * Example: DOC/2026/00001
     */
    @Column(name = "reference", unique = true, nullable = false, length = 50)
    private String referenceNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_type")
    private DocumentType dote;

    /**
     * Current lifecycle class.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_class")
    private DocumentClass doca;
     /**
     * Current lifecycle status.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_status")
    private DocumentStatus dosa;

    /**
     * Date the document was registered in the system.
     */
    @Column(name = "registered")
    private LocalDateTime registeredAt;

    /**
     * Date the document was completed.
     */
    @Column(name = "completion")
    private LocalDateTime completedAt;

    /**
     * Date the document was archived.
     */
    @Column(name = "archived")
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
    @Column(name = "filed", length = 100)
    private String fileNumber;

    private String note;
    private String fileName;

    private Long fileSize;
    private String contentType;

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

}