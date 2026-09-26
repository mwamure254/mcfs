package com.mfano.mcfs.utils.documents.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mfano.mcfs.utils.documents.models.DocumentType;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {
     Optional<DocumentType> findByName(String name);

}