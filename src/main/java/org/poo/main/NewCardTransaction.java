package org.poo.main;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"timestamp", "description", "card", "cardHolder", "amount"})
public class NewCardTransaction extends Transaction{
    @JsonProperty("card")
    private String card;
    @JsonProperty("cardHolder")
    private String cardHolder;
    @JsonProperty("account")
    private String account;

    public NewCardTransaction(int timestamp, String description, String card, String cardHolder, String account) {
        super(timestamp, description);
        this.card = card;
        this.cardHolder = cardHolder;
        this.account = account;
    }
}
