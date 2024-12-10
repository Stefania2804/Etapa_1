package org.poo.main;

public final class OnlinePayment implements PayStrategy {

    @Override
    public void pay(final Account account, final double amount) {
            account.setBalance(account.getBalance() - amount);
    }
}
