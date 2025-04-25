package com.springboot.MyTodoList.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Table(name = "SPRINT")
public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int ID;
    @Column(name = "NAME")
    String name;
    @Column(name = "DESCRIPTION")
    String description;
    @Column(name = "START_DATE")
    OffsetDateTime start_date;
    @Column(name = "END_DATE")
    OffsetDateTime end_date;
    @Column(name = "STATUS")
    int status;
    @Column(name = "CREATED_AT")
    OffsetDateTime created_at;
    @Column(name = "UPDATED_AT")
    OffsetDateTime updated_at;
    @JsonBackReference(value = "project-sprints")
    @ManyToOne
    @JoinColumn(name = "PROJECT_ID", nullable = false)
    private Project project;
    @Transient
    private int project_id;
    @JsonManagedReference(value = "sprint-tasks")
    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Task> tasks;

    public Sprint() {
    }

    public Sprint(int ID, String name, String description, OffsetDateTime start_date, OffsetDateTime end_date,
            int status,
            OffsetDateTime created_at, OffsetDateTime updated_at, Project project, List<Task> tasks) {
        this.tasks = tasks;
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.start_date = start_date;
        this.end_date = end_date;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.project = project;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getStart_date() {
        return start_date;
    }

    public void setStart_date(OffsetDateTime start_date) {
        this.start_date = start_date;
    }

    public OffsetDateTime getEnd_date() {
        return end_date;
    }

    public void setEnd_date(OffsetDateTime end_date) {
        this.end_date = end_date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public OffsetDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(OffsetDateTime created_at) {
        this.created_at = created_at;
    }

    public OffsetDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(OffsetDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public int getProject_id() {
        return project_id;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "Sprint{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                ", status=" + status +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", project_id=" + project_id +
                ", tasks=" + tasks +
                '}';
    }

    public String description() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String start = start_date != null ? start_date.format(formatter) : "N/A";
        String end = end_date != null ? end_date.format(formatter) : "N/A";
        return String.format(
                "📦 Sprint: %s | ID: %d\n" +
                        "📝 Description: %s\n" +
                        "🕒 Start: %s | 🕒 End: %s\n" +
                        "📊 Status: %s",
                name,
                ID,
                description != null ? description : "No description provided",
                start,
                end,
                statusText());
    }

    public String kpiStatus() {
        StringBuilder sb = new StringBuilder();

        int totalTasks = tasks.size();
        long completedTasks = tasks.stream().filter(task -> task.getStatus() == 3).count();
        long otherTasks = totalTasks - completedTasks;

        sb.append("📊 Sprint KPI Status:\n")
                .append("Total Tasks: ").append(totalTasks).append("\n")
                .append("Tasks Status: ").append(completedTasks).append(" Completed, ")
                .append(otherTasks).append(" In Progress\n");

        return sb.toString();
    }

    private String statusText() {
        switch (status) {
            case 0:
                return "📋 Planned";
            case 1:
                return "🚧 In Progress";
            case 2:
                return "✅ Completed";
            case 3:
                return "❌ Canceled";
            default:
                return "❓ Unknown";
        }
    }
}