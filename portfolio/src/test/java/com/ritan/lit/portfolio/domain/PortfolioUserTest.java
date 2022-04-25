package com.ritan.lit.portfolio.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ritan.lit.portfolio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PortfolioUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PortfolioUser.class);
        PortfolioUser portfolioUser1 = new PortfolioUser();
        portfolioUser1.setId(1L);
        PortfolioUser portfolioUser2 = new PortfolioUser();
        portfolioUser2.setId(portfolioUser1.getId());
        assertThat(portfolioUser1).isEqualTo(portfolioUser2);
        portfolioUser2.setId(2L);
        assertThat(portfolioUser1).isNotEqualTo(portfolioUser2);
        portfolioUser1.setId(null);
        assertThat(portfolioUser1).isNotEqualTo(portfolioUser2);
    }
}
