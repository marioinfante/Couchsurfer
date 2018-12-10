package cs184.cs.ucsb.edu.couchsurfer;

import android.net.Uri;

import java.util.Date;

public interface PostFactory {
    public CouchPost createPost(String author, String authorUid, String description, double longitude, double latitude, double price,
                                String start_date, String end_date, Uri pictures);

    public CouchPost createPost(String author, String authorUid, String description, double longitude, double latitude, double price,
                                String start_date, String end_date, Uri pictures, String booker, Boolean accepted);
}
