package com.mfano.mcfs.utils.documents.services;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.mfano.mcfs.utils.documents.models.DocumentStatus;
import com.mfano.mcfs.utils.documents.repositories.DocumentStatusRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentStatusService {
    private final DocumentStatusRepository documentStatusRepository;

    // Get All documents
    public List<DocumentStatus> findAll() {
        return documentStatusRepository.findAll();
    }

    // Get Role By Id
    public DocumentStatus findById(Long id) {
        return documentStatusRepository.findById(id).orElse(null);
    }

    // Delete Role
    public void delete(Long id) {
        documentStatusRepository.deleteById(id);
    }

    // Update Role
    public void save(DocumentStatus role) {
        documentStatusRepository.save(role);
    }

    public DocumentStatus findByName(String name) {
        return documentStatusRepository.findByName(name).orElse(null);
    }

    public void toggleActive(Long id) {
        DocumentStatus existing = findById(id);
        existing.setActive(!Boolean.TRUE.equals(existing.isActive()));
        save(existing);
    }

    public void update(Long id, DocumentStatus dosa){
        DocumentStatus existing = findById(id);

        existing.setName(dosa.getName());
        existing.setDescription(dosa.getDescription());        

        save(existing);
    }
}
