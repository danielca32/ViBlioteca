package com.danielcastro.viblioteca;

import java.io.Serializable;
import java.util.Date;

public class Loan implements Serializable {

    private String key;
    private String ISBN;
    private String date;
    private String expirationDate;
    private boolean returned;
    private String user;
    private String imageUrl;
    private String title;
    private String name;


    public Loan() {
    }

    public Loan(String key, String ISBN, String date, String expirationDate, boolean returned, String user, String imageUrl, String title, String name) {
        this.key = key;
        this.ISBN = ISBN;
        this.date = date;
        this.imageUrl = imageUrl;
        this.title = title;
        this.expirationDate = expirationDate;
        this.returned = returned;
        this.user = user;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
