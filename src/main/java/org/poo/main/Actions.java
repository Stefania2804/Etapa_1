package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.poo.fileio.CommandInput;
import org.poo.utils.Utils;

public abstract class Actions {
    /**
     * Executa fiecare comanda din input in functie de numele ei.
     *
     */
    public static void applyCommand(final CommandInput commandInput, final InfoBank infoBank,
                                    final ObjectMapper objectMapper, final ArrayNode output) {
        try {
            switch (commandInput.getCommand()) {
                case "printUsers":
                    JsonOutput.printUsers(infoBank, commandInput, objectMapper, output);
                    break;
                case "addAccount":
                    String email = commandInput.getEmail();
                    for (User user : infoBank.getUsers()) {
                        if (user.getEmail().equals(email)) {
                            Transaction transaction = new NewAccTransaction
                                    (commandInput.getTimestamp(),
                                    "New account created");
                            user.addTransaction(transaction);
                            String currency = commandInput.getCurrency();
                            String type = commandInput.getAccountType();
                            String iban = Utils.generateIBAN();
                            switch(type) {
                                case "classic":
                                    Account classic = new Classic(iban, 0.0, currency, type);
                                    user.addClassic((Classic) classic);
                                    user.addAccounts(classic);
                                    infoBank.addAccount(classic);
                                    break;
                                case "savings":
                                    Account savings = new Savings(iban, 0.0, currency, type,
                                            commandInput.getInterestRate());
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
                    int success = 0;
                    String ibanAcc = commandInput.getAccount();
                    String userEmail = commandInput.getEmail();
                    User userFound = new User(null, null, userEmail);
                    for (User user : infoBank.getUsers()) {
                        if (user.getEmail().equals(userEmail)) {
                            userFound = user;
                            for (Account accs : user.getAccounts()) {
                                if (accs.getIban().equals(ibanAcc)) {
                                    String cardNumber = Utils.generateCardNumber();
                                    Card card = new NormalCard(cardNumber, "active");
                                    accs.addCard(card);
                                    Transaction transaction = new NewCardTransaction(commandInput.getTimestamp(),
                                            "New card created",
                                            cardNumber, userEmail, ibanAcc);
                                    user.addTransaction(transaction);
                                    success = 1;
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    if (success == 0) {
                        Transaction transaction = new Transaction(commandInput.getTimestamp(),
                                "The account doesn't belong to the user");
                        userFound.addTransaction(transaction);
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
                                    Transaction transaction = new NewCardTransaction(commandInput.getTimestamp(),
                                            "New card created",
                                            cardNumber, userEmailOneTime, ibanAccOnetime);
                                    user.addTransaction(transaction);
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
                                    JsonOutput.deleteAccount(commandInput, objectMapper, output);
                                    break;
                                } else {
                                    JsonOutput.deleteAccountError(commandInput, objectMapper, output);
                                }
                            }
                        }
                    }
                    break;
                case "deleteCard":
                    String cardNumber = commandInput.getCardNumber();
                    for (User user : infoBank.getUsers()) {
                        for (Account account : user.getAccounts()) {
                            for (Card card : account.getCards()) {
                                if (card.getCardNumber().equals(cardNumber)) {
                                    account.deleteCard(card);
                                    Transaction transaction = new DeleteCardTransaction(commandInput.getTimestamp(),
                                            "The card has been destroyed", cardNumber, user.getEmail(), account.getIban());
                                    user.addTransaction(transaction);
                                    break;
                                }
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
                    int cardFound = 0;
                    for (User user : infoBank.getUsers()) {
                        for (Account account : user.getAccounts()) {
                            for (Card card : account.getCards()) {
                                if (card.getCardNumber().equals(commandInput.getCardNumber())) {
                                    if (account.getBalance() <= account.getMinBalance()) {
                                        Transaction transaction = new CardStatusTransaction(commandInput.getTimestamp(),
                                        "You have reached the minimum amount of funds, the card will be frozen");
                                        user.addTransaction(transaction);
                                        card.setStatus("frozen");
                                    }
                                    cardFound = 1;
                                }
                            }
                        }
                    }
                    if (cardFound == 0) {
                        JsonOutput.errorCard(commandInput, objectMapper, output);
                    }
                    break;
                case "payOnline":
                    int enoughFunds = 0;
                    int successPay = 0;
                    String payOnlineEmail = commandInput.getEmail();
                    String payOnlineCard = commandInput.getCardNumber();
                    for (User user : infoBank.getUsers()) {
                        if (user.getEmail().equals(payOnlineEmail)) {
                            for (Account account : user.getAccounts()) {
                                for (Card card : account.getCards()) {
                                    if (card.getCardNumber().equals(payOnlineCard)) {
                                        if (card.getStatus().equals("frozen")) {
                                            Transaction transaction = new ErrorPaymentTransaction(commandInput.getTimestamp(),
                                                    "The card is frozen");
                                            user.addTransaction(transaction);
                                        }
                                        double amount = infoBank.exchange(commandInput.getCurrency(),
                                                account.getCurrency(), commandInput.getAmount());
                                        PayStrategy onlinePayment = new OnlinePayment();
                                        PaymentContext context = new PaymentContext(onlinePayment);
                                        if (account.getBalance() >= amount && card.getStatus().equals("active")) {
                                            enoughFunds = 1;
                                        }
                                        if (enoughFunds == 1 && card.getStatus().equals("active")) {
                                            context.executePayment(account, amount);
                                            if (card.getClass() == OneTimeCard.class) {
                                                card.setCardNumber(Utils.generateCardNumber());
                                            }
                                            Transaction transaction = new OnlinePayTransaction(commandInput.getTimestamp(),
                                                    "Card payment", amount, commandInput.getCommerciant());
                                            user.addTransaction(transaction);
                                        }
                                        if (enoughFunds == 0 && card.getStatus().equals("active")){
                                            Transaction transaction = new ErrorPaymentTransaction(commandInput.getTimestamp(),
                                                    "Insufficient funds");
                                            user.addTransaction(transaction);
                                        }
                                        successPay = 1;
                                    }
                                }
                            }
                        }
                    }
                    if (successPay == 0) {
                        JsonOutput.errorCard(commandInput, objectMapper, output);
                    }
                    break;
                case "sendMoney":
                    int senderFound = 0;
                    int receiverFound = 0;
                    String sender = commandInput.getAccount();
                    String receiver = commandInput.getReceiver();
                    String senderCurrency = new String();
                    String receiverCurrency = new String();
                    Account senderAccount = new Account(null, 0.0, null, null);
                    Account receiverAccount = new Account(null, 0.0, null, null);
                    User userSender = new User(null, null, null);
                    User userReceiver = new User(null, null, null);
                    for (User user : infoBank.getUsers()) {
                        for (Account account : user.getAccounts()) {
                            if (account.getIban().equals(sender)) {
                                senderCurrency = account.getCurrency();
                                senderAccount = account;
                                userSender = user;
                                senderFound = 1;
                            }
                            if (account.getIban().equals(receiver) || account.getIban().equals(infoBank.getHashMap().get(receiver))) {
                                receiverCurrency = account.getCurrency();
                                receiverAccount = account;
                                userReceiver = user;
                                receiverFound = 1;
                            }
                        }
                    }
                    if (senderFound == 0 || receiverFound == 0) {
                        break;
                    }
                    if (senderAccount.getBalance() < commandInput.getAmount()) {
                        Transaction transaction = new ErrorPaymentTransaction(commandInput.getTimestamp(),
                                "Insufficient funds");
                        userSender.addTransaction(transaction);
                        break;
                    }
                    double amount = infoBank.exchange(senderCurrency,
                            receiverCurrency, commandInput.getAmount());
                    PayStrategy bankTransfer = new BankTransfer(receiverAccount,
                            amount, senderCurrency, commandInput.getAmount());
                    PaymentContext context = new PaymentContext(bankTransfer);
                    context.executePayment(senderAccount, commandInput.getAmount());
                    Transaction transaction = new SendMoneyTransaction(commandInput.getTimestamp(),
                            commandInput.getDescription(),
                            sender, receiver, bankTransfer.toString(), "sent");
                    userSender.addTransaction(transaction);
                    break;
                case "setAlias":
                    for (User user : infoBank.getUsers()) {
                        if (user.getEmail().equals(commandInput.getEmail())) {
                            for (Account account : user.getAccounts()) {
                                if (account.getIban().equals(commandInput.getAccount())) {
                                    infoBank.setAlias(commandInput.getAlias(), commandInput.getAccount());
                                }
                            }
                        }
                    }
                    break;
                case "printTransactions":
                    String emailTransaction = commandInput.getEmail();
                    for (User user : infoBank.getUsers()) {
                        if (user.getEmail().equals(emailTransaction)) {
                            JsonOutput.printTransactions(commandInput, user,
                                    objectMapper, output);
                        }
                    }
                default:
                    break;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
