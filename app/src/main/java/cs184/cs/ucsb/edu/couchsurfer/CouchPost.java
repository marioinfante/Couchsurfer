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

    public CouchPost() { }

    public CouchPost(String author, String authorUid, String description, double longitude, double latitude, double price, Date start_date, Date end_date, String pictures, String booker, Boolean accepted) {
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

    public CouchPost(String author, String authorUid, String description, double longitude, double latitude, double price, Date start_date, Date end_date, String pictures) {
        this.author = author;
        this.authorUid = authorUid;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.price = price;
        this.start_date = start_date;
        this.end_date = end_date;
        this.pictures = pictures;
        this.booker = "none";
        this.accepted = false;
    }

    @Override
    public String getPostId() {
        return postId;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getAuthorUid() {
        return authorUid;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Double getLongitude() {
        return longitude;
    }

    @Override
    public Double getLatitude() {
        return latitude;
    }

    @Override
    public Double getPrice() {
        return price;
    }

    @Override
    public Date getStart_date() {
        return start_date;
    }

    @Override
    public Date getEnd_date() {
        return end_date;
    }

    @Override
    public String getPictures() {
        return pictures;
    }

    public String getBooker() {
        return booker;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setPostId(String postId) { this.postId = postId; }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    @Override
    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    @Override
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
}
