package com.danielcastro.viblioteca;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class DBHelper {
    private static final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private static User user;

    public static boolean loanBook(Book book, User user) {
        if (Integer.parseInt(book.getLoaned()) < Integer.parseInt(book.getStock())) {
            book.setLoaned(String.valueOf((Integer.parseInt(book.getLoaned()) + 1)));
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df.setTimeZone(tz);
            String nowAsISO = df.format(new Date());
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DATE, 28);
            String returnAsISO = df.format(cal.getTime());

            Loan loan = new Loan(db.child("loans").push().getKey(), book.getISBN(), nowAsISO, returnAsISO, false, user.getUID(), book.getImageUrl(), book.getTitle(), user.getName());

            db.child("loans").child(loan.getKey()).setValue(loan);
            db.child("books").child(book.getISBN()).setValue(book);
            return true;
        } else {
            return false;
        }
    }

    public static void reduceBookStock(Book book) {
        if (Integer.parseInt(book.getLoaned()) < Integer.parseInt(book.getStock())) {
            if (Integer.parseInt(book.getStock()) > 0) {
                book.setStock(String.valueOf((Integer.parseInt(book.getStock()) - 1)));
                db.child("books").child(book.getISBN()).setValue(book);
            }
        }
    }

    public static void returnLoan(Loan loan) {
        db.child("books").child(loan.getISBN()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Book book = snapshot.getValue(Book.class);
                if (book != null) {
                    book.setLoaned(String.valueOf((Integer.parseInt(book.getLoaned()) - 1)));
                    loan.setReturned(true);
                    db.child("books").child(book.getISBN()).setValue(book);
                    db.child("loans").child(loan.getKey()).setValue(loan);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static void extendLoan(Loan loan) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        java.util.Date date = Date.from(Instant.parse(loan.getExpirationDate()));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 7);
        String returnAsISO = df.format(cal.getTime());
        loan.setExpirationDate(returnAsISO);

        db.child("loans").child(loan.getKey()).setValue(loan);
    }

    public static User getUser(){
        return user;
    }
    public static void setUser(User setUser){
        user = setUser;
    }
}

