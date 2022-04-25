package com.ritan.lit.social.repository;

import com.ritan.lit.social.domain.UserFollowing;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the UserFollowing entity.
 */
@Repository
public interface UserFollowingRepository extends UserFollowingRepositoryWithBagRelationships, JpaRepository<UserFollowing, Long> {
    default Optional<UserFollowing> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<UserFollowing> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<UserFollowing> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
