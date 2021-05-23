package com.danielcastro.viblioteca;

import java.io.Serializable;
import java.util.Date;

public class Loan implements Serializable {

    private String ISBN;
    private String code;
    private String date;
    private String expirationDate;
    private boolean markedAsReturned;
    private boolean returned;
    private String user;
    private String imageUrl;
    private String title;


    public Loan() {
    }

    public Loan(String ISBN, String code, String date, String expirationDate, boolean markedAsReturned, boolean returned, String user, String imageUrl, String title) {
        this.ISBN = ISBN;
        this.code = code;
        this.date = date;
        this.expirationDate = expirationDate;
        this.markedAsReturned = markedAsReturned;
        this.returned = returned;
        this.user = user;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isMarkedAsReturned() {
        return markedAsReturned;
    }

    public void setMarkedAsReturned(boolean markedAsReturned) {
        this.markedAsReturned = markedAsReturned;
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
}
