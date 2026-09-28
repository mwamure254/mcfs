package com.mfano.mcfs.utils.documents.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mfano.mcfs.utils.documents.models.DocumentTask;

public interface DocumentTaskRepository extends JpaRepository<DocumentTask, Long> {
     Optional<DocumentTask> findByRecipient(String name);
     DocumentTask findBySender(String sender);
}