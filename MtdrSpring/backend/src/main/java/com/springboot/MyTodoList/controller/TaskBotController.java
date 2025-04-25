package com.springboot.MyTodoList.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.springboot.MyTodoList.messageModel.MessageModel;
import com.springboot.MyTodoList.messageModel.MessageModelFactory;
import com.springboot.MyTodoList.model.Asignee;
import com.springboot.MyTodoList.model.AsigneeId;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.model.Subtask;
import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.service.AsigneeService;
import com.springboot.MyTodoList.service.SprintService;
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
	private SprintService sprintService;
	private UserService userService;
	private AsigneeService asigneeService;
	private String botName;

	private TaskController taskController;
	private SubtaskController subtaskController;
	private SprintController sprintController;

	public TaskBotController(String botToken, String botName, TaskService taskService,
			SubtaskService subtaskService, UserService userService, TaskController taskController,
			SubtaskController subtaskController, SprintController sprintController, SprintService sprintService,
			AsigneeService asigneeService) {
		super(botToken);
		logger.info("Bot Token: " + botToken);
		logger.info("Bot name: " + botName);
		this.taskService = taskService;
		this.subtaskService = subtaskService;
		this.userService = userService;
		this.botName = botName;
		this.taskController = taskController;
		this.subtaskController = subtaskController;
		this.sprintService = sprintService;
		this.sprintController = sprintController;
		this.asigneeService = asigneeService;
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (!update.hasMessage() || (!update.getMessage().hasText() && !update.getMessage().hasContact())) {
			return;
		}

		// Obtain message text, and chatId
		String messageTextFromTelegram = update.getMessage().getText();
		long chatId = update.getMessage().getChatId();

		System.out.println("Getting user");
		Optional<User> registeredUser = checkForChatId(chatId);
		System.out.println("User: " + registeredUser.toString());

		try {
			if (!registeredUser.isPresent()) {
				if (update.getMessage().hasContact()) {
					String phone = update.getMessage().getContact().getPhoneNumber();
					userService.linkPhoneWithChatId(chatId, phone);
					messageTextFromTelegram = BotLabels.MENU_SCREEN.getLabel();
				} else {
					requestContact(chatId);
					return;
				}
			}
		} catch (NoSuchElementException e) {
			SendMessage messageToTelegram = new SendMessage();
			messageToTelegram.setChatId(chatId);
			// update message to default to main screen
			messageToTelegram.setText(BotMessages.PHONE_NOT_REGISTERED.getMessage());
			try {
				execute(messageToTelegram);
			} catch (TelegramApiException e1) {
				logger.error(e1.getLocalizedMessage(), e1);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		User user = registeredUser.get();
		System.out.println(user.toString());
		System.out.println("Selected project id: " + user.getSelectedProject_id());

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

			List<Sprint> sprints = getActiveTasks(user.getSelectedProject_id());
			if (sprints.size() == 0) {
				messageToTelegram.setText(BotMessages.BOT_WELCOME.getMessage() + "\n\n" +
						"🚧 No active sprints found for your selected project.\n");
			} else {
				StringBuilder sb = new StringBuilder();
				for (Sprint sprint : sprints) {
					sb.append(sprint.description() + "\n");
					sb.append(sprint.kpiStatus());
				}
				messageToTelegram.setText(BotMessages.BOT_WELCOME.getMessage() + "\n\n" +
						sb.toString());
			}

			ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
			List<KeyboardRow> keyboard = new ArrayList<>();
			// first row
			KeyboardRow row = new KeyboardRow();
			row.add(BotLabels.LIST_ALL_TASKS.getLabel());
			row.add(BotLabels.ADD_NEW_TASK.getLabel());
			row.add(BotLabels.ASIGN_USER_TO_TASK.getLabel());
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

			List<Sprint> sprints = getActiveTasks(user.getSelectedProject_id());
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

			if (sprints.isEmpty()) {
				sb.append("🚧 No active sprints found for your selected project.\n");
			}

			System.out.println("Active sprints: " + sprints.toString());

			for (Sprint sprint : sprints) {
				List<Task> allItems = sprint.getTasks();
				sb.append(sprint.description() + "\n");

				List<Task> activeItems = allItems.stream()
						.filter(item -> item.getStatus() == 0 || item.getStatus() == 1 || item.getStatus() == 2)
						.collect(Collectors.toList());

				if (activeItems.size() > 0) {
					sb.append("Active Tasks: \n");
				}
				for (Task item : activeItems) {
					KeyboardRow currentRow = new KeyboardRow();
					currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.DONE.getLabel());
					keyboard.add(currentRow);
					sb.append(item.quickDescription() + "\n");

					List<Asignee> asignees = asigneeService.getAsigneesByTaskId(item.getID());
					if (asignees.size() > 0) {
						sb.append("👥 Asignees: \n");
						for (Asignee asignee : asignees) {
							User assignedUser = userService.getItemById(asignee.getId().getUserId()).getBody();
							sb.append("- " + assignedUser.getUsername() + "\n");
						}
					}
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

			}
			// command back to main screen
			KeyboardRow mainScreenRowBottom = new KeyboardRow();
			mainScreenRowBottom.add(BotLabels.MENU_SCREEN.getLabel());
			keyboard.add(mainScreenRowBottom);

			keyboardMarkup.setKeyboard(keyboard);

			SendMessage messageToTelegram = new SendMessage();
			messageToTelegram.setChatId(chatId);
			String text = sb.toString().trim();
			if (text.isEmpty()) {
				text = "No hay tareas activas ni finalizadas registradas en tu(s) sprint(s).";
			}
			messageToTelegram.setText(text);
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
		} else if (messageTextFromTelegram.indexOf(BotCommands.ASIGNEE_LIST.getCommand()) != -1
				|| messageTextFromTelegram
						.indexOf(BotLabels.ASIGN_USER_TO_TASK.getLabel()) != -1) {
			// list asignees on the menu and show command to add asignee
			ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
			List<KeyboardRow> keyboard = new ArrayList<>();
			KeyboardRow mainScreenRowTop = new KeyboardRow();
			mainScreenRowTop.add(BotLabels.MENU_SCREEN.getLabel());
			keyboard.add(mainScreenRowTop);
			KeyboardRow firstRow = new KeyboardRow();
			firstRow.add(BotLabels.ADD_NEW_TASK.getLabel());
			keyboard.add(firstRow);
			keyboardMarkup.setKeyboard(keyboard);

			SendMessage messageToTelegram = new SendMessage();
			messageToTelegram.setChatId(chatId);
			messageToTelegram.setText(BotMessages.ADD_ASIGNEE_INSTRUCTIONS.getMessage());
			messageToTelegram.setReplyMarkup(keyboardMarkup);

			try {
				execute(messageToTelegram);
			} catch (TelegramApiException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		} else if (messageTextFromTelegram.indexOf(BotLabels.ASIGNEE.getLabel()) != -1) {
			String[] parts = messageTextFromTelegram
					.split(BotLabels.DASH.getLabel(), 3);
			try {
				if (parts.length == 3 && BotLabels.ASIGNEE.getLabel().equals(parts[2])) {
					Integer taskId = Integer.valueOf(parts[0]);
					String username = parts[1];

					System.out.println("Task id: " + taskId);
					System.out.println("Username: " + username);
					Task task = getTaskById(taskId).getBody();
					User asignedUser = userService.getUserByUsername(username).getBody();

					if (asignedUser == null || task == null) {
						BotHelper.sendMessageToTelegram(chatId, BotMessages.INVALID_COMMAND.getMessage(), this);
						return;
					}

					AsigneeId asigneeId = new AsigneeId(taskId, asignedUser.getID());
					Asignee asignee = new Asignee();
					asignee.setId(asigneeId);

					asigneeService.addAsignee(asignee);
					BotHelper.sendMessageToTelegram(chatId, BotMessages.ASIGNEE_ADDED.getMessage(), this);

				} else {
					BotHelper.sendMessageToTelegram(chatId, BotMessages.INVALID_COMMAND.getMessage(), this);
				}
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}

		} else if (messageTextFromTelegram.indexOf(BotCommands.ASIGNED_TASK_LIST.getCommand()) != -1) {
			List<Sprint> sprints = getActiveTasks(user.getSelectedProject_id());
			List<Integer> sprint_ids = sprints.stream().map(Sprint::getID).collect(Collectors.toList());

			StringBuilder sb = new StringBuilder();
			sb.append("Asigned tasks: \n");

			List<Asignee> asignees = asigneeService.getAsigneesByUserId(user.getID());
			boolean found = false;

			for (Asignee asignee : asignees) {
				Task task = taskService.getItemById(asignee.getId().getTaskId()).getBody();
				System.out.println("Task sprint: " + task.getSprint().toString());
				if (task != null && task.getStatus() != 3 && sprint_ids.contains(task.getSprint().getID())) {
					sb.append(task.quickDescription() + "\n");
					found = true;
				}
			}

			if (!found) {
				// no tasks matched → clear and show the fallback message
				sb.setLength(0);
				sb.append("No assigned tasks available");
			}

			// list asignees on the menu and show command to add asignee
			ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
			List<KeyboardRow> keyboard = new ArrayList<>();
			KeyboardRow mainScreenRowTop = new KeyboardRow();
			mainScreenRowTop.add(BotLabels.MENU_SCREEN.getLabel());
			keyboard.add(mainScreenRowTop);
			KeyboardRow firstRow = new KeyboardRow();
			firstRow.add(BotLabels.ADD_NEW_TASK.getLabel());
			keyboard.add(firstRow);
			keyboardMarkup.setKeyboard(keyboard);

			SendMessage messageToTelegram = new SendMessage();
			messageToTelegram.setChatId(chatId);
			messageToTelegram.setReplyMarkup(keyboardMarkup);
			messageToTelegram.setText(sb.toString());

			try {
				execute(messageToTelegram);
			} catch (TelegramApiException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		} else {
			try {
				// use command parser
				CommandParser commandParser = new CommandParser(messageTextFromTelegram);
				String action = commandParser.getAction();
				String type = commandParser.getType();
				Map<String, String> parameters = commandParser.getFields();

				System.out.println("Action: " + action);
				System.out.println("Type: " + type);
				System.out.println("Parameters: " + parameters);

				MessageModelFactory messageModelFactory = new MessageModelFactory(taskController, subtaskController,
						sprintController);
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

	private void requestContact(long chatId) {
		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText("Please share your phone number to link your account.");

		ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
		KeyboardRow row = new KeyboardRow();
		KeyboardButton phoneButton = new KeyboardButton("📱 Share Phone Number");
		phoneButton.setRequestContact(true);
		row.add(phoneButton);
		keyboard.setKeyboard(List.of(row));
		keyboard.setResizeKeyboard(true);
		keyboard.setOneTimeKeyboard(true);

		message.setReplyMarkup(keyboard);

		try {
			execute(message);
		} catch (TelegramApiException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	public Optional<User> checkForChatId(long chatId) {
		System.out.println("Checking for chatId: " + chatId);
		ResponseEntity<User> existingUser = userService.getUserByChatId(chatId);
		System.out.println("Existing user: " + existingUser.toString());
		return existingUser.getBody() != null ? Optional.of(existingUser.getBody()) : Optional.empty();
	}

	public List<Sprint> getActiveTasks(int project_id) {
		return sprintService.findActiveSprintsByProjectId(project_id);
	}

	public ResponseEntity<Asignee> addAsignee(@RequestBody Asignee asignee) throws Exception {
		Asignee td = asigneeService.addAsignee(asignee);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("location", "" + td.getId());
		responseHeaders.set("Access-Control-Expose-Headers", "location");
		// URI location = URI.create(""+td.getID())

		return ResponseEntity.ok().headers(responseHeaders).build();
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