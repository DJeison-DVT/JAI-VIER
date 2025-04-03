package com.springboot.MyTodoList.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Table(name = "TASK")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int ID;
    @Column(name = "TITLE")
    String title;
    @Column(name = "DESCRIPTION")
    String description;
    @Column(name = "CREATED_AT")
    OffsetDateTime created_at;
    @Column(name = "UPDATED_AT")
    OffsetDateTime updated_at;
    @Column(name = "DUE_DATE")
    OffsetDateTime due_date;
    @Column(name = "PRIORITY")
    int priority;
    @Column(name = "STATUS")
    int status;
    @Column(name = "ESTIMATED_HOURS")
    int estimated_hours;
    @JsonManagedReference(value = "task-subtasks")
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Subtask> subtasks;
    @JsonManagedReference(value = "task-comments")
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments;
    @JsonBackReference(value = "sprint-tasks")
    @ManyToOne
    @JoinColumn(name = "SPRINT_ID", nullable = false)
    private Sprint sprint;
    @Transient
    private int sprint_id;

    public Task() {
    }

    public Task(int ID, String title, String description, OffsetDateTime created_at, OffsetDateTime updated_at,
            OffsetDateTime due_date, int priority, int status, int estimated_hours, List<Subtask> subtasks,
            Sprint sprint) {
        this.ID = ID;
        this.sprint = sprint;
        this.title = title;
        this.description = description;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.due_date = due_date;
        this.priority = priority;
        this.status = status;
        this.estimated_hours = estimated_hours;
        this.subtasks = subtasks;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public OffsetDateTime getDue_date() {
        return due_date;
    }

    public void setDue_date(OffsetDateTime due_date) {
        this.due_date = due_date;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getEstimated_hours() {
        return estimated_hours;
    }

    public void setEstimated_hours(int estimated_hours) {
        this.estimated_hours = estimated_hours;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public Sprint getSprint() {
        return sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public int getSprint_id() {
        return sprint_id;
    }

    public void setSprint_id(int sprint_id) {
        this.sprint_id = sprint_id;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Task{" +
                "ID=" + ID +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", due_date=" + due_date +
                ", priority=" + priority +
                ", status=" + status +
                ", estimated_hours=" + estimated_hours +
                ", subtasks=" + (subtasks != null ? subtasks.toString() : "[]") +
                ", comments=" + (comments != null ? comments.toString() : "[]") +
                ", project_id=" + sprint_id +
                '}';
    }

    public String publicDescription() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return String.format(
                "📝 %s, 🆔 ID: %d\n" +
                        "   📝 Description: %s\n" +
                        "   📌 Due: %s | ⚡ Priority: %s | 🔄 Status: %s\n",
                title, ID, description, due_date.format(formatter), priorityText(), statusText());
    }

    public String quickDescription() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return String.format("🆔 ID: %d | 📝 %s | 📌 Due: %s | \n⚡ Priority: %s | 🔄 Status: %s", ID, title,
                due_date.format(formatter),
                priorityText(),
                statusText());
    }

    // Helper method to convert priority to text
    private String priorityText() {
        switch (priority) {
            case 1:
                return "🟢 Low";
            case 2:
                return "🟡 Medium";
            case 3:
                return "🔴 High";
            default:
                return "⚠️ Critical";
        }
    }

    // Helper method to convert status to text
    private String statusText() {
        switch (status) {
            case 0:
                return "📝 TODO"; // Task needs to be done
            case 1:
                return "⏳ In Progress"; // Task is currently being worked on
            case 2:
                return "🔍 In Review"; // Task is being reviewed
            case 3:
                return "✅ Completed"; // Task is finished
            default:
                return "⚠️ Unknown Status"; // Catch-all for unexpected values
        }
    }
}
