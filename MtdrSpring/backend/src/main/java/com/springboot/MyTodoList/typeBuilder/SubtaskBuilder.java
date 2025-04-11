package com.springboot.MyTodoList.typeBuilder;

import com.springboot.MyTodoList.model.Subtask;
import java.util.Map;

public class SubtaskBuilder implements TypeBuilder<Subtask> {
    @Override
    public Subtask build(Map<String, String> fields) throws IllegalArgumentException {
        Subtask task = new Subtask();

        task.setTitle(fields.getOrDefault("name", "Unnamed Subtask"));
        task.setDescription(fields.getOrDefault("description", "No description"));
        task.setTask_id(Integer.parseInt(fields.get("task_id")));

        return task;
    }
}
