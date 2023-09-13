package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private ArrayList<Book> books = new ArrayList<>();
    private OnBookListener mOnBookListener;

    public BookAdapter(ArrayList<Book> books, OnBookListener onBookListener) {
        this.books = books;
        this.mOnBookListener = onBookListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        return new ViewHolder(view, mOnBookListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Book book = books.get(i);
        Log.d(TAG, "onBindViewHolder: ");
        viewHolder.book_name.setText(book.getName());
        //viewHolder.book_link.setText(book.getBookLink());

    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView book_name, book_price, book_link;
        OnBookListener onBookListener;

        public ViewHolder(@NonNull View itemView, OnBookListener onBookListener) {
            super(itemView);
            Log.d(TAG, "onBindViewHolder: " + book_name);
            book_name = itemView.findViewById(R.id.book_name);
            book_price = itemView.findViewById(R.id.book_price);
            book_link = itemView.findViewById(R.id.book_link);
            this.onBookListener = onBookListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onBookListener.onBookClick(getAdapterPosition());
        }
    }

    public void addBooks(ArrayList<Book> newBooks) {
        books.addAll(newBooks);
    }

    public interface OnBookListener{
        void onBookClick(int position);
    }
}
