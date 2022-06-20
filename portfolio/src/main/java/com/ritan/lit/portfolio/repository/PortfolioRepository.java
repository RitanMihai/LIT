package com.ritan.lit.portfolio.repository;

import com.ritan.lit.portfolio.domain.Portfolio;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the Portfolio entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    Optional<List<Portfolio>> getAllByPortfolioUserUser(String user);
}
