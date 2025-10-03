package org.habittracker.models;

public class Habit {
    private int id;
    private String name;
    private boolean completed;  // extra field

    // Constructor used when we already know the id and name
    public Habit(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Constructor used in DashboardController (only name)
    public Habit(String name) {
        this.name = name;
    }

    // Constructor used in HabitDAO (id, name, completed)
    public Habit(int id, String name, boolean completed) {
        this.id = id;
        this.name = name;
        this.completed = completed;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isCompleted() {
        return completed;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
