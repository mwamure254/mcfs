package com.mfano.mcfs.utils.documents.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mcfs.auth.models.Role;
import com.mfano.mcfs.utils.documents.models.DocumentStatus;
import com.mfano.mcfs.utils.documents.models.DocumentTask;

public interface DocumentTaskRepository extends JpaRepository<DocumentTask, Long> {
     Optional<DocumentTask> findByRecipientIn(Set<Role> recipient);

     DocumentTask findByCreatedBy(String sender);

     Optional<DocumentTask> findByReference(String reference);

     Optional<DocumentTask> findByDosa(DocumentStatus byName);

     List<DocumentTask> findCountByDosaIn(
               List<DocumentStatus> statuses);
}