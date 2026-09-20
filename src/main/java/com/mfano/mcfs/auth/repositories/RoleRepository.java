package com.mfano.mcfs.auth.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mfano.mcfs.auth.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

     Optional<Role> findByName(String name);

    @Query(value = "SELECT * FROM role WHERE id NOT IN (SELECT role_id FROM user_roles WHERE user_id = ?1)", nativeQuery = true)
    List<Role> getUserNotRoles(Long userId);

    @Query(value = "SELECT * FROM role WHERE id IN (SELECT role_id FROM user_roles WHERE user_id = ?1)", nativeQuery = true)
    List<Role> getUserRoles(Long userId);
}
