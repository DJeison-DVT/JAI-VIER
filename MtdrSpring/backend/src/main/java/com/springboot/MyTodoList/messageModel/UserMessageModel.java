package com.springboot.MyTodoList.messageModel;

import java.util.List;

import org.jvnet.hk2.annotations.Service;
import org.springframework.http.ResponseEntity;

import com.springboot.MyTodoList.controller.ProjectController;
import com.springboot.MyTodoList.controller.ProjectMemberController;
import com.springboot.MyTodoList.controller.UserController;
import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.model.ProjectMember;
import com.springboot.MyTodoList.model.User;

@Service
public class UserMessageModel implements MessageModel<User> {
    private UserController userController;
    private ProjectMemberController projectMemberController;
    private ProjectController projectController;

    public UserMessageModel(UserController userController, ProjectMemberController projectMemberController,
            ProjectController projectController) {
        this.userController = userController;
        this.projectMemberController = projectMemberController;
        this.projectController = projectController;
    }

    @Override
    public String reportSingle(int id, User user) {
        ResponseEntity<User> userEntity = userController.getUserById(user.getID());
        if (userEntity.getStatusCodeValue() == 200) {
            User exisitngUser = userEntity.getBody();
            return exisitngUser.publicDescription();
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
            sb.append(project.quickDescription());
            sb.append("\n");

            List<ProjectMember> pms = projectMemberController.getProjectMembersByProjectId(project_id);
            for (ProjectMember pm : pms) {
                ResponseEntity<User> userEntity = userController.getUserById(pm.getUser_id());
                if (userEntity.getStatusCodeValue() != 200) {
                    continue;
                }
                User u = userEntity.getBody();
                sb.append("    ");
                sb.append(u.quickDescription());
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    @Override
    public String reportSpecific(int id) {
        ResponseEntity<User> userEntity = userController.getUserById(id);
        if (userEntity.getStatusCodeValue() == 200) {
            User user = userEntity.getBody();
            return user.publicDescription();
        } else {
            return "El usuario no fue encontrado";
        }
    }

    @Override
    public String post(User user) {
        try {
            ResponseEntity<User> userEntity = userController.addUser(user);
            if (userEntity.getStatusCodeValue() == 201) {
                return "Usuario creado";
            } else {
                return "No se pudo crear el usuario";
            }
        } catch (Exception e) {
            return "No se pudo crear el usuario";
        }
    }

    @Override
    public String update(int id, User user) {
        ResponseEntity<User> userEntity = userController.updateUser(user, id);
        if (userEntity.getStatusCodeValue() == 200) {
            return "Usuario actualizado";
        } else {
            return "No se pudo actualizar el usuario";
        }
    }

    @Override
    public String delete(int id) {
        ResponseEntity<User> userEntity = userController.deleteUser(id);
        if (userEntity.getStatusCodeValue() == 200) {
            return "Usuario eliminado";
        } else {
            return "No se pudo eliminar el usuario";
        }
    }
}
