package cs184.cs.ucsb.edu.couchsurfer;

import android.net.Uri;

import java.util.Date;

public interface Post {

    // getters
    public String getPostId();
    public String getAuthor();
    public String getAuthorUid();
    public String getDescription();
    public Double getLongitude();
    public Double getLatitude();
    public Double getPrice();
    public String getStart_date();
    public String getEnd_date();
    public Uri getPicture();

    // setters
    public void setDescription(String description);
    public void setPrice(double price);
    public void setStart_date(String start_date);
    public void setEnd_date(String end_date);
    public void setLatitude(double latitude);
    public void setLongitude(double longitude);
}
