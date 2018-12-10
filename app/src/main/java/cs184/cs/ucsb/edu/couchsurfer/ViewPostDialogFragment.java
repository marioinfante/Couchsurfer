package cs184.cs.ucsb.edu.couchsurfer;

import android.hardware.camera2.TotalCaptureResult;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ViewPostDialogFragment extends DialogFragment {

    Button requestButton;

    public ViewPostDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ViewPostDialogFragment newInstance(CouchPost couch) {
        ViewPostDialogFragment frag = new ViewPostDialogFragment();
        Bundle args = new Bundle();
        args.putString("picture_url", couch.getPicture().toString());
        args.putDouble("price", couch.getPrice());
        args.putString("author", couch.getAuthor());
        args.putString("description", couch.getDescription());
        args.putString("date", couch.getStart_date());

        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.viewpost_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        String picture_url = args.getString("picture_url");
        double price = args.getDouble("price");
        String author = args.getString("author");
        String description = args.getString("description");
        String date = args.getString("date");

        TextView date_tv = view.findViewById(R.id.dates_dialog);
        TextView price_tv = view.findViewById(R.id.price_dialog);
        TextView author_tv = view.findViewById(R.id.author_dialog);
        TextView description_tv = view.findViewById(R.id.description_dialog);
        ImageView imageview = view.findViewById(R.id.picture_dialog);
        Button requestButton = view.findViewById(R.id.requestButton);

        String formattedPrice = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP).toString();

        String text = "Price: $" + formattedPrice;
        price_tv.setText(text);

        text = "Author: " + author;
        author_tv.setText(text);

        text = "Description: " + description;
        description_tv.setText(text);

        text = "Available Date: " + date;
        date_tv.setText(text);

        Picasso.with(getContext())
                .load(picture_url).resize(500, 500)
                .into(imageview);

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // implement backend stuff later
                Toast.makeText(getContext(), "The owner has been sent a request to book", Toast.LENGTH_LONG).show();
            }
        });
    }
}
