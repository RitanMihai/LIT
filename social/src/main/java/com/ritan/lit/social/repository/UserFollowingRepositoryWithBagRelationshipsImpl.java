package com.ritan.lit.social.repository;

import com.ritan.lit.social.domain.UserFollowing;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.hibernate.annotations.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class UserFollowingRepositoryWithBagRelationshipsImpl implements UserFollowingRepositoryWithBagRelationships {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Optional<UserFollowing> fetchBagRelationships(Optional<UserFollowing> userFollowing) {
        return userFollowing.map(this::fetchSocialUsers);
    }

    @Override
    public Page<UserFollowing> fetchBagRelationships(Page<UserFollowing> userFollowings) {
        return new PageImpl<>(
            fetchBagRelationships(userFollowings.getContent()),
            userFollowings.getPageable(),
            userFollowings.getTotalElements()
        );
    }

    @Override
    public List<UserFollowing> fetchBagRelationships(List<UserFollowing> userFollowings) {
        return Optional.of(userFollowings).map(this::fetchSocialUsers).get();
    }

    UserFollowing fetchSocialUsers(UserFollowing result) {
        return entityManager
            .createQuery(
                "select userFollowing from UserFollowing userFollowing left join fetch userFollowing.socialUsers where userFollowing is :userFollowing",
                UserFollowing.class
            )
            .setParameter("userFollowing", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<UserFollowing> fetchSocialUsers(List<UserFollowing> userFollowings) {
        return entityManager
            .createQuery(
                "select distinct userFollowing from UserFollowing userFollowing left join fetch userFollowing.socialUsers where userFollowing in :userFollowings",
                UserFollowing.class
            )
            .setParameter("userFollowings", userFollowings)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
