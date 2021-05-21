package com.danielcastro.viblioteca;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailFragment extends Fragment {

    public static final String BOOK_CON = "Book";

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference imageRef;
    private String imageURL;

    private Book book;
    private TextView detailTextViewTitle, detailTextViewPublisher, detailTextViewAuthor;
    private ImageView detailImageView;

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(Book book) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(BOOK_CON, book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        imageRef = storage.getReference().child("images");
        if (getArguments() != null) {
            book = ((Book) getArguments().getSerializable(BOOK_CON));
            detailTextViewTitle = rootView.findViewById(R.id.detailTextViewTitle);
            detailTextViewAuthor = rootView.findViewById(R.id.detailTextViewAuthor);
            detailTextViewPublisher = rootView.findViewById(R.id.detailTextViewPublisher);
            detailImageView = rootView.findViewById(R.id.detailImageView);
            detailTextViewAuthor.setText(book.getAuthor());
            detailTextViewTitle.setText(book.getTitle());
            detailTextViewPublisher.setText(book.getPublisher());
            imageRef.child(book.getImageUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    imageURL = uri.toString();

                    Glide.with(getContext())
                            .load(imageURL)
                            .into(detailImageView);
                    //  Glide.with(holder.getImageElement().getContext()).load(imageURL).into(holder.getImageElement());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });

        }

        return rootView;
    }
}