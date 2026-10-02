package com.mfano.mcfs.utils.documents.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.mfano.mcfs.auth.models.Role;
import com.mfano.mcfs.auth.services.RoleService;
import com.mfano.mcfs.config.CustomUserDetails;
import com.mfano.mcfs.utils.documents.models.DocumentStatus;
import com.mfano.mcfs.utils.documents.models.DocumentTask;
import com.mfano.mcfs.utils.documents.models.Documents;
import com.mfano.mcfs.utils.documents.repositories.DocumentTaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentTaskService {
    private final DocumentTaskRepository taskRepository;
    private final DocumentService documentService;

    // Get All documents
    public List<DocumentTask> findAll() {
        return taskRepository.findAll();
    }

    // Get Role By Id
    public DocumentTask findById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    // Delete Role
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    // Update Role
    public void save(DocumentTask task, CustomUserDetails auth) {
        List<DocumentTask> existingTask = findByReference(task.getReference());
        if (existingTask == null) {
            taskRepository.save(task);
        } else {
            for (DocumentTask documentTask : existingTask) {
                Documents doc = documentService.findByReference(task.getReference());
                if (task.getDosa() == "COMPLETED") {
                    doc.setCompletedAt(LocalDateTime.now());
                    documentService.save(doc);
                }
                if (task.getDosa() == "ARCHIVED") {
                    doc.setArchivedAt(LocalDateTime.now());
                    documentService.save(doc);
                }
                documentTask.setActive(false);
                documentTask.setUpdatedBy(auth.getEmail());
                documentTask.setDosa(task.getDosa());
                documentTask.setUpdatedAt(LocalDateTime.now());
                taskRepository.save(documentTask);
            }

            taskRepository.save(task);
        }

    }

    public List<DocumentTask> findByRecipient(Set<Role> recipient) {
        return taskRepository.findByRecipientIn(recipient);
    }

    public List<DocumentTask> findByDosa(String dosa) {
        return taskRepository.findByDosa(dosa);
    }

    public List<DocumentTask> findByUpdatedBy(String updatedBy) {
        return taskRepository.findByUpdatedBy(updatedBy);
    }

    public List<DocumentTask> findBySender(String sender) {
        return taskRepository.findByCreatedBy(sender);
    }

    public List<DocumentTask> findByReference(String reference) {
        return taskRepository.findByReference(reference);
    }

    public List<DocumentTask> findPended() {
        return taskRepository.findByDosa("PENDING");
    }

    public List<DocumentTask> findCompleted() {
        return taskRepository.findByDosa("COMPLETED");
    }

    public List<DocumentTask> findArchived() {
        return taskRepository.findByDosa("ARCHIVED");
    }

    public List<DocumentTask> findProgress() {
        List<String> statuses = List.of(
                "REVIEWED",
                "FORWARDED");

        return taskRepository.findByDosaIn(statuses);
    }

    public List<DocumentTask> findPending() {
        List<String> statuses = List.of(
                "PENDING",
                "RECEIVED",
                "RETRIEVED",
                "RETURNED");

        return taskRepository.findByDosaIn(statuses);
    }

    public List<DocumentTask> findComplete() {
        List<String> statuses = List.of(
                "CLOSED",
                "COMPLETED",
                "DISPOSED",
                "REJECTED",
                "ARCHIVED",
                "APPROVED");

        return taskRepository.findByDosaIn(statuses);
    }

    public List<DocumentTask> findByReferencePending(String reference) {
        return taskRepository.findAll().stream()
                .filter(entry -> entry.getReference().equals(reference) && entry.isActive())
                .toList();
    }

    public List<DocumentTask> findByReferenceComplete(String reference) {
        return taskRepository.findAll().stream()
                .filter(entry -> entry.getReference().equals(reference) && !entry.isActive())
                .toList();
    }

    public List<DocumentTask> findByRecipientPending(Set<Role> recipient) {
        if (recipient == null) {
            return Collections.emptyList();
        }
        return findPending().stream()
                .filter(entry -> entry.getRecipient().equals(recipient) && entry.isActive())
                .toList();
    }

    public List<DocumentTask> findByRecipientPended(Set<Role> recipient) {
        if (recipient == null) {
            return Collections.emptyList();
        }
        return findAll().stream()
                .filter(entry -> entry.getRecipient().equals(recipient) && entry.isActive())
                .toList();

    }

    public List<DocumentTask> findByRecipientComplete() {
        return findComplete().stream()
                .filter(entry -> !entry.isActive())
                .toList();
    }

    public List<DocumentTask> findByRecipientCompleted(String updatedBy) {
        if (updatedBy == null || updatedBy.isBlank()) {
            return Collections.emptyList();
        }

        return findAll().stream()
                .filter(task -> task.getUpdatedBy() != null)
                .filter(task -> task.getUpdatedBy().equals(updatedBy))
                .filter(task -> !task.isActive())
                .toList();
    }
}