package com.mfano.mcfs.auth.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mcfs.auth.models.Branch;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    Branch findByName(String name);
}
