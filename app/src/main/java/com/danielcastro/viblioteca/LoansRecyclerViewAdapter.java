package com.danielcastro.viblioteca;


import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class LoansRecyclerViewAdapter extends RecyclerView.Adapter<LoansRecyclerViewAdapter.ViewHolder> {

    private final List<Loan> elements;
    private final List<Loan> originalItems;

    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final Context context;
    private final DatabaseReference db;
    private final User user;
    private final FragmentManager fragmentManager;
    private StorageReference imageRef;
    private final Resources.Theme theme;


    public LoansRecyclerViewAdapter(Context context, List<Loan> elements, List<Loan> originalItems, User user, FragmentManager fragmentManager, DatabaseReference db) {
        this.context = context;
        this.elements = elements;
        this.user = user;
        this.originalItems = originalItems;
        this.fragmentManager = fragmentManager;
        this.db = db;
        theme = context.getTheme();
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
        holder.getTxtElementDateStart().setText(elements.get(position).getDate().substring(0, 10));
        holder.getTxtElementDateEnd().setText(elements.get(position).getExpirationDate().substring(0, 10));
        holder.getTxtElementName().setText(elements.get(position).getName());

        Glide.with(holder.getImageElement().getContext())
                .load(imageRef.child(elements.get(position).getImageUrl())).diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.getImageElement());

        TimeZone tz = TimeZone.getTimeZone("UTC");
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        java.util.Date date = Date.from(Instant.parse(elements.get(position).getExpirationDate()));
        Date actualDate = new Date();
        TypedValue typedValue = new TypedValue();
        if (elements.get(position).isReturned()) {
            theme.resolveAttribute(R.attr.boxBackgroundColor, typedValue, true);
          holder.getCardView().setCardBackgroundColor(typedValue.data);
        } else if (!elements.get(position).isReturned() && date.before(actualDate)) {
            theme.resolveAttribute(R.attr.boxStrokeErrorColor, typedValue, true);
            holder.getCardView().setCardBackgroundColor(typedValue.data);
        } else if (date.after(actualDate)) {
            theme.resolveAttribute(R.attr.boxStrokeColor, typedValue, true);
            holder.getCardView().setCardBackgroundColor(typedValue.data);
        }
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public void filter(String stringSearch) {
        if (stringSearch.length() == 0) {
            elements.clear();
            elements.addAll(originalItems);
        } else {
            List<Loan> collection = originalItems.stream().filter(i -> i.getName().toLowerCase().contains(stringSearch.toLowerCase()) || i.getTitle().toLowerCase().contains(stringSearch.toLowerCase())).collect(Collectors.toList());
            elements.clear();
            elements.addAll(collection);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final CardView cardView;
        private final TextView loanTextViewISBN, loanTextViewTitle, loanTextViewDateStart, loanTextViewDateEnd, loanTextViewName;
        private final ImageView loanImageView;

        public ViewHolder(View itemView) {

            super(itemView);

            cardView = itemView.findViewById(R.id.card_view_loan);
            loanTextViewISBN = itemView.findViewById(R.id.loanTextViewISBN);
            loanTextViewTitle = itemView.findViewById(R.id.loanTextViewTitle);
            loanTextViewDateStart = itemView.findViewById(R.id.loanTextViewDateStart);
            loanTextViewDateEnd = itemView.findViewById(R.id.loanTextViewDateReturn);
            loanTextViewName = itemView.findViewById(R.id.loanTextViewName);
            loanImageView = itemView.findViewById(R.id.loanImageView);
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                showPopupMenu(view, position);
            });
        }

        private void showPopupMenu(View view, int position) {
            PopupMenu popupMenu = new PopupMenu(context, view);
            MenuInflater menuInflater = popupMenu.getMenuInflater();
            menuInflater.inflate(R.menu.loan_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new Menu(position));
            if (elements.get(position).isReturned()) {
                popupMenu.getMenu().getItem(1).setEnabled(false);
            }
            if (!user.getRole().equals("VIB_ADMIN") || elements.get(position).isReturned()) {
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

        public TextView getTxtElementName() {
            return loanTextViewName;
        }

        public CardView getCardView() {
            return cardView;
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
            int itemId = menuItem.getItemId();
            if (itemId == R.id.detailsLoanMenu) {
                db.child("books").child(elements.get(menuPosition).getISBN()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Book book = snapshot.getValue(Book.class);
                        Fragment fragment = DetailFragment.newInstance(book);
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            } else if (itemId == R.id.extendLoanMenu) {
                DBHelper.extendLoan(elements.get(menuPosition));
            } else if (itemId == R.id.markAsReturnedMenu) {
                DBHelper.returnLoan(elements.get(menuPosition));
            }
            return false;
        }
    }
}
