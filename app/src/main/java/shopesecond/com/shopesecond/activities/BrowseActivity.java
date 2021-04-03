package shopesecond.com.shopesecond.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.net.MalformedURLException;
import java.net.URL;


import shopesecond.com.shopesecond.R;
import shopesecond.com.shopesecond.adapters.AdvertAdapter;
import shopesecond.com.shopesecond.adapters.AdvertCarAdapter;

import shopesecond.com.shopesecond.adapters.AdvertFashionAdapter;
import shopesecond.com.shopesecond.models.Advert;
import shopesecond.com.shopesecond.models.AdvertCar;
import shopesecond.com.shopesecond.models.AdvertFashion;

public class BrowseActivity extends Base {

    // Widgets
    ListView productsView;
    ListView fashionProductsView;
    ListView carsView;
    TextView browseEmptyDefaultText;
    RadioGroup choice_radio_group;
    TextView emptyAdvertCategory, lvTitle, lvPrice, lvLocation, filler, lvMake, lvModel, lvYear, lvCarPrice;
    ImageButton searchButton;
    EditText EditTextSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        // Initialise widgets
        emptyAdvertCategory = findViewById(R.id.emptyAdvertCategory);
        emptyAdvertCategory.setVisibility(View.GONE);
        productsView = findViewById(R.id.productsView);
        fashionProductsView = findViewById(R.id.fashionProductsView);
        carsView = findViewById(R.id.carsView);
        choice_radio_group = findViewById(R.id.choice_radio_group);
        browseEmptyDefaultText = findViewById(R.id.browseEmptyDefaultText);
        searchButton = findViewById(R.id.searchButton);
        EditTextSearch = findViewById(R.id.EditTextSearch);
        lvTitle = findViewById(R.id.lvTitle);
        lvPrice = findViewById(R.id.lvPrice);
        lvLocation = findViewById(R.id.lvLocation);
        filler = findViewById(R.id.filler);
        lvMake = findViewById(R.id.lvMake);
        lvModel = findViewById(R.id.lvModel);
        lvCarPrice = findViewById(R.id.lvCarPrice);
        lvYear = findViewById(R.id.lvYear);

        // Initialising Google API client
        mGoogleSignInClient = createGoogleClient();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = EditTextSearch.getText().toString();
                String type;
                int radioID = choice_radio_group.getCheckedRadioButtonId();
                if (radioID == R.id.advert_radioButton) {
                    type = "General";
                } else if (radioID == R.id.fashionAd_radioButton) {
                    type = "Fashion";
                } else {
                    type = "Car";
                }
                firebaseUserSearch(searchText, type);
            }
        });


        // Bind adapter to ListView
        final AdvertAdapter adapter = new AdvertAdapter(this, adverts);
        productsView.setAdapter(adapter);

        // Display AlertDialog with confirmation whether to delete this ad
        productsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BrowseActivity.this);
                alertDialogBuilder.setTitle("You are about to delete an advert!");
                alertDialogBuilder.setMessage("Really delete this advert?");
                alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get item at clicked position
                        Advert advert = (Advert) parent.getItemAtPosition(position);
                        DatabaseReference clickedPos = databaseAds.child(advert.getProductID());
                        // Delete from Firebase
                        clickedPos.removeValue();

                        // Iterate through array to find element with specific ID
                        for (int j = 0; j < adverts.size(); j++) {
                            Advert obj = adverts.get(j);

                            if (obj.getProductID().equals(advert.getProductID())) {
                                // Found, delete.
                                adverts.remove(j);
                                // Notify adapter of changed data
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }
                        dialog.dismiss();
                        if (adverts.isEmpty() && fashionAdverts.isEmpty() && carAdverts.isEmpty()) {
                            finishAffinity();
                            Intent backToMain = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(backToMain);
                        } else if (adverts.isEmpty()) {
                            emptyAdvertCategory.setVisibility(View.VISIBLE);
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialogBuilder.show();
                return true;
            }
        });

        // Open up activity to view individual advert
        // passing the data with Bundle
        productsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent ViewAdvertIntent = new Intent(getApplicationContext(), ViewAdvertActivity.class);
                // Put extras into the Bundle
                Bundle b = new Bundle();
                Advert advert = (Advert) parent.getItemAtPosition(position);
                b.putInt("pos", position);
                b.putString("id", advert.getProductID());
                b.putString("image", advert.getImageUri());
                b.putString("title", advert.getProductTitle());
                b.putString("price", Double.toString(advert.getProductPrice()));
                b.putString("location", advert.getProductLocation());
                b.putString("description", advert.getProductDescription());
                b.putString("userEmail", advert.getUserEmail());
                ViewAdvertIntent.putExtras(b);
                // Start ViewAdvertActivity
                startActivityForResult(ViewAdvertIntent, 0);
            }
        });


        // Bind adapter to ListView
        final AdvertFashionAdapter adapterFashion = new AdvertFashionAdapter(this, fashionAdverts);
        fashionProductsView.setAdapter(adapterFashion);


        // Display AlertDialog with confirmation whether to delete this ad
        fashionProductsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BrowseActivity.this);
                alertDialogBuilder.setTitle("You are about to delete an advert!");
                alertDialogBuilder.setMessage("Really delete this advert?");
                alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get item at clicked position
                        AdvertFashion advert = (AdvertFashion) parent.getItemAtPosition(position);
                        DatabaseReference clickedPos = databaseFashionAds.child(advert.getProductID());
                        // Delete from Firebase
                        clickedPos.removeValue();

                        // Iterate through array to find element with specific ID
                        for (int j = 0; j < fashionAdverts.size(); j++) {
                            AdvertFashion obj = fashionAdverts.get(j);

                            if (obj.getProductID().equals(advert.getProductID())) {
                                // Found, delete.
                                fashionAdverts.remove(j);
                                // Notify adapter of changed data
                                adapterFashion.notifyDataSetChanged();
                                break;
                            }
                        }
                        dialog.dismiss();
                        if (adverts.isEmpty() && fashionAdverts.isEmpty() && carAdverts.isEmpty()) {
                            finishAffinity();
                            Intent backToMain = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(backToMain);
                        } else if (fashionAdverts.isEmpty()) {
                            emptyAdvertCategory.setVisibility(View.VISIBLE);
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialogBuilder.show();
                return true;
            }
        });

        // Open up activity to view individual advert
        // passing the data with Bundle
        fashionProductsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent ViewAdvertFashionIntent = new Intent(getApplicationContext(), ViewAdvertFashionActivity.class);
                // Put extras into the Bundle
                Bundle b = new Bundle();
                AdvertFashion advertFashion = (AdvertFashion) parent.getItemAtPosition(position);
                b.putInt("pos", position);
                b.putString("id", advertFashion.getProductID());
                b.putString("image", advertFashion.getImageUri());
                b.putString("title", advertFashion.getProductTitle());
                b.putString("price", Double.toString(advertFashion.getProductPrice()));
                b.putString("type", advertFashion.getProductType());
                b.putString("size", advertFashion.getProductSize());
                b.putString("location", advertFashion.getProductLocation());
                b.putString("description", advertFashion.getProductDescription());
                b.putString("userEmail", advertFashion.getUserEmail());
                ViewAdvertFashionIntent.putExtras(b);
                // Start ViewAdvertActivity
                startActivityForResult(ViewAdvertFashionIntent, 0);
            }
        });


        final AdvertCarAdapter adapterCar = new AdvertCarAdapter(this, carAdverts);
        carsView.setAdapter(adapterCar);

        // Display AlertDialog with confirmation whether to delete this ad
        carsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BrowseActivity.this);
                alertDialogBuilder.setTitle("You are about to delete an advert!");
                alertDialogBuilder.setMessage("Really delete this advert?");
                alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get item at clicked position
                        AdvertCar advert = (AdvertCar) parent.getItemAtPosition(position);
                        DatabaseReference clickedPos = databaseCarAds.child(advert.getCarID());
                        // Delete from Firebase
                        clickedPos.removeValue();

                        // Iterate through array to find element with specific ID
                        for (int j = 0; j < carAdverts.size(); j++) {
                            AdvertCar obj = carAdverts.get(j);

                            if (obj.getCarID().equals(advert.getCarID())) {
                                // Found, delete.
                                carAdverts.remove(j);
                                // Notify adapter of changed data
                                adapterCar.notifyDataSetChanged();
                                break;
                            }
                        }
                        dialog.dismiss();
                        if (adverts.isEmpty() && fashionAdverts.isEmpty() && carAdverts.isEmpty()) {
                            finishAffinity();
                            Intent backToMain = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(backToMain);
                        } else if (carAdverts.isEmpty()) {
                            emptyAdvertCategory.setVisibility(View.VISIBLE);
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialogBuilder.show();
                return true;
            }
        });

        // Open up activity to view individual advert
        // passing the data with Bundle
        carsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent ViewAdvertCarIntent = new Intent(getApplicationContext(), ViewAdvertCarActivity.class);
                // Put extras into the Bundle
                Bundle b = new Bundle();
                AdvertCar advertCar = (AdvertCar) parent.getItemAtPosition(position);
                b.putInt("pos", position);
                b.putString("id", advertCar.getCarID());
                b.putString("image", advertCar.getImageUri());
                b.putString("make", advertCar.getCarMake());
                b.putString("model", advertCar.getCarModel());
                b.putInt("year", advertCar.getCarYear());
                b.putString("price", Double.toString(advertCar.getCarPrice()));
                b.putString("location", advertCar.getCarLocation());
                b.putString("description", advertCar.getCarDescription());
                b.putString("userEmail", advertCar.getUserEmail());
                ViewAdvertCarIntent.putExtras(b);
                // Start ViewAdvertActivity
                startActivityForResult(ViewAdvertCarIntent, 0);
            }
        });

        // Get intent extra with value of radio button from View activity
        int selectedRadioButton = getIntent().getIntExtra("selectRadioButton", R.id.advert_radioButton);
        Log.v("MyLogs", String.valueOf(selectedRadioButton));

        switch (selectedRadioButton) {
            case R.id.advert_radioButton:
                // if there are no adverts, hide all ListViews
                // and only show the emptyCategory TextView
                if (adverts.isEmpty()) {
                    emptyAdvertCategory.setVisibility(View.VISIBLE);
                    choice_radio_group.check(R.id.advert_radioButton);
                    productsView.setVisibility(View.GONE);
                    fashionProductsView.setVisibility(View.GONE);
                    carsView.setVisibility(View.GONE);
                    filler.setVisibility(View.GONE);
                    lvTitle.setVisibility(View.GONE);
                    lvLocation.setVisibility(View.GONE);
                    lvPrice.setVisibility(View.GONE);
                    lvMake.setVisibility(View.GONE);
                    lvModel.setVisibility(View.GONE);
                    lvCarPrice.setVisibility(View.GONE);
                    lvYear.setVisibility(View.GONE);
                }
                // if adverts is not empty, show respective ListView
                // and hide emptyCategory TextView
                else {
                    emptyAdvertCategory.setVisibility(View.GONE);
                    choice_radio_group.check(R.id.advert_radioButton);
                    productsView.setVisibility(View.VISIBLE);
                    fashionProductsView.setVisibility(View.GONE);
                    carsView.setVisibility(View.GONE);
                    filler.setVisibility(View.VISIBLE);
                    lvTitle.setVisibility(View.VISIBLE);
                    lvLocation.setVisibility(View.VISIBLE);
                    lvPrice.setVisibility(View.VISIBLE);
                    lvMake.setVisibility(View.GONE);
                    lvModel.setVisibility(View.GONE);
                    lvCarPrice.setVisibility(View.GONE);
                    lvYear.setVisibility(View.GONE);
                }
                break;
            case R.id.fashionAd_radioButton:
                if (fashionAdverts.isEmpty()) {
                    emptyAdvertCategory.setVisibility(View.VISIBLE);
                    choice_radio_group.check(R.id.fashionAd_radioButton);
                    productsView.setVisibility(View.GONE);
                    fashionProductsView.setVisibility(View.GONE);
                    carsView.setVisibility(View.GONE);
                    filler.setVisibility(View.GONE);
                    lvTitle.setVisibility(View.GONE);
                    lvLocation.setVisibility(View.GONE);
                    lvPrice.setVisibility(View.GONE);
                    lvMake.setVisibility(View.GONE);
                    lvModel.setVisibility(View.GONE);
                    lvCarPrice.setVisibility(View.GONE);
                    lvYear.setVisibility(View.GONE);
                } else {
                    emptyAdvertCategory.setVisibility(View.GONE);
                    choice_radio_group.check(R.id.fashionAd_radioButton);
                    productsView.setVisibility(View.GONE);
                    fashionProductsView.setVisibility(View.VISIBLE);
                    carsView.setVisibility(View.GONE);
                    filler.setVisibility(View.VISIBLE);
                    lvTitle.setVisibility(View.VISIBLE);
                    lvLocation.setVisibility(View.VISIBLE);
                    lvPrice.setVisibility(View.VISIBLE);
                    lvMake.setVisibility(View.GONE);
                    lvModel.setVisibility(View.GONE);
                    lvCarPrice.setVisibility(View.GONE);
                    lvYear.setVisibility(View.GONE);
                }
                break;
            case R.id.carAd_radioButton:
                if (carAdverts.isEmpty()) {
                    emptyAdvertCategory.setVisibility(View.VISIBLE);
                    choice_radio_group.check(R.id.carAd_radioButton);
                    productsView.setVisibility(View.GONE);
                    fashionProductsView.setVisibility(View.GONE);
                    carsView.setVisibility(View.GONE);
                    filler.setVisibility(View.GONE);
                    lvTitle.setVisibility(View.GONE);
                    lvLocation.setVisibility(View.GONE);
                    lvPrice.setVisibility(View.GONE);
                    lvMake.setVisibility(View.GONE);
                    lvModel.setVisibility(View.GONE);
                    lvCarPrice.setVisibility(View.GONE);
                    lvYear.setVisibility(View.GONE);
                } else {
                    emptyAdvertCategory.setVisibility(View.GONE);
                    choice_radio_group.check(R.id.carAd_radioButton);
                    productsView.setVisibility(View.GONE);
                    fashionProductsView.setVisibility(View.GONE);
                    carsView.setVisibility(View.VISIBLE);
                    filler.setVisibility(View.VISIBLE);
                    lvTitle.setVisibility(View.GONE);
                    lvLocation.setVisibility(View.GONE);
                    lvPrice.setVisibility(View.GONE);
                    lvMake.setVisibility(View.VISIBLE);
                    lvModel.setVisibility(View.VISIBLE);
                    lvCarPrice.setVisibility(View.VISIBLE);
                    lvYear.setVisibility(View.VISIBLE);
                }
                break;
        }

        // Manage which ListView is visible depending on selected radio button
        choice_radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.advert_radioButton) {
                    // Clear search box
                    EditTextSearch.setText(null);

                    // if there are no adverts, hide all ListViews
                    // and only show the emptyCategory TextView
                    if (adverts.isEmpty()) {
                        emptyAdvertCategory.setVisibility(View.VISIBLE);
                        productsView.setVisibility(View.GONE);
                        fashionProductsView.setVisibility(View.GONE);
                        carsView.setVisibility(View.GONE);
                        filler.setVisibility(View.GONE);
                        lvTitle.setVisibility(View.GONE);
                        lvLocation.setVisibility(View.GONE);
                        lvPrice.setVisibility(View.GONE);
                        lvMake.setVisibility(View.GONE);
                        lvModel.setVisibility(View.GONE);
                        lvCarPrice.setVisibility(View.GONE);
                        lvYear.setVisibility(View.GONE);
                    }
                    // if adverts is not empty, show respective ListView
                    // and hide emptyCategory TextView
                    else {
                        emptyAdvertCategory.setVisibility(View.GONE);
                        productsView.setVisibility(View.VISIBLE);
                        fashionProductsView.setVisibility(View.GONE);
                        carsView.setVisibility(View.GONE);
                        filler.setVisibility(View.VISIBLE);
                        lvTitle.setVisibility(View.VISIBLE);
                        lvLocation.setVisibility(View.VISIBLE);
                        lvPrice.setVisibility(View.VISIBLE);
                        lvMake.setVisibility(View.GONE);
                        lvModel.setVisibility(View.GONE);
                        lvCarPrice.setVisibility(View.GONE);
                        lvYear.setVisibility(View.GONE);
                    }
                } else if (checkedId == R.id.fashionAd_radioButton) {
                    EditTextSearch.setText(null);

                    if (fashionAdverts.isEmpty()) {
                        emptyAdvertCategory.setVisibility(View.VISIBLE);
                        productsView.setVisibility(View.GONE);
                        fashionProductsView.setVisibility(View.GONE);
                        carsView.setVisibility(View.GONE);
                        filler.setVisibility(View.GONE);
                        lvTitle.setVisibility(View.GONE);
                        lvLocation.setVisibility(View.GONE);
                        lvPrice.setVisibility(View.GONE);
                        lvMake.setVisibility(View.GONE);
                        lvModel.setVisibility(View.GONE);
                        lvCarPrice.setVisibility(View.GONE);
                        lvYear.setVisibility(View.GONE);
                    } else {
                        emptyAdvertCategory.setVisibility(View.GONE);
                        productsView.setVisibility(View.GONE);
                        fashionProductsView.setVisibility(View.VISIBLE);
                        carsView.setVisibility(View.GONE);
                        filler.setVisibility(View.VISIBLE);
                        lvTitle.setVisibility(View.VISIBLE);
                        lvLocation.setVisibility(View.VISIBLE);
                        lvPrice.setVisibility(View.VISIBLE);
                        lvMake.setVisibility(View.GONE);
                        lvModel.setVisibility(View.GONE);
                        lvCarPrice.setVisibility(View.GONE);
                        lvYear.setVisibility(View.GONE);
                    }
                } else if (checkedId == R.id.carAd_radioButton) {
                    EditTextSearch.setText(null);

                    if (carAdverts.isEmpty()) {
                        emptyAdvertCategory.setVisibility(View.VISIBLE);
                        productsView.setVisibility(View.GONE);
                        fashionProductsView.setVisibility(View.GONE);
                        carsView.setVisibility(View.GONE);
                        filler.setVisibility(View.VISIBLE);
                        lvTitle.setVisibility(View.GONE);
                        lvLocation.setVisibility(View.GONE);
                        lvPrice.setVisibility(View.GONE);
                        lvMake.setVisibility(View.GONE);
                        lvModel.setVisibility(View.GONE);
                        lvCarPrice.setVisibility(View.GONE);
                        lvYear.setVisibility(View.GONE);
                    } else {
                        emptyAdvertCategory.setVisibility(View.GONE);
                        productsView.setVisibility(View.GONE);
                        fashionProductsView.setVisibility(View.GONE);
                        carsView.setVisibility(View.VISIBLE);
                        filler.setVisibility(View.VISIBLE);
                        lvTitle.setVisibility(View.GONE);
                        lvLocation.setVisibility(View.GONE);
                        lvPrice.setVisibility(View.GONE);
                        lvMake.setVisibility(View.VISIBLE);
                        lvModel.setVisibility(View.VISIBLE);
                        lvCarPrice.setVisibility(View.VISIBLE);
                        lvYear.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        if (!adverts.isEmpty() || !fashionAdverts.isEmpty() || !carAdverts.isEmpty()) {
            browseEmptyDefaultText.setVisibility(View.GONE);
            choice_radio_group.setVisibility(View.VISIBLE);
        } else {
            finishAffinity();
            Intent backToMain = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(backToMain);
        }
    }

    private void firebaseUserSearch(String searchText, String radioSelection) {

        // Firebase search query variable
        Query firebaseSearchQuery;

        switch (radioSelection) {
            case ("General"):
                // Search based on productTitle
                firebaseSearchQuery = databaseAds.orderByChild("productTitle").startAt(searchText).endAt(searchText + "\uf8ff");

                // Set layout and query for FirebaseListAdapter
                FirebaseListOptions<Advert> optionsAd = new FirebaseListOptions.Builder<Advert>()
                        .setLayout(R.layout.row_advert)
                        .setQuery(firebaseSearchQuery, Advert.class)
                        .build();

                // Populate ListView with the search results
                FirebaseListAdapter<Advert> firebaseListAdapterGeneral = new FirebaseListAdapter<Advert>(optionsAd) {
                    @Override
                    protected void populateView(View v, Advert model, int position) {
                        ImageView productImage = v.findViewById(R.id.row_image);
                        TextView tvTitle = v.findViewById(R.id.row_title);
                        TextView tvPrice = v.findViewById(R.id.row_price);
                        TextView tvLocation = v.findViewById(R.id.row_location);

                        try {
                            // Download URL for image from Firebase Storage
                            URL downloadURL = new URL(model.getImageUri());
                            // Load image URL into ImageView
                            Glide
                                    .with(BrowseActivity.this)
                                    .load(downloadURL)
                                    .apply(new RequestOptions()
                                            .centerCrop()
                                            .placeholder(R.mipmap.ic_launcher_round)
                                            .error(R.mipmap.ic_launcher_round))
                                    .into(productImage);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        tvTitle.setText(model.getProductTitle());
                        String productPrice = "Rs" + model.getProductPrice();
                        tvPrice.setText(productPrice);
                        tvLocation.setText(model.getProductLocation());
                    }
                };
                firebaseListAdapterGeneral.startListening();
                // Bind adapter to ListView
                productsView.setAdapter(firebaseListAdapterGeneral);
                break;

            case ("Fashion"):
                firebaseSearchQuery = databaseFashionAds.orderByChild("productTitle").startAt(searchText).endAt(searchText + "\uf8ff");

                FirebaseListOptions<AdvertFashion> optionsFashion = new FirebaseListOptions.Builder<AdvertFashion>()
                        .setLayout(R.layout.row_advert_fashion)
                        .setQuery(firebaseSearchQuery, AdvertFashion.class)
                        .build();

                FirebaseListAdapter<AdvertFashion> firebaseListAdapterFashion = new FirebaseListAdapter<AdvertFashion>(optionsFashion) {
                    @Override
                    protected void populateView(View v, AdvertFashion model, int position) {
                        ImageView productImage = v.findViewById(R.id.row_image);
                        TextView tvTitle = v.findViewById(R.id.row_title);
                        TextView tvPrice = v.findViewById(R.id.row_price);
                        TextView tvLocation = v.findViewById(R.id.row_location);

                        try {
                            // Download URL for image from Firebase Storage
                            URL downloadURL = new URL(model.getImageUri());
                            // Load image URL into ImageView
                            Glide
                                    .with(BrowseActivity.this)
                                    .load(downloadURL)
                                    .apply(new RequestOptions()
                                            .centerCrop()
                                            .placeholder(R.mipmap.ic_launcher_round)
                                            .error(R.mipmap.ic_launcher_round))
                                    .into(productImage);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        tvTitle.setText(model.getProductTitle());
                        String productPrice = "Rs" + model.getProductPrice();
                        tvPrice.setText(productPrice);
                        tvLocation.setText(model.getProductLocation());
                    }
                };
                firebaseListAdapterFashion.startListening();
                fashionProductsView.setAdapter(firebaseListAdapterFashion);
                break;

            default:
                firebaseSearchQuery = databaseCarAds.orderByChild("carMake").startAt(searchText).endAt(searchText + "\uf8ff");

                FirebaseListOptions<AdvertCar> optionsCar = new FirebaseListOptions.Builder<AdvertCar>()
                        .setLayout(R.layout.row_advert_car)
                        .setQuery(firebaseSearchQuery, AdvertCar.class)
                        .build();

                FirebaseListAdapter<AdvertCar> firebaseListAdapterCar = new FirebaseListAdapter<AdvertCar>(optionsCar) {
                    @Override
                    protected void populateView(View v, AdvertCar model, int position) {
                        ImageView productImage = v.findViewById(R.id.row_image);
                        TextView tvMake = v.findViewById(R.id.row_make);
                        TextView tvModel = v.findViewById(R.id.row_model);
                        TextView tvYear = v.findViewById(R.id.row_year);
                        TextView tvPrice = v.findViewById(R.id.row_price);

                        try {
                            // Download URL for image from Firebase Storage
                            URL downloadURL = new URL(model.getImageUri());
                            // Load image URL into ImageView
                            Glide
                                    .with(BrowseActivity.this)
                                    .load(downloadURL)
                                    .apply(new RequestOptions()
                                            .centerCrop()
                                            .placeholder(R.mipmap.ic_launcher_round)
                                            .error(R.mipmap.ic_launcher_round))
                                    .into(productImage);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        tvMake.setText(model.getCarMake());
                        tvModel.setText(model.getCarModel());
                        tvYear.setText(String.valueOf(model.getCarYear()));
                        String productPrice = "Rs" + model.getCarPrice();
                        tvPrice.setText(productPrice);
                    }
                };
                firebaseListAdapterCar.startListening();
                carsView.setAdapter(firebaseListAdapterCar);
        }
    }

}
