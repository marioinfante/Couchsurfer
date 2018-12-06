package cs184.cs.ucsb.edu.couchsurfer;

import java.util.Date;

public class CouchPost implements Post {

    private String postId;
    private String author;
    private String authorUid;
    private String description;
    private Double longitude;
    private Double latitude;
    private Double price;
    private Date start_date;
    private Date end_date;
    private String pictures;
    private String booker;
    private Boolean accepted;

    public CouchPost(String postId, String author, String authorUid, String description, double longitude, double latitude, double price, Date start_date, Date end_date, String pictures, String booker, Boolean accepted) {
        this.postId = postId;
        this.author = author;
        this.authorUid = authorUid;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.price = price;
        this.start_date = start_date;
        this.end_date = end_date;
        this.pictures = pictures;
        this.booker = booker;
        this.accepted = accepted;
    }

    @Override
    public String getPostId() {
        return postId;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public String getAuthorUid() {
        return authorUid;
    }

    public String getDescription() {
        return description;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getPrice() {
        return price;
    }

    public Date getStart_date() {
        return start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public String getPictures() {
        return pictures;
    }

    public String getBooker() {
        return booker;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
}
