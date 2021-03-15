package com.danielcastro.viblioteca;


import android.net.Uri;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import java.util.List;

public class BooksRecyclerViewAdapter extends RecyclerView.Adapter<BooksRecyclerViewAdapter.ViewHolder>{

    private List<Book> elements;
    private RequestQueue queue = null;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference imageRef;

    private String imageURL;

    public BooksRecyclerViewAdapter(List<Book> elements){
        this.elements = elements;
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

     /* TODO: ???
        imageURL = imageRef.child(elements.get(position).getImageUrl()).getDownloadUrl().toString();
        Glide.with(holder.getImageElement().getContext())
                .load(imageURL)
                .into(holder.getImageElement()); */

        imageRef.child(elements.get(position).getImageUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageURL = uri.toString();

                 Glide.with(holder.getImageElement().getContext())
                     .load(imageURL)
                       .into(holder.getImageElement());

                Glide.with(holder.getImageElement().getContext()).load(imageURL).into(holder.getImageElement());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

    @Override
    public int getItemCount() {
        System.out.println(elements.size());
        return elements.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView bookTextViewTitle, bookTextViewPublisher, bookTextViewAuthor;
        private ImageView bookImageView;

        public ViewHolder(View itemView){

            super(itemView);
            bookTextViewTitle= itemView.findViewById(R.id.bookTextViewTitle);
            bookTextViewAuthor= itemView.findViewById(R.id.bookTextViewAuthor);
            bookTextViewPublisher= itemView.findViewById(R.id.bookTextViewPublisher);
            bookImageView= itemView.findViewById(R.id.bookImageView);

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
        public ImageView getImageElement(){
            return bookImageView;
    }
}}
