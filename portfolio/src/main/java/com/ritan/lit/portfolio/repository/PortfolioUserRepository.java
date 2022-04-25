package com.ritan.lit.portfolio.repository;

import com.ritan.lit.portfolio.domain.PortfolioUser;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the PortfolioUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PortfolioUserRepository extends JpaRepository<PortfolioUser, Long> {}
