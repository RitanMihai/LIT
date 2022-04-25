package com.ritan.lit.social.repository;

import com.ritan.lit.social.domain.UserReaction;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the UserReaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserReactionRepository extends JpaRepository<UserReaction, Long> {}
