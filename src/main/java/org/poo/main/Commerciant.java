package org.poo.main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Commerciant {
    @JsonIgnore
    private int payOnlineTimestamp;
    @JsonProperty("commerciant")
    private String name;
    @JsonProperty("total")
    private double amount;

    public Commerciant (double amount, String name, int payOnlineTimestamp) {
        this.amount = amount;
        this.name = name;
        this.payOnlineTimestamp = payOnlineTimestamp;
    }

    public int getPayOnlineTimestamp() {
        return payOnlineTimestamp;
    }

    public void setPayOnlineTimestamp(int payOnlineTimestamp) {
        this.payOnlineTimestamp = payOnlineTimestamp;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
