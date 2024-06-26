package com.example.booknest;

// Import necessary libraries
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.booknest.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    // Declare necessary variables for binding, authentication, and progress bar
    private ActivityRegisterBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    // onCreate: Initializes the activity, sets up binding, and defines click listeners
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize view binding for this activity
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Initialize FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance();

        // Retrieve and initially hide the ProgressBar
        progressBar = binding.progressBar;
        progressBar.setVisibility(View.GONE);

        // Set click listener for the back button to navigate to the previous screen
        binding.backBtn.setOnClickListener(v -> onBackPressed());
        // Set click listener for the register button to start the validation process
        binding.registerBtn.setOnClickListener(v -> validateData());
    }

    // validateData: Validates user input before attempting to register the account
    private void validateData() {
        // Retrieve user input and trim whitespace
        String name = binding.nameEt.getText().toString().trim();
        String email = binding.emailEt.getText().toString().trim();
        String password = binding.passwordEt.getText().toString().trim();
        String cPassword = binding.cPasswordEt.getText().toString().trim();

        // Check for empty name
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter your name ...", Toast.LENGTH_SHORT).show();
        }
        // Validate email pattern
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email pattern...!", Toast.LENGTH_SHORT).show();
        }
        // Check for empty password
        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter password...!", Toast.LENGTH_SHORT).show();
        }
        // Check for empty confirmation password
        else if (TextUtils.isEmpty(cPassword)) {
            Toast.makeText(this, "Confirm password...!", Toast.LENGTH_SHORT).show();
        }
        // Check if password and confirmation password match
        else if (!password.equals(cPassword)) {
            Toast.makeText(this, "Password doesn't match...!", Toast.LENGTH_SHORT).show();
        }
        // If all checks pass, proceed to create the user account
        else {
            createUserAccount(name, email, password);
        }
    }

    // createUserAccount: Uses FirebaseAuth to create a new user account
    private void createUserAccount(String name, String email, String password) {
        // Display progress bar while account is being created
        progressBar.setVisibility(View.VISIBLE);
        // Create a new user account with the given email and password
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> updateUserInfo(name, email))
                .addOnFailureListener(e -> {
                    // Hide progress bar and show error message if account creation fails
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // updateUserInfo: Adds additional information about the new user in the Realtime Database
    private void updateUserInfo(String name, String email) {
        // Get the user's unique ID generated by Firebase
        String uid = firebaseAuth.getUid();
        // Create a hashmap to store user information
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("name", name);
        hashMap.put("profileImage", ""); // Placeholder for future profile images
        hashMap.put("userType", "user");
        hashMap.put("timestamp", System.currentTimeMillis());

        // Reference the "Users" node in the Firebase Realtime Database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        // Save the new user's information under their unique ID
        ref.child(uid).setValue(hashMap)
                .addOnSuccessListener(unused -> {
                    // Hide the progress bar, notify the user, and start the dashboard activity
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Account created...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, DashboardUserActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Hide the progress bar and display an error message on failure
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
