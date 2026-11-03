package com.mfano.mcfs.records.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mcfs.records.models.DocumentType;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {
     Optional<DocumentType> findByName(String name);

}