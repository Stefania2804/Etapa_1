package org.poo.main;

import org.poo.utils.Utils;

public class OnlinePayment implements PayStrategy {

    @Override
    public void pay(Account account, double amount) {
        if (account.getBalance() >= amount) {
            account.setBalance(account.getBalance() - amount);
        }
    }
}
