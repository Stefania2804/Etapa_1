package org.poo.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class InfoBank {
    private ArrayList<User> users;
    private ArrayList<Account> accounts;
    private ArrayList<Exchange> exchanges;
    private HashMap<String, String> hashMap;

    public InfoBank() {
        users = new ArrayList<>();
        accounts = new ArrayList<>();
        exchanges = new ArrayList<>();
        hashMap = new HashMap<>();
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

    public HashMap<String, String> getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap<String, String> hashMap) {
        this.hashMap = hashMap;
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

    public double recursiveExchange(String from, String to, double amount, Set<String> visited) {
        if (from.equals(to)) {
            return amount;
        }

        visited.add(from);

        for (Exchange exchange : exchanges) {
            if (exchange.getFrom().equals(from) && !visited.contains(exchange.getTo())) {

                double newAmount = amount * exchange.getRate();
                double result = recursiveExchange(exchange.getTo(), to, newAmount, visited);
                if (result != -1) {
                    return result;
                }
            }

            if (exchange.getTo().equals(from) && !visited.contains(exchange.getFrom())) {
                double newAmount = amount / exchange.getRate();
                double result = recursiveExchange(exchange.getFrom(), to, newAmount, visited);
                if (result != -1) {
                    return result;
                }
            }
        }
        return -1;
    }

    public double exchange(String from, String to, double amount) {

        Set<String> visited = new HashSet<>();
        double result = recursiveExchange(from, to, amount, visited);
        return result;
    }
    public void setAlias(String aliasName, String iban) {
        hashMap.put(aliasName, iban);
    }
}
