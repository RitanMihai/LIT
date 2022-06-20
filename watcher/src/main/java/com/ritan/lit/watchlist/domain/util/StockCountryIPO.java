package com.ritan.lit.watchlist.domain.util;

public class StockCountryIPO {
    private String ticker;
    private String country;
    private Integer ipo_date;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getIpo_date() {
        return ipo_date;
    }

    public void setIpo_date(Integer ipo_date) {
        this.ipo_date = ipo_date;
    }
}
