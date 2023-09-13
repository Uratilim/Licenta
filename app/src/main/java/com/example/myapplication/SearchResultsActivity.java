package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchResultsActivity extends AppCompatActivity implements BookAdapter.OnBookListener {

    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private int currentPage = 1; // Initialize to page 1
    private int totalPageCount; // Assign the total pages from the API response
    private Button previousButton, nextButton;

    private static final String BASE_URL = "http://10.0.2.2:5000/";
    Bundle globalBundle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage > 1) {
                    currentPage--;
                    fetchBooksForCurrentPage();
                    updateButtonVisibility();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage < totalPageCount) {
                    currentPage++;
                    fetchBooksForCurrentPage();
                    updateButtonVisibility();
                }
            }
        });

        Bundle bundle = getIntent().getExtras();
        globalBundle = bundle;
        totalPageCount = bundle.getInt("totalPages", 1); // Default to 1 if not provided
        Log.d(TAG, "onCreate: total pages = " + totalPageCount);
        ArrayList<Book> arraylist  = bundle.getParcelableArrayList("searchResults");
        displayBooks(arraylist);
    }

    private void fetchBooksForCurrentPage() {
        // Update your API request with the current page parameter
        // For example, if using Retrofit:
        // Call<List<Book>> call = apiService.searchBooks(query, currentPage);

        // Make the API request and update your RecyclerView adapter with the new data
        // ...
    }

    private void updateButtonVisibility() {
        Button previousButton = findViewById(R.id.previousButton);
        Button nextButton = findViewById(R.id.nextButton);

        if (currentPage > 1) {
            previousButton.setVisibility(View.VISIBLE);
        } else {
            previousButton.setVisibility(View.GONE);
        }

        if (currentPage < totalPageCount) {
            nextButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setVisibility(View.GONE);
        }
    }

    // Method to create ApiService with a custom base URL
    private ApiService createApiService(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

//    private void fetchBooksFromApi(String query) {
//        // Create an instance of the ApiService with your base URL
//        ApiService apiService = createApiService(BASE_URL);
//
//        // Make an API call to search for books with the given query
//        Call<List<Book>> call = apiService.searchBooks(query);
//
//        call.enqueue(new Callback<List<Book>>() {
//            @Override
//            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
//                if (response.isSuccessful()) {
//                    List<Book> searchResults = response.body();
//                    // Update the UI with the search results
//                    displayBooks(searchResults);
//                } else {
//                    // Handle network error gracefully
//                    showMessage("Failed to fetch search results from API.");
//                    // Set an empty adapter to prevent "No adapter attached" warning
//                    recyclerView.setAdapter(new BookAdapter(new ArrayList<>()));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Book>> call, Throwable t) {
//                // Handle network error gracefully
//                showMessage("Network error: " + t.getMessage());
//                // Set an empty adapter to prevent "No adapter attached" warning
//                recyclerView.setAdapter(new BookAdapter(new ArrayList<>()));
//            }
//        });
//    }

    private void displayBooks(List<Book> books) {
        adapter = new BookAdapter((ArrayList<Book>) books, this);
        recyclerView.setAdapter(adapter);
    }

    private void showMessage(String message) {
        Toast.makeText(SearchResultsActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookClick(int position) {
        Log.d(TAG, "onBookClick: clicked " + position);
        Intent intent = new Intent(this, BookPageActivity.class);
        ArrayList<Book> arraylist  = globalBundle.getParcelableArrayList("searchResults");
        Log.d(TAG, "onBookClick: arraylist" + arraylist);

        Bundle bundle1 = new Bundle();
        //bundle1.putParcelableArrayList("arraylist", arraylist.get(position));
        bundle1.putParcelable("bookprint", arraylist.get(position));
        intent.putExtras(bundle1);
        startActivity(intent);
    }
}
