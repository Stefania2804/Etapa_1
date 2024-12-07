package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.poo.fileio.CommandInput;
import org.poo.utils.Utils;

public class Actions {
    public static void applyCommand(CommandInput commandInput, InfoBank infoBank, ObjectMapper objectMapper, ArrayNode output) {
        try {
            switch (commandInput.getCommand()) {
                case "printUsers":
                    JsonOutput.printUsers(infoBank, commandInput, objectMapper, output);
                    break;
                case "addAccount":
                    String email = commandInput.getEmail();
                    for (User user : infoBank.getUsers()) {
                        if (user.getEmail().equals(email)) {
                            String currency = commandInput.getCurrency();
                            String type = commandInput.getAccountType();
                            String IBAN = Utils.generateIBAN();
                            switch(type) {
                                case "classic":
                                    Account classic = new Classic(IBAN, 0.0, currency, type);
                                    user.addClassic((Classic) classic);
                                    user.addAccounts(classic);
                                    infoBank.addAccount(classic);
                                    break;
                                case "savings":
                                    Account savings = new Savings(IBAN, 0.0, currency, type, commandInput.getInterestRate());
                                    user.addSavings((Savings) savings);
                                    user.addAccounts(savings);
                                    infoBank.addAccount(savings);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    break;
                case "addFunds":
                    String iban = commandInput.getAccount();
                    for (Account acc : infoBank.getAccounts()) {
                        if (acc.getIban().equals(iban)) {
                            acc.setBalance(commandInput.getAmount() + acc.getBalance());
                        }
                    }
                    break;
                case "createCard":
                    String ibanAcc = commandInput.getAccount();
                    String userEmail = commandInput.getEmail();
                    for (User user : infoBank.getUsers()) {
                        if (user.getEmail().equals(userEmail)) {
                            for (Account accs : user.getAccounts()) {
                                if (accs.getIban().equals(ibanAcc)) {
                                    String cardNumber = Utils.generateCardNumber();
                                    Card card = new NormalCard(cardNumber, "active");
                                    accs.addCard(card);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    break;
                case "createOneTimeCard":
                    String ibanAccOnetime = commandInput.getAccount();
                    String userEmailOneTime = commandInput.getEmail();
                    for (User user : infoBank.getUsers()) {
                        if (user.getEmail().equals(userEmailOneTime)) {
                            for (Account accs : user.getAccounts()) {
                                if (accs.getIban().equals(ibanAccOnetime)) {
                                    String cardNumber = Utils.generateCardNumber();
                                    Card card = new OneTimeCard(cardNumber, "active");
                                    accs.addCard(card);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    break;
                case "deleteAccount":
                    String ibanAccDelete = commandInput.getAccount();
                    String userEmailDelete = commandInput.getEmail();
                    for (User user : infoBank.getUsers()) {
                        if (user.getEmail().equals(userEmailDelete)) {
                            for (Account accs : user.getAccounts()) {
                                if (accs.getIban().equals(ibanAccDelete) && accs.getBalance() == 0.0) {
                                    user.deleteFromUser(accs);
                                    infoBank.deleteFromBank(accs);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    JsonOutput.deleteAccount(commandInput, objectMapper, output);
                    break;
                case "deleteCard":
                    String cardNumber = commandInput.getCardNumber();
                    for (Account account : infoBank.getAccounts()) {
                        for (Card card : account.getCards()) {
                            if (card.getCardNumber().equals(cardNumber)) {
                                account.deleteCard(card);
                                break;
                            }
                        }
                    }
                    break;
                case "setMinBalance":
                    double minBalance = commandInput.getMinBalance();
                    String minBalanceIban = commandInput.getAccount();
                    for (Account account : infoBank.getAccounts()) {
                        if (account.getIban().equals(minBalanceIban)) {
                            account.setMinBalance(minBalance);
                            break;
                        }
                    }
                case "checkCardStatus":
                    break;
                case "payOnline":
                    int success = 0;
                    String payOnlineEmail = commandInput.getEmail();
                    String payOnlineCard = commandInput.getCardNumber();
                    for (User user : infoBank.getUsers()) {
                        if (user.getEmail().equals(payOnlineEmail)) {
                            for (Account account : user.getAccounts()) {
                                for (Card card : account.getCards()) {
                                    if (card.getCardNumber().equals(payOnlineCard)) {
                                        double amount = infoBank.exchange(commandInput.getCurrency(),
                                                account.getCurrency(), commandInput.getAmount());
                                        PayStrategy onlinePayment = new OnlinePayment();
                                        PaymentContext context = new PaymentContext(onlinePayment);
                                        context.executePayment(account, amount);
                                        if (card.getClass() == OneTimeCard.class) {
                                            card.setCardNumber(Utils.generateCardNumber());
                                        }
                                        success = 1;
                                    }
                                }
                            }
                        }
                    }
                    if (success == 0) {
                        JsonOutput.errorOnlinePayment(commandInput, objectMapper, output);
                    }
                    break;
                case "sendMoney":
                    String sender = commandInput.getAccount();
                    String receiver = commandInput.getReceiver();
                    String senderCurrency = null;
                    String receiverCurrency = null;
                    Account senderAccount = new Account(null, 0.0, null, null);
                    Account receiverAccount = new Account(null, 0.0, null, null);
                    for (Account account : infoBank.getAccounts()) {
                        if (account.getIban().equals(sender)) {
                            senderCurrency = account.getCurrency();
                            senderAccount = account;
                        }
                        if (account.getIban().equals(receiver)) {
                            receiverCurrency = account.getCurrency();
                            receiverAccount = account;
                        }
                    }
                    double amount = infoBank.exchange(senderCurrency, receiverCurrency, commandInput.getAmount());
                    PayStrategy bankTransfer = new BankTransfer(receiverAccount, amount);
                    PaymentContext context = new PaymentContext(bankTransfer);
                    context.executePayment(senderAccount, commandInput.getAmount());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
