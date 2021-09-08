package com.thisrahul.quantumassignment.model;

public class User {
    private final String name;
    private String number;
    private final String email;
    private String password;
    private final String signInMethod;

    public User(String name, String number, String email, String password, String signInMethod) {
        this.name = name;
        this.number = number;
        this.email = email;
        this.password = password;
        this.signInMethod = signInMethod;
    }

    public User(String name, String email, String signInMethod) {
        this.name = name;
        this.email = email;
        this.signInMethod = signInMethod;
    }

}
