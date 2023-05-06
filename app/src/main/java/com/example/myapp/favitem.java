package com.example.myapp;


public class favitem {
    private String ticker;
    private String name;
    private double curPrice;
    private double changePrice;
    private double changePricePer;
    private int shares;
    private double cost;


    public favitem(String ticker,String name,double curPrice,double changePrice,double changePricePer,int shares,double cost) {
        this.ticker = ticker;
        this.name = name;
        this.curPrice = curPrice;
        this.changePrice = changePrice;
        this.changePricePer = changePricePer;
        this.shares = shares;
        this.cost = cost;

    }

    public String getTicker() {
        return ticker;
    }
    public String getComName() {
        return name;
    }
    public double getPrice() {
        return curPrice;
    }
    public double getPriceChange() {
        return changePrice;
    }
    public double getPriceChangePer() {
        return changePricePer;
    }
    public int getShares(){return shares;}
    public double getCost(){return cost;}



}

