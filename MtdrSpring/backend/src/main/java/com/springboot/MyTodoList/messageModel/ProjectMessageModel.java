package com.springboot.MyTodoList.messageModel;

import java.util.List;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.springboot.MyTodoList.controller.ProjectController;
import com.springboot.MyTodoList.controller.ProjectMemberController;
import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.model.ProjectMember;
import com.springboot.MyTodoList.model.User;

@Service
public class ProjectMessageModel implements MessageModel<Project> {
    private ProjectController projectController;

    @Autowired
    private ProjectMemberController projectMemberController;

    public ProjectMessageModel(ProjectController projectController) {
        this.projectController = projectController;
    }

    @Override
    public String reportSingle(int id, User user) {
        List<ProjectMember> projectMemberEntity = projectMemberController.getProjectMembersByUserId(user.getID());
        if (projectMemberEntity.size() == 0) {
            return "El usuario no tiene proyectos";
        }

        if (projectMemberEntity.stream().noneMatch(pm -> pm.getProject_id() == id)) {
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
        List<ProjectMember> projectMembers = projectMemberController.getProjectMembersByUserId(user.getID());
        if (projectMembers.size() == 0) {
            return "El usuario no tiene proyectos";
        }

        StringBuilder sb = new StringBuilder();

        for (ProjectMember projectMember : projectMembers) {
            int project_id = projectMember.getProject_id();
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
