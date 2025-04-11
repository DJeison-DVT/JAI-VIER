package com.springboot.MyTodoList.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {
    private String action;
    private String type;
    private Map<String, String> fields;

    private static final Pattern FIELD_PATTERN = Pattern.compile("--(\\w+)=\"([^\"]*)\"");

    public CommandParser(String message) {
        fields = new HashMap<>();
        parseMessage(message);
    }

    private void parseMessage(String message) {
        String[] parts = message.split(" ", 3); // Splits into {action, type, fields}
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid command format. Use: /{action} {type} {fields}");
        }

        this.action = parts[0].substring(1); // Remove leading "/"
        this.type = parts[1];

        if (parts.length == 3) {
            parseFields(parts[2]); // Extract key-value pairs
        }
    }

    private void parseFields(String fieldString) {
        Matcher matcher = FIELD_PATTERN.matcher(fieldString);
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            fields.put(key, value);
        }
    }

    public String getAction() {
        return action;
    }

    public String getType() {
        return type;
    }

    public String getField(String key) {
        return fields.getOrDefault(key, null);
    }

    public boolean hasField(String key) {
        return fields.containsKey(key);
    }

    public Map<String, String> getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return "CommandParser{" +
                "action='" + action + '\'' +
                ", type='" + type + '\'' +
                ", fields=" + fields +
                '}';
    }
}
