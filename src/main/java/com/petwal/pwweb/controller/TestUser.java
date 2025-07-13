package com.petwal.pwweb.controller;

public class TestUser {

    private int id;
    private String name;

    public TestUser() {
    }

    public TestUser(final int id, final String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
