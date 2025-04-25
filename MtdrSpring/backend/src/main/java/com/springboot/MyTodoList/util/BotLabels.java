package com.springboot.MyTodoList.util;

public enum BotLabels {
	// checked
	MENU_SCREEN("Menu"),
	// task related
	DONE("DONE"),
	UNDO("UNDO"),
	DELETE("DELETE"),
	INFO("INFO"),
	LIST_ALL_SUBTASKS("LIST"),
	// subtask related
	CHECK("CHECK"),
	UNCHECK("UNCHECK"),
	DELETE_SUBTASK("DELETE_SUB"),
	// asignee related
	ASIGNEE("ASIGN"),

	// unchecked
	HIDE_MAIN_SCREEN("Hide Main Screen"),
	LIST_ALL_TASKS("List All Tasks"),
	ADD_NEW_TASK("Add New Item"),
	ASIGN_USER_TO_TASK("Add Asignee"),
	DASH("-");

	private String label;

	BotLabels(String enumLabel) {
		this.label = enumLabel;
	}

	public String getLabel() {
		return label;
	}

}
