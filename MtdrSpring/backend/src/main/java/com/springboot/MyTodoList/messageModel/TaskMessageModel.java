package com.springboot.MyTodoList.messageModel;

import java.util.List;

import org.jvnet.hk2.annotations.Service;
import org.springframework.http.ResponseEntity;

import com.springboot.MyTodoList.controller.SprintController;
import com.springboot.MyTodoList.controller.TaskController;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.model.User;

@Service
public class TaskMessageModel implements MessageModel<Task> {

    private TaskController taskController;
    private SprintController sprintController;
    private String token;

    public TaskMessageModel(TaskController taskController,
            SprintController sprintController, String token) {
        this.sprintController = sprintController;
        this.taskController = taskController;
        this.token = token;
    }

    @Override
    public String reportSingle(int id, User user) {
        ResponseEntity<Task> taskEntity = taskController.getTaskById(token, id);
        if (taskEntity.getStatusCodeValue() == 200) {
            Task exisitngTask = taskEntity.getBody();
            return exisitngTask.publicDescription();
        } else {
            return "La tarea no fue encontrado";
        }
    }

    @Override
    public String reportAll(User user) {
        StringBuilder sb = new StringBuilder();

        List<Sprint> sprints = sprintController.getActiveSprints(token, user.getSelectedProject_id());
        if (sprints.size() == 0) {
            return "No hay sprints activos en el proyecto";
        }

        for (Sprint sprint : sprints) {
            for (Task task : sprint.getTasks()) {
                sb.append(task.publicDescription());
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    @Override
    public String reportSpecific(int id) {
        ResponseEntity<Task> taskEntity = taskController.getTaskById(token, id);
        if (taskEntity.getStatusCodeValue() == 200) {
            Task task = taskEntity.getBody();
            return task.publicDescription();
        } else {
            return "La tarea no fue encontrada";
        }
    }

    @Override
    public String post(Task task) {
        try {
            System.out.println(task.toString());
            ResponseEntity<Task> taskEntity = taskController.addTask(token, task);
            System.out.println("Result" + taskEntity.getStatusCodeValue());
            if (taskEntity.getStatusCodeValue() == 201) {
                return "Tarea creada";
            } else {
                return "No se pudo crear la tarea";
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return "No se pudo crear la tarea";
        }
    }

    @Override
    public String update(int id, Task task) {
        ResponseEntity<Task> taskEntity = taskController.updateTask(token, task, id);
        if (taskEntity.getStatusCodeValue() == 200) {
            return "Tarea actualizada";
        } else {
            return "No se pudo actualizar la tarea";
        }
    }

    @Override
    public String delete(int id) {
        ResponseEntity<Boolean> taskEntity = taskController.deleteTask(token, id);
        if (taskEntity.getStatusCodeValue() == 200) {
            return "Tarea eliminada";
        } else {
            return "No se pudo eliminar la tarea";
        }
    }
}
