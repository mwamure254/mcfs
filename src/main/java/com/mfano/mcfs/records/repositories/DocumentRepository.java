package com.mfano.mcfs.records.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mfano.mcfs.records.models.DocumentType;
import com.mfano.mcfs.records.models.Documents;

public interface DocumentRepository extends JpaRepository<Documents, Long> {
     Optional<Documents> findByName(String name);

     Documents findBySender(String sender);

     Documents findByReferenceNumber(String reference);

     List<Documents> findByCreatedAtBetween(
               LocalDateTime start,
               LocalDateTime end);

     long countByDote(DocumentType documentType);

}