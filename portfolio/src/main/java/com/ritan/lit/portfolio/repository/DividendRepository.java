package com.ritan.lit.portfolio.repository;

import com.ritan.lit.portfolio.domain.Dividend;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Dividend entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DividendRepository extends JpaRepository<Dividend, Long> {}
