package com.springboot.MyTodoList.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "PROJECT")
public class Project {
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
    @JsonManagedReference(value = "project-sprints")
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Sprint> sprints;

    public Project() {
    }

    public Project(int ID, String name, String description, OffsetDateTime start_date, OffsetDateTime end_date,
            int status,
            OffsetDateTime created_at, OffsetDateTime updated_at, List<Task> tasks, List<Sprint> sprints) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.start_date = start_date;
        this.end_date = end_date;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.sprints = sprints;
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

    public List<Sprint> getSprints() {
        return sprints;
    }

    public void setSprints(List<Sprint> sprints) {
        this.sprints = sprints;
    }

    @Override
    public String toString() {
        return "Project{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                ", status=" + status +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", sprints=" + sprints +
                '}';
    }

    public String publicDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "📂 *Project:* %s, 🆔 ID: %d\n" +
                        "   📝 Description: %s\n" +
                        "   📅 Start: %s | ⏳ End: %s\n" +
                        "   🔄 Status: %s\n",
                name, ID, description, start_date, (end_date != null ? end_date : "Ongoing"), statusText()));

        // if (tasks != null && !tasks.isEmpty()) {
        // sb.append("📌 *Tasks:*\n");
        // for (Task task : tasks) {
        // sb.append(task.quickDescription()).append("\n");
        // }
        // } else {
        // sb.append("📌 No tasks assigned yet.\n");
        // }

        return sb.toString();
    }

    public String quickDescription() {
        return String.format("🆔 ID: %d, 📂 *Project:* %s\n" +
                "   📝 Description: %s\n" +
                "   📅 Start: %s | ⏳ End: %s\n" +
                "   🔄 Status: %s\n",
                name, description, start_date, (end_date != null ? end_date : "Ongoing"), statusText());
    }

    // Helper method to convert status to text
    private String statusText() {
        switch (status) {
            case 0:
                return "📋 Planning";
            case 1:
                return "🚧 In Progress";
            case 2:
                return "✅ Completed";
            case 3:
                return "⏸️ On Hold";
            default:
                return "⚠️ Unknown";
        }
    }

}
