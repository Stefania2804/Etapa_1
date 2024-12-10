package org.poo.main;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"timestamp", "description", "senderIBAN", "receiverIBAN", "amount", "transferTye"})
public class SendMoneyTransaction extends Transaction {
    @JsonProperty("senderIBAN")
    private String senderIban;
    @JsonProperty("receiverIBAN")
    private String receiverIban;
    @JsonProperty("amount")
    private String amount;
    @JsonProperty("transferType")
    private String transferType;

    public SendMoneyTransaction(int timestamp, String description, String senderIban,
                                String receiverIban, String amount, String transferType) {
        super(timestamp, description);
        this.senderIban = senderIban;
        this.receiverIban = receiverIban;
        this.amount = amount;
        this.transferType = transferType;
    }
}
