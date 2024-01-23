package com.example.gestiondecommerce;

public class MVT {
    private  String idClient ;
    private String commercial ;
    private String Date ;
    private   int Montant ;
    private boolean validation_commercial ;
    private boolean validation_admin ;

    public String getIdClient() {
        return idClient;
    }

    public String getCommercial() {
        return commercial;
    }

    public String getDate() {
        return Date;
    }

    public int getMontant() {
        return Montant;
    }

    public boolean isValidation_commercial() {
        return validation_commercial;
    }

    public boolean isValidation_admin() {
        return validation_admin;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    public void setCommercial(String commercial) {
        this.commercial = commercial;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setMontant(int montant) {
        Montant = montant;
    }

    public void setValidation_commercial(boolean validation_commercial) {
        this.validation_commercial = validation_commercial;
    }

    public void setValidation_admin(boolean validation_admin) {
        this.validation_admin = validation_admin;
    }
}
