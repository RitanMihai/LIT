package com.ritan.lit.social.repository;

import com.ritan.lit.social.domain.Post;
import com.ritan.lit.social.domain.SocialUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Post entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllBySocialUser(SocialUser socialUser, Pageable pageable);
}
