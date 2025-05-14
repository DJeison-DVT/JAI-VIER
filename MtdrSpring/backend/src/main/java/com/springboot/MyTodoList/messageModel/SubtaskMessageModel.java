package com.springboot.MyTodoList.messageModel;

import java.util.List;

import org.jvnet.hk2.annotations.Service;
import org.springframework.http.ResponseEntity;

import com.springboot.MyTodoList.controller.SprintController;
import com.springboot.MyTodoList.controller.SubtaskController;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.model.Subtask;
import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.model.User;

@Service
public class SubtaskMessageModel implements MessageModel<Subtask> {
    private SubtaskController subtaskController;
    private SprintController sprintController;
    private String token;

    public SubtaskMessageModel(SubtaskController subtaskController, SprintController sprintController, String token) {
        this.subtaskController = subtaskController;
        this.sprintController = sprintController;
        this.token = token;
    }

    @Override
    public String reportSingle(int id, User user) {
        ResponseEntity<Subtask> subtaskEntity = subtaskController.getSubtaskById(id);
        if (subtaskEntity.getStatusCodeValue() == 200) {
            Subtask exisitngSubtask = subtaskEntity.getBody();
            return exisitngSubtask.publicDescription();
        } else {
            return "La subtarea no fue encontrado";
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
                sb.append(task.quickDescription());
                sb.append("\n");
                for (Subtask subtask : task.getSubtasks()) {
                    sb.append(subtask.publicDescription());
                    sb.append("\n");
                }
            }
        }

        return sb.toString();
    }

    @Override
    public String reportSpecific(int id) {
        ResponseEntity<Subtask> subtaskEntity = subtaskController.getSubtaskById(id);
        if (subtaskEntity.getStatusCodeValue() == 200) {
            Subtask task = subtaskEntity.getBody();
            return task.publicDescription();
        } else {
            return "La subtarea no fue encontrada";
        }
    }

    @Override
    public String post(Subtask subtask) {
        try {
            ResponseEntity<Subtask> subtaskEntity = subtaskController.addSubtask(subtask);
            if (subtaskEntity.getStatusCodeValue() == 201) {
                return "Subtarea creada";
            } else {
                return "No se pudo crear la subtarea";
            }
        } catch (Exception e) {
            return "No se pudo crear la subtarea";
        }
    }

    @Override
    public String update(int id, Subtask subtask) {
        ResponseEntity<Subtask> subtaskEntity = subtaskController.updateSubtask(subtask, id);
        if (subtaskEntity.getStatusCodeValue() == 200) {
            return "Subtarea actualizada";
        } else {
            return "No se pudo actualizar la subtarea";
        }
    }

    @Override
    public String delete(int id) {
        ResponseEntity<Boolean> subtaskEntity = subtaskController.deleteSubtask(id);
        if (subtaskEntity.getStatusCodeValue() == 200) {
            return "Subtarea eliminada";
        } else {
            return "No se pudo eliminar la tarea";
        }
    }
}
