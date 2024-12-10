package org.poo.main;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"timestamp", "description"})
public class ErrorPaymentTransaction extends Transaction{

    public ErrorPaymentTransaction(int timestamp, String description) {
        super(timestamp, description);
    }
}
