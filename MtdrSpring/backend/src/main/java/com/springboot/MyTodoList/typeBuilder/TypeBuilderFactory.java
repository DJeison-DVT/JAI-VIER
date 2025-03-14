package com.springboot.MyTodoList.typeBuilder;

public class TypeBuilderFactory {
    public static TypeBuilder<?> getBuilder(String type) {
        switch (type.toLowerCase()) {
            case "task":
                return new TaskBuilder();
            case "project":
                return new ProjectBuilder();
            case "user":
                return new UserBuilder();
            case "subtask":
                return new SubtaskBuilder();
            default:
                throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }
}
