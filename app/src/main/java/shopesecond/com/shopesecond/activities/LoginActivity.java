package shopesecond.com.shopesecond.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import shopesecond.com.shopesecond.R;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    // Widgets
    private Button buttonLogin;
    private Button buttonRegister;
    private Button mGoogleBtn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressDialog progressDialog;

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "MyLogs";

    // Firebase Auth object
    private FirebaseAuth firebaseAuth;

    // Google API client object
    private GoogleApiClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);

        // Getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            // Means user is already logged in
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Initialising Google API client
        mGoogleSignInClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(), "Error with Google API client!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        // Initialising widgets
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);
        mGoogleBtn = findViewById(R.id.buttonGoogle);

        progressDialog = new ProgressDialog(this);

        // Set onClick listeners to buttons
        buttonLogin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
        mGoogleBtn.setOnClickListener(this);
    }


    private void userLogin() {

        // Get values from widgets
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        // Checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        // If the email and password are not empty
        // displaying a progress dialog
        progressDialog.setMessage("Logging in, please wait...");
        progressDialog.show();

        // Logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Login error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Handle onClick events
     */
    @Override
    public void onClick(View view) {
        if (view == buttonLogin) {
            userLogin();
        } else if (view == buttonRegister) {
            finish();
            startActivity(new Intent(this, RegisterActivity.class));
        } else {
            signIn();
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                progressDialog.setMessage("Loading, please wait...");
                progressDialog.show();
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(getApplicationContext(), "Unsuccessful authentication with Google", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        final AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mGoogleSignInClient.connect();
                            progressDialog.dismiss();
                            Log.v(TAG, "signInWithCredential:success");
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        } else {
                            Log.v(TAG, "signInWithCredential:NOT_success");
                        }
                    }
                });
    }

}