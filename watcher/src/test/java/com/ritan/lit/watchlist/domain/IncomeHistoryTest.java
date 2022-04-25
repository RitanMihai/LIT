package com.ritan.lit.watchlist.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ritan.lit.watchlist.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IncomeHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(IncomeHistory.class);
        IncomeHistory incomeHistory1 = new IncomeHistory();
        incomeHistory1.setId(1L);
        IncomeHistory incomeHistory2 = new IncomeHistory();
        incomeHistory2.setId(incomeHistory1.getId());
        assertThat(incomeHistory1).isEqualTo(incomeHistory2);
        incomeHistory2.setId(2L);
        assertThat(incomeHistory1).isNotEqualTo(incomeHistory2);
        incomeHistory1.setId(null);
        assertThat(incomeHistory1).isNotEqualTo(incomeHistory2);
    }
}
