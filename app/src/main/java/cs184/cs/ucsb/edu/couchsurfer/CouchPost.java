package cs184.cs.ucsb.edu.couchsurfer;

import android.net.Uri;
import android.util.Log;

import java.util.Date;

public class CouchPost implements Post {

    private String postId;
    private String author;
    private String authorUid;
    private String description;
    private Double longitude;
    private Double latitude;
    private Double price;
    private String start_date;
    private String end_date;
    private Uri picture;
    private String booker;
    private Boolean accepted;

    public CouchPost(String author, String authorUid, String description, double longitude, double latitude, double price, String start_date, String end_date, Uri picture, String booker, Boolean accepted) {
        this.author = author;
        this.authorUid = authorUid;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.price = price;
        this.start_date = start_date;
        this.end_date = end_date;
        this.picture = picture;
        this.booker = booker;
        this.accepted = accepted;
    }

    public CouchPost(String author, String authorUid, String description, double longitude, double latitude, double price, String start_date, String end_date, Uri picture) {
        this.author = author;
        this.authorUid = authorUid;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.price = price;
        this.start_date = start_date;
        this.end_date = end_date;
        this.picture = picture;
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
    public String getStart_date() {
        return start_date;
    }

    public Integer getStartDateDay() {
        int firstIndex = start_date.indexOf("/");
        String subs = start_date.substring(firstIndex + 1, start_date.length() - 1);
        int secondIndex = subs.indexOf("/");
        return Integer.parseInt(subs.substring(0, secondIndex - 1));
    }

    public Integer getStartDateMonth() {
        int index = start_date.indexOf("/");
        return Integer.parseInt(start_date.substring(0, index -1));
    }

    public Integer getStartDateYear() {
        int firstIndex = start_date.indexOf("/");
        String subs = start_date.substring(firstIndex + 1, start_date.length() - 1);
        int secondIndex = subs.indexOf("/");
        return Integer.parseInt(subs.substring(secondIndex + 1, subs.length() - 1));
    }

    @Override
    public String getEnd_date() {
        return end_date;
    }

    @Override
    public Uri getPicture() {
        return picture;
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
    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    @Override
    public void setEnd_date(String end_date) {
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
