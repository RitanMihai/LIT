package com.ritan.lit.watchlist.repository;

import com.ritan.lit.watchlist.domain.StockExchange;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the StockExchange entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StockExchangeRepository extends JpaRepository<StockExchange, Long> {}
