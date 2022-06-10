package com.ritan.lit.watchlist.domain;


import java.util.List;

public class PageableStock {
    private List<Stock>  stocks;
    private Long size;

    public PageableStock(){
        /* EMPTY */
    }

    public List<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(List<Stock> stocks) {
        this.stocks = stocks;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
