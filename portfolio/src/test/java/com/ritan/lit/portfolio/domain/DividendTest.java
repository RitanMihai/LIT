package com.ritan.lit.portfolio.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ritan.lit.portfolio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DividendTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Dividend.class);
        Dividend dividend1 = new Dividend();
        dividend1.setId(1L);
        Dividend dividend2 = new Dividend();
        dividend2.setId(dividend1.getId());
        assertThat(dividend1).isEqualTo(dividend2);
        dividend2.setId(2L);
        assertThat(dividend1).isNotEqualTo(dividend2);
        dividend1.setId(null);
        assertThat(dividend1).isNotEqualTo(dividend2);
    }
}
