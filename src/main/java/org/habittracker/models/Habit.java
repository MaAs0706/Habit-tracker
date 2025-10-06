package org.habittracker.models;

public class Habit {
    private int id;
    private String name;
    private boolean completed;
    private String googleEventId; // <-- new field

    // Constructors
    public Habit(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Habit(String name) {
        this.name = name;
    }

    public Habit(int id, String name, boolean completed) {
        this.id = id;
        this.name = name;
        this.completed = completed;
    }

    public Habit(int id, String name, boolean completed, String googleEventId) {
        this.id = id;
        this.name = name;
        this.completed = completed;
        this.googleEventId = googleEventId;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public boolean isCompleted() { return completed; }
    public String getGoogleEventId() { return googleEventId; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setGoogleEventId(String googleEventId) { this.googleEventId = googleEventId; }
}
