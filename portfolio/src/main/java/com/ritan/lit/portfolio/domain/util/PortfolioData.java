package com.ritan.lit.portfolio.domain.util;

public class PortfolioData implements Comparable<PortfolioData>{
    private String name;
    private Double invested;
    private Integer stockNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getInvested() {
        return invested;
    }

    public void setInvested(Double invested) {
        this.invested = invested;
    }

    public Integer getStockNumber() {
        return stockNumber;
    }

    public void setStockNumber(Integer stockNumber) {
        this.stockNumber = stockNumber;
    }

    /* Sort in reverse in reverse order */
    @Override
    public int compareTo(PortfolioData o) {
        return o.getInvested().compareTo(this.invested);
    }
}
