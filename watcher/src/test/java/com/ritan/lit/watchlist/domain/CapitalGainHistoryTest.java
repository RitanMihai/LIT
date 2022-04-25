package com.ritan.lit.watchlist.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ritan.lit.watchlist.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CapitalGainHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CapitalGainHistory.class);
        CapitalGainHistory capitalGainHistory1 = new CapitalGainHistory();
        capitalGainHistory1.setId(1L);
        CapitalGainHistory capitalGainHistory2 = new CapitalGainHistory();
        capitalGainHistory2.setId(capitalGainHistory1.getId());
        assertThat(capitalGainHistory1).isEqualTo(capitalGainHistory2);
        capitalGainHistory2.setId(2L);
        assertThat(capitalGainHistory1).isNotEqualTo(capitalGainHistory2);
        capitalGainHistory1.setId(null);
        assertThat(capitalGainHistory1).isNotEqualTo(capitalGainHistory2);
    }
}
