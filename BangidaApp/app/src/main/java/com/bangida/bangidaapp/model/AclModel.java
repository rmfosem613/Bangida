package com.bangida.bangidaapp.model;

public class AclModel {
    private String  alcontent, id;
    private Boolean alcheck;

    public AclModel( String id, String alcontent, Boolean alcheck) {
        this.id = id;
        this.alcontent = alcontent;
        this.alcheck = alcheck;
    }

    public String getId() {
        return id;
    }

    public String getAlcontent() {
        return alcontent;
    }

    public Boolean getAlcheck() {
        return alcheck;
    }
}
