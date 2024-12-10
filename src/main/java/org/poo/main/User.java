package org.poo.main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class User {
    private String firstName;
    private String lastName;
    private String email;
    @JsonIgnore
    private ArrayList<Classic> classics;
    @JsonIgnore
    private ArrayList<Savings> savings;
    private ArrayList<Account> accounts;
    @JsonIgnore
    private List<Transaction> transactions;

    public User(final String firstName, final String lastName, final String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        classics = new ArrayList<>();
        savings = new ArrayList<>();
        accounts = new ArrayList<>();
        transactions = new ArrayList<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public ArrayList<Classic> getClassics() {
        return classics;
    }

    public void setClassics(ArrayList<Classic> classics) {
        this.classics = classics;
    }

    public ArrayList<Savings> getSavings() {
        return savings;
    }

    public void setSavings(final ArrayList<Savings> savings) {
        this.savings = savings;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(final ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void addClassic(final Classic account) {
        classics.add(account);
    }
    public void addSavings(final Savings account) {
        savings.add(account);
    }
    public void addAccounts(final Account account) {
        accounts.add(account);
    }
    public void deleteFromUser(final Account account) {
        accounts.remove(account);
        if (account.getClass() == Savings.class) {
            savings.remove(account);
            return;
        }
        if (account.getClass() == Classic.class) {
            classics.remove(account);
        }
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }
}
