package com.bangida.bangidaapp.model;

public class CalListModel {
    private Boolean pcheck;
    private String animals, cdate, sche;

    public CalListModel(Boolean pcheck, String animals, String cdate, String sche) {
        this.pcheck = pcheck;
        this.animals = animals;
        this.cdate = cdate;
        this.sche = sche;
    }
    public Boolean getPcheck() {
        return pcheck;
    }

    public String getAnimals() {
        return animals;
    }

    public String getCdate() {
        return cdate;
    }

    public String getSche() {
        return sche;
    }

}
