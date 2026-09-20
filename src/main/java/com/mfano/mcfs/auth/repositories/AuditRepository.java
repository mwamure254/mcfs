package com.mfano.mcfs.auth.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mcfs.auth.models.AuditEntry;

public interface AuditRepository extends JpaRepository<AuditEntry, Long> {

    List<AuditEntry> findByAction(String action);
    
}
