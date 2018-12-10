package cs184.cs.ucsb.edu.couchsurfer;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RequestsFragment extends Fragment {

    public RequestsAdapter requestsAdapter;
    public ListView listview;

    FirebaseUser currentUser;
    private DatabaseReference reqRef;
    private DatabaseReference postRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reqRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid()).child("requests");
        postRef = FirebaseDatabase.getInstance().getReference().child("posts");
        return inflater.inflate(R.layout.requests_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                pullToRefresh.setRefreshing(false);
            }
        });

        final MainActivity main = (MainActivity) getActivity();

        requestsAdapter = new RequestsAdapter(main.requestedCouches, getContext());
        listview = view.findViewById(R.id.requestsLV);
        listview.setAdapter(requestsAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CouchPost couch = main.requestedCouches.get(position);
                Snackbar.make(view, couch.getAuthor(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });
    }

    public void refreshData(){
        // rrefresh data
    }


}
