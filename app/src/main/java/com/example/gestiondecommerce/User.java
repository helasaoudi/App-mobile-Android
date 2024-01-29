package com.example.gestiondecommerce;

public class User {


    private String Id;
    private  String email ;
    private  String password ;
    private   String name ;
    private  String role ;
    private int tel ;
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String id, String email, String password, String name, String role, int tel) {
        Id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.tel = tel;
    }

    public User() {

    }

    public String getEmail() {
        return email;
    }

    public void setTel(int tel) {
        this.tel = tel;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTel(){
        return String.valueOf(this.tel);
    }

    @Override
    public String toString(){
        return name;
    }
}