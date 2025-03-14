package com.springboot.MyTodoList.util;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class CommandParserTest {

    @Test
    void testSingleArgument() {
        String input = "/create user --name=\"John Doe\"";
        CommandParser parser = new CommandParser(input);

        Map<String, String> result = parser.getFields();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get("name"));
    }

    @Test
    void testValidCommandParsing() {
        String command = "/create task --title=\"Buy groceries\" --priority=\"high\"";
        CommandParser parser = new CommandParser(command);

        assertEquals("create", parser.getAction());
        assertEquals("task", parser.getType());
        assertEquals("Buy groceries", parser.getField("title"));
        assertEquals("high", parser.getField("priority"));
        assertTrue(parser.hasField("title"));
        assertTrue(parser.hasField("priority"));
        assertFalse(parser.hasField("deadline"));
    }

    @Test
    void testNoFieldsCommand() {
        String command = "/delete task";
        CommandParser parser = new CommandParser(command);

        assertEquals("delete", parser.getAction());
        assertEquals("task", parser.getType());
        assertTrue(parser.getFields().isEmpty());
    }

    @Test
    void testEmptyFieldValue() {
        String command = "/update task --title=\"\"";
        CommandParser parser = new CommandParser(command);

        assertEquals("update", parser.getAction());
        assertEquals("task", parser.getType());
        assertTrue(parser.hasField("title"));
        assertEquals("", parser.getField("title"));
    }

    @Test
    void testInvalidCommandFormat() {
        String invalidCommand = "/invalidCommand";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CommandParser(invalidCommand);
        });

        assertEquals("Invalid command format. Use: /{action} {type} {fields}", exception.getMessage());
    }

}
