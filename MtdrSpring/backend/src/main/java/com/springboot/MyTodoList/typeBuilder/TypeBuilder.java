package com.springboot.MyTodoList.typeBuilder;

import java.util.Map;

public interface TypeBuilder<T> {
    T build(Map<String, String> fields) throws IllegalArgumentException;
}
