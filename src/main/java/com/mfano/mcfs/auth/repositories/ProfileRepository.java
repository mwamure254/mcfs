package com.mfano.mcfs.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mcfs.auth.models.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long>{ 
        public Profile findByUserId(Long userId);
}
