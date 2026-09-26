package com.mfano.mcfs.auth.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mcfs.auth.models.Branch;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByName(String name);
}
