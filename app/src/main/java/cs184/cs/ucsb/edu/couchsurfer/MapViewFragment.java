package cs184.cs.ucsb.edu.couchsurfer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static cs184.cs.ucsb.edu.couchsurfer.MainActivity.adapter;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    public HashMap<String, Marker> markers;
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

        markers = new HashMap<>();
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
                if (price < 20) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (price < 50) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                } else if (price < 100) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                } else {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
                markers.put(postId, mMap.addMarker(markerOptions));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String postId = dataSnapshot.getKey();
                String priceString = dataSnapshot.child("price").getValue().toString();
                Double price = Double.parseDouble(priceString);
                String formattedPrice = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP).toString();

                Marker marker = markers.get(postId);
                marker.setTitle("$" + formattedPrice);

                if (price < 20) {
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (price < 50) {
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                } else if (price < 100) {
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                } else {
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String postId = dataSnapshot.getKey();
                Marker currMarker = markers.get(postId);
                markers.remove(postId);
                currMarker.remove();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*for(int i = 0; i < main.couches.size(); ++i){
            LatLng latLng = new LatLng(main.couches.get(i).getLatitude(),main.couches.get(i).getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(main.couches.get(i).getAuthor()));
        }*/


        // When a marker is clicked, show dialog
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getContext(), marker.getTitle() ,Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    public void refreshData(){
        // rrefresh data
    }

}
