package com.mfano.mcfs.auth.services;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import com.mfano.mcfs.auth.models.Branch;
import com.mfano.mcfs.auth.repositories.BranchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchService {
    private final BranchRepository branchRepository;

    public void createBranch(Branch branch) {
        branchRepository.save(branch);
    }

    public Branch getBranchById(Long id) {
        return branchRepository.findById(id).orElse(null);
    }

    public void deleteBranch(Long id) {
        branchRepository.deleteById(id);
    }

    public void updateBranch(Long id, Branch updatedBranch) {
        Branch existingBranch = branchRepository.findById(id).orElse(null);
        if (existingBranch != null) {
            existingBranch.setName(updatedBranch.getName());
            existingBranch.setLocation(updatedBranch.getLocation());
            branchRepository.save(existingBranch);
        }
    }

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    public List<Branch> findAll() {
        return branchRepository.findAll();
    }

    public Branch findById(Long id) {
        return branchRepository.findById(id).orElse(null);
    }

    public Branch findByName(String name) {
        return branchRepository.findByName(name).orElse(null);
    }
}
