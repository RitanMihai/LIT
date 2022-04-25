package com.ritan.lit.social.repository;

import com.ritan.lit.social.domain.UserFollowing;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface UserFollowingRepositoryWithBagRelationships {
    Optional<UserFollowing> fetchBagRelationships(Optional<UserFollowing> userFollowing);

    List<UserFollowing> fetchBagRelationships(List<UserFollowing> userFollowings);

    Page<UserFollowing> fetchBagRelationships(Page<UserFollowing> userFollowings);
}
