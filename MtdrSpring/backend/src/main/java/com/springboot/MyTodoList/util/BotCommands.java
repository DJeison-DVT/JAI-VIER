package com.springboot.MyTodoList.util;

public enum BotCommands {
	// checked
	START_COMMAND("/start"),
	TASK_LIST("/task"),

	// unchecked
	SUBTASK_LIST("/subtask"),
	HIDE_COMMAND("/hide"),
	PROJECT_LIST("/project"),
	USER_LIST("/user");

	private String command;

	BotCommands(String enumCommand) {
		this.command = enumCommand;
	}

	public String getCommand() {
		return command;
	}
}
