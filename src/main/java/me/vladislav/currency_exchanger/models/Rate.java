package me.vladislav.currency_exchanger.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

public class Rate {
    @JsonProperty(required = true)
    private int id;
    @JsonProperty(required = true)
    private me.vladislav.currency_exchanger.models.Currency baseCurrency;
    @JsonProperty(required = true)
    private me.vladislav.currency_exchanger.models.Currency targetCurrency;
    @JsonProperty(required = true)
    private BigDecimal rate;

    public Rate(int id, me.vladislav.currency_exchanger.models.Currency baseCurrency, me.vladislav.currency_exchanger.models.Currency targetCurrency, BigDecimal rate){
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Currency targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rate rate = (Rate) o;
        return id == rate.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Rate{" +
                "id=" + id +
                ", baseCurrency=" + baseCurrency.toString() +
                ", targetCurrency=" + targetCurrency.toString() +
                ", rate=" + rate +
                '}';
    }

}
