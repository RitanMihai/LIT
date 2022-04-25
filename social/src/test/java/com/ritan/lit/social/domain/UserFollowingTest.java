package com.ritan.lit.social.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ritan.lit.social.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserFollowingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserFollowing.class);
        UserFollowing userFollowing1 = new UserFollowing();
        userFollowing1.setId(1L);
        UserFollowing userFollowing2 = new UserFollowing();
        userFollowing2.setId(userFollowing1.getId());
        assertThat(userFollowing1).isEqualTo(userFollowing2);
        userFollowing2.setId(2L);
        assertThat(userFollowing1).isNotEqualTo(userFollowing2);
        userFollowing1.setId(null);
        assertThat(userFollowing1).isNotEqualTo(userFollowing2);
    }
}
