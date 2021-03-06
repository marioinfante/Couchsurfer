package cs184.cs.ucsb.edu.couchsurfer;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class RequestDialogFragment extends DialogFragment {

    static CouchPost post;
    private DatabaseReference postRef;

    String description;
    String postId;
    String bookerId;

    TextView nameTV;
    TextView phoneTV;
    ImageView profileIV;
    Button acceptBtn;
    Button rejectBtn;

    public RequestDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static RequestDialogFragment newInstance(CouchPost couch) {
        RequestDialogFragment frag = new RequestDialogFragment();
        Bundle args = new Bundle();
        args.putString("description", couch.getDescription());

        frag.setArguments(args);

        post = couch;

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        postRef = FirebaseDatabase.getInstance().getReference().child("posts");
        return inflater.inflate(R.layout.request_dialog, container);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameTV = (TextView) view.findViewById(R.id.nameTV);
        phoneTV = (TextView) view.findViewById(R.id.phoneTV);
        profileIV = (ImageView) view.findViewById(R.id.profileIV);
        acceptBtn = (Button) view.findViewById(R.id.acceptBtn);
        rejectBtn = (Button) view.findViewById(R.id.rejectBtn);

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postRef.child(postId).child("accepted").setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        Log.e("TAG", "booked set to true");
                        dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "failed to set booked to true");
                    }
                });
            }
        });

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postRef.child(postId).child("booker").setValue("none").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void T) {
                        Log.e("TAG", "set booker to none");
                        dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "failed to set booker to none");
                    }
                });

            }
        });


        Bundle args = getArguments();
        description = args.getString("description");

        postRef.orderByChild("description").equalTo(description).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("TAG", "CHILDREN: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot c : dataSnapshot.getChildren()) {
                    postId = c.getKey().toString();
                    Log.e("TAG", "postid: " + postId);
                    //Change name
                    postRef.child(postId).child("booker").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.e("TAG", "booker" + dataSnapshot.getValue(String.class));
                            bookerId = dataSnapshot.getValue(String.class);
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                            usersRef.child(bookerId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Log.e("TAG", "booker info " + dataSnapshot.getValue());
                                        nameTV.setText("Name: " + dataSnapshot.child("fullName").getValue().toString());
                                        phoneTV.setText("Phone: " + dataSnapshot.child("phoneNo").getValue().toString());
                                        Glide.with(view)
                                                .load(dataSnapshot.child("profilePicUrl").getValue().toString())
                                                .apply(new RequestOptions().placeholder(R.drawable.default_profile_pic))
                                                .into(profileIV);
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

                        @Override
                        public void onCancelled(DatabaseError e) {

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
