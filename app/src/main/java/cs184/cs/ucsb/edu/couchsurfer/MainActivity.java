package cs184.cs.ucsb.edu.couchsurfer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    public ArrayList<CouchPost> couches;
    public ListView listview;
    public static CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start login activity
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);

        // Start the toolbar
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Start listview as the first view
        startListViewFragment();
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

        return super.onOptionsItemSelected(item);
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
        ListViewFragment listViewFragment = new ListViewFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.main, listViewFragment);
        ft.commit();
    }
}
