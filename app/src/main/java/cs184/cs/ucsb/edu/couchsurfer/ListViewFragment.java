package cs184.cs.ucsb.edu.couchsurfer;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import static cs184.cs.ucsb.edu.couchsurfer.MainActivity.adapter;

public class ListViewFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.listview_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                pullToRefresh.setRefreshing(false);
            }
        });

        final MainActivity main = (MainActivity) getActivity();
        adapter = new CustomAdapter(main.couches, getContext());
        main.listview = view.findViewById(R.id.listview);
        main.listview.setAdapter(adapter);
        main.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CouchPost couch = main.couches.get(position);
                Snackbar.make(view, couch.getAuthor(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });


        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
    }

    public void refreshData(){
        // rrefresh data
    }

}
