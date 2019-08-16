package Objects;

public class EyeWitness {

    public int id,sender_id,receiver_id,eye_witness_id;
    public String message;
    public String sender_title;
    public String sender_first_name;
    public String sender_last_name;
    public String sender_other_names;
    public String sender_image;
    public String sender_phone;
    public String file_name,file_type;
    public String date;
    public String timestamp;
    public int send_type;

    public void setSend_type(int send_type) {
        this.send_type = send_type;
    }

    public void setSender_phone(String sender_phone) {
        this.sender_phone = sender_phone;
    }

    public void setSender_image(String sender_image) {
        this.sender_image = sender_image;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public void setEye_witness_id(int eye_witness_id) {
        this.eye_witness_id = eye_witness_id;
    }

    public void setSender_title(String sender_title) {
        this.sender_title = sender_title;
    }

    public void setSender_first_name(String sender_first_name) {
        this.sender_first_name = sender_first_name;
    }

    public void setSender_last_name(String sender_last_name) {
        this.sender_last_name = sender_last_name;
    }

    public void setSender_other_names(String sender_other_names) {
        this.sender_other_names = sender_other_names;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
