package com.mfano.mcfs.auth.services;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.mfano.mcfs.auth.models.Role;
import com.mfano.mcfs.auth.models.User;
import com.mfano.mcfs.auth.repositories.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    // Get All Roles
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    // Get Role By Id
    public Role findById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    // Delete Role
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

    // Update Role
    public void save(Role role) {
        roleRepository.save(role);
    }

    public void update(Long id, Role role){
        Role existing = findById(id);

        existing.setName(role.getName());
        existing.setDescription(role.getDescription());        

        save(existing);
    }

    public void toggleActive(Long id) {
        Role existing = findById(id);
        existing.setActive(!Boolean.TRUE.equals(existing.isActive()));
        save(existing);
    }

    public List<Role> getUserNotRoles(User user) {
        return roleRepository.getUserNotRoles(user.getId());
    }

    public Set<Role> getUserRoles(User user) {
        return user.getRoles();
    }
    public Role findByName(String name) {
        return roleRepository.findByName(name).orElse(null);
    }
}
