package com.ritan.lit.portfolio.domain.services;

import java.time.LocalDate;

public class DateDoublePair implements Comparable<DateDoublePair>{
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

    @Override
    public int compareTo(DateDoublePair dateDoublePair) {
        return this.date.compareTo(dateDoublePair.date);
    }
}
