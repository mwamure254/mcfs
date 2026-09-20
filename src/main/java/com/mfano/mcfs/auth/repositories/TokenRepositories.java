package com.mfano.mcfs.auth.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mcfs.auth.models.User;
import com.mfano.mcfs.auth.models.VerificationToken;
import java.util.List;

public interface TokenRepositories extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    void deleteByUserId(Long id);

    void deleteByToken(String token);

    void deleteByUserIdAndToken(Long userId, String token);

    void deleteByUserIdAndTokenAndExpiryDateLessThan(Long userId, String token, java.util.Date date);

    List<VerificationToken> findByUser(User user);
}
