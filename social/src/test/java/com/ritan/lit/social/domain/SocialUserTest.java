package com.ritan.lit.social.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ritan.lit.social.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SocialUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SocialUser.class);
        SocialUser socialUser1 = new SocialUser();
        socialUser1.setId(1L);
        SocialUser socialUser2 = new SocialUser();
        socialUser2.setId(socialUser1.getId());
        assertThat(socialUser1).isEqualTo(socialUser2);
        socialUser2.setId(2L);
        assertThat(socialUser1).isNotEqualTo(socialUser2);
        socialUser1.setId(null);
        assertThat(socialUser1).isNotEqualTo(socialUser2);
    }
}
