package com.ritan.lit.watchlist.repository;

import com.ritan.lit.watchlist.domain.DividendHistory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the DividendHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DividendHistoryRepository extends JpaRepository<DividendHistory, Long> {}
