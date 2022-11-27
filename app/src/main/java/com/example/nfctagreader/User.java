package com.example.nfctagreader;



public class User {
   private  int id;
   private String name;
   private float balance;

    public User(int id, String name, float balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }
    public void addBalance(float blc){
        balance+=blc;
    }
    public void subBalance(float blc){
        balance-=blc;
    }





}
