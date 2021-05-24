package com.danielcastro.viblioteca;


import android.net.Uri;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.List;
import java.util.stream.Collectors;

public class BooksRecyclerViewAdapter extends RecyclerView.Adapter<BooksRecyclerViewAdapter.ViewHolder> {

    private List<Book> elements;
    private List<Book> originalItems;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference imageRef;
    private Context context;
    private String imageURL;
    private DatabaseReference db;
    private User user;
    private FragmentManager fragmentManager;

    public BooksRecyclerViewAdapter(Context context, List<Book> elements, List<Book> originalItems, User user, FragmentManager fragmentManager) {
        this.context = context;
        this.elements = elements;
        this.originalItems = originalItems;
        this.user = user;
        this.fragmentManager = fragmentManager;
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
        return elements.size();
    }

    public void filter(String stringSearch){
        if(stringSearch.length() == 0){
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

        private TextView bookTextViewTitle, bookTextViewPublisher, bookTextViewAuthor;
        private ImageView bookImageView;

        public ViewHolder(View itemView) {

            super(itemView);
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
                popupMenu.getMenu().getItem(2).setEnabled(false);

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
            switch (menuItem.getItemId()) {
                case R.id.menuDetails:
                    Fragment fragment = DetailFragment.newInstance(elements.get(menuPosition));
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                    break;
                case R.id.menuLoan:
                    DBHelper.loanBook(elements.get(menuPosition),user);
                    break;
                case R.id.menuDelete:
                    DBHelper.reduceBookStock(elements.get(menuPosition));
                    break;
            }
            return false;
        }
    }
}
