package com.danielcastro.viblioteca;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
public class LoanFragment extends Fragment {
    private final List<Loan> elements = new ArrayList<>();
    private User user;
    public LoanFragment() {}

    public static LoanFragment newInstance() {
        LoanFragment fragment = new LoanFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        user = ((MainActivity) getActivity()).getUser();
        View rootView = inflater.inflate(R.layout.fragment_loan, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.loanRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        LoansRecyclerViewAdapter adapter = new LoansRecyclerViewAdapter(this.getContext(), elements, user, getParentFragmentManager(), db);
        recyclerView.setAdapter(adapter);
        Query myLoansQuery = db.child("loans").orderByChild("user").equalTo(user.getUID());
        myLoansQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                elements.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Loan loan = item.getValue(Loan.class);
                    elements.add(loan);
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
    public void onStart() {
        super.onStart();
    }

    @SuppressWarnings("ConstantConditions") //Supressing because the parent Activity cannot be null.
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}

