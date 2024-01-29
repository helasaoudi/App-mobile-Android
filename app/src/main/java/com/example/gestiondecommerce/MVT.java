package com.example.gestiondecommerce;
import com.google.firebase.database.PropertyName;

public class MVT {
    @PropertyName("id")
    private  String id ;
    private String nomClient;
    private  String idClient ;
    private String commercial ;
    private String Date ;
    private   int  montant ;
    private boolean validation_commercial ;
    private boolean validation_admin ;

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public String getNomClient() {
        return nomClient;
    }

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
        return montant;
    }

    public void setMontant(int montant) {
        this.montant = montant;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public void setValidation_commercial(boolean validation_commercial) {
        this.validation_commercial = validation_commercial;
    }

    public void setValidation_admin(boolean validation_admin) {
        this.validation_admin = validation_admin;
    }
}