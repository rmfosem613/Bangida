package com.bangida.bangidaapp.model;

public class AnimalModel {
    private String id, petname, breed, birth, etc;

    public AnimalModel(String id, String petname, String breed, String etc) {
        this.id = id;
        this.petname = petname;
        this.breed = breed;
        this.etc = etc;
    }

    public String getId() {
        return id;
    }

    public String getPetname() {
        return petname;
    }

    public String getBreed() {
        return breed;
    }

    public String getEtc() {
        return etc;
    }
}
