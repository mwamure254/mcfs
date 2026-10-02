package com.mfano.mcfs.utils.documents.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mcfs.auth.models.Role;
import com.mfano.mcfs.utils.documents.models.DocumentStatus;
import com.mfano.mcfs.utils.documents.models.DocumentTask;

public interface DocumentTaskRepository extends JpaRepository<DocumentTask, Long> {
     List<DocumentTask> findByRecipientIn(Set<Role> recipient);

     List<DocumentTask> findByCreatedBy(String sender);

     List<DocumentTask> findByReference(String reference);

     List<DocumentTask> findByDosa(DocumentStatus byName);

     //List<DocumentTask> findCountByDosaIn(List<String> statuses);
     List<DocumentTask> findByDosaIn(List<String> statuses);

     List<DocumentTask> findByDosa(String dosa);

     List<DocumentTask> findByUpdatedBy(String updatedBy);
}