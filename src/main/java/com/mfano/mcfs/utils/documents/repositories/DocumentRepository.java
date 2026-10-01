package com.mfano.mcfs.utils.documents.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mcfs.utils.documents.models.Documents;

public interface DocumentRepository extends JpaRepository<Documents, Long> {
     Optional<Documents> findByName(String name);

     Documents findBySender(String sender);

     Documents findByReferenceNumber(String reference);

     List<Documents> findByCreatedAtBetween(
               LocalDateTime start,
               LocalDateTime end);
}