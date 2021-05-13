package com.example.phonebook.models;

public class PersonInfo {
    private int id;
    private String name, lastName, middleName, phone;

    public PersonInfo(int id, String lastName, String name, String middleName, String phone) {
        this.id = id;
        this.lastName = lastName;
        this.name = name;
        this.middleName = middleName;
        this.phone = phone;
    }

    public int getID() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return name;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getPhone() {
        return phone;
    }
}