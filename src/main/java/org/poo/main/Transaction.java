package org.poo.main;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"timestamp", "description"})
public class Transaction {
    @JsonProperty("timestamp")
    private int timestamp;
    @JsonProperty("description")
    private String description;

    public Transaction(int timestamp, String description) {
        this.timestamp = timestamp;
        this.description = description;
    }
    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
