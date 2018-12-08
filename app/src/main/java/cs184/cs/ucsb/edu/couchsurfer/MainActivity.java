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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public ArrayList<CouchPost> couches;
    public ListView listview;
    public ListViewFragment listViewFragment;
    public MapViewFragment mapViewFragment;
    public static CustomAdapter adapter;

    Toolbar toolbar;
    DrawerLayout mDrawer;
    NavigationView nvDrawer;

    ActionBarDrawerToggle drawerToggle;

    FragmentManager fm;
    FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);

        mapViewFragment = new MapViewFragment();
        listViewFragment = new ListViewFragment();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
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

}