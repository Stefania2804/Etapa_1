package org.poo.main;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
@JsonPropertyOrder({"timestamp", "description", "currency", "amount", "involvedAccounts", "error"})
public class ErrorSplitPaymentTransaction extends SplitPaymentTransaction {
    @JsonProperty("error")
    private String error;
    public ErrorSplitPaymentTransaction(int timestamp, String description, String currency,
                                   double amount, List<String> involvedAccounts, String error) {
        super(timestamp, description, currency, amount, involvedAccounts);
        this.error = error;
    }
}
