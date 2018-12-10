package cs184.cs.ucsb.edu.couchsurfer;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;

import static cs184.cs.ucsb.edu.couchsurfer.MainActivity.adapter;

public class ListViewFragment extends Fragment {

    MainActivity main;
    FloatingActionButton newPostButton;
    DatabaseReference db;
    public ArrayList<CouchPost> couches, filtered_couches;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.listview_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        main = (MainActivity) getActivity();

        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                filterList();
                pullToRefresh.setRefreshing(false);
            }
        });

        // populates couches and filtered couches
        populateData();

        couches = new ArrayList<>();
        adapter = new CustomAdapter(couches, getContext());

        main.listview = view.findViewById(R.id.listview);
        main.listview.setAdapter(adapter);
        main.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CouchPost couch = couches.get(position);
                FragmentManager fm = ((MainActivity) getContext()).getSupportFragmentManager();

                // pass in couch args in here
                ViewPostDialogFragment viewPostDialogFragment = ViewPostDialogFragment.newInstance(0);

                viewPostDialogFragment.show(fm, "dialog_fragment");
            }
        });

        newPostButton = getView().findViewById(R.id.newPostButton);
        newPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newPostIntent = new Intent(getActivity(), NewPostActivity.class );
                //navigationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(newPostIntent);
            }
        });
    }

    public void populateData(){
        db = FirebaseDatabase.getInstance().getReference();

        db.child("posts").addChildEventListener(new ChildEventListener() {
            // Populate Data
            String author, authorUid, description, booker, pictures, postId;
            double latitude, longitude, price;
            Date start_date, end_date;
            Uri uri = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.drawable.sample_7);
            Boolean accepted = false;

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("tag", "On Child Added, " + couches.size());
                // TODO get author name
                author = "";
                authorUid = dataSnapshot.child("authorUid").getValue().toString();
                description = dataSnapshot.child("description").getValue().toString();
                booker = dataSnapshot.child("booker").getValue().toString();
                pictures = dataSnapshot.child("pictures").getValue().toString();
                latitude = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                longitude = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
                postId = dataSnapshot.getKey();
                price = Double.parseDouble(dataSnapshot.child("price").getValue().toString());
                String formattedPrice = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP).toString();
                start_date = new Date();
                end_date = new Date();
                accepted = false;

                adapter.addToAdapter(new CouchPost(author, authorUid, description, longitude, latitude, price, start_date, end_date, uri.toString(), booker, accepted));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("tag","On Child Changed");
                // TODO get author name
                author = "";
                authorUid = dataSnapshot.child("authorUid").getValue().toString();
                description = dataSnapshot.child("description").getValue().toString();
                booker = dataSnapshot.child("booker").getValue().toString();
                pictures = dataSnapshot.child("pictures").getValue().toString();
                latitude = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                longitude = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
                postId = dataSnapshot.getKey();
                price = Double.parseDouble(dataSnapshot.child("price").getValue().toString());
                String formattedPrice = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP).toString();
                start_date = new Date();
                end_date = new Date();
                accepted = false;

                Log.d("tag", couches.toString());

                adapter.addToAdapter(new CouchPost(author, authorUid, description, longitude, latitude, price, start_date, end_date, pictures, booker, accepted));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                // TODO remove from list
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void filterList(){
        filtered_couches = new ArrayList<>();

        for(int i = 0; i < couches.size(); ++i){
            // Filter Distance
            float[] distance = new float[5];
            Location.distanceBetween(couches.get(i).getLatitude(), couches.get(i).getLongitude(), 34.412936, -119.847863, distance);
            double metersToMiles = 1609.34;

            // If distance is farther than fDistance, go to next item in couches
            if(distance[0]*metersToMiles > main.fDistance)
            {
                break;
            }
            // Filter Price
            if(couches.get(i).getPrice() > main.fPriceMax || couches.get(i).getPrice() < main.fPriceMin)
            {
                break;
            }
            // Filter Date, must not be null AND be the same date
            if(main.fDate != null &&
                (couches.get(i).getStart_date().getDay() != main.fDate.getDay() ||
                couches.get(i).getStart_date().getMonth() != main.fDate.getMonth() ||
                couches.get(i).getStart_date().getYear() != main.fDate.getYear()))
            {
                break;
            }

            filtered_couches.add(couches.get(i));
        }

        adapter.changeDataset(filtered_couches);
    }
}
