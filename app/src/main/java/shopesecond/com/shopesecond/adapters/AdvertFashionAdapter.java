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
import shopesecond.com.shopesecond.models.AdvertFashion;



public class AdvertFashionAdapter extends ArrayAdapter<AdvertFashion> {

    private Context context;
    private List<AdvertFashion> fashionAdverts;
    private ViewHolder v;

    public AdvertFashionAdapter(Context context, List<AdvertFashion> fashionAdverts) {
        super(context, R.layout.row_advert_fashion, fashionAdverts);
        this.context = context;
        this.fashionAdverts = fashionAdverts;
    }

    static class ViewHolder {
        ImageView productImage;
        TextView productTitle;
        TextView productPrice;
        TextView productLocation;
    }

    @Override
    public AdvertFashion getItem(int position) {
        return fashionAdverts.get(position);
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
            view = li.inflate(R.layout.row_advert_fashion, parent, false);
        } else {
            view = convertView;
        }

        v = new AdvertFashionAdapter.ViewHolder();
        v.productImage = view.findViewById(R.id.row_image);
        v.productTitle = view.findViewById(R.id.row_title);
        v.productPrice = view.findViewById(R.id.row_price);
        v.productLocation = view.findViewById(R.id.row_location);

        final AdvertFashion dataSet = fashionAdverts.get(position);

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
                    .into(v.productImage);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        v.productTitle.setText(dataSet.getProductTitle());
        String productPrice = "Rs" + dataSet.getProductPrice();
        v.productPrice.setText(productPrice);
        v.productLocation.setText(dataSet.getProductLocation());

        return view;
    }

    @Override
    public int getCount() {
        return fashionAdverts.size();
    }
}
