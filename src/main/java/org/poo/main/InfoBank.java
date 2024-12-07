package org.poo.main;

import java.util.ArrayList;

public class InfoBank {
    private ArrayList<User> users;
    private ArrayList<Account> accounts;
    private ArrayList<Exchange> exchanges;

    public InfoBank() {
        users = new ArrayList<>();
        accounts = new ArrayList<>();
        exchanges = new ArrayList<>();
    }
    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    public ArrayList<Exchange> getExchanges() {
        return exchanges;
    }

    public void setExchanges(ArrayList<Exchange> exchanges) {
        this.exchanges = exchanges;
    }

    public void addUser(User user) {
        users.add(user);
    }
    public void addAccount(Account account) {
        accounts.add(account);
    }
    public void addExchange(Exchange exchange) {
        exchanges.add(exchange);
    }
    public void deleteFromBank(Account account) {
        accounts.remove(account);
    }
    public double exchange(String from, String to, double amount) {
        for (Exchange exchange : exchanges) {
            if (exchange.getFrom().equals(from) && exchange.getTo().equals(to)) {
                return amount * exchange.getRate();
            }
        }
        for (Exchange inter : exchanges) {
            if (inter.getFrom().equals(from)) {

                double interAmount = amount * inter.getRate();
                String interCurrency = inter.getTo();

                for (Exchange finalExchange : exchanges) {
                    if (finalExchange.getFrom().equals(interCurrency) && finalExchange.getTo().equals(to)) {
                        return interAmount * finalExchange.getRate();
                    }
                }
            }
        }
        for (Exchange inter : exchanges) {
            if (inter.getTo().equals(from)) {

                double interAmount = amount / inter.getRate();
                String interCurrency = inter.getFrom();

                for (Exchange finalExchange : exchanges) {
                    if (finalExchange.getTo().equals(interCurrency) && finalExchange.getFrom().equals(to)) {
                        return interAmount / finalExchange.getRate();
                    }
                }
            }
        }
        for (Exchange inter : exchanges) {
            if (inter.getTo().equals(from)) {

                double interAmount = amount / inter.getRate();
                String interCurrency = inter.getFrom();

                for (Exchange finalExchange : exchanges) {
                    if (finalExchange.getFrom().equals(interCurrency) && finalExchange.getTo().equals(to)) {
                        return interAmount * finalExchange.getRate();
                    }
                }
            }
        }
        for (Exchange inter : exchanges) {
            if (inter.getFrom().equals(to)) {

                double interAmount = amount * inter.getRate();
                String interCurrency = inter.getTo();

                for (Exchange finalExchange : exchanges) {
                    if (finalExchange.getTo().equals(interCurrency) && finalExchange.getFrom().equals(from)) {
                        return interAmount / finalExchange.getRate();
                    }
                }
            }
        }
        return amount;
    }
}
