package com.ritan.lit.watchlist.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ritan.lit.watchlist.domain.Company;
import com.ritan.lit.watchlist.repository.CompanyRepository;
import com.ritan.lit.watchlist.repository.search.CompanySearchRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Company}.
 */
@Service
@Transactional
public class CompanyService {

    private final Logger log = LoggerFactory.getLogger(CompanyService.class);

    private final CompanyRepository companyRepository;

    private final CompanySearchRepository companySearchRepository;

    public CompanyService(CompanyRepository companyRepository, CompanySearchRepository companySearchRepository) {
        this.companyRepository = companyRepository;
        this.companySearchRepository = companySearchRepository;
    }

    /**
     * Save a company.
     *
     * @param company the entity to save.
     * @return the persisted entity.
     */
    public Company save(Company company) {
        log.debug("Request to save Company : {}", company);
        Company result = companyRepository.save(company);
        companySearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a company.
     *
     * @param company the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Company> partialUpdate(Company company) {
        log.debug("Request to partially update Company : {}", company);

        return companyRepository
            .findById(company.getId())
            .map(existingCompany -> {
                if (company.getName() != null) {
                    existingCompany.setName(company.getName());
                }
                if (company.getImage() != null) {
                    existingCompany.setImage(company.getImage());
                }
                if (company.getImageContentType() != null) {
                    existingCompany.setImageContentType(company.getImageContentType());
                }
                if (company.getDescription() != null) {
                    existingCompany.setDescription(company.getDescription());
                }
                if (company.getEmployees() != null) {
                    existingCompany.setEmployees(company.getEmployees());
                }
                if (company.getSector() != null) {
                    existingCompany.setSector(company.getSector());
                }
                if (company.getIndustry() != null) {
                    existingCompany.setIndustry(company.getIndustry());
                }
                if (company.getCeo() != null) {
                    existingCompany.setCeo(company.getCeo());
                }
                if (company.getSite() != null) {
                    existingCompany.setSite(company.getSite());
                }
                if (company.getDateOfEstablishment() != null) {
                    existingCompany.setDateOfEstablishment(company.getDateOfEstablishment());
                }

                return existingCompany;
            })
            .map(companyRepository::save)
            .map(savedCompany -> {
                companySearchRepository.save(savedCompany);

                return savedCompany;
            });
    }

    /**
     * Get all the companies.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Company> findAll() {
        log.debug("Request to get all Companies");
        return companyRepository.findAll();
    }

    /**
     * Get one company by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Company> findOne(Long id) {
        log.debug("Request to get Company : {}", id);
        return companyRepository.findById(id);
    }

    /**
     * Delete the company by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Company : {}", id);
        companyRepository.deleteById(id);
        companySearchRepository.deleteById(id);
    }

    /**
     * Search for the company corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Company> search(String query) {
        log.debug("Request to search Companies for query {}", query);
        return StreamSupport.stream(companySearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
