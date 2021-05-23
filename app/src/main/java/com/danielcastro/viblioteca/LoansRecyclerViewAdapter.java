package com.danielcastro.viblioteca;


import android.net.Uri;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.List;

public class LoansRecyclerViewAdapter extends RecyclerView.Adapter<LoansRecyclerViewAdapter.ViewHolder> {

    private List<Loan> elements;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference imageRef;
    private Context context;
    private String imageURL;
    private DatabaseReference db;
    private User user;
    private FragmentManager fragmentManager;

    public LoansRecyclerViewAdapter(Context context, List<Loan> elements, User user, FragmentManager fragmentManager, DatabaseReference db) {
        this.context = context;
        this.elements = elements;
        this.user = user;
        this.fragmentManager = fragmentManager;
        this.db = db;
    }


    @NonNull
    @Override
    public LoansRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewElement = LayoutInflater.from(parent.getContext()).inflate(R.layout.loan_single_view, parent, false);
        imageRef = storage.getReference().child("images");
        return new ViewHolder(viewElement);
    }

    @Override
    public void onBindViewHolder(@NonNull LoansRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.getTxtElementISBN().setText(elements.get(position).getISBN());
        holder.getTxtElementTitle().setText(elements.get(position).getTitle());
        holder.getTxtElementDateStart().setText(elements.get(position).getDate().substring(0, 9));
        holder.getTxtElementDateEnd().setText(elements.get(position).getExpirationDate().substring(0, 9));

        imageRef.child(elements.get(position).getImageUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageURL = uri.toString();

                Glide.with(holder.getImageElement().getContext())
                        .load(imageURL)
                        .into(holder.getImageElement());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

    @Override
    public int getItemCount() {
        System.out.println(elements.size());
        return elements.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView loanTextViewISBN, loanTextViewTitle, loanTextViewDateStart, loanTextViewDateEnd;
        private ImageView loanImageView;

        public ViewHolder(View itemView) {

            super(itemView);

            loanTextViewISBN = itemView.findViewById(R.id.loanTextViewISBN);
            loanTextViewTitle = itemView.findViewById(R.id.loanTextViewTitle);
            loanTextViewDateStart = itemView.findViewById(R.id.loanTextViewDateStart);
            loanTextViewDateEnd = itemView.findViewById(R.id.loanTextViewDateReturn);
            loanImageView = itemView.findViewById(R.id.loanImageView);
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                showPopupMenu(view, position);
            });
        }

        private void showPopupMenu(View view, int position) {
            PopupMenu popupMenu = new PopupMenu(context, view);
            MenuInflater menuInflater = popupMenu.getMenuInflater();
            menuInflater.inflate(R.menu.menu_main, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new Menu(position));
            if (!user.getRole().equals("VIB_ADMIN")) {
                popupMenu.getMenu().getItem(2).setEnabled(false);

            }
            popupMenu.show();
        }

        public TextView getTxtElementISBN() {
            return loanTextViewISBN;
        }

        public TextView getTxtElementTitle() {
            return loanTextViewTitle;
        }

        public TextView getTxtElementDateStart() {
            return loanTextViewDateStart;
        }

        public TextView getTxtElementDateEnd() {
            return loanTextViewDateEnd;
        }

        public ImageView getImageElement() {
            return loanImageView;
        }
    }

    public class Menu implements PopupMenu.OnMenuItemClickListener {
        Integer menuPosition;

        public Menu(int menuPosition) {
            this.menuPosition = menuPosition;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menuDetails:
                    break;
                case R.id.menuLoan:
                    break;
                case R.id.menuDelete:
                    break;
            }
            return false;
        }
    }
}
