package com.springboot.MyTodoList.typeBuilder;

import com.springboot.MyTodoList.model.Project;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ProjectBuilder implements TypeBuilder<Project> {
    @Override
    public Project build(Map<String, String> fields) throws IllegalArgumentException {
        Project project = new Project();

        project.setName(fields.getOrDefault("name", "Unnamed Project"));
        project.setDescription(fields.getOrDefault("description", "No description"));

        if (fields.containsKey("start_date")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            project.setStart_date(OffsetDateTime.parse(fields.get("start_date"), formatter));
        }

        if (fields.containsKey("end_date")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            project.setEnd_date(OffsetDateTime.parse(fields.get("end_date"), formatter));
        }

        project.setStatus(Integer.parseInt(fields.getOrDefault("status", "0")));
        return project;
    }
}
