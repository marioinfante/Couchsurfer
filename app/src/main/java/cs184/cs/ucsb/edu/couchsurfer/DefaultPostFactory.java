package cs184.cs.ucsb.edu.couchsurfer;

import android.net.Uri;

import java.util.Date;

public class DefaultPostFactory implements PostFactory {

    public CouchPost createPost(String author, String authorUid, String description, double longitude, double latitude, double price,
                                String start_date, String end_date, Uri pictures) {
        CouchPost post = new CouchPost(author, authorUid, description, longitude, latitude, price, start_date, end_date, pictures);
        return post;
    }

    public CouchPost createPost(String author, String authorUid, String description, double longitude, double latitude, double price,
                                String start_date, String end_date, Uri pictures, String booker, Boolean accepted) {
        CouchPost post = new CouchPost(author, authorUid, description, longitude, latitude, price, start_date, end_date, pictures, booker, accepted);
        return post;
    }
}
