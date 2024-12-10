package org.poo.main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.poo.utils.Utils;

import java.util.ArrayList;

public class Savings extends Account {
    @JsonIgnore
    private double interestRate;

    public Savings(String IBAN, double balance, String currency, String type, double interestRate) {
        super(IBAN, balance, currency, type);
        this.interestRate = interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
