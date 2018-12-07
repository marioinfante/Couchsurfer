package cs184.cs.ucsb.edu.couchsurfer;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    public ArrayList<Couch> couches;
    ListView listView;
    public static CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Populate Data
        couches = new ArrayList<>();
        Uri uri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.drawable.sample_7);

        Date date = new Date();
        date.setDate(9);
        date.setMonth(10);
        date.setYear(2018);
        couches.add(new Couch(0,0,"Lindsey", "This is a mighty fine couch", 4.20,uri,date,date));

        // Start the toolbar
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Start the listview
        ListViewFragment listViewFragment = new ListViewFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.main, listViewFragment);
        ft.commit();
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

    public List<Couch> getCouches(){
        return couches;
    }
}
