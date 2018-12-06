package cs184.cs.ucsb.edu.couchsurfer;

public class User {
    private String uid;
    private String username;
    private String profilePicUrl;
    private String fullName;
    private String phoneNo;
    private String city;
    private String state;

    public User() {
    }

    public User(String uid, String username, String fullName, String phoneNo, String city, String state) {
        this.uid = uid;
        this.username = username;
        this.fullName = fullName;
        this.phoneNo = phoneNo;
        this.city = city;
        this.state = state;
        this.profilePicUrl = "";
    }

    public String getUid() {
        return uid;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }


    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

}
