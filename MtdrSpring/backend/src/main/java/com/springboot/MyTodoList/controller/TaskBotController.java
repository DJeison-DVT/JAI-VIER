package com.springboot.MyTodoList.controller;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.springboot.MyTodoList.messageModel.MessageModel;
import com.springboot.MyTodoList.messageModel.MessageModelFactory;
import com.springboot.MyTodoList.messageModel.TaskMessageModel;
import com.springboot.MyTodoList.model.Subtask;
import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.service.SubtaskService;
import com.springboot.MyTodoList.service.TaskService;
import com.springboot.MyTodoList.service.UserService;
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.util.CommandParser;
import com.springboot.MyTodoList.typeBuilder.TypeBuilder;
import com.springboot.MyTodoList.typeBuilder.TypeBuilderFactory;

public class TaskBotController extends TelegramLongPollingBot {

	private static final Logger logger = LoggerFactory.getLogger(TaskBotController.class);
	private TaskService taskService;
	private SubtaskService subtaskService;
	private UserService userService;
	private String botName;

	private TaskController taskController;
	private SubtaskController subtaskController;
	private UserController userController;
	private ProjectController projectController;
	private ProjectMemberController projectMemberController;

	public TaskBotController(String botToken, String botName, TaskService taskService, SubtaskService subtaskService,
			UserService userService, TaskController taskController, SubtaskController subtaskController,
			UserController userController, ProjectController projectController,
			ProjectMemberController projectMemberController) {
		super(botToken);
		logger.info("Bot Token: " + botToken);
		logger.info("Bot name: " + botName);
		this.taskService = taskService;
		this.subtaskService = subtaskService;
		this.userService = userService;
		this.botName = botName;

		this.taskController = taskController;
		this.subtaskController = subtaskController;
		this.userController = userController;
		this.projectController = projectController;
		this.projectMemberController = projectMemberController;
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (!update.hasMessage() || !update.getMessage().hasText()) {
			return;
		}

		// Obtain message text, and chatId
		String messageTextFromTelegram = update.getMessage().getText();
		long chatId = update.getMessage().getChatId();

		if (messageTextFromTelegram.equals(BotLabels.HIDE_MAIN_SCREEN.getLabel())
				|| messageTextFromTelegram.equals(BotCommands.HIDE_COMMAND.getCommand())) {
			SendMessage messageToTelegram = new SendMessage();
			messageToTelegram.setChatId(chatId);
			messageToTelegram.setText(BotMessages.BYE.getMessage());
			try {
				execute(messageToTelegram);
			} catch (TelegramApiException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		} else if (messageTextFromTelegram.equals(BotCommands.START_COMMAND.getCommand())
				|| messageTextFromTelegram.equals(BotLabels.MENU_SCREEN.getLabel())
				|| messageTextFromTelegram.equals(BotLabels.ADD_NEW_TASK.getLabel())) {

			SendMessage messageToTelegram = new SendMessage();
			messageToTelegram.setChatId(chatId);
			messageToTelegram.setText(BotMessages.BOT_WELCOME.getMessage());

			ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
			List<KeyboardRow> keyboard = new ArrayList<>();

			// first row
			KeyboardRow row = new KeyboardRow();
			row.add(BotLabels.LIST_ALL_TASKS.getLabel());
			row.add(BotLabels.ADD_NEW_TASK.getLabel());
			// Add the first row to the keyboard
			keyboard.add(row);

			// second row
			row = new KeyboardRow();
			row.add(BotLabels.HIDE_MAIN_SCREEN.getLabel());
			keyboard.add(row);

			// Set the keyboard
			keyboardMarkup.setKeyboard(keyboard);

			// Add the keyboard markup
			messageToTelegram.setReplyMarkup(keyboardMarkup);

			try {
				execute(messageToTelegram);
			} catch (TelegramApiException e) {
				logger.error(e.getLocalizedMessage(), e);
			}

		} else if (messageTextFromTelegram.indexOf(BotLabels.DONE.getLabel()) != -1) {

			String done = messageTextFromTelegram.substring(0,
					messageTextFromTelegram.indexOf(BotLabels.DASH.getLabel()));
			Integer id = Integer.valueOf(done);

			try {

				Task item = getTaskById(id).getBody();
				item.setStatus(3);
				updateTask(item, id);
				BotHelper.sendMessageToTelegram(chatId, BotMessages.TASK_DONE.getMessage(), this);
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}

		} else if (messageTextFromTelegram.indexOf(BotLabels.UNDO.getLabel()) != -1) {

			String undo = messageTextFromTelegram.substring(0,
					messageTextFromTelegram.indexOf(BotLabels.DASH.getLabel()));
			Integer id = Integer.valueOf(undo);

			try {
				Task item = getTaskById(id).getBody();
				item.setStatus(0);
				updateTask(item, id);
				BotHelper.sendMessageToTelegram(chatId, BotMessages.TASK_UNDONE.getMessage(), this);

			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		} else if (messageTextFromTelegram.indexOf(BotLabels.DELETE_SUBTASK.getLabel()) != -1) {

			String delete = messageTextFromTelegram.substring(0,
					messageTextFromTelegram.indexOf(BotLabels.DASH.getLabel()));
			Integer id = Integer.valueOf(delete);

			try {

				deleteSubtask(id);
				BotHelper.sendMessageToTelegram(chatId, BotMessages.SUBTASK_DELETED.getMessage(), this);

			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}

		} else if (messageTextFromTelegram.indexOf(BotLabels.DELETE.getLabel()) != -1) {

			String delete = messageTextFromTelegram.substring(0,
					messageTextFromTelegram.indexOf(BotLabels.DASH.getLabel()));
			Integer id = Integer.valueOf(delete);

			try {

				deleteTask(id).getBody();
				BotHelper.sendMessageToTelegram(chatId, BotMessages.TASK_DELETED.getMessage(), this);

			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		} else if (messageTextFromTelegram.indexOf(BotLabels.UNCHECK.getLabel()) != -1) {

			String undo = messageTextFromTelegram.substring(0,
					messageTextFromTelegram.indexOf(BotLabels.DASH.getLabel()));
			Integer id = Integer.valueOf(undo);

			try {
				Subtask item = getSubtaskById(id).getBody();
				item.setStatus(0);
				updateSubtask(item, id);
				BotHelper.sendMessageToTelegram(chatId, BotMessages.SUBTASK_UNDONE.getMessage(), this);

			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}

		} else if (messageTextFromTelegram.indexOf(BotLabels.CHECK.getLabel()) != -1) {

			String done = messageTextFromTelegram.substring(0,
					messageTextFromTelegram.indexOf(BotLabels.DASH.getLabel()));
			Integer id = Integer.valueOf(done);

			try {
				Subtask item = getSubtaskById(id).getBody();
				item.setStatus(2);
				updateSubtask(item, id);
				BotHelper.sendMessageToTelegram(chatId, BotMessages.SUBTASK_DONE.getMessage(), this);
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}

			// Return /report task
		} else if (messageTextFromTelegram.equals(BotCommands.TASK_LIST.getCommand())
				|| messageTextFromTelegram.equals(BotLabels.LIST_ALL_TASKS.getLabel())) {

			List<Task> allItems = getAllTasks();
			ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
			List<KeyboardRow> keyboard = new ArrayList<>();

			// command back to main screen
			KeyboardRow mainScreenRowTop = new KeyboardRow();
			mainScreenRowTop.add(BotLabels.MENU_SCREEN.getLabel());
			keyboard.add(mainScreenRowTop);

			KeyboardRow firstRow = new KeyboardRow();
			firstRow.add(BotLabels.ADD_NEW_TASK.getLabel());
			keyboard.add(firstRow);

			StringBuilder sb = new StringBuilder();

			List<Task> activeItems = allItems.stream()
					.filter(item -> item.getStatus() == 0 || item.getStatus() == 1 || item.getStatus() == 2)
					.collect(Collectors.toList());

			if (activeItems.size() > 0) {
				sb.append("Active Tasks: \n");
			}
			for (Task item : activeItems) {
				KeyboardRow currentRow = new KeyboardRow();
				currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.DONE.getLabel());
				currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.LIST_ALL_SUBTASKS.getLabel());
				keyboard.add(currentRow);
				sb.append(item.quickDescription() + "\n");
			}

			List<Task> doneItems = allItems.stream().filter(item -> item.getStatus() == 3)
					.collect(Collectors.toList());

			if (doneItems.size() > 0) {
				sb.append("Done Tasks: \n");
			}
			for (Task item : doneItems) {
				KeyboardRow currentRow = new KeyboardRow();
				currentRow.add(item.getTitle());
				currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.UNDO.getLabel());
				currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.DELETE.getLabel());
				keyboard.add(currentRow);
				sb.append(item.quickDescription() + "\n");
			}

			// command back to main screen
			KeyboardRow mainScreenRowBottom = new KeyboardRow();
			mainScreenRowBottom.add(BotLabels.MENU_SCREEN.getLabel());
			keyboard.add(mainScreenRowBottom);

			keyboardMarkup.setKeyboard(keyboard);

			SendMessage messageToTelegram = new SendMessage();
			messageToTelegram.setChatId(chatId);
			messageToTelegram.setText(sb.toString());
			messageToTelegram.setReplyMarkup(keyboardMarkup);

			try {
				execute(messageToTelegram);
			} catch (TelegramApiException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		} else if (messageTextFromTelegram.indexOf(BotLabels.LIST_ALL_SUBTASKS.getLabel()) != -1) {
			// list on menu the selected task subtasks
			String taskId = messageTextFromTelegram.substring(0,
					messageTextFromTelegram.indexOf(BotLabels.DASH.getLabel()));
			Integer id = Integer.valueOf(taskId);

			try {
				Task task = getTaskById(id).getBody();
				StringBuilder sb = new StringBuilder();

				sb.append("Task: \n" + task.publicDescription() + "\n");
				ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
				List<KeyboardRow> keyboard = new ArrayList<>();

				// command back to main screen
				KeyboardRow mainScreenRowTop = new KeyboardRow();
				mainScreenRowTop.add(BotLabels.MENU_SCREEN.getLabel());
				keyboard.add(mainScreenRowTop);

				KeyboardRow firstRow = new KeyboardRow();
				firstRow.add(BotLabels.ADD_NEW_TASK.getLabel());
				keyboard.add(firstRow);

				List<Subtask> activeItems = task.getSubtasks().stream()
						.filter(item -> item.getStatus() == 0 || item.getStatus() == 1)
						.collect(Collectors.toList());

				if (activeItems.size() > 0) {
					sb.append("Active Subtasks: \n");
				}
				for (Subtask item : activeItems) {
					KeyboardRow currentRow = new KeyboardRow();
					currentRow.add(item.getTitle());
					currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.CHECK.getLabel());
					currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.DELETE_SUBTASK.getLabel());
					keyboard.add(currentRow);
					sb.append(item.publicDescription() + "\n");
				}

				List<Subtask> doneItems = task.getSubtasks().stream().filter(item -> item.getStatus() == 2)
						.collect(Collectors.toList());

				if (doneItems.size() > 0) {
					sb.append("Done Subtasks: \n");
				}

				for (Subtask item : doneItems) {
					KeyboardRow currentRow = new KeyboardRow();
					currentRow.add(item.getTitle());
					currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.UNCHECK.getLabel());
					currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.DELETE_SUBTASK.getLabel());
					keyboard.add(currentRow);
					sb.append(item.publicDescription() + "\n");
				}

				// command back to main screen
				KeyboardRow mainScreenRowBottom = new KeyboardRow();
				mainScreenRowBottom.add(BotLabels.MENU_SCREEN.getLabel());
				keyboard.add(mainScreenRowBottom);

				keyboardMarkup.setKeyboard(keyboard);

				SendMessage messageToTelegram = new SendMessage();
				messageToTelegram.setChatId(chatId);
				messageToTelegram.setText(sb.toString());
				messageToTelegram.setReplyMarkup(keyboardMarkup);

				execute(messageToTelegram);

			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}

		} else {
			try {
				User user = getUserById(1).getBody();

				// use command parser
				CommandParser commandParser = new CommandParser(messageTextFromTelegram);
				String action = commandParser.getAction();
				String type = commandParser.getType();
				Map<String, String> parameters = commandParser.getFields();

				System.out.println("Action: " + action);
				System.out.println("Type: " + type);
				System.out.println("Parameters: " + parameters);

				MessageModelFactory messageModelFactory = new MessageModelFactory(taskController,
						projectMemberController,
						projectController, subtaskController, userController);
				MessageModel<?> messageModel = messageModelFactory.getMessageModel(type);
				TypeBuilder<?> typeBuilder = TypeBuilderFactory.getBuilder(type);

				logger.info("Created TypeBuilder instance: {}", typeBuilder.getClass().getSimpleName());
				logger.info("Created MessageModel instance: {}", messageModel.getClass().getSimpleName());

				StringBuilder sb = new StringBuilder();
				switch (action) {
					case "report":
						String report_result = "";
						if (commandParser.hasField("id")) {
							int report_id = Integer.parseInt(commandParser.getField("id"));
							report_result = messageModel.reportSingle(report_id, user);
						} else if (commandParser.hasField(type + "_id")) {
							int report_id = Integer.parseInt(commandParser.getField(type + "_id"));
							report_result = messageModel.reportSpecific(report_id);
						} else {
							report_result = messageModel.reportAll(user);
						}
						sb.append(report_result);
						break;
					case "create":
						Object create_object = typeBuilder.build(parameters);
						@SuppressWarnings("unchecked")
						String create_response = ((MessageModel<Object>) messageModel).post(create_object);
						sb.append(create_response);
						break;
					case "update":
						if (!commandParser.hasField("id")) {
							throw new IllegalArgumentException("No id provided for update");
						}
						int id = Integer.parseInt(commandParser.getField("id"));
						Object update_object = typeBuilder.build(parameters);
						@SuppressWarnings("unchecked")
						String update_response = ((MessageModel<Object>) messageModel).update(id,
								update_object);
						sb.append(update_response);
						break;
					case "delete":
						if (!commandParser.hasField("id")) {
							throw new IllegalArgumentException("No id provided for delete");
						}
						int delete_id = Integer.parseInt(commandParser.getField("id"));
						String delete = messageModel.delete(delete_id);
						sb.append(delete);
						break;
					default:
						break;
				}
				SendMessage messageToTelegram = new SendMessage();
				messageToTelegram.setChatId(chatId);
				messageToTelegram.setText(BotMessages.SUCCESFUL_COMMAND.getMessage() + "\n" +
						sb.toString());

				try {
					execute(messageToTelegram);
				} catch (TelegramApiException e1) {
					logger.error(e1.getLocalizedMessage(), e1);
				}

			} catch (Exception e) {
				SendMessage messageToTelegram = new SendMessage();
				messageToTelegram.setChatId(chatId);
				messageToTelegram.setText(BotMessages.INVALID_COMMAND.getMessage());

				try {
					execute(messageToTelegram);
				} catch (TelegramApiException e1) {
					logger.error(e1.getLocalizedMessage(), e1);
				}
				logger.error(e.getLocalizedMessage(), e);
			}
		}
	}

	@Override
	public String getBotUsername() {
		return botName;
	}

	// GET /tasklist
	public List<Task> getAllTasks() {
		return taskService.findAll();
	}

	// GET BY ID /tasklist/{id}
	public ResponseEntity<Task> getTaskById(@PathVariable int id) {
		try {
			ResponseEntity<Task> responseEntity = taskService.getItemById(id);
			return new ResponseEntity<Task>(responseEntity.getBody(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	public ResponseEntity<User> getUserById(@PathVariable int id) {
		try {
			ResponseEntity<User> responseEntity = userService.getItemById(id);
			return new ResponseEntity<User>(responseEntity.getBody(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	// GET BY ID /subtasklist/{id}
	public ResponseEntity<Subtask> getSubtaskById(@PathVariable int id) {
		try {
			ResponseEntity<Subtask> responseEntity = subtaskService.getItemById(id);
			return new ResponseEntity<Subtask>(responseEntity.getBody(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	// PUT /tasklist
	public ResponseEntity addTask(@RequestBody Task task) throws Exception {
		Task td = taskService.addTask(task);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("location", "" + td.getID());
		responseHeaders.set("Access-Control-Expose-Headers", "location");
		// URI location = URI.create(""+td.getID())

		return ResponseEntity.ok().headers(responseHeaders).build();
	}

	// PUT /subtasklist
	public ResponseEntity addSubtask(@RequestBody Subtask subtask) throws Exception {
		Subtask td = subtaskService.addSubtask(subtask);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("location", "" + td.getID());
		responseHeaders.set("Access-Control-Expose-Headers", "location");
		// URI location = URI.create(""+td.getID())

		return ResponseEntity.ok().headers(responseHeaders).build();
	}

	// UPDATE /tasklist/{id}
	public ResponseEntity updateTask(@RequestBody Task task, @PathVariable int id) {
		try {
			Task task1 = taskService.updateTask(id, task);
			return new ResponseEntity<>(task1, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	// UPDATE /subtasklist/{id}
	public ResponseEntity updateSubtask(@RequestBody Subtask subtask, @PathVariable int id) {
		try {
			Subtask subtask1 = subtaskService.updateSubtask(id, subtask);
			return new ResponseEntity<>(subtask1, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	// DELETE tasklist/{id}
	public ResponseEntity<Boolean> deleteTask(@PathVariable("id") int id) {
		Boolean flag = false;
		try {
			flag = taskService.deleteTask(id);
			return new ResponseEntity<>(flag, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(flag, HttpStatus.NOT_FOUND);
		}
	}

	// DELETE subtasklist/{id}
	public ResponseEntity<Boolean> deleteSubtask(@PathVariable("id") int id) {
		Boolean flag = false;
		try {
			flag = subtaskService.deleteSubtask(id);
			return new ResponseEntity<>(flag, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(flag, HttpStatus.NOT_FOUND);
		}
	}

}