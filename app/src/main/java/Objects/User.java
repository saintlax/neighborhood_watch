package Objects;

/**
 * Created by user on 11/26/2018.
 */

public class User {
    public int id,user_type;
    public String title;
    public String image;
    public String password;
    public String phone;
    public String email;
    public String thumbnail;
    public String date;
    public String email_code;
    public String access;
    public int user_id;
    public String other_names;
    public String status;


    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String first_name;
    public String last_name;

    public void setOther_names(String other_names) {
        this.other_names = other_names;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public int getId() {
        return id;
    }

    public int getUser_type() {
        return user_type;
    }

    public String getImage() {
        return image;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public User(){}
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public void setStatus(String status) {this.status = status;}
    public void setEmail(String email) {
        this.email = email;
    }
    public void setThumbnail(String thumbnail) {this.thumbnail = thumbnail;}
    public void setPassword(String password) {this.password = password;}
    public void setId(int id) {
        this.id = id;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setPhone(String phone) {this.phone = phone;}
    public void setAccess(String access) {
        this.access = access;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public void setEmail_code(String email_code) {this.email_code = email_code;}

}
