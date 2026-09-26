package com.mfano.mcfs.auth.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mcfs.auth.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    User findByEmailAndPassword(String email, String password);

    User findByBranch_Id(Long storeId);

}
