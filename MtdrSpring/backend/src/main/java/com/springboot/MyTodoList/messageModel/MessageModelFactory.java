package com.springboot.MyTodoList.messageModel;

import org.springframework.stereotype.Component;

import com.springboot.MyTodoList.controller.*;
import com.springboot.MyTodoList.model.*;

@Component
public class MessageModelFactory {
    private final ProjectController projectController;
    private final TaskController taskController;
    private final SubtaskController subtaskController;
    private final UserController userController;

    public MessageModelFactory(ProjectController projectController, TaskController taskController,
            SubtaskController subtaskController, UserController userController,
            ProjectMemberController projectMemberController) {
        this.projectController = projectController;
        this.taskController = taskController;
        this.subtaskController = subtaskController;
        this.userController = userController;
    }

    @SuppressWarnings("unchecked") // Safe cast
    public <T> MessageModel<T> getModel(Class<T> type) {
        if (type == Project.class) {
            return (MessageModel<T>) new ProjectMessageModel(projectController);
        } else if (type == Task.class) {
            return (MessageModel<T>) new TaskMessageModel(taskController);
        } else if (type == Subtask.class) {
            return (MessageModel<T>) new SubtaskMessageModel(subtaskController);
        } else if (type == User.class) {
            return (MessageModel<T>) new UserMessageModel(userController);
        } else {
            throw new IllegalArgumentException("Unsupported model type: " + type.getSimpleName());
        }
    }
}
