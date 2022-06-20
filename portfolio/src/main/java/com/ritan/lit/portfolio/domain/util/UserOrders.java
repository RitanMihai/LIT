package com.ritan.lit.portfolio.domain.util;
import java.time.Instant;

public class UserOrders {
    private Instant date;
    private Double sum;

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }
}
