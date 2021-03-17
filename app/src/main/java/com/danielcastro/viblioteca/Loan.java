package com.danielcastro.viblioteca;

import java.util.Date;

public class Loan {

    private String code;
    private Date date;
    private Date expirationDate;
    private boolean markedAsReturned;
    private boolean returned;
    private String user;

    public Loan(){};

    public Loan(String code, Date date, Date expirationDate, boolean markedAsReturned, boolean returned, String user) {
        this.code = code;
        this.date = date;
        this.expirationDate = expirationDate;
        this.markedAsReturned = markedAsReturned;
        this.returned = returned;
        this.user = user;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
