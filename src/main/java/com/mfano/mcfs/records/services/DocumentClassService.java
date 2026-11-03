package com.mfano.mcfs.records.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mfano.mcfs.records.models.DocumentClass;
import com.mfano.mcfs.records.repositories.DocumentClassRepository;

@Service
public class DocumentClassService {
    private final DocumentClassRepository documentClassRepository;

    public DocumentClassService(DocumentClassRepository documentClassRepository) {
        this.documentClassRepository = documentClassRepository;
    }

    // Get All documents
    public List<DocumentClass> findAll() {
        return documentClassRepository.findAll();
    }

    // Get Role By Id
    public DocumentClass findById(Long id) {
        return documentClassRepository.findById(id).orElse(null);
    }

    // Delete Role
    public void delete(Long id) {
        documentClassRepository.deleteById(id);
    }

    // Update Role
    public void save(DocumentClass dc) {
        documentClassRepository.save(dc);
    }

    public DocumentClass findByName(String name) {
        return documentClassRepository.findByName(name).orElse(null);
    }

    public void toggleActive(Long id) {
        DocumentClass existing = findById(id);
        existing.setActive(!Boolean.TRUE.equals(existing.isActive()));
        save(existing);
    }

    public void update(Long id, DocumentClass doca) {
        DocumentClass existing = findById(id);

        existing.setName(doca.getName());
        existing.setDescription(doca.getDescription());

        save(existing);
    }
}
