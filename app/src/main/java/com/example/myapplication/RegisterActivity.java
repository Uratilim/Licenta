package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextEmail, editTextPassword;
    private Button buttonRegister;
    private ImageButton buttonTogglePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonTogglePassword = findViewById(R.id.buttonTogglePassword);

        buttonTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle password visibility
                if (editTextPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    // Password is currently visible, so hide it
                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    buttonTogglePassword.setImageResource(R.drawable.ic_visibility_off);
                } else {
                    // Password is currently hidden, so show it
                    editTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    buttonTogglePassword.setImageResource(R.drawable.ic_visibility_on);
                }

                // Move the cursor to the end of the password text
                editTextPassword.setSelection(editTextPassword.getText().length());
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the input values
                String username = editTextUsername.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Validate credentials
                String validationMessage = validateCredentials(username, email, password);
                if (validationMessage.equals("Valid")) {
                    // Create a JSON object with the user data
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("username", username);
                        jsonObject.put("email", email);
                        jsonObject.put("password", password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Make the API request to register the user
                    RegisterTask registerTask = new RegisterTask();
                    registerTask.execute(jsonObject.toString());
                } else {
                    Toast.makeText(RegisterActivity.this, validationMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String validateCredentials(String username, String email, String password) {
        // Validate fields
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            return "Invalid credentials. All fields are required.";
        }

        // Additional requirements for credentials
        if (username.length() < 3 || username.length() > 20) {
            return "Invalid credentials. Username must be between 3 and 20 characters.";
        }

        if (password.length() < 6 || password.length() > 20) {
            return "Invalid credentials. Password must be between 6 and 20 characters.";
        }

        // You can add email validation logic here if needed

        return "Valid";
    }

    private class RegisterTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String apiUrl = "http://10.0.2.2:5000/register";

            try {
                // Create a URL object with the API URL
                URL url = new URL(apiUrl);

                // Create a HttpURLConnection object
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Get the request body data
                String requestBody = params[0];

                // Write the request body data to the connection output stream
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(requestBody.getBytes());
                outputStream.close();

                // Get the response code
                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    //Registration successful
                    return "Registration successful";
                } else if (responseCode == HttpURLConnection.HTTP_CONFLICT) {
                    // Email already registered
                    return "Email already registered";
                } else {
                    // Other error
                    return "Registration failed";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Registration failed";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Handle the result of the background task
            Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }
}
