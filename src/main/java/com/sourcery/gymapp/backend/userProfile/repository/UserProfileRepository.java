package com.sourcery.gymapp.backend.userProfile.repository;


import com.sourcery.gymapp.backend.userProfile.model.UserProfile;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    UserProfile findUserProfileByUserId(UUID userId);
}