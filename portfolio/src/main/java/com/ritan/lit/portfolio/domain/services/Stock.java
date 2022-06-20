package com.ritan.lit.portfolio.domain.services;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Stock {
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }
}
