package cs184.cs.ucsb.edu.couchsurfer;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RequestsAdapter extends ArrayAdapter<CouchPost> implements View.OnClickListener {

    private ArrayList<CouchPost> dataSet;
    Context mContext;

    // View lookup cache
    public class ViewHolder {
        TextView priceTV;
        TextView descriptionTV;
        ImageView pictureIV;
        TextView statusTV;
        Button cancelBtn;
    }

    public RequestsAdapter(ArrayList<CouchPost> data, Context context) {
        super(context, R.layout.request_row_item, data);
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
                Snackbar.make(v, "Author " + couch.getAuthor(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CouchPost couch = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        RequestsAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.request_row_item, parent, false);

            viewHolder.descriptionTV = (TextView) convertView.findViewById(R.id.row_description);
            viewHolder.pictureIV = (ImageView) convertView.findViewById(R.id.row_image);
            viewHolder.priceTV = (TextView) convertView.findViewById(R.id.row_price);
            viewHolder.statusTV = (TextView) convertView.findViewById(R.id.statusTV);
            viewHolder.cancelBtn = (Button) convertView.findViewById(R.id.cancelBtn);

            viewHolder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (RequestsAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.descriptionTV.setText(couch.getDescription());
        viewHolder.priceTV.setText( ((Double)couch.getPrice()).toString());
        viewHolder.pictureIV.setTag(position);
        Picasso.with(mContext)
                .load(couch.getPicture()).resize(500, 500)
                .into(viewHolder.pictureIV);

        // Return the completed view to render on screen
        return convertView;
    }
}
