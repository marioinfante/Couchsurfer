package cs184.cs.ucsb.edu.couchsurfer;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import android.content.DialogInterface;

import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import android.util.Log;
import android.graphics.Bitmap;
import android.os.Environment;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final int SELECT_FILE = 20;

    private ImageView profilePicIV;

    private TextView fullNameTV, usernameTV, emailTV, phoneNoTV, stateTV, cityTV;

    private DatabaseReference dbRef;
    private StorageReference profileImageRef;

    FirebaseUser currentUser;

    private Uri imageUri;
    private String profileImageUrl;
    private String profileImgFileName;

    View view;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.profile_fragment, container, false);


        profilePicIV = (ImageView)view.findViewById(R.id.profilePicIV);
        fullNameTV = (TextView)view.findViewById(R.id.fullNameTV);
        usernameTV = (TextView)view.findViewById(R.id.usernameTV);
        emailTV = (TextView)view.findViewById(R.id.emailTV);
        phoneNoTV = (TextView)view.findViewById(R.id.phoneNoTV);
        stateTV = (TextView)view.findViewById(R.id.stateTV);
        cityTV = (TextView)view.findViewById(R.id.cityTV);
        view.findViewById(R.id.editProfileBtn).setOnClickListener(this);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference().child("users");

        if (currentUser != null) {
            Query query =  dbRef.child(currentUser.getUid());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        fullNameTV.setText(dataSnapshot.getValue(User.class).getFullName());
                        stateTV.setText("State: " + dataSnapshot.getValue(User.class).getState());
                        cityTV.setText("City: " + dataSnapshot.getValue(User.class).getCity());
                        usernameTV.setText("@"+ dataSnapshot.getValue(User.class).getUsername());
                        phoneNoTV.setText("Phone: " + dataSnapshot.getValue(User.class).getPhoneNo());
                        emailTV.setText("Email: " + currentUser.getEmail());
                        Glide.with(view)
                                .load(dataSnapshot.getValue(User.class).getProfilePicUrl())
                                .apply(new RequestOptions().placeholder(R.drawable.default_profile_pic))
                                .into(profilePicIV);

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

        profilePicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseProfilePic();
            }
        });

        return view;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.editProfileBtn:
                ProfileFragment.this.startActivity(new Intent(getActivity(), EditProfileActivity.class));
                break;
        }
    }

    private void chooseProfilePic() {
        //Displays dialog to choose pic from camera or gallery
        final CharSequence[] items = {"Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");

        //SET ITEMS AND THERE LISTENERS
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        Uri data = Uri.parse(pictureDirectoryPath);
        photoPickerIntent.setDataAndType(data, "image/*");
        startActivityForResult(photoPickerIntent, SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK && data!= null) {
            // the address of the image on the SD Card.
            imageUri = data.getData();
            if (requestCode == SELECT_FILE) {
                // declare a stream to read the image data from the SD Card.
                InputStream inputStream;
                try {
                    inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    profilePicIV.setImageBitmap(image);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Unable to open image", Toast.LENGTH_LONG).show();
                }
            }
            uploadImagetoFirebaseStorage();
        }
        else {
            Log.wtf("ERROR: ", "data is null");
        }
    }
    private void uploadImagetoFirebaseStorage() {
        profileImgFileName = imageUri.getLastPathSegment() + ".jpg";
        profileImageRef = FirebaseStorage.getInstance().getReference().child("profilepics/" + profileImgFileName);
        if (imageUri != null) {
            profileImageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.wtf("MSG: ", "File successfully uploaded to firebase");
                            saveUserInformation();
                        }
                    });
        }
    }

    private void saveUserInformation() {
        if (profileImgFileName != null) {
            Log.e("TAG", "profileImgFileName: " + profileImgFileName);
            Log.e("TAG", "TAG" + profileImageRef.getDownloadUrl());
            profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.wtf("MSG: ", "GOT URL: " + uri);
                    Map<String, Object> userUpdates = new HashMap<>();
                    userUpdates.put("profilePicUrl", uri.toString());
                    if (profileImgFileName != null) {
                        dbRef.child(currentUser.getUid()).updateChildren(userUpdates)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.wtf("MSG: ", "SUCCESSFULLY ADDED TO DATABASE");
                                    }
                                });
                    }
                }
            });
        }
        else {
                Log.wtf("ERROR: ", "profileImageUrl is null");
            }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
