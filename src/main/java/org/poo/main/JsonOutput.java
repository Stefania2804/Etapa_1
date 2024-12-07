package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

public class JsonOutput {
    public static void printUsers(InfoBank infoBank, CommandInput commandInput, ObjectMapper objectMapper, ArrayNode output) {
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
    public static void deleteAccount(CommandInput commandInput, ObjectMapper objectMapper, ArrayNode output) {
        ObjectNode commandNode = objectMapper.createObjectNode();
        commandNode.put("command", "deleteAccount");
        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("success", "Account deleted");
        outputNode.put("timestamp", commandInput.getTimestamp());

        commandNode.set("output", outputNode);
        commandNode.put("timestamp", commandInput.getTimestamp());
        output.add(commandNode);
    }
    public static void errorOnlinePayment(CommandInput commandInput, ObjectMapper objectMapper, ArrayNode output) {
        ObjectNode commandNode = objectMapper.createObjectNode();
        commandNode.put("command", "payOnline");
        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("timestamp", commandInput.getTimestamp());
        outputNode.put("description", "Card not found");

        commandNode.set("output", outputNode);
        commandNode.put("timestamp", commandInput.getTimestamp());
        output.add(commandNode);
    }
}
