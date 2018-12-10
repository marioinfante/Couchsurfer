package cs184.cs.ucsb.edu.couchsurfer;

import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CouchsurferDatabase {
    private DatabaseReference rootRef;
    private DatabaseReference postsRef;
    private DatabaseReference usersRef;
    private PostFactory factory;

    private HashMap<String,String> currentMap;
    private CouchPost currentPost;

    public CouchsurferDatabase() {
        rootRef = FirebaseDatabase.getInstance().getReference();
        postsRef = rootRef.child("posts");
        usersRef = rootRef.child("users");
        factory = new DefaultPostFactory();
        currentMap = new HashMap<>();
    }

    // For this method it is important to note that one must take the return value and save it
    // over the post object passed in because this method updates the postId of that post

    public CouchPost addPost(CouchPost post) {
        currentMap = makePostMap(post);
        currentPost = post;

        Uri image = post.getPicture();
        final String imageFileName = image.getLastPathSegment() + ".jpg";
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("couchpics/" + imageFileName);
        if (image != null) {
            imageRef.putFile(image)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            currentPost.setPostId(savePostInformation(imageFileName));
                        }
                    });
        }
        return currentPost;
    }

    public User addUser(User user){
        HashMap<String, String> userMap = makeUserMap(user);
        String userId = user.getUid();
        usersRef.child(userId).setValue(userMap);

        ArrayList<String> postRequests;
        postRequests = user.getPostRequests();
        int numRequests = postRequests.size();
        for(int i = 0; i < numRequests; i++){
            usersRef.child(userId).child("requests").child(Integer.toString(i + 1)).setValue(postRequests.get(i));
        }
        return user;
    }

    public void attemptToBookPost(String postId, String booker){
        postsRef.child(postId).child("booker").setValue(booker);
    }

    public void acceptPostBooking(String postId){
        postsRef.child(postId).child("accepted").setValue("true");
    }

    public void cancelPostBooking(String postId){
        postsRef.child(postId).child("booker").setValue("none");
        postsRef.child(postId).child("accepted").setValue("false");
    }

    public void addPostRequest(String userId, final String postId) {
        usersRef.child(userId).child("requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long numChildren = dataSnapshot.getChildrenCount();
                dataSnapshot.getRef().child(Long.toString(numChildren + 1)).setValue(postId);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void deletePost(String postId){
        postsRef.child(postId).removeValue();
    }
    public void deleteUser(String userId){
        usersRef.child(userId).removeValue();
    }

    public void updateUserRating(String userId, int rating) {
        usersRef.child(userId).child("rating").setValue(rating);
    }

    public void updatePostDescription(String postId, String newDescription){
        postsRef.child(postId).child("description").setValue(newDescription);
    }

    public void updatePostPrice(String postId, String newPrice){
        postsRef.child(postId).child("price").setValue(newPrice);
    }
  
    private HashMap<String, String> makePostMap(CouchPost post) {
        HashMap<String, String> postMap = new HashMap<>();
        postMap.put("author", post.getAuthor());
        postMap.put("authorUid", post.getAuthorUid());
        postMap.put("description", post.getDescription());
        postMap.put("longitude", post.getLongitude().toString());
        postMap.put("latitude", post.getLatitude().toString());
        postMap.put("price", post.getPrice().toString());
        postMap.put("start_date", post.getStart_date());
        postMap.put("end_date", post.getEnd_date());
        postMap.put("booker", post.getBooker());
        postMap.put("accepted", "false");
        return postMap;
    }

    private HashMap<String, String> makeUserMap(User user) {
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("uid", user.getUid());
        userMap.put("username", user.getUsername());
        userMap.put("profilePicUrl", user.getProfilePicUrl());
        userMap.put("fullName", user.getFullName());
        userMap.put("phoneNo", user.getPhoneNo());
        userMap.put("city", user.getCity());
        userMap.put("state", user.getState());
        userMap.put("rating", Integer.toString(user.getRating()));
        return userMap;
    }

    private String savePostInformation(final String imageFileName) {
        final String postId = postsRef.push().getKey();
        if (imageFileName != null) {
            FirebaseStorage.getInstance().getReference().child("couchpics/" + imageFileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if (imageFileName != null) {
                        currentMap.put("picture", uri.toString());
                        postsRef.child(postId).setValue(currentMap);
                    }
                }
            });
        }
        else {
            Log.d("ERROR: ", "profileImageUrl is null");
        }
        return postId;
    }

    public DatabaseReference getRoot() { return rootRef; }

}
