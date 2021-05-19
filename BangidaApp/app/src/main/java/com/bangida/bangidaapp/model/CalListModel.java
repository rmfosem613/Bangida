package com.bangida.bangidaapp.model;

public class CalListModel {
    private String  cdate, sche;
    private Boolean pcheck;

    public CalListModel( String cdate, String sche, Boolean pcheck) {
        this.cdate = cdate;
        this.sche = sche;
        this.pcheck = pcheck;
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
