package org.poo.main;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"timestamp", "description", "amount", "commerciant"})
public class OnlinePayTransaction extends Transaction {
    @JsonProperty("amount")
    private double amount;
    @JsonProperty("commerciant")
    private String commerciant;

    public OnlinePayTransaction(int timestamp, String description, double amount, String commerciant) {
        super(timestamp, description);
        this.amount = amount;
        this.commerciant = commerciant;
    }
}
