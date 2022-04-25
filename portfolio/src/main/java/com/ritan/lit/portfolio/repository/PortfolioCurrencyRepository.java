package com.ritan.lit.portfolio.repository;

import com.ritan.lit.portfolio.domain.PortfolioCurrency;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the PortfolioCurrency entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PortfolioCurrencyRepository extends JpaRepository<PortfolioCurrency, Long> {}
