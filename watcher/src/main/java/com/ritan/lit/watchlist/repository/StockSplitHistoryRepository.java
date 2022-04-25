package com.ritan.lit.watchlist.repository;

import com.ritan.lit.watchlist.domain.StockSplitHistory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the StockSplitHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StockSplitHistoryRepository extends JpaRepository<StockSplitHistory, Long> {}
