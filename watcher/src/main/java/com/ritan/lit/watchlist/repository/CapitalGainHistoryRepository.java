package com.ritan.lit.watchlist.repository;

import com.ritan.lit.watchlist.domain.CapitalGainHistory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the CapitalGainHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CapitalGainHistoryRepository extends JpaRepository<CapitalGainHistory, Long> {}
