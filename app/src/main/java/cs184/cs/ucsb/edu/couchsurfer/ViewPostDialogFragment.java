package cs184.cs.ucsb.edu.couchsurfer;

import android.hardware.camera2.TotalCaptureResult;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ViewPostDialogFragment extends DialogFragment {

    Button requestButton;
    MainActivity main;

    static CouchPost post;

    String picture_url;
    double price;
    String author;
    String description;
    String date;
    String authorUid;
    String postId;

    private DatabaseReference postRef;

    public ViewPostDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ViewPostDialogFragment newInstance(CouchPost couch) {
        ViewPostDialogFragment frag = new ViewPostDialogFragment();
        Bundle args = new Bundle();
        args.putString("picture_url", couch.getPicture().toString());
        args.putDouble("price", couch.getPrice());
        args.putString("author", couch.getAuthor());
        args.putString("description", couch.getDescription());
        args.putString("date", couch.getStart_date());
        args.putString("authorUid", couch.getAuthorUid());

        frag.setArguments(args);

        post = couch;

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.viewpost_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        picture_url = args.getString("picture_url");
        price = args.getDouble("price");
        author = args.getString("author");
        description = args.getString("description");
        date = args.getString("date");
        authorUid = args.getString("authorUid");

        TextView date_tv = view.findViewById(R.id.dates_dialog);
        TextView price_tv = view.findViewById(R.id.price_dialog);
        TextView author_tv = view.findViewById(R.id.author_dialog);
        TextView description_tv = view.findViewById(R.id.description_dialog);
        ImageView imageview = view.findViewById(R.id.picture_dialog);
        Button requestButton = view.findViewById(R.id.requestButton);

        main = (MainActivity) getActivity();

        postRef = FirebaseDatabase.getInstance().getReference().child("posts");

        String formattedPrice = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP).toString();

        String text = "Price: $" + formattedPrice;
        price_tv.setText(text);

        text = "Author: " + author;
        author_tv.setText(text);

        text = "Description: " + description;
        description_tv.setText(text);

        text = "Available Date: " + date;
        date_tv.setText(text);

        if(authorUid.equals(main.currentUser.getUid())){
            requestButton.setText("EDIT");
        }

        Picasso.with(getContext())
                .load(picture_url).resize(500, 500)
                .into(imageview);

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // implement backend stuff later
                if(authorUid.equals(main.currentUser.getUid())) {
                    Toast.makeText(getContext(), "Cannot edit right now. Try again later.", Toast.LENGTH_SHORT).show();
                }
                else {
                    postRef.orderByChild("description").equalTo(description).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                postId = child.getKey().toString();
                                Log.e("TAG", "key: " + postId);
                                postRef.child(postId).child("booker").setValue(main.currentUser.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        System.out.println("updated booker");
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError e) {

                        }
                    });
                }
            }
        });
    }

}
