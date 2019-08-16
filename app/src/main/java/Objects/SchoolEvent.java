package Objects;

/**
 * Created by user on 4/26/2019.
 */

public class SchoolEvent {
    public int id,user_id,year,day,minute,hour;
    public String theme;
    public String event_date;
    public String location;

    public String event_type;
    public String date;
    public String time;
    public String image;
    public String thumbnail;
    public String sendTo;
    public String ano;
    public String month;

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSender_phone(String sender_phone) {
        this.sender_phone = sender_phone;
    }

    public void setSender_image(String sender_image) {
        this.sender_image = sender_image;
    }

    public String sender_name;
    public String status;
    public String sender_phone;
    public String sender_image;

    public void setOn_date(String on_date) {
        this.on_date = on_date;
    }

    public String on_date;

    public void setId(int id) {
        this.id = id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
