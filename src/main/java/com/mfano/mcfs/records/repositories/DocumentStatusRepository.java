package com.mfano.mcfs.records.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mcfs.records.models.DocumentStatus;

public interface DocumentStatusRepository extends JpaRepository<DocumentStatus, Long> {
     Optional<DocumentStatus> findByName(String name);

}
