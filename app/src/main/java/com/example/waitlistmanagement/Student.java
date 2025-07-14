package com.example.waitlistmanagement;

public class Student {
    private long id;
    private String name;
    private String course;
    private String priority;

    public Student(long id, String name, String course, String priority) {
        this.id = id;
        this.name = name;
        this.course = course;
        this.priority = priority;
    }

    public Student( String name, String course, String priority) {
        this.name = name;
        this.course = course;
        this.priority = priority;
    }
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
