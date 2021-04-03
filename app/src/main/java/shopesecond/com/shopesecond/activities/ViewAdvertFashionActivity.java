package shopesecond.com.shopesecond.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker;

import java.net.MalformedURLException;
import java.net.URL;

import shopesecond.com.shopesecond.R;
import shopesecond.com.shopesecond.models.AdvertFashion;


public class ViewAdvertFashionActivity extends Base implements View.OnClickListener {

    ImageView imageViewProduct;
    TextView textViewTitle, textViewPrice, textViewType, textViewSize, textViewLocation, textViewDetails, textViewUser;
    EditText EditTextTitle, EditTextPrice, EditTextType, EditTextSize, EditTextDetails;
    AutoCompleteTextView autoCompleteCounty;
    RadioGroup RadioGroupType;
    Spinner spinnerClothingSizes;
    ScrollableNumberPicker snp_shoesSizes;
    Button buttonUpdate, buttonDelete, buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_advert_fashion);

        imageViewProduct = findViewById(R.id.imageViewProduct);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewPrice = findViewById(R.id.textViewPrice);
        textViewType = findViewById(R.id.textViewType);
        textViewSize = findViewById(R.id.textViewSize);
        textViewLocation = findViewById(R.id.textViewLocation);
        textViewDetails = findViewById(R.id.textViewDetails);
        EditTextTitle = findViewById(R.id.EditTextTitle);
        EditTextPrice = findViewById(R.id.EditTextPrice);
        EditTextType = findViewById(R.id.EditTextType);
        RadioGroupType = findViewById(R.id.typeRadioGroup);
        EditTextSize = findViewById(R.id.EditTextSize);
        autoCompleteCounty = findViewById(R.id.autoCompleteCounty);
        EditTextDetails = findViewById(R.id.EditTextDetails);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonDelete = findViewById(R.id.buttonDelete);
        buttonSave = findViewById(R.id.buttonSave);
        spinnerClothingSizes = findViewById(R.id.clothingSizeSpinner);
        snp_shoesSizes = findViewById(R.id.snp_shoeSizes);
        textViewUser = findViewById(R.id.textViewUser);

        // Populate fields with values from selected position from ListView
        // Passed values with Bundle from BrowseActivity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            try {
                // Download URL for image from Firebase Storage
                URL downloadURL = new URL(bundle.getString("image"));
                // Load image URL into ImageView
                Glide
                        .with(ViewAdvertFashionActivity.this)
                        .load(downloadURL)
                        .apply(new RequestOptions()
                                .centerCrop()
                                .placeholder(R.mipmap.ic_launcher_round)
                                .error(R.mipmap.ic_launcher_round))
                        .into(imageViewProduct);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            String postedByStr = "Posted by: " + bundle.getString("userEmail");
            textViewUser.setText(postedByStr);
            EditTextTitle.setText(bundle.getString("title"));
            EditTextPrice.setText(bundle.getString("price"));
            EditTextType.setText(bundle.getString("type"));
            EditTextSize.setText(bundle.getString("size"));
            autoCompleteCounty.setText(bundle.getString("location"));
            EditTextDetails.setText(bundle.getString("description"));
        }

        // Set onClickListeners for buttons
        buttonUpdate.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);

        // Make fields disabled by default
        EditTextTitle.setEnabled(false);
        EditTextPrice.setEnabled(false);
        EditTextType.setEnabled(false);
        EditTextSize.setEnabled(false);
        autoCompleteCounty.setEnabled(false);
        EditTextDetails.setEnabled(false);

        // Set the max input length of title to 25 characters
        InputFilter[] filter = new InputFilter[1];
        filter[0] = new InputFilter.LengthFilter(25);
        EditTextTitle.setFilters(filter);

        // Set the max length of price to 3
        filter[0] = new InputFilter.LengthFilter(3);
        EditTextPrice.setFilters(filter);

        // Set the max length of location to 9
        filter[0] = new InputFilter.LengthFilter(9);
        autoCompleteCounty.setFilters(filter);

        // Set the max length of details to 50
        filter[0] = new InputFilter.LengthFilter(100);
        EditTextDetails.setFilters(filter);

        // Load string-array from resources to give suggestions
        // to the user when they start typing
        ArrayAdapter<String> arrayAdapterCounties = new ArrayAdapter<>(ViewAdvertFashionActivity.this, android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.country));
        autoCompleteCounty.setAdapter(arrayAdapterCounties);
        // Show suggestions after 1 symbol is typed
        autoCompleteCounty.setThreshold(1);
    }

    /**
     * Handle onClick events
     */
    @Override
    public void onClick(View v) {
        if (v == buttonUpdate) {
            // Hide update and show save button
            buttonUpdate.setVisibility(View.GONE);
            buttonSave.setVisibility(View.VISIBLE);

            // Hide EditText and show RadioGroup
            RadioGroupType.setVisibility(View.VISIBLE);
            EditTextType.setVisibility(View.GONE);
            EditTextSize.setVisibility(View.GONE);

            // Show widgets depending on radio button clicked
            RadioGroupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.clothing_radioButton) {
                        spinnerClothingSizes.setVisibility(View.VISIBLE);
                        snp_shoesSizes.setVisibility(View.GONE);
                    } else {
                        spinnerClothingSizes.setVisibility(View.GONE);
                        snp_shoesSizes.setVisibility(View.VISIBLE);
                    }
                }
            });

            // Populate string-array with clothing sizes to the spinner
            ArrayAdapter<String> myAdapter = new ArrayAdapter<>(ViewAdvertFashionActivity.this,
                    R.layout.spinner_item, getResources().getStringArray(R.array.clothingSizes));
            myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerClothingSizes.setAdapter(myAdapter);

            // Enable editing text
            EditTextTitle.setEnabled(true);
            EditTextPrice.setEnabled(true);
            RadioGroupType.setEnabled(true);
            autoCompleteCounty.setEnabled(true);
            EditTextDetails.setEnabled(true);

            // Determine which widget will be needed
            if (EditTextType.getText().toString().equals("Clothing")) {
                RadioGroupType.check(R.id.clothing_radioButton);
                spinnerClothingSizes.setVisibility(View.VISIBLE);
            } else {
                RadioGroupType.check(R.id.shoes_radioButton);
                snp_shoesSizes.setVisibility(View.VISIBLE);
            }

            // Set onClickListener for save button
            buttonSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Check if the user entered an existing county
                    autoCompleteCounty.setValidator(new AutoCompleteTextView.Validator() {
                        @Override
                        public boolean isValid(CharSequence text) {
                            for (int j = 0; j < getResources().getStringArray(R.array.country).length; j++) {
                                String currentElement = getResources().getStringArray(R.array.country)[j];
                                if (autoCompleteCounty.getText().toString().trim().equals(currentElement)) {
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

                    // Check if there are empty fields and set errors to alert the user
                    if (TextUtils.isEmpty(EditTextTitle.getText())) {
                        EditTextTitle.setError("Title is required!");
                        EditTextTitle.requestFocus();
                    } else if (TextUtils.isEmpty(EditTextPrice.getText())) {
                        EditTextPrice.setError("Price is required!");
                        EditTextPrice.requestFocus();
                    } else if (TextUtils.isEmpty(autoCompleteCounty.getText()) || !autoCompleteCounty.getValidator().isValid(autoCompleteCounty.getText())) {
                        autoCompleteCounty.setError("Empty or invalid county!");
                        autoCompleteCounty.requestFocus();
                    } else if (TextUtils.isEmpty(EditTextDetails.getText())) {
                        EditTextDetails.setError("Details field is required!");
                        EditTextDetails.requestFocus();
                    } else {
                        Bundle bundle = getIntent().getExtras();
                        // Get values from fields
                        String id = bundle.getString("id");
                        int position = bundle.getInt("pos");
                        String image = bundle.getString("image");
                        String title = EditTextTitle.getText().toString();
                        String priceStr = EditTextPrice.getText().toString();
                        double price = Double.valueOf(priceStr);
                        int radioID = RadioGroupType.getCheckedRadioButtonId();
                        String type;
                        // Set value of type for each radio button clicked
                        if (radioID == R.id.clothing_radioButton) {
                            type = "Clothing";
                        } else {
                            type = "Shoes";
                        }
                        String size;
                        if (type.equals("Clothing")) {
                            size = spinnerClothingSizes.getSelectedItem().toString();
                        } else {
                            size = String.valueOf(snp_shoesSizes.getValue());
                        }
                        String location = autoCompleteCounty.getText().toString();
                        String description = EditTextDetails.getText().toString();
                        String email = bundle.getString("userEmail");

                        // Create the updated Advert
                        AdvertFashion fashionAd = new AdvertFashion(id, image, title, price, type, size, location, description, email);
                        // Update database and arrayList
                        databaseFashionAds.child(id).setValue(fashionAd);
                        fashionAdverts.set(position, fashionAd);
                        Toast.makeText(getApplicationContext(), "Successfully updated advert!", Toast.LENGTH_SHORT).show();

                        // Hide save and show update button
                        buttonSave.setVisibility(View.GONE);
                        buttonUpdate.setVisibility(View.VISIBLE);

                        // Disable editing text
                        EditTextTitle.setEnabled(false);
                        EditTextPrice.setEnabled(false);
                        autoCompleteCounty.setEnabled(false);
                        EditTextDetails.setEnabled(false);

                        // Hide/show widgets
                        RadioGroupType.setVisibility(View.GONE);
                        snp_shoesSizes.setVisibility(View.GONE);
                        spinnerClothingSizes.setVisibility(View.GONE);
                        EditTextType.setVisibility(View.VISIBLE);
                        EditTextSize.setVisibility(View.VISIBLE);

                        EditTextType.setText(type);
                        EditTextSize.setText(size);
                    }
                }
            });
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewAdvertFashionActivity.this);
            alertDialogBuilder.setTitle("You are about to delete an advert!");
            alertDialogBuilder.setMessage("Really delete this advert?");
            alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Bundle bundle = getIntent().getExtras();
                    // Get position
                    String id = bundle.getString("id");
                    // Get advert at clicked position from database
                    DatabaseReference clickedPos = databaseFashionAds.child(id);
                    // Removing advert from database and arrayList
                    clickedPos.removeValue();

                    // Iterate through array to find element with specific ID
                    for (int j = 0; j < fashionAdverts.size(); j++) {
                        AdvertFashion obj = fashionAdverts.get(j);

                        if (obj.getProductID().equals(id)) {
                            //found, delete.
                            fashionAdverts.remove(j);
                            break;
                        }

                    }

                    // Close all previous activities and open BrowseActivity
                    finishAffinity();
                    Intent BrowseIntent = new Intent(getApplicationContext(), BrowseActivity.class);
                    BrowseIntent.putExtra("selectRadioButton", R.id.fashionAd_radioButton);
                    startActivity(BrowseIntent);
                }
            });
            alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialogBuilder.show();
        }
    }


    @Override
    public void onBackPressed() {
        Intent backToBrowse = new Intent(getApplicationContext(), BrowseActivity.class);
        backToBrowse.putExtra("selectRadioButton", R.id.fashionAd_radioButton);
        startActivity(backToBrowse);
    }

}