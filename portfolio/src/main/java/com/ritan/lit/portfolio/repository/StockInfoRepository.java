package com.ritan.lit.portfolio.repository;

import com.ritan.lit.portfolio.domain.StockInfo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the StockInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StockInfoRepository extends JpaRepository<StockInfo, Long> {}
