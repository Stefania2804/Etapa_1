package org.poo.main;

public class PaymentContext {
    private PayStrategy strategy;

    public PaymentContext(PayStrategy strategy) {
        this.strategy = strategy;
    }

    public void executePayment(Account account, double amount) {
        strategy.pay(account, amount);
    }
}
