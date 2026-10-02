package com.mfano.mcfs.utils.documents.services;

import java.time.LocalDateTime;
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
    private final DocumentStatusService statusService;
    private final RoleService roleService;
    private boolean active = true; // Default value for active

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
        DocumentTask existingTask = findByReferenceAndActive(task.getReference());
        if (existingTask == null) {
            taskRepository.save(task);
        } else {
            existingTask.setActive(false);
            existingTask.setUpdatedBy(auth.getEmail());
            existingTask.setDosa(statusService.findByName("COMPLETED"));
            existingTask.setUpdatedAt(LocalDateTime.now());
            taskRepository.save(existingTask);

            taskRepository.save(task);
        }

    }

    public DocumentTask findByRecipient(Set<Role> recipient) {
        return taskRepository.findByRecipientIn(recipient).orElse(null);
    }

    public DocumentTask findBySender(String sender) {
        return taskRepository.findByCreatedBy(sender);
    }

    public DocumentTask findByReference(String reference) {
        return taskRepository.findByReference(reference).orElse(null);
    }

    public DocumentTask findPended() {
        return taskRepository.findByDosa(statusService.findByName("PENDING")).orElse(null);
    }

    public DocumentTask findCompleted() {
        return taskRepository.findByDosa(statusService.findByName("COMPLETED")).orElse(null);
    }

    public DocumentTask findArchived() {
        return taskRepository.findByDosa(statusService.findByName("ARCHIVED")).orElse(null);
    }

    public List<DocumentTask> findProgress() {
        List<DocumentStatus> statuses = List.of(
                statusService.findByName("REVIEWED"),
                statusService.findByName("FORWARDED"));

        return taskRepository
                .findCountByDosaIn(statuses);
    }

    public List<DocumentTask> findPending(CustomUserDetails auth) {

        List<DocumentStatus> statuses = List.of(
                statusService.findByName("PENDING"),
                statusService.findByName("RECEIVED"),
                statusService.findByName("RETRIEVED"),
                statusService.findByName("RETURNED"));

        return taskRepository
                .findCountByDosaIn(statuses);

    }

    public List<DocumentTask> findComplete(CustomUserDetails auth) {

        List<DocumentStatus> statuses = List.of(
                statusService.findByName("CLOSED"),
                statusService.findByName("COMPLETED"),
                statusService.findByName("DISPOSED"),
                statusService.findByName("REJECTED"),
                statusService.findByName("ARCHIVED"),
                statusService.findByName("APPROVED"));

        return taskRepository
                .findCountByDosaIn(statuses);

    }

    public DocumentTask findByReferenceAndActive(String reference) {
        this.active = true; // Ensure that only active tasks are considered
        return taskRepository.findAll().stream()
                .filter(entry -> entry.getReference().equals(reference) && entry.isActive() == active)
                .findFirst()
                .orElse(null);
    }
}
