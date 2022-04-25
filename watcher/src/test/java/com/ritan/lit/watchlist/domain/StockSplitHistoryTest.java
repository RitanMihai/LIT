package com.ritan.lit.watchlist.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ritan.lit.watchlist.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockSplitHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockSplitHistory.class);
        StockSplitHistory stockSplitHistory1 = new StockSplitHistory();
        stockSplitHistory1.setId(1L);
        StockSplitHistory stockSplitHistory2 = new StockSplitHistory();
        stockSplitHistory2.setId(stockSplitHistory1.getId());
        assertThat(stockSplitHistory1).isEqualTo(stockSplitHistory2);
        stockSplitHistory2.setId(2L);
        assertThat(stockSplitHistory1).isNotEqualTo(stockSplitHistory2);
        stockSplitHistory1.setId(null);
        assertThat(stockSplitHistory1).isNotEqualTo(stockSplitHistory2);
    }
}
