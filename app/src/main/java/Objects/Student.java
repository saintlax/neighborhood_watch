package Objects;

/**
 * Created by user on 1/23/2019.
 */

public class Student {
    public int id,user_id,teacher_id,classroom_id;

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String fullname;
    public String birthdate;
    public String batch;
    public String school_group;
    public String semester;
    public String extra_curricular;
    public String enrollment_no;
    public String roll_no;
    public String parent_phone;
    public String parent_email;
    public String parent_name;
    public String parent_image;
    public String parent_thumbnail;
    public String address;
    public String city;

    public void setImage(String image) {
        this.image = image;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String image;
    public String thumbnail;
    public Student(){}

    public void setId(int id) {
        this.id = id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setTeacher_id(int teacher_id) {
        this.teacher_id = teacher_id;
    }

    public void setClassroom_id(int classroom_id) {
        this.classroom_id = classroom_id;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public void setSchool_group(String school_group) {
        this.school_group = school_group;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setExtra_curricular(String extra_curricular) {
        this.extra_curricular = extra_curricular;
    }

    public void setEnrollment_no(String enrollment_no) {
        this.enrollment_no = enrollment_no;
    }

    public void setRoll_no(String roll_no) {
        this.roll_no = roll_no;
    }

    public void setParent_phone(String parent_phone) {
        this.parent_phone = parent_phone;
    }

    public void setParent_email(String parent_email) {
        this.parent_email = parent_email;
    }

    public void setParent_name(String parent_name) {
        this.parent_name = parent_name;
    }

    public void setParent_image(String parent_image) {
        this.parent_image = parent_image;
    }

    public void setParent_thumbnail(String parent_thumbnail) {
        this.parent_thumbnail = parent_thumbnail;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
