package com.eldroid.studentdatabase;

public class Student {
    private int id;
    private String name;
    private String course;
    private String imageUri;

    public Student(int id, String name, String course, String imageUri) {
        this.id = id;
        this.name = name;
        this.course = course;
        this.imageUri = imageUri;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCourse() { return course; }
    public String getImageUri() { return imageUri; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setCourse(String course) { this.course = course; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
}
