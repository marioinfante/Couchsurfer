package cs184.cs.ucsb.edu.couchsurfer;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static cs184.cs.ucsb.edu.couchsurfer.MainActivity.adapter;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    public ArrayList<Marker> markers;
    public MainActivity main;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mapview_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        main = (MainActivity) getActivity();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        markers = new ArrayList<>();
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;

        // Move camera to UCSB
        LatLng ucsb = new LatLng(34.412936, -119.847863);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ucsb));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14.8f));

        for(int i = 0; i < main.couches.size(); ++i){
            LatLng latLng = new LatLng(main.couches.get(i).getLatitude(),main.couches.get(i).getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(main.couches.get(i).getAuthor()));
        }


        // When a marker is clicked, show dialog
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getContext(),"Helo",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    public void refreshData(){
        // rrefresh data
    }

}
