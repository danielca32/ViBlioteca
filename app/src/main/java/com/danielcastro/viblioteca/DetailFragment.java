package com.danielcastro.viblioteca;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailFragment extends Fragment {

    public static final String BOOK_CON = "Book";

    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private String imageURL;
    private User user;

    private Book book;
    private EditText detailTextViewTitle;
    private EditText detailTextViewPublisher;
    private EditText detailTextViewAuthor;
    private EditText detailTextViewDate;
    private EditText detailTextViewEdition;
    private EditText detailTextViewGenre;
    private EditText detailTextViewDescription;

    private ImageView detailImageView;

    public DetailFragment() {
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
        user = DBHelper.getUser();
        MainActivity.setFragment(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        StorageReference imageRef = storage.getReference().child("images");

        if (getArguments() != null) {
            book = ((Book) getArguments().getSerializable(BOOK_CON));
            CardView editCardView = rootView.findViewById(R.id.detailCard_edit);
            CardView loanCardView = rootView.findViewById(R.id.detailCard_loan);


            detailTextViewTitle = rootView.findViewById(R.id.detailTextViewTitle);
            detailTextViewAuthor = rootView.findViewById(R.id.detailTextViewAuthor);
            detailTextViewPublisher = rootView.findViewById(R.id.detailTextViewPublisher);
            detailTextViewEdition = rootView.findViewById(R.id.detailTextViewEdition);
            EditText detailTextViewISBN = rootView.findViewById(R.id.detailTextViewISBN);
            detailTextViewDescription = rootView.findViewById(R.id.detailTextViewDescription);
            detailTextViewDate = rootView.findViewById(R.id.detailTextViewPublishingTime);
            detailTextViewGenre = rootView.findViewById(R.id.detailTextViewGenre);

            detailImageView = rootView.findViewById(R.id.detailImageView);
            detailTextViewAuthor.setText(book.getAuthor());
            detailTextViewTitle.setText(book.getTitle());
            detailTextViewPublisher.setText(book.getPublisher());
            detailTextViewEdition.setText(book.getEdition());
            detailTextViewDescription.setText(book.getDescription());
            detailTextViewDate.setText(book.getDate());
            detailTextViewGenre.setText(book.getGenre());
            detailTextViewISBN.setText(book.getISBN());
            detailTextViewISBN.setKeyListener(null);

            if (!user.getRole().equals("VIB_ADMIN")) {
                detailTextViewAuthor.setKeyListener(null);
                detailTextViewTitle.setKeyListener(null);
                detailTextViewPublisher.setKeyListener(null);
                detailTextViewEdition.setKeyListener(null);
                detailTextViewDescription.setKeyListener(null);
                detailTextViewDate.setKeyListener(null);
                detailTextViewGenre.setKeyListener(null);
                editCardView.setVisibility(View.GONE);
            }

            if (user.getRole().equals("VIB_ADMIN")) {
                editCardView.setOnClickListener(v -> {
                    book.setAuthor(detailTextViewAuthor.getText().toString());
                    book.setDescription(detailTextViewDescription.getText().toString());
                    book.setEdition(detailTextViewEdition.getText().toString());
                    book.setGenre(detailTextViewGenre.getText().toString());
                    book.setDate(detailTextViewDate.getText().toString());
                    book.setTitle(detailTextViewTitle.getText().toString());
                    book.setPublisher(detailTextViewPublisher.getText().toString());
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    db.child("books").child(book.getISBN()).setValue(book).addOnCompleteListener(task -> Toast.makeText(getContext(), "Book successfully edited", Toast.LENGTH_LONG).show());
                });
            }

            loanCardView.setOnClickListener(v -> {
                if (DBHelper.loanBook(book, user)) {
                    Toast.makeText(getContext(), "Book successfully loaned", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "ERROR", Toast.LENGTH_LONG).show();
                }
            });

            imageRef.child(book.getImageUrl()).getDownloadUrl().addOnSuccessListener(uri -> {
                imageURL = uri.toString();

                Glide.with(requireContext())
                        .load(imageURL)
                        .into(detailImageView);
            }).addOnFailureListener(exception -> {
            });

        }

        return rootView;
    }
}