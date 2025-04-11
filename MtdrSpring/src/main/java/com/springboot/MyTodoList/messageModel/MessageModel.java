package com.springboot.MyTodoList.messageModel;

import com.springboot.MyTodoList.model.User;

public interface MessageModel<T> {

    String reportSingle(int id, User user);

    String reportAll(User user);

    String reportSpecific(int id);

    String post(T t);

    String update(int id, T t);

    String delete(int id);
}
