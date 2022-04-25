package com.ritan.lit.social.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ritan.lit.social.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserReactionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserReaction.class);
        UserReaction userReaction1 = new UserReaction();
        userReaction1.setId(1L);
        UserReaction userReaction2 = new UserReaction();
        userReaction2.setId(userReaction1.getId());
        assertThat(userReaction1).isEqualTo(userReaction2);
        userReaction2.setId(2L);
        assertThat(userReaction1).isNotEqualTo(userReaction2);
        userReaction1.setId(null);
        assertThat(userReaction1).isNotEqualTo(userReaction2);
    }
}
