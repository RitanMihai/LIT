package com.ritan.lit.watchlist.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ritan.lit.watchlist.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DividendHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DividendHistory.class);
        DividendHistory dividendHistory1 = new DividendHistory();
        dividendHistory1.setId(1L);
        DividendHistory dividendHistory2 = new DividendHistory();
        dividendHistory2.setId(dividendHistory1.getId());
        assertThat(dividendHistory1).isEqualTo(dividendHistory2);
        dividendHistory2.setId(2L);
        assertThat(dividendHistory1).isNotEqualTo(dividendHistory2);
        dividendHistory1.setId(null);
        assertThat(dividendHistory1).isNotEqualTo(dividendHistory2);
    }
}
