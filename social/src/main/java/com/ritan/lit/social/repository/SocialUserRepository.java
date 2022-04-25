package com.ritan.lit.social.repository;

import com.ritan.lit.social.domain.SocialUser;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the SocialUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SocialUserRepository extends JpaRepository<SocialUser, Long> {}
