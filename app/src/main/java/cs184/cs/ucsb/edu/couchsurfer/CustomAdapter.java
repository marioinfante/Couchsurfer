package cs184.cs.ucsb.edu.couchsurfer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import com.squareup.picasso.Picasso;

public class CustomAdapter extends ArrayAdapter<CouchPost> implements View.OnClickListener{

    private ArrayList<CouchPost> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView price;
        TextView description;
        ImageView picture;
    }

    public CustomAdapter(ArrayList<CouchPost> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        CouchPost couch = getItem(position);

        switch (v.getId())
        {
            case R.id.row_description:
                FragmentManager fm = ((MainActivity) getContext()).getSupportFragmentManager();
                ViewPostDialogFragment viewPostDialogFragment = ViewPostDialogFragment.newInstance(0);
                viewPostDialogFragment.show(fm, "rating_fragment");
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CouchPost couch = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);

            viewHolder.description = (TextView) convertView.findViewById(R.id.row_description);
            viewHolder.picture = (ImageView) convertView.findViewById(R.id.row_image);
            viewHolder.price = (TextView) convertView.findViewById(R.id.row_price);

            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.description.setText(couch.getDescription());
        viewHolder.price.setText( ((Double)couch.getPrice()).toString());
        viewHolder.picture.setTag(position);
        Picasso.with(mContext)
                .load(couch.getPictures()).resize(500, 500)
                .into(viewHolder.picture);

        // Return the completed view to render on screen
        return convertView;
    }
}