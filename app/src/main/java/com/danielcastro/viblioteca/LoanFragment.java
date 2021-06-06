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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoanFragment extends Fragment  implements SearchView.OnQueryTextListener{
    private final List<Loan> elements = new ArrayList<>();
    private final List<Loan> originalItems = new ArrayList<>();

    LoansRecyclerViewAdapter adapter;

    public LoanFragment() {
    }

    public static LoanFragment newInstance() {
        return new LoanFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        User user = DBHelper.getUser();
        View rootView = inflater.inflate(R.layout.fragment_loan, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.loanRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        adapter = new LoansRecyclerViewAdapter(this.requireContext(), elements, originalItems, user, getParentFragmentManager(), db);
        recyclerView.setAdapter(adapter);
        SearchView searchView = rootView.findViewById(R.id.loanSearchView);
        searchView.setOnQueryTextListener(this);

        Query myLoansQuery;
        if(user.getRole().equals("VIB_ADMIN")) {
            myLoansQuery = db.child("loans").orderByChild("user");
        } else {
            myLoansQuery = db.child("loans").orderByChild("user").equalTo(user.getUID());
        }
        myLoansQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                elements.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Loan loan = item.getValue(Loan.class);
                    elements.add(loan);
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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return false;
    }
}

