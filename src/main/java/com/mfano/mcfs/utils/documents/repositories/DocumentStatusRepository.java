package com.mfano.mcfs.utils.documents.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mfano.mcfs.utils.documents.models.DocumentStatus;

public interface DocumentStatusRepository extends JpaRepository<DocumentStatus, Long> {
     Optional<DocumentStatus> findByName(String name);

}
