package com.example.notespro.Model;

public class TaskModel {
    private String taskTitle;
    private boolean isCompleted;
    private boolean isMarkedForDeletion; // New field to track deletion

    // Default constructor required for Firestore
    public TaskModel() {}

    // Constructor to initialize taskTitle and isCompleted fields
    public TaskModel(String taskTitle, boolean isCompleted) {
        this.taskTitle = taskTitle;
        this.isCompleted = isCompleted;
        this.isMarkedForDeletion = false; // Default value
    }

    // Getter and Setter for taskTitle
    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    // Getter and Setter for isCompleted
    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    // Getter and Setter for isMarkedForDeletion
    public boolean isMarkedForDeletion() {
        return isMarkedForDeletion;
    }

    public void setMarkedForDeletion(boolean markedForDeletion) {
        isMarkedForDeletion = markedForDeletion;
    }
}
