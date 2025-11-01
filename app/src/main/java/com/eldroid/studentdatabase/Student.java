package com.eldroid.studentdatabase;

public class Student {
    private String name;
    private String course;
    private int imageResId;

    public Student(String name, String course, int imageResId) {
        this.name = name;
        this.course = course;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getCourse() { return course; }
    public int getImageResId() { return imageResId; }
}
