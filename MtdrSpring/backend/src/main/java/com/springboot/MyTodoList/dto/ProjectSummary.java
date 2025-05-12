package com.springboot.MyTodoList.dto;

import java.time.OffsetDateTime;

public class ProjectSummary {
    private final Integer id;
    private final String name;
    private final String description;
    private final OffsetDateTime startDate;
    private final OffsetDateTime endDate;
    private final Integer status;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    public ProjectSummary(Integer id, String name, String description,
            OffsetDateTime startDate, OffsetDateTime endDate,
            Integer status, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public OffsetDateTime getStartDate() {
        return startDate;
    }

    public OffsetDateTime getEndDate() {
        return endDate;
    }

    public Integer getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

}