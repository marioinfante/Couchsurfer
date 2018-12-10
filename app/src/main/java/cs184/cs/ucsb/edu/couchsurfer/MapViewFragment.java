package cs184.cs.ucsb.edu.couchsurfer;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

public class MapViewFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;

    public MainActivity main;
    DatabaseReference db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mapview_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        main = (MainActivity) getActivity();
        db = FirebaseDatabase.getInstance().getReference();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;

        // Move camera to UCSB
        LatLng ucsb = new LatLng(34.412936, -119.847863);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ucsb));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14.8f));

        db.child("posts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                LatLng location = new LatLng(Double.parseDouble(dataSnapshot.child("latitude").getValue().toString()), Double.parseDouble(dataSnapshot.child("longitude").getValue().toString()));
                String postId = dataSnapshot.getKey();
                String priceString = dataSnapshot.child("price").getValue().toString();
                Double price = Double.parseDouble(priceString);
                String formattedPrice = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP).toString();

                MarkerOptions markerOptions = new MarkerOptions().position(location).title("$" + formattedPrice);
                if (price <= 20) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(203));
                } else if (price <= 40) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(121));
                }else if (price <= 60) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(64));
                } else if (price <= 80) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(39));
                } else if (price <= 100) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(25));
                }else {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(1));
                }
                Marker m = mMap.addMarker(markerOptions);
                main.markerMap.put(postId, m);
                main.idMap.put(m, postId);

                filterMarkers();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String postId = dataSnapshot.getKey();
                String priceString = dataSnapshot.child("price").getValue().toString();
                Double price = Double.parseDouble(priceString);
                String formattedPrice = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP).toString();

                Marker marker = main.markerMap.get(postId);
                main.idMap.remove(marker);
                marker.setTitle("$" + formattedPrice);

                if (price <= 20) {
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(203));
                } else if (price <= 40) {
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(121));
                }else if (price <= 60) {
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(64));
                } else if (price <= 80) {
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(39));
                } else if (price <= 100) {
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(25));
                }else {
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(1));
                }
                main.markerMap.remove(postId);
                main.markerMap.put(postId, marker);
                main.idMap.put(marker, postId);

                filterMarkers();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String postId = dataSnapshot.getKey();
                Marker currMarker = main.markerMap.get(postId);
                main.markerMap.remove(postId);
                main.idMap.remove(currMarker);
                currMarker.remove();

                filterMarkers();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        final Marker m = marker;

        DatabaseReference query = FirebaseDatabase.getInstance().getReference().child("posts").child(main.idMap.get(marker));
        query.keepSynced(true);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //initialize a new couch
                final String postId = dataSnapshot.getKey();
                final String author = dataSnapshot.child("author").getValue().toString();
                final String authorUid = dataSnapshot.child("authorUid").getValue().toString();
                final String description = dataSnapshot.child("description").getValue().toString();
                final Double longitude = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
                final Double latitude = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                final Double price = Double.parseDouble(dataSnapshot.child("price").getValue().toString());
                final String start_date = dataSnapshot.child("start_date").getValue().toString();
                final String end_date = dataSnapshot.child("end_date").getValue().toString();
                final String booker = dataSnapshot.child("booker").getValue().toString();
                final Boolean accepted = Boolean.parseBoolean(dataSnapshot.child("accepted").getValue().toString());
                String imageLocation = dataSnapshot.child("picture").getValue().toString();

                StorageReference httpsRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageLocation);
                httpsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        CouchPost cp = new CouchPost(author, authorUid, description, longitude, latitude, price, start_date, end_date, uri, booker, accepted);
                        cp.setPostId(postId);
                        FragmentManager fm = ((MainActivity) getContext()).getSupportFragmentManager();
                        ViewPostDialogFragment viewPostDialogFragment = ViewPostDialogFragment.newInstance(cp);
                        viewPostDialogFragment.show(fm, "dialog_fragment");
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return false;
    }

    public void refreshData(){
        // rrefresh data
    }

    public void filterMarkers(){
        // main.fDistance
    }

}
