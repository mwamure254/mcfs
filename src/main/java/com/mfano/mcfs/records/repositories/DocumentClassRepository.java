package com.mfano.mcfs.records.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mcfs.records.models.DocumentClass;

public interface DocumentClassRepository extends JpaRepository<DocumentClass, Long> {
     Optional<DocumentClass> findByName(String name);

}