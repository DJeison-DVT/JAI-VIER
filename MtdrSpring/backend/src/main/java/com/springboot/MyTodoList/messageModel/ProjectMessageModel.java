package com.springboot.MyTodoList.messageModel;

import java.util.List;

import org.jvnet.hk2.annotations.Service;
import org.springframework.http.ResponseEntity;

import com.springboot.MyTodoList.controller.ProjectController;
import com.springboot.MyTodoList.controller.ProjectMemberController;
import com.springboot.MyTodoList.dto.ProjectSummary;
import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.model.User;

@Service
public class ProjectMessageModel implements MessageModel<Project> {
    private ProjectController projectController;
    private ProjectMemberController projectMemberController;

    public ProjectMessageModel(ProjectController projectController, ProjectMemberController projectMemberController) {
        this.projectController = projectController;
        this.projectMemberController = projectMemberController;
    }

    @Override
    public String reportSingle(int id, User user) {
        List<ProjectSummary> projects = projectMemberController.getProjectMembersByUserId(user.getID());
        if (projects.size() == 0) {
            return "El usuario no tiene proyectos";
        }

        if (projects.stream().noneMatch(pm -> pm.getId() == id)) {
            return "El usuario no tiene acceso a este proyecto";
        }

        ResponseEntity<Project> projectEntity = projectController.getProjectById(id);
        if (projectEntity.getStatusCodeValue() == 200) {
            Project exisitngProject = projectEntity.getBody();
            return exisitngProject.publicDescription();
        } else {
            return "El usuario no fue encontrado";
        }
    }

    @Override
    public String reportAll(User user) {
        List<ProjectSummary> projects = projectMemberController.getProjectMembersByUserId(user.getID());
        if (projects.size() == 0) {
            return "El usuario no tiene proyectos";
        }

        StringBuilder sb = new StringBuilder();

        for (ProjectSummary pr : projects) {
            int project_id = pr.getId();
            ResponseEntity<Project> projectEntity = projectController.getProjectById(project_id);
            if (projectEntity.getStatusCodeValue() != 200) {
                continue;
            }
            Project project = projectEntity.getBody();
            sb.append(project.publicDescription());
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public String reportSpecific(int id) {
        ResponseEntity<Project> projectEntity = projectController.getProjectById(id);
        if (projectEntity.getStatusCodeValue() == 200) {
            Project project = projectEntity.getBody();
            return project.publicDescription();
        } else {
            return "El proyecto no fue encontrado";
        }
    }

    @Override
    public String post(Project project) {
        try {
            ResponseEntity<Project> projectEntity = projectController.addProject(project);
            if (projectEntity.getStatusCodeValue() == 201) {
                return "Proyecto creado";
            } else {
                return "No se pudo crear el proyecto";
            }
        } catch (Exception e) {
            return "No se pudo crear el proyecto";
        }
    }

    @Override
    public String update(int id, Project project) {
        ResponseEntity<Project> projectEntity = projectController.updateProject(project, id);
        if (projectEntity.getStatusCodeValue() == 200) {
            return "Proyecto actualizado";
        } else {
            return "No se pudo actualizar el proyecto";
        }
    }

    @Override
    public String delete(int id) {
        ResponseEntity<Boolean> projectEntity = projectController.deleteProject(id);
        if (projectEntity.getStatusCodeValue() == 200) {
            return "Proyecto eliminado";
        } else {
            return "No se pudo eliminar el proyecto";
        }
    }
}
