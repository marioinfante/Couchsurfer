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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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

        users = new HashMap<>();
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
                if(users.get(authorUid) != null) {
                    author = users.get(authorUid).getFullName();
                }
                else{
                    author = "";
                }
                description = dataSnapshot.child("description").getValue().toString();
                booker = dataSnapshot.child("booker").getValue().toString();
                pictures = dataSnapshot.child("picture").getValue().toString();
                latitude = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                longitude = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
                postId = dataSnapshot.getKey();
                price = Double.parseDouble(dataSnapshot.child("price").getValue().toString());
                accepted = Boolean.parseBoolean(dataSnapshot.child("accepted").getValue().toString());

                // Format Dates
                // TODO may cause issues with new start_date string
                DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
                try {
                    start_date = format.parse(dataSnapshot.child("start_date").getValue().toString());
                    start_date.setYear(start_date.getYear() - 100);
                    start_date.setMonth(start_date.getMonth() + 1);
                    end_date = format.parse(dataSnapshot.child("end_date").getValue().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                CouchPost couch = new CouchPost(author, authorUid, description, longitude, latitude, price, start_date, end_date, pictures, booker, accepted);
                couch.setPostId(postId);
                adapter.addToAdapter(couch);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("tag", "On Child Added, " + couches.size());

                authorUid = dataSnapshot.child("authorUid").getValue().toString();
                author = users.get(authorUid).getFullName();
                description = dataSnapshot.child("description").getValue().toString();
                booker = dataSnapshot.child("booker").getValue().toString();
                pictures = dataSnapshot.child("picture").getValue().toString();
                latitude = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                longitude = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
                postId = dataSnapshot.getKey();
                price = Double.parseDouble(dataSnapshot.child("price").getValue().toString());
                accepted = Boolean.parseBoolean(dataSnapshot.child("accepted").getValue().toString());

                // Format Dates
                // TODO may cause issues with new start_date string
                DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
                try {
                    start_date = format.parse(dataSnapshot.child("start_date").getValue().toString());
                    end_date = format.parse(dataSnapshot.child("end_date").getValue().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                CouchPost couch = findCouchPost(postId);

                if(couch != null){
                    couch.setPrice(price);
                    couch.setAccepted(accepted);
                    couch.setDescription(description);
                    couch.setLatitude(latitude);
                    couch.setLongitude(longitude);
                    couch.setStart_date(start_date);
                    couch.setEnd_date(end_date);
                }
                else{
                    couch = new CouchPost(author, authorUid, description, longitude, latitude, price, start_date, end_date, pictures, booker, accepted);
                    couch.setPostId(postId);
                    adapter.addToAdapter(couch);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Remove from list
                removeCouchPost(dataSnapshot.getKey());
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
            String uid, city, fullname, phoneno, state, username;

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Populate Users list
                uid = dataSnapshot.getKey();
                city = dataSnapshot.child("city").getValue().toString();
                fullname = dataSnapshot.child("fullName").getValue().toString();
                phoneno = dataSnapshot.child("phoneNo").getValue().toString();
                state = dataSnapshot.child("state").getValue().toString();
                username = dataSnapshot.child("username").getValue().toString();

                User user = new User(uid,username,fullname,phoneno,city,state);
                users.put(uid,user);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Change the User
                User user = users.get(dataSnapshot.getKey());
                uid = dataSnapshot.getKey();
                city = dataSnapshot.child("city").getValue().toString();
                fullname = dataSnapshot.child("fullName").getValue().toString();
                phoneno = dataSnapshot.child("phoneNo").getValue().toString();
                state = dataSnapshot.child("state").getValue().toString();
                username = dataSnapshot.child("username").getValue().toString();

                if(user != null){
                    user.setCity(city);
                    user.setFullName(fullname);
                    user.setState(state);
                    user.setUsername(username);
                    user.setPhoneNo(phoneno);
                }
                else{
                    users.put(uid,user);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Remove the User
                users.remove(dataSnapshot.getKey());
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
            // Filter Distance, find distance between the couch location and UCSB latlng
            // TODO change to phone location
            float[] distance = new float[1];
            Location.distanceBetween(couches.get(i).getLatitude(), couches.get(i).getLongitude(), 34.412936, -119.847863, distance);
            double metersToMiles = 1609.34;
            // If distance is farther than distance from UCSB, go to next item in couches
            if(distance[0]*metersToMiles > main.fDistance)
            {
                continue;
            }
            // Filter Price
            Log.d("tag", "Couch Price: " + couches.get(i).getPrice() + " and Max Price: " + main.fPriceMax);
            if(couches.get(i).getPrice() > main.fPriceMax || couches.get(i).getPrice() < main.fPriceMin)
            {
                continue;
            }
            // Filter Date, must not be null AND be the same date
            if(main.fDate != null &&
                (couches.get(i).getStart_date().getDay() != main.fDate.getDay() ||
                couches.get(i).getStart_date().getMonth() != main.fDate.getMonth() ||
                couches.get(i).getStart_date().getYear() != main.fDate.getYear()))
            {
                continue;
            }

            filtered_couches.add(couches.get(i));
        }
        adapter.changeDataset(filtered_couches);
    }

    public CouchPost findCouchPost(String postId){
        for(int i = 0; i < couches.size(); ++i){
            if(couches.get(i).getPostId() == postId){
                return couches.get(i);
            }
        }
        return null;
    }

    public void removeCouchPost(String postId){
        for(int i = 0; i < couches.size(); ++i){
            if(couches.get(i).getPostId() == postId){
                couches.remove(i);
                return;
            }
        }
    }

    public User findUser(String userId){
        for(int i = 0; i < users.size(); ++i){
            if(users.get(i).getUid() == userId){
                return users.get(i);
            }
        }
        return null;
    }
}
