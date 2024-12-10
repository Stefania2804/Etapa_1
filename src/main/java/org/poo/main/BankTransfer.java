package org.poo.main;

public class BankTransfer implements PayStrategy {
    private Account receiver;
    private double exchangedAmount;
    private String currency;
    private double amount;

    public BankTransfer(Account receiver, double exchangedAmount, String currency, double amount) {
        this.receiver = receiver;
        this.exchangedAmount = exchangedAmount;
        this.currency = currency;
        this.amount = amount;
    }
    public void pay(Account account, double amount) {
            account.setBalance(account.getBalance() - amount);

            receiver.setBalance(receiver.getBalance() + exchangedAmount);
    }
    public String toString() {
        return amount + " " +currency;
    }
}
