package com.mfano.mcfs.utils.documents.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mcfs.utils.documents.models.DocumentClass;

public interface DocumentClassRepository extends JpaRepository<DocumentClass, Long> {
     Optional<DocumentClass> findByName(String name);

}