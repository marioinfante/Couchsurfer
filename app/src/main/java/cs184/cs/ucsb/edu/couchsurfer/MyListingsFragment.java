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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class MyListingsFragment extends Fragment {
    MainActivity main;
    FloatingActionButton newPostButton;
    DatabaseReference db;
    HashMap<String,User> users;
    public ArrayList<CouchPost> couches, filtered_couches;

    MyListingsAdapter adapter;

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

        adapter = new MyListingsAdapter(couches, getContext());

        // populates couches and filtered couches
        populateData();

        main.listview = view.findViewById(R.id.listview);
        main.listview.setAdapter(adapter);

        // This is the code for when a row item is clicked
        main.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                long viewId = view.getId();
                CouchPost couch = couches.get(position);
                Log.e("TAG", "position: " + position);
                if (viewId == R.id.requestsTV) {
                    FragmentManager fm = ((MainActivity) getContext()).getSupportFragmentManager();
                    // pass in couch args in here
                    RequestDialogFragment requestDialogFragment = RequestDialogFragment.newInstance(couch);
                    requestDialogFragment.show(fm, "dialog_fragment");
                }
                else {
                    FragmentManager fm = ((MainActivity) getContext()).getSupportFragmentManager();
                    // pass in couch args in here
                    ViewPostDialogFragment viewPostDialogFragment = ViewPostDialogFragment.newInstance(couch);
                    viewPostDialogFragment.show(fm, "dialog_fragment");
                }
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

        // POSTS
        db.child("posts").addChildEventListener(new ChildEventListener() {
            String author, authorUid, description, booker, postId;
            double latitude, longitude, price;
            String start_date, end_date;
            Boolean accepted;
            Uri photoUri;

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
                latitude = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                longitude = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
                postId = dataSnapshot.getKey();
                price = Double.parseDouble(dataSnapshot.child("price").getValue().toString());
                accepted = Boolean.parseBoolean(dataSnapshot.child("accepted").getValue().toString());
                start_date = dataSnapshot.child("start_date").getValue().toString();
                end_date = dataSnapshot.child("end_date").getValue().toString();
                photoUri = Uri.parse(dataSnapshot.child("picture").getValue().toString());

                StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.child("picture").getValue().toString());
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    CouchPost couch = new CouchPost(author, authorUid, description, longitude, latitude, price, start_date, end_date, photoUri, booker, accepted);
                    String post = postId;
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "SUCCESS " + couches.size());
                        couch.setPostId(post);
                        couches.add(couch);
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("tag", "On Child Changed, " + couches.size());

                authorUid = dataSnapshot.child("authorUid").getValue().toString();
                author = users.get(authorUid).getFullName();
                description = dataSnapshot.child("description").getValue().toString();
                booker = dataSnapshot.child("booker").getValue().toString();
                latitude = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                longitude = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
                postId = dataSnapshot.getKey();
                price = Double.parseDouble(dataSnapshot.child("price").getValue().toString());
                accepted = Boolean.parseBoolean(dataSnapshot.child("accepted").getValue().toString());
                start_date = dataSnapshot.child("start_date").getValue().toString();
                end_date = dataSnapshot.child("end_date").getValue().toString();
                photoUri = Uri.parse(dataSnapshot.child("picture").getValue().toString());

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
                    StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.child("picture").getValue().toString());
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        CouchPost couch = new CouchPost(author, authorUid, description, longitude, latitude, price, start_date, end_date, photoUri, booker, accepted);
                        String post = postId;
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("tag","Change Success! " + couches.size());
                            couch.setPostId(post);
                            couches.add(couch);
                        }
                    });
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
    }

    // Filterlist here is meant for just author and this user
    public void filterList(){
        filtered_couches = new ArrayList<>();

        for(int i = 0; i < couches.size(); ++i){
            if(couches.get(i).getAuthorUid().equals(main.currentUser.getUid())){
                filtered_couches.add(couches.get(i));
            }
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
