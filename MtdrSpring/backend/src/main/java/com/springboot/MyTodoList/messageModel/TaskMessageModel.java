package com.springboot.MyTodoList.messageModel;

import java.util.List;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.springboot.MyTodoList.controller.ProjectController;
import com.springboot.MyTodoList.controller.ProjectMemberController;
import com.springboot.MyTodoList.controller.TaskController;
import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.model.ProjectMember;
import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.model.User;

@Service
public class TaskMessageModel implements MessageModel<Task> {
    private TaskController taskController;

    @Autowired
    private ProjectMemberController projectMemberController;

    @Autowired
    private ProjectController projectController;

    public TaskMessageModel(TaskController taskController) {
        this.taskController = taskController;
    }

    @Override
    public String reportSingle(int id, User user) {
        ResponseEntity<Task> taskEntity = taskController.getTaskById(id);
        if (taskEntity.getStatusCodeValue() == 200) {
            Task exisitngTask = taskEntity.getBody();
            return exisitngTask.publicDescription();
        } else {
            return "La tarea no fue encontrado";
        }
    }

    @Override
    public String reportAll(User user) {
        List<ProjectMember> projectMembers = projectMemberController.getProjectMembersByUserId(user.getID());
        if (projectMembers.size() == 0) {
            return "El usuario no tiene tareas";
        }

        StringBuilder sb = new StringBuilder();

        for (ProjectMember projectMember : projectMembers) {
            int project_id = projectMember.getProject_id();
            ResponseEntity<Project> projectEntity = projectController.getProjectById(project_id);
            if (projectEntity.getStatusCodeValue() != 200) {
                continue;
            }
            Project project = projectEntity.getBody();
            for (Task task : project.getTasks()) {
                sb.append(task.publicDescription());
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    @Override
    public String reportSpecific(int id) {
        ResponseEntity<Task> taskEntity = taskController.getTaskById(id);
        if (taskEntity.getStatusCodeValue() == 200) {
            Task task = taskEntity.getBody();
            return task.publicDescription();
        } else {
            return "La tarea no fue encontrada";
        }
    }

    @Override
    public String update(int id, Task task) {
        ResponseEntity<Task> taskEntity = taskController.updateTask(task, id);
        if (taskEntity.getStatusCodeValue() == 200) {
            return "Tarea actualizada";
        } else {
            return "No se pudo actualizar la tarea";
        }
    }

    @Override
    public String delete(int id) {
        ResponseEntity<Boolean> taskEntity = taskController.deleteTask(id);
        if (taskEntity.getStatusCodeValue() == 200) {
            return "Tarea eliminada";
        } else {
            return "No se pudo eliminar la tarea";
        }
    }
}
