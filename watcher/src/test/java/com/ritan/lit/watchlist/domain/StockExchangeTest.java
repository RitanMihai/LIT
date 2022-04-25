package com.ritan.lit.watchlist.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ritan.lit.watchlist.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockExchangeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockExchange.class);
        StockExchange stockExchange1 = new StockExchange();
        stockExchange1.setId(1L);
        StockExchange stockExchange2 = new StockExchange();
        stockExchange2.setId(stockExchange1.getId());
        assertThat(stockExchange1).isEqualTo(stockExchange2);
        stockExchange2.setId(2L);
        assertThat(stockExchange1).isNotEqualTo(stockExchange2);
        stockExchange1.setId(null);
        assertThat(stockExchange1).isNotEqualTo(stockExchange2);
    }
}
