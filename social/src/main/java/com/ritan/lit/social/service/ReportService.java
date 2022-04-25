package com.ritan.lit.social.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.social.domain.Report;
import com.ritan.lit.social.repository.ReportRepository;
import com.ritan.lit.social.repository.search.ReportSearchRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Report}.
 */
@Service
@Transactional
public class ReportService {

    private final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final ReportRepository reportRepository;

    private final ReportSearchRepository reportSearchRepository;

    public ReportService(ReportRepository reportRepository, ReportSearchRepository reportSearchRepository) {
        this.reportRepository = reportRepository;
        this.reportSearchRepository = reportSearchRepository;
    }

    /**
     * Save a report.
     *
     * @param report the entity to save.
     * @return the persisted entity.
     */
    public Report save(Report report) {
        log.debug("Request to save Report : {}", report);
        Report result = reportRepository.save(report);
        reportSearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a report.
     *
     * @param report the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Report> partialUpdate(Report report) {
        log.debug("Request to partially update Report : {}", report);

        return reportRepository
            .findById(report.getId())
            .map(existingReport -> {
                if (report.getType() != null) {
                    existingReport.setType(report.getType());
                }
                if (report.getDescription() != null) {
                    existingReport.setDescription(report.getDescription());
                }

                return existingReport;
            })
            .map(reportRepository::save)
            .map(savedReport -> {
                reportSearchRepository.save(savedReport);

                return savedReport;
            });
    }

    /**
     * Get all the reports.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Report> findAll() {
        log.debug("Request to get all Reports");
        return reportRepository.findAll();
    }

    /**
     * Get one report by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Report> findOne(Long id) {
        log.debug("Request to get Report : {}", id);
        return reportRepository.findById(id);
    }

    /**
     * Delete the report by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Report : {}", id);
        reportRepository.deleteById(id);
        reportSearchRepository.deleteById(id);
    }

    /**
     * Search for the report corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Report> search(String query) {
        log.debug("Request to search Reports for query {}", query);
        return StreamSupport.stream(reportSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
