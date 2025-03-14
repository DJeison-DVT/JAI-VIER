package com.springboot.MyTodoList.messageModel;

import com.springboot.MyTodoList.controller.*;

public class MessageModelFactory {
    private final TaskController taskController;
    private final ProjectMemberController projectMemberController;
    private final ProjectController projectController;
    private final SubtaskController subtaskController;
    private final UserController userController;

    public MessageModelFactory(TaskController taskController, ProjectMemberController projectMemberController,
            ProjectController projectController, SubtaskController subtaskController, UserController userController) {
        this.taskController = taskController;
        this.projectMemberController = projectMemberController;
        this.projectController = projectController;
        this.subtaskController = subtaskController;
        this.userController = userController;
    }

    @SuppressWarnings("unchecked")
    public <T> MessageModel<T> getMessageModel(String type) {
        switch (type.toLowerCase()) {
            case "task":
                return (MessageModel<T>) new TaskMessageModel(taskController, projectMemberController,
                        projectController);
            case "project":
                return (MessageModel<T>) new ProjectMessageModel(projectController, projectMemberController);
            case "user":
                return (MessageModel<T>) new UserMessageModel(userController, projectMemberController,
                        projectController);
            case "subtask":
                return (MessageModel<T>) new SubtaskMessageModel(subtaskController, projectMemberController,
                        projectController);
            default:
                throw new IllegalArgumentException("Unsupported model type: " + type);
        }
    }
}
