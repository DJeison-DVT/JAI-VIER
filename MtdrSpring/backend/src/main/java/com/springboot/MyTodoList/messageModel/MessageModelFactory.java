package com.springboot.MyTodoList.messageModel;

import com.springboot.MyTodoList.controller.*;

public class MessageModelFactory {
    private final TaskController taskController;
    private final SprintController sprintController;
    private final SubtaskController subtaskController;

    public MessageModelFactory(TaskController taskController,
            SubtaskController subtaskController, SprintController sprintController) {
        this.taskController = taskController;
        this.subtaskController = subtaskController;
        this.sprintController = sprintController;
    }

    @SuppressWarnings("unchecked")
    public <T> MessageModel<T> getMessageModel(String type, String token) {
        switch (type.toLowerCase()) {
            case "task":
                return (MessageModel<T>) new TaskMessageModel(taskController, sprintController, token);
            case "subtask":
                return (MessageModel<T>) new SubtaskMessageModel(subtaskController, sprintController, token);
            default:
                throw new IllegalArgumentException("Unsupported model type: " + type);
        }
    }
}
