package Objects;

/**
 * Created by user on 12/25/2018.
 */

public class Message {
    public int id;



    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public int receiver_id;

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public int sender_id;
    public String message;
    public String time;
    public String timestamp;
    public String type;
    public String status;

    public void setReceiver_phone(String receiver_phone) {
        this.receiver_phone = receiver_phone;
    }

    public String receiver_phone;

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setType(String type) {
        this.type = type;
    }
}
