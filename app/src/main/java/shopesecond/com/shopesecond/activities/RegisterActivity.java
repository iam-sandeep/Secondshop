package shopesecond.com.shopesecond.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import shopesecond.com.shopesecond.R;
import shopesecond.com.shopesecond.models.User;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    // Widgets
    private EditText editTextEmail;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private AutoCompleteTextView autoCompleteCounty;
    private Button buttonRegister;
    private ProgressDialog progressDialog;

    // Defining FirebaseAuth object
    private FirebaseAuth firebaseAuth;

    // Database reference object
    private DatabaseReference databaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialising firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            // That means user is already logged in
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        // Getting the reference of Users node
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        // Initialising widgets
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        autoCompleteCounty = findViewById(R.id.autoCompleteCounty);
        buttonRegister = findViewById(R.id.buttonRegister);
        progressDialog = new ProgressDialog(this);

        // Load string-array from resources to give suggestions
        // to the user when they start typing
        ArrayAdapter<String> arrayAdapterCounties = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.country));
        autoCompleteCounty.setAdapter(arrayAdapterCounties);
        // Show suggestions after 1 symbol is typed
        autoCompleteCounty.setThreshold(1);

        // Set max input length of autoCompleteCounty to 9 chars
        InputFilter[] filter = new InputFilter[1];
        filter[0] = new InputFilter.LengthFilter(9);
        autoCompleteCounty.setFilters(filter);

        // Set onClick listeners for the buttons
        buttonRegister.setOnClickListener(this);
    }

    private void registerUser() {

        // Get input values from widgets
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String username = editTextUsername.getText().toString().trim();
        final String county = autoCompleteCounty.getText().toString().trim();

        // Check if the user entered an existing county
        autoCompleteCounty.setValidator(new AutoCompleteTextView.Validator() {
            @Override
            public boolean isValid(CharSequence text) {
                for (int j = 0; j < getResources().getStringArray(R.array.country).length; j++) {
                    String currentElement = getResources().getStringArray(R.array.country)[j];
                    if (county.equals(currentElement)) {
                        Log.v("MyLogs", "FOUND COUNTY IN ARRAY!");
                        return true;
                    }
                }
                return false;
            }

            @Override
            public CharSequence fixText(CharSequence invalidText) {
                return null;
            }
        });
        autoCompleteCounty.performValidation();

        // Checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
        } else if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Username is required!");
            editTextUsername.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
        } else if (TextUtils.isEmpty(county) || !autoCompleteCounty.getValidator().isValid(county)) {
            autoCompleteCounty.setError("Empty or invalid county!");
            autoCompleteCounty.requestFocus();
        } else {

            // If the email and password are not empty
            // display a progress dialog
            progressDialog.setMessage("Registering, please wait...");
            progressDialog.show();

            // Creating a new user
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // Checking if successful
                            if (task.isSuccessful()) {
                                // Getting the created user
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                String id = firebaseUser.getUid();

                                // Create new User object to store extra data about user
                                User user = new User(id, email, username, password, county);

                                // Store in Firebase database
                                databaseUsers.child(id).setValue(user);

                                // Close activity
                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Toast.makeText(RegisterActivity.this, "Registration error", Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
        }
    }


    /**
     * Handle onClick events
     */
    @Override
    public void onClick(View view) {

        if (view == buttonRegister) {
            registerUser();
        }

    }
}