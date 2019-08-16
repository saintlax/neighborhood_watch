package Objects;

/**
 * Created by user on 2/1/2019.
 */

public class Birthdays {
    public int id;
    public int receiver_id;
    public int sender_id;

    public void setId(int id) {
        this.id = id;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int status;
    public String message, date;
}
