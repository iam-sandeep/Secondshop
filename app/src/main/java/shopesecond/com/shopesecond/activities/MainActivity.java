package shopesecond.com.shopesecond.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import shopesecond.com.shopesecond.R;


public class MainActivity extends Base implements View.OnClickListener {

    // Widgets
    private TextView textViewUserEmail;
    private Button sellButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialising widgets
        textViewUserEmail = findViewById(R.id.textViewUserEmail);
        sellButton = findViewById(R.id.sellButton);
        buttonGeneral = findViewById(R.id.buttonGeneral);
        buttonFashion = findViewById(R.id.buttonFashion);
        buttonCar = findViewById(R.id.buttonCar);

        // Set listeners for buttons
        sellButton.setOnClickListener(this);
        buttonGeneral.setOnClickListener(this);
        buttonFashion.setOnClickListener(this);
        buttonCar.setOnClickListener(this);

        // Initialising Firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialising Google API client
        mGoogleSignInClient = createGoogleClient();

        // Getting current user

        FirebaseUser user = firebaseAuth.getCurrentUser();

        // If the user is not logged in
        // that means current user will return null
        if (user == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Displaying logged in user name
        String welcomeUser = "Welcome, " + user.getEmail() + "!";
        textViewUserEmail.setText(welcomeUser);
    }


    /**
     * Load adverts from Firebase Database when onStart state is triggered
     */
    @Override
    protected void onStart() {
        super.onStart();

        loadAdverts();
        loadFashionAdverts();
        loadCarAdverts();
    }


    /**
     * Handle onClick events
     */
    @Override
    public void onClick(View v) {
        if (v == sellButton) {
            sellButtonPressed(v);
        } else if (v == buttonGeneral) {
            Intent BrowseIntent = new Intent(getApplicationContext(), BrowseActivity.class);
            BrowseIntent.putExtra("selectRadioButton", R.id.advert_radioButton);
            startActivity(BrowseIntent);
        } else if (v == buttonFashion) {
            Intent BrowseIntent = new Intent(getApplicationContext(), BrowseActivity.class);
            BrowseIntent.putExtra("selectRadioButton", R.id.fashionAd_radioButton);
            startActivity(BrowseIntent);
        } else {
            Intent BrowseIntent = new Intent(getApplicationContext(), BrowseActivity.class);
            BrowseIntent.putExtra("selectRadioButton", R.id.carAd_radioButton);
            startActivity(BrowseIntent);
        }
    }

}