package com.mfano.mcfs.utils.documents.services;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.mfano.mcfs.utils.documents.models.DocumentType;
import com.mfano.mcfs.utils.documents.repositories.DocumentTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentTypeService {
    private final DocumentTypeRepository documentTypeRepository;

    // Get All documents
    public List<DocumentType> findAll() {
        return documentTypeRepository.findAll();
    }

    // Get Role By Id
    public DocumentType findById(Long id) {
        return documentTypeRepository.findById(id).orElse(null);
    }

    // Delete Role
    public void delete(Long id) {
        documentTypeRepository.deleteById(id);
    }

    // Update Role
    public void save(DocumentType dt) {
        documentTypeRepository.save(dt);
    }

    public DocumentType findByName(String name) {
        return documentTypeRepository.findByName(name).orElse(null);
    }

    public void toggleActive(Long id) {
        DocumentType existing = findById(id);
        existing.setActive(!Boolean.TRUE.equals(existing.isActive()));
        save(existing);
    }


    public void update(Long id, DocumentType dote){
        DocumentType existing = findById(id);

        existing.setName(dote.getName());
        existing.setDescription(dote.getDescription());        

        save(existing);
    }
}
