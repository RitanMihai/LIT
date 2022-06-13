package com.ritan.lit.watchlist.repository;

import com.ritan.lit.watchlist.domain.PriceHistory;
import com.ritan.lit.watchlist.domain.Stock;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the PriceHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    List<PriceHistory> findAllByStock(Stock stock);
}
