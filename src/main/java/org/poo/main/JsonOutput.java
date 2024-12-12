package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

public abstract class JsonOutput {
    /**
     * Printeaza utilizatorii.
     *
     */
    public static void printUsers(final InfoBank infoBank, final CommandInput commandInput,
                                  final ObjectMapper objectMapper, final ArrayNode output) {
        ObjectNode commandNode = objectMapper.createObjectNode();
        commandNode.put("command", "printUsers");

        ArrayNode usersArray = objectMapper.createArrayNode();
        for (User user : infoBank.getUsers()) {
            usersArray.add(objectMapper.valueToTree(user));
        }

        commandNode.set("output", usersArray);
        commandNode.put("timestamp", commandInput.getTimestamp());
        output.add(commandNode);
    }
    /**
     * Sterge contul unui utilizator.
     *
     */
    public static void deleteAccount(final CommandInput commandInput,
                                     final ObjectMapper objectMapper,
                                     final ArrayNode output) {
        ObjectNode commandNode = objectMapper.createObjectNode();
        commandNode.put("command", "deleteAccount");
        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("success", "Account deleted");
        outputNode.put("timestamp", commandInput.getTimestamp());

        commandNode.set("output", outputNode);
        commandNode.put("timestamp", commandInput.getTimestamp());
        output.add(commandNode);
    }
    /**
     * Afiseaza eroare la plata online cu cardul.
     *
     */
    public static void errorCard(final CommandInput commandInput,
                                          final ObjectMapper objectMapper, final ArrayNode output) {
        ObjectNode commandNode = objectMapper.createObjectNode();
        commandNode.put("command", commandInput.getCommand());
        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("timestamp", commandInput.getTimestamp());
        outputNode.put("description", "Card not found");

        commandNode.set("output", outputNode);
        commandNode.put("timestamp", commandInput.getTimestamp());
        output.add(commandNode);
    }
    /**
     * Printeaza tranzactiile unui utilizator.
     *
     */
    public static void printTransactions(final CommandInput commandInput, final User user,
                                         final ObjectMapper objectMapper, final ArrayNode output) {
        ObjectNode commandNode = objectMapper.createObjectNode();
        commandNode.put("command", "printTransactions");

        ArrayNode transactionsArray = objectMapper.createArrayNode();
        for (Transaction transaction : user.getTransactions()) {
            transactionsArray.add(objectMapper.valueToTree(transaction));
        }

        commandNode.set("output", transactionsArray);
        commandNode.put("timestamp", commandInput.getTimestamp());
        output.add(commandNode);
    }
    public static void deleteAccountError(CommandInput commandInput, ObjectMapper objectMapper, ArrayNode output) {
        ObjectNode commandNode = objectMapper.createObjectNode();
        commandNode.put("command", "deleteAccount");
        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("error", "Account couldn't be deleted - see org.poo.transactions for details");
        outputNode.put("timestamp", commandInput.getTimestamp());

        commandNode.set("output", outputNode);
        commandNode.put("timestamp", commandInput.getTimestamp());
        output.add(commandNode);
    }
    public static void printClassicReport(CommandInput commandInput, Account account, ObjectMapper objectMapper, ArrayNode output) {
        ObjectNode commandNode = objectMapper.createObjectNode();
        commandNode.put("command", "report");

        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("IBAN", account.getIban());
        outputNode.put("balance", account.getBalance());
        outputNode.put("currency", account.getCurrency());

        ArrayNode transactionsArray = objectMapper.createArrayNode();
        for (Transaction transaction : account.getTransactions()) {
            if (transaction.getTimestamp() >= commandInput.getStartTimestamp()
                    && transaction.getTimestamp() <= commandInput.getEndTimestamp()) {
                transactionsArray.add(objectMapper.valueToTree(transaction));
            }
        }

        outputNode.set("transactions", transactionsArray);

        commandNode.set("output", outputNode);
        commandNode.put("timestamp", commandInput.getTimestamp());

        output.add(commandNode);
    }
    public static void interestRateError(CommandInput commandInput, ObjectMapper objectMapper, ArrayNode output) {
        ObjectNode commandNode = objectMapper.createObjectNode();
        commandNode.put("command", commandInput.getCommand());
        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("timestamp", commandInput.getTimestamp());
        outputNode.put("description", "This is not a savings account");

        commandNode.set("output", outputNode);
        commandNode.put("timestamp", commandInput.getTimestamp());
        output.add(commandNode);
    }
    public static void printSpendingsReport(CommandInput commandInput, Account account, ObjectMapper objectMapper, ArrayNode output) {
        ObjectNode commandNode = objectMapper.createObjectNode();
        commandNode.put("command", "spendingsReport");

        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("IBAN", account.getIban());
        outputNode.put("balance", account.getBalance());
        outputNode.put("currency", account.getCurrency());

        ArrayNode transactionsArray = objectMapper.createArrayNode();
        ArrayNode commerciantsArray = objectMapper.createArrayNode();
        for (Transaction transaction : account.getTransactions()) {
            if (transaction.getDescription().equals("Card payment")
                    && transaction.getTimestamp() >= commandInput.getStartTimestamp()
                    && transaction.getTimestamp() <= commandInput.getEndTimestamp()) {
                transactionsArray.add(objectMapper.valueToTree(transaction));
            }
        }
        for (Commerciant commerciant : account.getCommerciants()) {
            if (commerciant.getPayOnlineTimestamp() >= commandInput.getStartTimestamp()
                    && commerciant.getPayOnlineTimestamp() <= commandInput.getEndTimestamp()) {
                commerciantsArray.add(objectMapper.valueToTree(commerciant));
            }
        }
        outputNode.set("transactions", transactionsArray);
        outputNode.set("commerciants", commerciantsArray);

        commandNode.set("output", outputNode);
        commandNode.put("timestamp", commandInput.getTimestamp());

        output.add(commandNode);
        for (Commerciant commerciant : account.getCommerciants()) {
            if (commerciant.getPayOnlineTimestamp() >= commandInput.getStartTimestamp()
                    && commerciant.getPayOnlineTimestamp() <= commandInput.getEndTimestamp()) {
                commerciant.setAmount(0.0);
            }
        }
    }
    public static void errorAccount(CommandInput commandInput, ObjectMapper objectMapper, ArrayNode output) {
        ObjectNode commandNode = objectMapper.createObjectNode();
        commandNode.put("command", commandInput.getCommand());
        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("timestamp", commandInput.getTimestamp());
        outputNode.put("description", "Account not found");

        commandNode.set("output", outputNode);
        commandNode.put("timestamp", commandInput.getTimestamp());
        output.add(commandNode);
    }
}
