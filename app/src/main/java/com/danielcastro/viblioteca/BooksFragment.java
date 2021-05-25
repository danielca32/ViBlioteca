package com.danielcastro.viblioteca;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
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


public class BooksFragment extends Fragment implements SearchView.OnQueryTextListener {
    private final List<Book> elements = new ArrayList<>();
    private final List<Book> originalItems = new ArrayList<>();

    BooksRecyclerViewAdapter adapter;

    public BooksFragment() {
    }

    public static BooksFragment newInstance() {
        return new BooksFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        View rootView = inflater.inflate(R.layout.fragment_books, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.bookRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        User user = DBHelper.getUser();
        if (user != null){
        adapter = new BooksRecyclerViewAdapter(this.getContext(), elements, originalItems, user, getParentFragmentManager());
        recyclerView.setAdapter(adapter); }
        SearchView searchView = rootView.findViewById(R.id.bookSearchView);
        searchView.setOnQueryTextListener(this);

        db.child("books").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                elements.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Book book = item.getValue(Book.class);
                    elements.add(book);
                }
                originalItems.addAll(elements);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return false;
    }
}

