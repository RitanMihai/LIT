package com.ritan.lit.social.repository;

import com.ritan.lit.social.domain.UserReaction;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the UserReaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserReactionRepository extends JpaRepository<UserReaction, Long> {
    Optional<UserReaction> findByPostIdAndSocialUserUser(Long postId, String username);
    Optional<List<UserReaction>> findAllBySocialUserUser(String user);
}
