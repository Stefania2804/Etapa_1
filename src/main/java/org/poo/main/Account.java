package org.poo.main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"IBAN", "balance", "currency", "type", "cards"})
public class Account implements Visitable{
    @JsonProperty("IBAN")
    private String iban;
    private double balance;
    private String currency;
    private String type;
    private ArrayList<Card> cards;
    @JsonIgnore
    private double minBalance;

    public Account(final String iban, final double balance, final String currency,
                   final String type) {
        this.iban = iban;
        this.balance = balance;
        this.currency = currency;
        this.type = type;
        this.cards = new ArrayList<>();
        minBalance = 0.0;
    }
    /**
     * Getter pentru iban.
     *
     */
    public String getIban() {
        return iban;
    }
    /**
     * Setter pentru iban.
     *
     */

    public void setIban(final String iban) {
        this.iban = iban;
    }
    /**
     * Getter pentru balance.
     *
     */

    public double getBalance() {
        return balance;
    }
    /**
     * Setter pentru balance.
     *
     */

    public void setBalance(final double balance) {
        this.balance = balance;
    }
    /**
     * Getter pentru currency.
     *
     */

    public String getCurrency() {
        return currency;
    }
    /**
     * Setter pentru currency.
     *
     */

    public void setCurrency(final String currency) {
        this.currency = currency;
    }
    /**
     * Getter pentru type.
     *
     */

    public String getType() {
        return type;
    }
    /**
     * Setter pentru type.
     *
     */

    public void setType(final String type) {
        this.type = type;
    }
    /**
     * Getter pentru cardurile asociate contului.
     *
     */

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setCards(final ArrayList<Card> card) {
        this.cards = card;
    }

    public double getMinBalance() {
        return minBalance;
    }

    public void setMinBalance(final double minBalance) {
        this.minBalance = minBalance;
    }


    public void addCard(final Card card) {
        cards.add(card);
    }

    public void accept(final Visitor v) {
        v.visit(this);
    }
    public void deleteCard(final Card card) {
        cards.remove(card);
    }

}
