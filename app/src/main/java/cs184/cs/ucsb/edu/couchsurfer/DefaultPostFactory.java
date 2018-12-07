package cs184.cs.ucsb.edu.couchsurfer;

import java.util.Date;

/**
 * Created by kailash on 2/4/18.
 */

public class DefaultPostFactory implements PostFactory {

    public CouchPost createPost(String author, String authorUid, String description, double longitude, double latitude, double price,
                                Date start_date, Date end_date, String pictures) {
        CouchPost post = new CouchPost(author, authorUid, description, longitude, latitude, price, start_date, end_date, pictures);
        return post;
    }

    public CouchPost createPost(String author, String authorUid, String description, double longitude, double latitude, double price,
                                Date start_date, Date end_date, String pictures, String booker, Boolean accepted) {
        CouchPost post = new CouchPost(author, authorUid, description, longitude, latitude, price, start_date, end_date, pictures, booker, accepted);
        return post;
    }
}