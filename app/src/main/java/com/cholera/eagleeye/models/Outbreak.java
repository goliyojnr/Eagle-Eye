package com.cholera.eagleeye.models;

public class Outbreak {
    private String name;
    private String details;

    public Outbreak(String name, String details) {
        this.name = name;
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }
}

