package com.ritan.lit.portfolio.repository;

import com.ritan.lit.portfolio.domain.Order;
import com.ritan.lit.portfolio.domain.util.PortfolioOrderGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the Order entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    /* The Query is too complex to use just default JPA Repository Syntax*/
    @Query(value = "SELECT COUNT(stock.ticker), SUM(ord.total), stock.ticker " +
        "FROM Order as ord " +
        "JOIN StockInfo as stock ON ord.stockInfo=stock " +
        "JOIN Portfolio as pr ON ord.portfolio = pr " +
        "JOIN PortfolioUser as user ON pr.portfolioUser = user " +
        "WHERE user.user = ?1 AND pr.name=?2 " +
        "GROUP BY stock.ticker " +
        "ORDER BY SUM(ord.total) DESC")
    Optional<List<Object[]>> getAllPortfoliosWithDetails(String user, String portfolioName);

    @Query(value = "SELECT ord.filledDate, SUM(ord.total) " +
        "FROM Order as ord " +
        "JOIN StockInfo as stock ON ord.stockInfo=stock " +
        "JOIN Portfolio as pr ON ord.portfolio = pr " +
        "JOIN PortfolioUser as user ON pr.portfolioUser = user " +
        "WHERE user.user = ?1 " +
        "GROUP BY ord.filledDate " +
        "ORDER BY ord.filledDate ASC "
    )
    Page<Object[]> findAllByPortfolioPortfolioUser(Pageable pageable, String User);

    @Query(value = "SELECT ord " +
        "FROM Order as ord " +
        "JOIN StockInfo as stock ON ord.stockInfo=stock " +
        "JOIN Portfolio as pr ON ord.portfolio = pr " +
        "JOIN PortfolioUser as user ON pr.portfolioUser = user " +
        "WHERE user.user = ?1 " +
        "ORDER BY ord.filledDate ASC ")
    Page<Order> findAllByPortfolioUser(Pageable pageable, String user);

    @Query(value = "SELECT ord " +
        "FROM Order as ord " +
        "JOIN StockInfo as stock ON ord.stockInfo=stock " +
        "JOIN Portfolio as pr ON ord.portfolio = pr " +
        "JOIN PortfolioUser as user ON pr.portfolioUser = user " +
        "WHERE user.user = ?1 AND pr.name = ?2 " +
        "ORDER BY ord.filledDate ASC ")
    Page<Order> findAllByPortfolioPortfolioUserAndPortfolio(Pageable pageable, String user, String portfolio);
}
