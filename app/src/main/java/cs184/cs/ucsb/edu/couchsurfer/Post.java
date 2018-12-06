package cs184.cs.ucsb.edu.couchsurfer;

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
    public Date getStart_date();
    public Date getEnd_date();
    public String getPictures();

    // setters
    public void setDescription(String description);
    public void setPrice(double price);
    public void setStart_date(Date start_date);
    public void setEnd_date(Date end_date);
    public void setLatitude(double latitude);
    public void setLongitude(double longitude);
}
