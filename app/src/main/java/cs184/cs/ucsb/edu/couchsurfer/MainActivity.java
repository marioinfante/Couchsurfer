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
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public ArrayList<CouchPost> couches;
    public ListView listview;
    public ListViewFragment listViewFragment;
    public MapViewFragment mapViewFragment;
    public FilterFragment filterFragment;
    public ProfileFragment profileFragment;
    public static CustomAdapter adapter;

    // Filter Variables
    public Date fDate;
    public double fPriceMin;
    public double fPriceMax;
    public double fDistance;


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

    FragmentManager fm;
    FragmentTransaction ft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        couchsurferDatabase = new CouchsurferDatabase();

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

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
        startListViewFragment();
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
            case R.id.nav_profile_fragment:
                ft = fm.beginTransaction();
                ft.replace(R.id.flContent, profileFragment);
                ft.addToBackStack(null);
                ft.commit();
                break;
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
            case R.id.nav_myListings_fragment:
                ft = fm.beginTransaction();
                ft.replace(R.id.flContent, listViewFragment);
                ft.addToBackStack(null);
                ft.commit();
                break;
            default:
                ft = fm.beginTransaction();
                ft.replace(R.id.flContent, listViewFragment);
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

    public void startListViewFragment(){
        // Populate Data
        String author = "Mathew Matt";
        String uid = "mattymatt";
        String description = "This is a luxury couch fit for a king";
        double longitude = -119.854267;
        double latitude = 34.411697;
        double price = 20.20;
        Date date = new Date();
        date.setDate(9);
        date.setMonth(10);
        date.setYear(2018);

        Uri uri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.drawable.sample_7);
        Boolean accepted = false;
        String booker = "Lindsey";

        couches = new ArrayList<>();

        couches.add(new CouchPost(author,uid,description,longitude,latitude, price, date,date,uri.toString(),booker,accepted));

        // Start the listview
        ft.add(R.id.flContent, listViewFragment);
        ft.commit();
    }

    public void defaultFragment(){
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
}