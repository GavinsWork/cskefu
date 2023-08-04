package com.cskefu.cc.model;


public class ConferenceList {
    String name;
    String id;
    Integer number;

    public ConferenceList() {
    }

    public ConferenceList(String name, String id, Integer number) {
        this.name = name;
        this.id = id;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
