package Objects;

/**
 * Created by user on 4/16/2019.
 */

public class LessonNote {
    public int id;
    public int note_id;
    public int exam_id;
    public int teacher_id;
    public String exam_title;
    public String note;
    public String date;
    public int status;

    public void setId(int id) {
        this.id = id;
    }

    public void setNote_id(int note_id) {
        this.note_id = note_id;
    }
    public void setExam_id(int exam_id) {
        this.exam_id = exam_id;
    }

    public void setTeacher_id(int teacher_id) {
        this.teacher_id = teacher_id;
    }

    public void setExam_title(String exam_title) {
        this.exam_title = exam_title;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
