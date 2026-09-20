package com.mfano.mcfs.auth.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mfano.mcfs.auth.models.AuditEntry;
import com.mfano.mcfs.auth.repositories.AuditRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditService {
     private final AuditRepository repo;

    public void record(String action, String details) {
        AuditEntry entry = new AuditEntry();
        entry.setAction(action);
        entry.setDetails(details);
        repo.save(entry);
    }

    public List<AuditEntry> findAll() {
        return repo.findAll();
    }

    public List<AuditEntry> findByAction(String action) {
        return repo.findByAction(action);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    public void deleteAll() {
        repo.deleteAll();
    }

    public AuditEntry findById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public void updateAuditEntry(Long id, AuditEntry updatedEntry) {
        AuditEntry existingEntry = repo.findById(id).orElse(null);
        if (existingEntry != null) {
            existingEntry.setAction(updatedEntry.getAction());
            existingEntry.setDetails(updatedEntry.getDetails());
            repo.save(existingEntry);
        }
    }

    public void createAuditEntry(AuditEntry entry) {
        repo.save(entry);
    }

    public List<AuditEntry> findByCreatedBy(String performedBy) {
        return repo.findAll().stream()
                .filter(entry -> entry.getCreatedBy().equals(performedBy))
                .toList();
    }

    public List<AuditEntry> findByDetailsContaining(String keyword) {
        return repo.findAll().stream()
                .filter(entry -> entry.getDetails() != null && entry.getDetails().contains(keyword))
                .toList();
    }

    public List<AuditEntry> findByActionAndCreateddBy(String action, String performedBy) {
        return repo.findAll().stream()
                .filter(entry -> entry.getAction().equals(action) && entry.getCreatedBy().equals(performedBy))
                .toList();
    }

}
