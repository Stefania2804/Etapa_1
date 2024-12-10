package org.poo.main;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"timestamp", "description"})
public class CardStatusTransaction extends Transaction{
    public CardStatusTransaction(int timestamp, String description) {
        super(timestamp, description);
    }
}
