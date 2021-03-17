package com.danielcastro.viblioteca;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class BooksFragment extends Fragment {
    private final List<Book> elements = new ArrayList<>();

    public BooksFragment() {}

    public static BooksFragment newInstance() {
        BooksFragment fragment = new BooksFragment(); //TODO ???
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_books_fragment, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.bookRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        BooksRecyclerViewAdapter adapter = new BooksRecyclerViewAdapter(elements);
        recyclerView.setAdapter(adapter);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot item : dataSnapshot.child("books").getChildren()) {
                    Book book = item.getValue(Book.class);
                    elements.add(book);
                }

                adapter.notifyDataSetChanged();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return rootView;

    }

    @Override
    public void onStart(){
        super.onStart();

    }
}

