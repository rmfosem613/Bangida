package com.bangida.bangidaapp.model;

public class AccModel {

    private Integer acprice;
    private String  acdate, accontent, id;

    public AccModel( String id, Integer acprice, String acdate, String accontent) {
        this.id = id;
        this.acprice = acprice;
        this.acdate = acdate;
        this.accontent = accontent;
    }

    public String getId() {
        return id;
    }

    public Integer getAcprice() {
        return acprice;
    }

    public String getAcdate() {
        return acdate;
    }

    public String getAccontent() {
        return accontent;
    }
}
