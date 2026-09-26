package com.mfano.mcfs.utils.documents.services;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.mfano.mcfs.utils.documents.models.Document;
import com.mfano.mcfs.utils.documents.repositories.DocumentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;

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
    public void save(Document role) {
        documentRepository.save(role);
    }

    public Document findByName(String name) {
        return documentRepository.findByName(name).orElse(null);
    }

    public Document findBySender(String sender) {
        return documentRepository.findBySender(sender);
    }
}
