package com.bangida.bangidaapp.model;

public class CalListModel {
    private String  cdate, sche, id;
    private Boolean pcheck;

    public CalListModel( String id, String cdate, String sche, Boolean pcheck) {
        this.id = id;
        this.cdate = cdate;
        this.sche = sche;
        this.pcheck = pcheck;
    }

    public String getId() {
        return id;
    }

    public String getCdate() {
        return cdate;
    }

    public String getSche() {
        return sche;
    }

    public Boolean getPcheck() {
        return pcheck;
    }

}
