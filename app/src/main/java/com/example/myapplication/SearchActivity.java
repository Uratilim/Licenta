package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {

    private ApiService apiService;
    private ArrayAdapter<Book> autoCompleteAdapter;
    private AutoCompleteTextView autoCompleteTextView;
    private List<Book> books;
    private int currentPage = 1;
    private int totalSearchPages;
    private int totalSearchResults;
    private String query;


    private static final int RESULTS_PER_PAGE = 10;
    private static final String BASE_URL = "http://10.0.2.2:5000/";

    public class SearchResult {
        private List<Book> results; // Change 'books' to 'results'
        private int total_pages;    // Change 'totalPages' to 'total_pages'

        public SearchResult(List<Book> results, int total_pages) {
            this.results = results;
            this.total_pages = total_pages;
        }

        public List<Book> getResults() {
            return results;
        }

        public int getTotalPages() {
            return total_pages;
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Create an ApiService instance with the custom base URL
        apiService = createApiService(BASE_URL); // Initialize your ApiService instance here
        query = getIntent().getStringExtra("query");
        // Initialize the AutoCompleteTextView
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView); // Corrected this line
        autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        autoCompleteTextView.setAdapter(autoCompleteAdapter);

        // Set the item click listener for AutoCompleteTextView

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = autoCompleteTextView.getText().toString();
                performSearch(query);
            }
        });

//        recyclerView = findViewById(R.id.recyclerView);
    }

    private void performSearch(String query) {
        searchBooks(query, currentPage); // Pass the current page to the searchBooks method
    }

    // Method to create ApiService with a custom base URL
    private ApiService createApiService(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    private void searchBooks(String query, int page) {
        // Construct the URL with the search query and page number
        String apiUrl = "http://10.0.2.2:5000/search/" + query + "?page=" + page;

        new SearchBooksTask().execute(apiUrl);
    }

    // AsyncTask to perform the network request
    // AsyncTask to perform the network request
    private class SearchBooksTask extends AsyncTask<String, Void, SearchResult> {
        @Override
        protected SearchResult doInBackground(String... urls) {
            String apiUrl = urls[0];
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Log the raw JSON response
                    Log.d("API_RESPONSE", "Raw JSON Response: " + response.toString());

                    try {
                        // Parse the JSON response
                        Object rootElement = new JSONTokener(response.toString()).nextValue();

                        if (rootElement instanceof JSONObject) {
                            // Handle the case where the root element is an object
                            JSONObject jsonObject = (JSONObject) rootElement;

                            // Convert the object to a single-element array
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.put(jsonObject);

                            // Extract the 'total_pages' field
                            int totalSearchPages = jsonObject.getInt("total_pages");

                            Log.d("PARSE_JSON", "total pagini" + totalSearchPages);
                            // Call the parseJsonResponse method with the JSONArray
                            Log.d(TAG, "doInBackground: booklist" + jsonArray);
                            List<Book> books = parseJsonResponse(jsonArray);
                            Log.d("PARSE_JSON", "aici am fost" + books);
                            return new SearchResult(books, totalSearchPages);
                        } else if (rootElement instanceof JSONArray) {
                            // Handle the case where the root element is an array
                            JSONArray jsonArray = (JSONArray) rootElement;

                            // Extract the 'total_pages' field
                            int totalSearchPages = jsonArray.getJSONObject(0).getInt("total_pages");

                            // Call the parseJsonResponse method with the JSONArray
                            List<Book> books = parseJsonResponse(jsonArray);

                            return new SearchResult(books, totalSearchPages);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSON_PARSE_ERROR", "Error parsing JSON: " + e.getMessage());
                    }
                } else {
                    Log.e("HTTP_ERROR", "HTTP error code: " + responseCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("IO_ERROR", "IO error: " + e.getMessage());
            }
            return null;
        }


        @Override
        protected void onPostExecute(SearchResult searchResult) {
            if (searchResult != null) {
                List<Book> searchResults = searchResult.getResults();
                int totalSearchPages = searchResult.getTotalPages();
                Log.d("DEBUG", "currentPage: " + currentPage);
                Log.d("DEBUG", "totalPages: " + searchResults);
                if (!searchResults.isEmpty()) {
                    // Update the UI with search results
                    showSearchResults(searchResults);

                    totalSearchResults = searchResults.size();
                    Log.d("DEBUG", "Previous Button Visibility: " + (currentPage > 1));
                    Log.d("DEBUG", "Next Button Visibility: " + (currentPage < totalSearchPages));
                } else {
                    showMessage("No books found");
                }
            } else {
                showMessage("Error fetching search results");
            }
        }
    }


    private List<Book> parseJsonResponse(JSONArray jsonArray) {
        List<Book> books = new ArrayList<>();
        try {

            JSONObject jsonObject = jsonArray.getJSONObject(0);//.getJSONObject("results");
            JSONArray random = jsonObject.getJSONArray("results");
            Log.d("PARSE_JSON", "parsejsonresponse" + random.length());

            for (int i = 0; i < random.length(); i++) {
                JSONObject bookObject = random.getJSONObject(i);
                int bookId = bookObject.optInt("bookid", -1);
                String name = bookObject.optString("name", "Unknown");
                float price = (float) bookObject.optDouble("price", -1.0);
                String link = bookObject.optString("link", "");
                Log.d(TAG, "parseJsonResponse: " + bookId);
                if (bookId != -1) {
                    // Only add the item if bookId is present
                    Book book = new Book(name, link, price, bookId);
                    Log.d(TAG, "parseJsonResponse: "+ book.getName());
                    books.add(book);
                } else {
                    // Log a message for items with missing bookId
                    Log.d("PARSE_JSON", "Book ID is missing for item " + i + ": " + bookObject.toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "parseJsonResponse: " + books.get(0));
        return books;
    }



    private void showMessage(String message) {
        Toast.makeText(SearchActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showSearchResults(List<Book> searchResults) {
        Log.d(TAG, "showSearchResults: clicked");
        Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class);

        // Pass the searchResults list
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("searchResults", new ArrayList<Book>(searchResults));
        intent.putExtras(bundle);

        // Pass the search query
        intent.putExtra("query", query);

        startActivity(intent);
    }



}
