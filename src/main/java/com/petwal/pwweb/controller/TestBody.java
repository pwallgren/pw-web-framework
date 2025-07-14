package com.petwal.pwweb.controller;

public class TestBody {

    private String name;
    private Integer age;

    public TestBody() {
    }

    public TestBody(final String name, final Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }
}
