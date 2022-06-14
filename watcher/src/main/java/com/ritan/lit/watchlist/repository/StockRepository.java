package com.ritan.lit.watchlist.repository;

import com.ritan.lit.watchlist.domain.Stock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the Stock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findAllBySector(String sector);
    List<Stock> findAllByIndustry(String industry);

    List<Stock> findAllBySectorAndMarketCapIsNotNull(String sector, Pageable pageable);
    List<Stock> findAllByIndustryAndMarketCapIsNotNull(String industry, Pageable slice);

    Optional<Stock> findByTicker(String ticker);

    /* I should use sort and pageable instead of raw sql */
    @Query(value = "SELECT s.sector, COUNT(s.sector) FROM Stock AS s GROUP BY s.sector ORDER BY COUNT(s.sector) DESC")
    List<Object[]> countStocksBySector();

    /* I should use sort and pageable instead of raw sql */
    @Query(value = "SELECT s.industry, COUNT(s.industry) FROM Stock AS s GROUP BY s.industry ORDER BY COUNT(s.sector) DESC")
    List<Object[]> countStocksByIndustry();

    /* Return rows based on sector */
    Long countBySector(String sector);
    Long countByIndustry(String industry);

    /* Return all rows */
    long count();




}
