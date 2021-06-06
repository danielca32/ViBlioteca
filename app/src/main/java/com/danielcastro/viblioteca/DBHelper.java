package com.danielcastro.viblioteca;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * @author dcast
 */
@SuppressLint("SimpleDateFormat")
public class DBHelper {
    private static final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private static User user;
    private static Boolean connected;

    public static void loanBook(Book book, User user, Context context) {
        if (connected) {
            if (Integer.parseInt(book.getLoaned()) < Integer.parseInt(book.getStock())) {

                Query myLoansQuery;
                myLoansQuery = db.child("loans").orderByChild("user").equalTo(user.getUID());
                myLoansQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Loan> loans = new ArrayList<>();
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            Loan loan = item.getValue(Loan.class);
                            loans.add(loan);
                        }
                        List<Loan> booksLoanedList = loans.stream().filter(loan -> !loan.isReturned()).collect(Collectors.toList());
                        if (booksLoanedList.size() < 3) {
                            List<Loan> bookAlreadyLoanedList = booksLoanedList.stream().filter(loan -> loan.getISBN().contains(book.getISBN())).collect(Collectors.toList());
                            if (bookAlreadyLoanedList.size() == 0) {


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
                                Toast.makeText(context, R.string.book_successfully_loaned, Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(context, R.string.book_already_loaned, Toast.LENGTH_LONG).show();
                            }
                        }
                        Toast.makeText(context, R.string.too_many_books_loaned, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            } else {
                Toast.makeText(context, R.string.error_no_books_left, Toast.LENGTH_LONG).show();

            }
        } else {
            Toast.makeText(context, R.string.operation_not_available, Toast.LENGTH_LONG).show();
        }
    }

    public static void reduceBookStock(Book book, Context context) {
        if (connected) {
            if (Integer.parseInt(book.getLoaned()) < Integer.parseInt(book.getStock())) {
                if (Integer.parseInt(book.getStock()) > 0) {
                    book.setStock(String.valueOf((Integer.parseInt(book.getStock()) - 1)));
                    db.child("books").child(book.getISBN()).setValue(book);
                }
            } else {
                Toast.makeText(context, R.string.close_loans_first, Toast.LENGTH_LONG).show();

            }
        } else {
            Toast.makeText(context, R.string.operation_not_available, Toast.LENGTH_LONG).show();
        }
    }

    public static void increaseBookStock(Book book) {
        if (Integer.parseInt(book.getLoaned()) < Integer.parseInt(book.getStock())) {
            book.setStock(String.valueOf((Integer.parseInt(book.getStock()) + 1)));
            db.child("books").child(book.getISBN()).setValue(book);
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

    public static User getUser() {
        return user;
    }

    public static void setUser(User setUser) {
        user = setUser;
    }

    public static Boolean getConnected() {
        return connected;
    }

    public static void setConnected(Boolean connected) {
        DBHelper.connected = connected;
    }
}

