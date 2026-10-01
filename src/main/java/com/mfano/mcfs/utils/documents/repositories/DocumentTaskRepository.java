package com.mfano.mcfs.utils.documents.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mcfs.auth.models.Role;
import com.mfano.mcfs.utils.documents.models.DocumentTask;

public interface DocumentTaskRepository extends JpaRepository<DocumentTask, Long> {
     Optional<DocumentTask> findByRecipient(Role recipient);
     DocumentTask findByCreatedBy(String sender);
}