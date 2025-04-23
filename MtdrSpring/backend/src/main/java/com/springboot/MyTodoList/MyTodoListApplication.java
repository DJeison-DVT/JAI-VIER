package com.springboot.MyTodoList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.springboot.MyTodoList.controller.SprintController;
import com.springboot.MyTodoList.controller.SubtaskController;
import com.springboot.MyTodoList.controller.TaskBotController;
import com.springboot.MyTodoList.controller.TaskController;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.service.SubtaskService;
import com.springboot.MyTodoList.service.TaskService;
import com.springboot.MyTodoList.service.UserService;
import com.springboot.MyTodoList.util.BotMessages;

@SpringBootApplication
public class MyTodoListApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(MyTodoListApplication.class);

	@Autowired
	private TaskService taskService;

	@Autowired
	private UserService userService;

	@Autowired
	private SubtaskService subtaskService;

	@Autowired
	private TaskController taskController;
	
	@Autowired
	private SubtaskController subtaskController;
	
	@Autowired
	private SprintService sprintService;
	
	@Autowired
	private SprintController sprintController;

	@Value("${telegram.bot.token}")
	private String telegramBotToken;

	@Value("${telegram.bot.name}")
	private String botName;

	public static void main(String[] args) {
		SpringApplication.run(MyTodoListApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(
					new TaskBotController(telegramBotToken, botName, taskService, subtaskService, userService,
							taskController, subtaskController, sprintController, sprintService));
			logger.info(BotMessages.BOT_REGISTERED_STARTED.getMessage());
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}
