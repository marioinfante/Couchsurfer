package cs184.cs.ucsb.edu.couchsurfer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static ArrayList<CouchPost> requestedCouches;

    public ListView listview;
    public ListViewFragment listViewFragment;
    public MapViewFragment mapViewFragment;
    public FilterFragment filterFragment;
    public ProfileFragment profileFragment;
    public RequestsFragment requestsFragment;
    public MyListingsFragment myListingsFragment;
    public static CustomAdapter adapter;

    // Filter Variables, default values (cuz i dont want to error check)
    public String fDate;
    public double fPriceMin = 0;
    public double fPriceMax = 999999;
    public double fDistance = 999999;

    public static HashMap<String, Marker> markerMap;
    public static HashMap<Marker, String> idMap;

    Toolbar toolbar;
    DrawerLayout mDrawer;
    NavigationView nvDrawer;
    CouchsurferDatabase couchsurferDatabase;
    ActionBarDrawerToggle drawerToggle;

    View headerLayout;
    ImageView headerProfilePic;
    TextView headerName;

    FirebaseUser currentUser;
    private DatabaseReference dbRef;
    private DatabaseReference reqRef;
    private DatabaseReference postRef;
    private CouchsurferDatabase db = new CouchsurferDatabase();

    FragmentManager fm;
    FragmentTransaction ft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        couchsurferDatabase = new CouchsurferDatabase();

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        markerMap = new HashMap<>();
        idMap = new HashMap<>();

        // Attaching the layout to the toolbar object
        toolbar = findViewById(R.id.tool_bar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        nvDrawer.setNavigationItemSelectedListener(this);

        drawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        headerLayout = nvDrawer.inflateHeaderView(R.layout.nav_header);
        headerProfilePic = headerLayout.findViewById(R.id.profilePicIV);
        headerName = headerLayout.findViewById(R.id.nameTV);
        setHeaderInfo();

        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);

        mapViewFragment = new MapViewFragment();
        listViewFragment = new ListViewFragment();
        filterFragment = new FilterFragment();
        profileFragment = new ProfileFragment();
        requestsFragment = new RequestsFragment();
        myListingsFragment = new MyListingsFragment();


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference().child("users");
        reqRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid()).child("requests");
        postRef = FirebaseDatabase.getInstance().getReference().child("posts");

        requestedCouches = new ArrayList<CouchPost>();

        setRequestedPostIds();

        // Start the listview
        ft.add(R.id.flContent, listViewFragment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.filter_button:
                // Clear calendar data
                fDate = null;

                ft = fm.beginTransaction();
                ft.replace(R.id.flContent, filterFragment);
                ft.addToBackStack(null);
                ft.commit();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        switch (item.getItemId()) {
            case R.id.nav_search_fragment:
                ft = fm.beginTransaction();
                ft.replace(R.id.flContent, listViewFragment);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.nav_maps_fragment:
                ft = fm.beginTransaction();
                ft.replace(R.id.flContent, mapViewFragment);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.nav_profile_fragment:
                ft = fm.beginTransaction();
                ft.replace(R.id.flContent, profileFragment);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.nav_myListings_fragment:
                ft = fm.beginTransaction();
                ft.replace(R.id.flContent, myListingsFragment);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.nav_myRequests_fragment:
                ft = fm.beginTransaction();
                ft.replace(R.id.flContent, requestsFragment);
                ft.addToBackStack(null);
                ft.commit();
            default:
                ft = fm.beginTransaction();
                ft.replace(R.id.flContent, requestsFragment);
                ft.addToBackStack(null);
                ft.commit();
        }

        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void defaultFragment() {
        ft = fm.beginTransaction();
        ft.replace(R.id.flContent, listViewFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void setHeaderInfo() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference().child("users");
        if (currentUser != null) {
            Query query =  dbRef.child(currentUser.getUid());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Glide.with(headerLayout)
                                .load(dataSnapshot.getValue(User.class).getProfilePicUrl())
                                .apply(new RequestOptions().placeholder(R.drawable.default_profile_pic))
                                .into(headerProfilePic);
                        headerName.setText(dataSnapshot.getValue(User.class).getFullName());
                    }
                    else {
                        Log.wtf("mytag", "dataSnapshot does not exists");
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("[Database Error]", databaseError.getMessage());
                }
            });
        }
    }

    public void setRequestedPostIds() {
        requestedCouches.clear();
        reqRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    postRef.child(child.getValue().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String author = currentUser.getDisplayName().toString();
                            final String authoruid = dataSnapshot.child("authorUid").getValue().toString();
                            final String description = dataSnapshot.child("description").getValue().toString();
                            final double longitude = Double.valueOf(dataSnapshot.child("longitude").getValue().toString());
                            final double latitude = Double.valueOf(dataSnapshot.child("latitude").getValue().toString());
                            final double price = Double.parseDouble(dataSnapshot.child("price").getValue().toString());
                            final String start_date = dataSnapshot.child("start_date").getValue().toString();
                            final String end_date = dataSnapshot.child("end_date").getValue().toString();
                            final String picture = dataSnapshot.child("picture").getValue().toString();
                            final String booker = dataSnapshot.child("booker").getValue().toString();
                            final boolean accepted = Boolean.valueOf(dataSnapshot.child("accepted").getValue().toString());


                            StorageReference httpsRef = FirebaseStorage.getInstance().getReferenceFromUrl(picture);
                            httpsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    CouchPost post = new CouchPost(author, authoruid, description, longitude, latitude, price, start_date, end_date, uri, booker, accepted);
                                    requestedCouches.add(post);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}