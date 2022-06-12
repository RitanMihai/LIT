package com.ritan.lit.watchlist.domain;


public class StockGroupByType {
    private Long number;
    private String category;

    public StockGroupByType(){
        /* EMPTY */
    }

    public StockGroupByType(String category, Long size){
        this.category = category;
        this.number = size;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
