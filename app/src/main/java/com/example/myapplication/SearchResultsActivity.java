package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookAdapter adapter;

    private static final String BASE_URL = "http://10.0.2.2:5000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve the search query from the intent
        //String query = getIntent().getStringExtra("searchQuery");

        // Fetch the list of books based on the search query
        //fetchBooksFromApi(query);
        Bundle bundle = getIntent().getExtras();
        Log.d("horatau", bundle.toString());
        ArrayList<Book> arraylist  = bundle.getParcelableArrayList("searchResults");
        displayBooks(arraylist);
    }

    // Method to create ApiService with a custom base URL
    private ApiService createApiService(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    private void fetchBooksFromApi(String query) {
        // Create an instance of the ApiService with your base URL
        ApiService apiService = createApiService(BASE_URL);

        // Make an API call to search for books with the given query
        Call<List<Book>> call = apiService.searchBooks(query);

        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                if (response.isSuccessful()) {
                    List<Book> searchResults = response.body();
                    // Update the UI with the search results
                    displayBooks(searchResults);
                } else {
                    // Handle network error gracefully
                    showMessage("Failed to fetch search results from API.");
                    // Set an empty adapter to prevent "No adapter attached" warning
                    recyclerView.setAdapter(new BookAdapter(new ArrayList<>()));
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                // Handle network error gracefully
                showMessage("Network error: " + t.getMessage());
                // Set an empty adapter to prevent "No adapter attached" warning
                recyclerView.setAdapter(new BookAdapter(new ArrayList<>()));
            }
        });
    }

    private void displayBooks(List<Book> books) {
        adapter = new BookAdapter(books);
        recyclerView.setAdapter(adapter);
    }

    private void showMessage(String message) {
        Toast.makeText(SearchResultsActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
