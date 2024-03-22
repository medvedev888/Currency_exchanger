package me.vladislav.currency_exchanger.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Currency {
    @JsonProperty(required = true)
    private int id;
    @JsonProperty(required = true)
    private String code;
    @JsonProperty(required = true)
    private String fullName;
    @JsonProperty(required = true)
    private String sign;

    public Currency(int id, String code, String fullName, String sign){
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return id == currency.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Currency{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", fullName='" + fullName + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
