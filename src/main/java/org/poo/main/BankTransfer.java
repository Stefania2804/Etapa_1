package org.poo.main;

public class BankTransfer implements PayStrategy {
    private Account receiver;
    private double exchangedAmount;

    public BankTransfer(Account receiver, double amount) {
        this.receiver = receiver;
        exchangedAmount = amount;
    }
    public void pay(Account account, double amount) {
        if (account.getBalance() >= amount) {
            account.setBalance(account.getBalance() - amount);

            receiver.setBalance(receiver.getBalance() + exchangedAmount);
        }
    }
}
