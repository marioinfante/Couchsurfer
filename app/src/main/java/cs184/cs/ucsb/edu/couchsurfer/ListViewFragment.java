package cs184.cs.ucsb.edu.couchsurfer;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static cs184.cs.ucsb.edu.couchsurfer.MainActivity.adapter;

public class ListViewFragment extends Fragment {
    MainActivity main;
    FloatingActionButton newPostButton;
    DatabaseReference db;
    HashMap<String,User> users;
    public ArrayList<CouchPost> couches, filtered_couches;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.listview_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        main = (MainActivity) getActivity();

        // Refresh just calls filter function
        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                filterList();
                pullToRefresh.setRefreshing(false);
            }
        });


        couches = new ArrayList<>();
        adapter = new CustomAdapter(couches, getContext());

        // populates couches and filtered couches
        populateData();

        main.listview = view.findViewById(R.id.listview);
        main.listview.setAdapter(adapter);

        // This is the code for when a row item is clicked
        main.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CouchPost couch = couches.get(position);
                FragmentManager fm = ((MainActivity) getContext()).getSupportFragmentManager();
                // pass in couch args in here
                ViewPostDialogFragment viewPostDialogFragment = ViewPostDialogFragment.newInstance(couch);
                viewPostDialogFragment.show(fm, "dialog_fragment");
            }
        });

        // NewPostActivity called here
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

    // Firebase code and populating the arraylist
    public void populateData(){
        db = FirebaseDatabase.getInstance().getReference();

        // POSTS
        db.child("posts").addChildEventListener(new ChildEventListener() {
            String author, authorUid, description, booker, pictures, postId;
            double latitude, longitude, price;
            Date start_date, end_date;
            Boolean accepted = false;

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("tag", "On Child Added, " + couches.size());

                authorUid = dataSnapshot.child("authorUid").getValue().toString();

                // TODO Problem: What if users isn't populated yet?
                author = users.get(authorUid).getFullName();

                description = dataSnapshot.child("description").getValue().toString();
                booker = dataSnapshot.child("booker").getValue().toString();
                pictures = dataSnapshot.child("picture").getValue().toString();
                latitude = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                longitude = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
                postId = dataSnapshot.getKey();
                price = Double.parseDouble(dataSnapshot.child("price").getValue().toString());
                accepted = Boolean.parseBoolean(dataSnapshot.child("accepted").getValue().toString());

                //TODO parse dates or just pull a string from DB
                StringBuilder sb = new StringBuilder();
                sb.append(dataSnapshot.child("start_date").getValue().toString());
                start_date = new Date();
                end_date = start_date;

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

        // USERS
        db.child("users").addChildEventListener(new ChildEventListener() {
            String uid, username, fullname, phoneno, city, state;

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
                accepted = Boolean.parseBoolean(dataSnapshot.child("accepted").getValue().toString());

                //TODO parse dates or just pull a string from DB
                start_date = new Date(2018,9,5);
                end_date = start_date;

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
