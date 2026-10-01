package com.mfano.mcfs.utils.documents.services;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.mfano.mcfs.auth.models.Role;
import com.mfano.mcfs.utils.documents.models.DocumentTask;
import com.mfano.mcfs.utils.documents.repositories.DocumentTaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentTaskService {
    private final DocumentTaskRepository taskRepository;

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
    public void save(DocumentTask task) {
        taskRepository.save(task);
    }

    public DocumentTask findByRecipient(Role recipient) {
        return taskRepository.findByRecipient(recipient).orElse(null);
    }

    public DocumentTask findBySender(String sender) {
        return taskRepository.findByCreatedBy(sender);
    }
}
