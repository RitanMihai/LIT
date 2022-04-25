package com.ritan.lit.portfolio.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ritan.lit.portfolio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PortfolioCurrencyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PortfolioCurrency.class);
        PortfolioCurrency portfolioCurrency1 = new PortfolioCurrency();
        portfolioCurrency1.setId(1L);
        PortfolioCurrency portfolioCurrency2 = new PortfolioCurrency();
        portfolioCurrency2.setId(portfolioCurrency1.getId());
        assertThat(portfolioCurrency1).isEqualTo(portfolioCurrency2);
        portfolioCurrency2.setId(2L);
        assertThat(portfolioCurrency1).isNotEqualTo(portfolioCurrency2);
        portfolioCurrency1.setId(null);
        assertThat(portfolioCurrency1).isNotEqualTo(portfolioCurrency2);
    }
}
