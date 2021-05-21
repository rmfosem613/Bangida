package com.bangida.bangidaapp.model;

public class AccModel {


    private String  acdate, acprice, accontent, id;

    public AccModel( String id, String acprice, String acdate, String accontent) {
        this.id = id;
        this.acprice = acprice;
        this.acdate = acdate;
        this.accontent = accontent;
    }

    public String getId() {
        return id;
    }

    public String getAcprice() {
        return acprice;
    }

    public String getAcdate() {
        return acdate;
    }

    public String getAccontent() {
        return accontent;
    }
}
