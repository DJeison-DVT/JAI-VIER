package com.springboot.MyTodoList.typeBuilder;

import com.springboot.MyTodoList.model.Task;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

public class TaskBuilder implements TypeBuilder<Task> {
    @Override
    public Task build(Map<String, String> fields) throws IllegalArgumentException {
        Task task = new Task();

        task.setTitle(fields.getOrDefault("name", "Unnamed Task"));
        task.setDescription(fields.getOrDefault("description", "No description"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        OffsetDateTime dueDate;
        String dateString = fields.getOrDefault("due_date", OffsetDateTime.now().format(formatter));
        try {
            dueDate = OffsetDateTime.parse(dateString + "T00:00:00Z", DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException e) {
            dueDate = OffsetDateTime.now();
        }
        task.setDue_date(dueDate);

        task.setPriority(Integer.parseInt(fields.getOrDefault("priority", "1")));
        task.setStatus(Integer.parseInt(fields.getOrDefault("status", "0")));
        task.setEstimated_hours(Integer.parseInt(fields.getOrDefault("estimated_hours", "0")));
        task.setReal_hours(Integer.parseInt(fields.getOrDefault("real_hours", "0")));
        if (!fields.containsKey("sprint_id")) {
            throw new IllegalArgumentException("Sprint ID is required");
        }
        task.setSprint_id(Integer.parseInt(fields.get("sprint_id")));
        return task;
    }
}
