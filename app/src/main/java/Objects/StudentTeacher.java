package Objects;

public class StudentTeacher {
    public int id,student_id,teacher_id,classroom_id;
    public String teacher_name;

    public void setTeacher_image(String teacher_image) {
        this.teacher_image = teacher_image;
    }

    public void setStudent_image(String student_image) {
        this.student_image = student_image;
    }

    public String teacher_image;
    public String student_name,student_image;

    public void setId(int id) {
        this.id = id;
    }

    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }

    public void setTeacher_id(int teacher_id) {
        this.teacher_id = teacher_id;
    }

    public void setClassroom_id(int classroom_id) {
        this.classroom_id = classroom_id;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public void setClassroom_name(String classroom_name) {
        this.classroom_name = classroom_name;
    }

    public String classroom_name;
}
