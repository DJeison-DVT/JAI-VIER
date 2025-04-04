package com.springboot.MyTodoList.util;

public enum BotMessages {
	PHONE_NOT_REGISTERED(
			"Your phone number is not registered! Please tell your manager to register you!"),
	BOT_WELCOME(
			"👋 Hello! I'm JAI-VIERbot! 🤖\n\n"
					+ "You can create a Task or Subtask using the `/create` command:\n\n"
					+ "📌 Create a Task: \n"
					+ "/create task --name=\"Undo project documentation\" --description=\"Document all API endpoints and database schema\" --due_date=\"2025-03-20\" --priority=\"2\" --status=\"0\" --estimated_hours=\"5\" --project_id=\"1\" \n"
					+ "🚀 Press the send button (blue arrow) once you type your task or select an option below!"),
	BOT_REGISTERED_STARTED("Bot registered and started succesfully!"),
	REPORT_INSTRUCTIONS(
			"To generate a report use the command /report {task, subtask, project, user} and an optional id, if you want a specific or all available data."
					+ "📊 Report on Tasks: \n"
					+ "`/report task --id=task_id`\n"
					+ "`/report subtask`"),
	UPDATE_INSTRUCTIONS(
			"To update a task or subtask use the command /update followed by the task or subtask id and the fields you want to update.\n\n"
					+ "📌 Update a Task: \n"
					+ "`/update task --id=task_id --name=\"Task name\" --description=\"Task description\" --dueDate=\"yyyy-MM-dd\"`\n"
					+ "`--priority=0-3` (higher is more important)\n"
					+ "`--status=0-3` (0: In Progress, 3: Done)\n"
					+ "`--estimated_hours=hours` (optional)\n"
					+ "`--project_id=project_id`\n\n"
					+ "📎 Update a Subtask: \n"
					+ "`/update subtask --id=subtask_id --name=\"Subtask name\" --description=\"Subtask description\"`\n"
					+ "`--status=0-3` (0: In Progress, 3: Done)\n"
					+ "`--task_id=task_id`\n\n"
					+ "🚀 Press the send button (blue arrow) once you type your task or select an option below!"),
	DELETE_INSTRUCTIONS(
			"To delete a task or subtask use the command /delete {task or subtask} id."),
	TASK_DONE("task done! Select /task to return to the list of todo tasks, or /start to go to the main screen."),
	TASK_UNDONE(
			"task undone! Select /task to return to the list of todo tasks, or /start to go to the main screen."),
	TASK_DELETED(
			"task deleted! Select /task to return to the list of todo tasks, or /start to go to the main screen."),
	NEW_TASK_ADDED(
			"New task added! Select /task to return to the list of todo tasks, or /start to go to the main screen."),
	SUBTASK_DONE(
			"subtask done! Select /subtasklist to return to the list of todo subtasks, or /start to go to the main screen."),
	SUBTASK_UNDONE(
			"subtask undone! Select /subtasklist to return to the list of todo subtasks, or /start to go to the main screen."),
	SUBTASK_DELETED(
			"subtask deleted! Select /subtasklist to return to the list of todo subtasks, or /start to go to the main screen."),
	NEW_SUBTASK_ADDED(
			"New subtask added! Select /subtasklist to return to the list of todo subtasks, or /start to go to the main screen."),
	INVALID_COMMAND("Invalid command! Please try again!"),
	SUCCESFUL_COMMAND("Command executed!"),
	BYE("Bye! Select /start to resume!");

	private String message;

	BotMessages(String enumMessage) {
		this.message = enumMessage;
	}

	public String getMessage() {
		return message;
	}

}
