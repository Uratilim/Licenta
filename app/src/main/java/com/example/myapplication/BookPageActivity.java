package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BookPageActivity extends AppCompatActivity {

    private int selectedBookId; // Updated to store the book ID
    private ImageView imageViewBook;
    private TextView bookNameTextView, okianPriceTextView;
    private LinearLayout linearLayoutStores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_page);


        Bundle bundle = getIntent().getExtras();
        Book bookPrint  = bundle.getParcelable("bookprint");
        Log.d(TAG, "onCreate: array list = " + bookPrint);
        // Receive the book ID
        selectedBookId = getIntent().getIntExtra("bookId", -1); // -1 is a default value if the key is not found

        if (selectedBookId != -1) {
            // Fetch book details from your data source based on the book ID
            Book book = fetchBookDetailsById(selectedBookId); // Updated method call
            initializeUI(book);
        }
    }

    private Book fetchBookDetailsById(int bookId) {
        // Initialize connection parameters
        String url = "jdbc:sqlserver://DESKTOP-JF79DLE\\MSSQLSERVER01:port;databaseName=AplicatieLicenta";
        String username = "";
        String password = "";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Establish a database connection
            connection = DriverManager.getConnection(url, username, password);

            // Define your SQL query to fetch book details based on bookId
            String query = "SELECT name, booklink, price FROM books WHERE bookid = ?";

            // Create a prepared statement
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, bookId);

            // Execute the query
            resultSet = preparedStatement.executeQuery();

            // Check if a result is available
            if (resultSet.next()) {
                String title = resultSet.getString("name");
                String link = resultSet.getString("booklink");
                float price = resultSet.getFloat("price");

                // Create and return the Book object
                return new Book(title, link, price, bookId);
            } else {
                // Book not found
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any database errors here
            return null;
        } finally {
            // Close database resources in a finally block
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeUI(Book book) {

        bookNameTextView = findViewById(R.id.bookNameTextView);
        bookNameTextView.setText(book.getName());
        //okianPriceTextView.setText(book.getPrice());

    }

    private void getBookInformationFromDatabase(String bookName) {
        // Make an API call to your Flask server
        String url = "http://10.0.2.2:5000/search/" + bookName;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle network failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();

                    // Parse the JSON response and extract the book and library information
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONArray bookArray = jsonObject.getJSONArray("book");
                        JSONArray libraryArray = jsonObject.getJSONArray("library");

                        // Process the book information
                        if (bookArray.length() > 0) {
                            JSONObject bookObject = bookArray.getJSONObject(0);
                            String bookPret = bookObject.getString("pret");
                            String bookLink = bookObject.getString("link");

                            // Process the library information
                            List<Store> storeList = new ArrayList<>();
                            for (int i = 0; i < libraryArray.length(); i++) {
                                JSONObject libraryObject = libraryArray.getJSONObject(i);
                                String libraryNume = libraryObject.getString("nume");

                                // Create a Store object with the retrieved information
                                Store store = new Store(libraryNume, bookPret, bookLink);
                                storeList.add(store);
                            }

                            // Update the UI with the retrieved information
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateUI(storeList);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // Handle JSON parsing error
                    }
                } else {
                    // Handle error response
                }
            }
        });
    }

    private void updateUI(List<Store> storeList) {
        // Update the UI with the retrieved information
        bookNameTextView.setText("Book Name"); // Set the book name here

        // Inflate and add store views to the layout
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Store store : storeList) {
            View storeView = inflater.inflate(R.layout.item_store, linearLayoutStores, false);

            ImageView imageViewStoreLogo = storeView.findViewById(R.id.imageViewStoreLogo);
            TextView textViewStoreName = storeView.findViewById(R.id.textViewStoreName);
            TextView textViewStorePrice = storeView.findViewById(R.id.textViewStorePrice);

            //imageViewStoreLogo.setImageResource(store.getLogo());
            textViewStoreName.setText(store.getName());
            textViewStorePrice.setText(store.getPrice());

            linearLayoutStores.addView(storeView);
        }
    }
}
