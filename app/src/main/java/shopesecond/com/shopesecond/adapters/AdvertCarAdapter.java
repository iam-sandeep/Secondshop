package shopesecond.com.shopesecond.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import shopesecond.com.shopesecond.R;
import shopesecond.com.shopesecond.models.AdvertCar;



public class AdvertCarAdapter extends ArrayAdapter<AdvertCar> {
    private Context context;
    private List<AdvertCar> carAdverts;
    private ViewHolder v;


    public AdvertCarAdapter(Context context, List<AdvertCar> carAdverts) {
        super(context, R.layout.row_advert_car, carAdverts);
        this.context = context;
        this.carAdverts = carAdverts;
    }

    static class ViewHolder {
        ImageView carImage;
        TextView carMake;
        TextView carModel;
        TextView carYear;
        TextView carPrice;
    }

    @Override
    public AdvertCar getItem(int position) {
        return carAdverts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.row_advert_car, parent, false);
        } else {
            view = convertView;
        }

        v = new ViewHolder();
        v.carImage = view.findViewById(R.id.row_image);
        v.carMake = view.findViewById(R.id.row_make);
        v.carModel = view.findViewById(R.id.row_model);
        v.carYear = view.findViewById(R.id.row_year);
        v.carPrice = view.findViewById(R.id.row_price);

        final AdvertCar dataSet = carAdverts.get(position);

        try {
            // Download URL for image from Firebase Storage
            URL downloadURL = new URL(dataSet.getImageUri());
            // Load image URL into ImageView
            Glide
                    .with(getContext())
                    .load(downloadURL)
                    .apply(new RequestOptions()
                            .centerCrop()
                            .placeholder(R.mipmap.ic_launcher_round)
                            .error(R.mipmap.ic_launcher_round))
                    .into(v.carImage);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        v.carMake.setText(dataSet.getCarMake());
        v.carModel.setText(dataSet.getCarModel());
        v.carYear.setText(String.valueOf(dataSet.getCarYear()));
        String carPrice = "Rs" + dataSet.getCarPrice();
        v.carPrice.setText(carPrice);

        return view;
    }

    @Override
    public int getCount() {
        return carAdverts.size();
    }

}

