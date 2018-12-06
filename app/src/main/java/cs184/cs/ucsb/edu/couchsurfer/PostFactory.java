package cs184.cs.ucsb.edu.couchsurfer;

import java.util.Date;

/**
 * Created by kailash on 2/4/18.
 */

public interface PostFactory {
    public CouchPost createPost(String postId, String author, String authorUid, String description, double longitude, double latitude, double price,
                                Date start_date, Date end_date, String pictures, String booker, Boolean accepted);
}
