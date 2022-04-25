package com.ritan.lit.watchlist.repository;

import com.ritan.lit.watchlist.domain.IncomeHistory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the IncomeHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IncomeHistoryRepository extends JpaRepository<IncomeHistory, Long> {}
