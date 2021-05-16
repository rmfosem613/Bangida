package com.bangida.bangidaapp.model;

public class RoomListModel {
    private String id, petname;

    public RoomListModel(String id, String petname) {
        this.id = id;
        this.petname = petname;
    }

    public String getId() {
        return id;
    }

    public String getPetname() {
        return petname;
    }

}
