package com.mfano.mcfs.utils.documents.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mfano.mcfs.utils.documents.models.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
     Optional<Document> findByName(String name);
     Document findBySender(String sender);
}