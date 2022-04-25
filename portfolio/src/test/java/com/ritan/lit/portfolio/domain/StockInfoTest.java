package com.ritan.lit.portfolio.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ritan.lit.portfolio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockInfoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockInfo.class);
        StockInfo stockInfo1 = new StockInfo();
        stockInfo1.setId(1L);
        StockInfo stockInfo2 = new StockInfo();
        stockInfo2.setId(stockInfo1.getId());
        assertThat(stockInfo1).isEqualTo(stockInfo2);
        stockInfo2.setId(2L);
        assertThat(stockInfo1).isNotEqualTo(stockInfo2);
        stockInfo1.setId(null);
        assertThat(stockInfo1).isNotEqualTo(stockInfo2);
    }
}
