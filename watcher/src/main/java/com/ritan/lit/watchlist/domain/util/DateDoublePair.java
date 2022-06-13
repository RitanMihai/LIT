package com.ritan.lit.watchlist.domain.util;

import java.time.LocalDate;

public class DateDoublePair {
    LocalDate date;
    Double value;

    public DateDoublePair() {
        /* EMPTY */
    }

    public DateDoublePair(LocalDate date, Double value) {
        this.date = date;
        this.value = value;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
