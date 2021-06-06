package com.danielcastro.viblioteca;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.List;
import java.util.stream.Collectors;

public class BooksRecyclerViewAdapter extends RecyclerView.Adapter<BooksRecyclerViewAdapter.ViewHolder> {

    private final List<Book> elements;
    private final List<Book> originalItems;
    private final FirebaseStorage storage;
    private final Context context;
    private final User user;
    private final FragmentManager fragmentManager;
    private StorageReference imageRef;

    public BooksRecyclerViewAdapter(Context context, List<Book> elements, List<Book> originalItems, User user, FragmentManager fragmentManager) {
        this.context = context;
        this.elements = elements;
        this.originalItems = originalItems;
        this.user = user;
        this.fragmentManager = fragmentManager;
        this.storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public BooksRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewElement = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_single_view, parent, false);
        imageRef = storage.getReference().child("images");
        return new ViewHolder(viewElement);
    }

    @Override
    public void onBindViewHolder(@NonNull BooksRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.getTxtElementTitle().setText(elements.get(position).getTitle());
        holder.getTxtElementAuthor().setText(elements.get(position).getAuthor());
        holder.getTxtElementPublisher().setText(elements.get(position).getPublisher());
        if(user.getRole().equals("VIB_ADMIN")) {
            String stockLeft = elements.get(position).getLoaned() + "/" + elements.get(position).getStock();
            holder.getTxtElementStock().setText(stockLeft);
        }

        Glide.with(holder.getImageElement().getContext())
                .load(imageRef.child(elements.get(position).getImageUrl())).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(holder.getImageElement());
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
            List<Book> collection = originalItems.stream().filter(i -> i.getTitle().toLowerCase().contains(stringSearch.toLowerCase())).collect(Collectors.toList());
            elements.clear();
            elements.addAll(collection);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView bookTextViewStock, bookTextViewTitle, bookTextViewPublisher, bookTextViewAuthor;
        private final ImageView bookImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            bookTextViewStock = itemView.findViewById(R.id.bookTextViewStock);
            bookTextViewTitle = itemView.findViewById(R.id.bookTextViewTitle);
            bookTextViewAuthor = itemView.findViewById(R.id.bookTextViewAuthor);
            bookTextViewPublisher = itemView.findViewById(R.id.bookTextViewPublisher);
            bookImageView = itemView.findViewById(R.id.bookImageView);
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                showPopupMenu(view, position);
            });
        }


        private void showPopupMenu(View view, int position) {
            PopupMenu popupMenu = new PopupMenu(context, view);
            MenuInflater menuInflater = popupMenu.getMenuInflater();
            menuInflater.inflate(R.menu.book_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new Menu(position));
            if (!user.getRole().equals("VIB_ADMIN")) {
                popupMenu.getMenu().getItem(2).setVisible(false);
                popupMenu.getMenu().getItem(3).setVisible(false);

            }
            popupMenu.show();
        }

        public TextView getTxtElementTitle() {
            return bookTextViewTitle;
        }

        public TextView getTxtElementAuthor() {
            return bookTextViewAuthor;
        }

        public TextView getTxtElementPublisher() {
            return bookTextViewPublisher;
        }

        public TextView getTxtElementStock() {
            return bookTextViewStock;
        }

        public ImageView getImageElement() {
            return bookImageView;
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
            if (itemId == R.id.menuDetails) {
                Fragment fragment = DetailFragment.newInstance(elements.get(menuPosition));
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
            } else if (itemId == R.id.menuLoan) {
                DBHelper.loanBook(elements.get(menuPosition), user, context);
            } else if (itemId == R.id.menuDelete) {
                DBHelper.reduceBookStock(elements.get(menuPosition), context);
            } else if (itemId == R.id.menuIncrease) {
                DBHelper.increaseBookStock(elements.get(menuPosition));
            }
            return false;
        }
    }
}
