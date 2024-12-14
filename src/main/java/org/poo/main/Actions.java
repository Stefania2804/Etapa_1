package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.poo.account.Account;
import org.poo.account.Classic;
import org.poo.account.Savings;
import org.poo.account.card.Card;
import org.poo.account.card.NormalCard;
import org.poo.account.card.OneTimeCard;
import org.poo.errorTransactions.DeleteAccountErrorTransaction;
import org.poo.errorTransactions.ErrorPaymentTransaction;
import org.poo.errorTransactions.ErrorSplitPaymentTransaction;
import org.poo.fileio.CommandInput;
import org.poo.transactions.*;
import org.poo.utils.Utils;
import java.util.Locale;

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
                            Transaction transaction = new NewAccTransaction(
                                    commandInput.getTimestamp(),
                                    "New account created");
                            user.addTransaction(transaction);
                            String currency = commandInput.getCurrency();
                            String type = commandInput.getAccountType();
                            String iban = Utils.generateIBAN();
                            switch (type) {
                                case "classic":
                                    Account classic = new Classic(iban, 0.0, currency, type);
                                    user.addClassic((Classic) classic);
                                    user.addAccounts(classic);
                                    infoBank.addAccount(classic);
                                    classic.addTransaction(transaction);
                                    break;
                                case "savings":
                                    Account savings = new Savings(iban, 0.0, currency, type,
                                            commandInput.getInterestRate());
                                    user.addSavings((Savings) savings);
                                    user.addAccounts(savings);
                                    infoBank.addAccount(savings);
                                    savings.addTransaction(transaction);
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
                                    Transaction transaction = new
                                            NewCardTransaction(commandInput.getTimestamp(),
                                            "New card created",
                                            cardNumber, userEmail, ibanAcc);
                                    user.addTransaction(transaction);
                                    accs.addTransaction(transaction);
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
                                    Transaction transaction = new
                                            NewCardTransaction(commandInput.getTimestamp(),
                                            "New card created",
                                            cardNumber, userEmailOneTime, ibanAccOnetime);
                                    user.addTransaction(transaction);
                                    accs.addTransaction(transaction);
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
                                    Transaction transaction = new
                                            DeleteAccountErrorTransaction(commandInput.getTimestamp(),
                                            "Account couldn't be deleted - "
                                                    + "there are funds remaining");
                                    user.addTransaction(transaction);
                                    accs.addTransaction(transaction);
                                    JsonOutput.deleteAccountError(commandInput, objectMapper, output);
                                    break;
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
                                            "The card has been destroyed",
                                            cardNumber, user.getEmail(), account.getIban());
                                    user.addTransaction(transaction);
                                    account.addTransaction(transaction);
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
                    break;
                case "checkCardStatus":
                    int cardFound = 0;
                    for (User user : infoBank.getUsers()) {
                        for (Account account : user.getAccounts()) {
                            for (Card card : account.getCards()) {
                                if (card.getCardNumber().equals(commandInput.getCardNumber())) {
                                    if (account.getBalance() <= account.getMinBalance()) {
                                        Transaction transaction = new CardStatusTransaction(
                                                commandInput.getTimestamp(),
                                        "You have reached the minimum amount of "
                                                + "funds, the card will be frozen");
                                        user.addTransaction(transaction);
                                        card.setStatus("frozen");
                                        account.addTransaction(transaction);
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
                                int comFound = 0;
                                for (Card card : account.getCards()) {
                                    if (card.getCardNumber().equals(payOnlineCard)) {
                                        if (card.getStatus().equals("frozen")) {
                                            Transaction transaction = new ErrorPaymentTransaction(
                                                    commandInput.getTimestamp(),
                                                    "The card is frozen");
                                            user.addTransaction(transaction);
                                            account.addTransaction(transaction);
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
                                            Transaction transaction = new OnlinePayTransaction(commandInput.getTimestamp(),
                                                    "Card payment", amount, commandInput.getCommerciant());
                                            user.addTransaction(transaction);
                                            account.addTransaction(transaction);
                                            if (card.getClass() == OneTimeCard.class) {
                                                Transaction deleteTransaction = new DeleteCardTransaction(commandInput.getTimestamp(),
                                                        "The card has been destroyed", card.getCardNumber(), user.getEmail(),
                                                        account.getIban());
                                                user.addTransaction(deleteTransaction);
                                                account.addTransaction(deleteTransaction);
                                                card.setCardNumber(Utils.generateCardNumber());
                                                Transaction createTransaction = new NewCardTransaction(commandInput.getTimestamp(),
                                                        "New card created",
                                                        card.getCardNumber(), user.getEmail(), account.getIban());
                                                user.addTransaction(createTransaction);
                                                account.addTransaction(createTransaction);
                                            }
                                            for (Commerciant commerciant : account.getCommerciants()) {
                                                if (commerciant.getName().equals(commandInput.getCommerciant())) {
                                                    comFound = 1;
                                                }
                                            }
                                            if (comFound == 0) {
                                                Commerciant commerciant = new Commerciant(0.0,
                                                        commandInput.getCommerciant(), commandInput.getTimestamp());
                                                account.addCommerciant(commerciant);
                                            }
                                        }
                                        if (enoughFunds == 0 && card.getStatus().equals("active")) {
                                            Transaction transaction = new ErrorPaymentTransaction(commandInput.getTimestamp(),
                                                    "Insufficient funds");
                                            user.addTransaction(transaction);
                                            account.addTransaction(transaction);
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
                            if (account.getIban().equals(receiver)
                                    || account.getIban().equals(infoBank.getHashMap().get(receiver))) {
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
                        senderAccount.addTransaction(transaction);
                        break;
                    }
                    double amount = infoBank.exchange(senderCurrency,
                            receiverCurrency, commandInput.getAmount());
                    PayStrategy bankTransfer = new BankTransfer(receiverAccount,
                            amount);
                    PaymentContext context = new PaymentContext(bankTransfer);
                    context.executePayment(senderAccount, commandInput.getAmount());
                    Transaction transactionSent = new SendMoneyTransaction(commandInput.getTimestamp(),
                            commandInput.getDescription(),
                            sender, receiver, sendMoneyToString(commandInput.getAmount(),
                            senderAccount.getCurrency()), "sent");
                    Transaction transactionReceived = new SendMoneyTransaction(commandInput.getTimestamp(),
                            commandInput.getDescription(),
                            sender, receiver, sendMoneyToString(amount,
                            receiverAccount.getCurrency()),
                            "received");
                    userSender.addTransaction(transactionSent);
                    senderAccount.addTransaction(transactionSent);
                    userReceiver.addTransaction(transactionReceived);
                    receiverAccount.addTransaction(transactionReceived);
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
                    break;
                case "splitPayment":
                    int cnt = 0;
                    double sum = commandInput.getAmount();
                    int lenth = 0;
                    String cardError = null;

                    if (commandInput.getAccounts() != null) {
                        lenth = commandInput.getAccounts().size();
                    }
                    double sumPerMember = sum / lenth;
                    for (String ibanAccount : commandInput.getAccounts()) {
                        for (Account account : infoBank.getAccounts()) {
                            if (account.getIban().equals(ibanAccount)) {
                                double sumPerMemberExchanged = infoBank.exchange(commandInput.getCurrency(),
                                        account.getCurrency(), sumPerMember);
                                if (account.getBalance() >= sumPerMemberExchanged) {
                                    cnt++;
                                } else {
                                    cardError = account.getIban();
                                }
                            }
                        }
                    }
                    if (cnt == lenth) {
                        for (User user : infoBank.getUsers()) {
                            for (Account account : user.getAccounts()) {
                                for (String ibanAccSplit : commandInput.getAccounts()) {
                                    if (account.getIban().equals(ibanAccSplit)) {
                                        double sumPerMemberExchanged = infoBank.exchange(commandInput.getCurrency(),
                                                account.getCurrency(), sumPerMember);
                                        account.setBalance(account.getBalance() - sumPerMemberExchanged);
                                        String formattedValue = String.format(Locale.US, "%.2f", sum);
                                        String description = splitPaymentToString(formattedValue, commandInput.getCurrency());
                                        Transaction transactionSplit = new SplitPaymentTransaction(commandInput.getTimestamp(),
                                                description, commandInput.getCurrency(), sumPerMember, commandInput.getAccounts());
                                        user.addTransaction(transactionSplit);
                                        account.addTransaction(transactionSplit);
                                    }
                                }
                            }
                        }
                    } else {
                        for (User user : infoBank.getUsers()) {
                            for (Account account : user.getAccounts()) {
                                for (String ibanAccSplit : commandInput.getAccounts()) {
                                    if (account.getIban().equals(ibanAccSplit)) {
                                        String formattedValue = String.format(Locale.US, "%.2f", sum);
                                        String description = splitPaymentToString(formattedValue, commandInput.getCurrency());
                                        String error = errorSplitPayment(cardError);
                                        Transaction transactionErrorSplit = new ErrorSplitPaymentTransaction(
                                                commandInput.getTimestamp(), description,
                                                commandInput.getCurrency(), sumPerMember,
                                                commandInput.getAccounts(), error);
                                        user.addTransaction(transactionErrorSplit);
                                        account.addTransaction(transactionErrorSplit);
                                    }
                                }
                            }
                        }
                    }
                    break;
                case "addInterest":
                    for (Account account : infoBank.getAccounts()) {
                        if (account.getIban().equals(commandInput.getAccount())) {
                            if (account.getClass() == Savings.class) {
                                account.setBalance(account.getBalance() + account.getBalance()
                                        * ((Savings) account).getInterestRate());
                            } else {
                                JsonOutput.interestRateError(commandInput, objectMapper, output);
                            }
                        }
                    }
                    break;
                case "changeInterestRate":
                    for (User user : infoBank.getUsers()) {
                        for (Account account : user.getAccounts()) {
                            if (account.getIban().equals(commandInput.getAccount())) {
                                if (account.getClass() == Savings.class) {
                                    ((Savings) account).setInterestRate(commandInput.getInterestRate());
                                    String description = changeToString(commandInput.getInterestRate());
                                    Transaction transaction = new ChangeIrTransaction(
                                            commandInput.getTimestamp(), description);
                                    user.addTransaction(transaction);
                                    account.addTransaction(transaction);
                                } else {
                                    JsonOutput.interestRateError(commandInput, objectMapper, output);
                                }
                            }
                        }
                    }
                    break;
                case "report":
                    int failure = 1;
                    for (Account account : infoBank.getAccounts()) {
                        if (account.getIban().equals(commandInput.getAccount())) {
                            failure = 0;
                            JsonOutput.printClassicReport(commandInput, account, objectMapper, output);
                        }
                    }
                    if (failure == 1) {
                        JsonOutput.errorAccount(commandInput, objectMapper, output);
                    }
                    break;
                case "spendingsReport":
                    int found = 0;
                    int savings = 0;
                    Account accountFound = new Account(null, 0.0, null, null);
                    for (Account account : infoBank.getAccounts()) {
                        if (account.getIban().equals(commandInput.getAccount())) {
                            found = 1;
                            accountFound = account;
                            if (account.getClass() == Savings.class) {
                                JsonOutput.errorSpendings(commandInput, objectMapper, output);
                                savings = 1;
                                break;
                            }
                        }
                    }
                    if (found == 0) {
                        JsonOutput.errorAccount(commandInput, objectMapper, output);
                        break;
                    }
                    if (savings == 0) {
                        for (Transaction transaction : accountFound.getTransactions()) {
                            if (transaction.getDescription().equals("Card payment")) {
                                if (transaction.getTimestamp() >= commandInput.getStartTimestamp()
                                        && transaction.getTimestamp() <= commandInput.getEndTimestamp()) {
                                    for (Commerciant commerciant : accountFound.getCommerciants()) {
                                        if (commerciant.getName().equals(((OnlinePayTransaction) transaction).getCommerciant())) {
                                            commerciant.setAmount(commerciant.getAmount() + ((OnlinePayTransaction) transaction).getAmount());
                                        }
                                    }
                                }
                            }
                        }
                        for (Account account : infoBank.getAccounts()) {
                            if (account.getIban().equals(commandInput.getAccount())) {
                                account.getCommerciants().sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
                                JsonOutput.printSpendingsReport(commandInput, account, objectMapper, output);
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    public static String splitPaymentToString(final String amount,
                                              final String currency) {
        return "Split payment of " + amount + " " + currency;
    }
    public static String sendMoneyToString(final double amount,
                                           final String currency) {
        return amount + " " + currency;
    }
    public static String errorSplitPayment(final String cardNumber) {
        return "Account " + cardNumber + " has insufficient funds for a split payment.";
    }
    public static String changeToString(final double rate) {
        return "Interest rate of the account changed to " + rate;
    }
}
