package Objects;

public class BroadcastMessage {
    public int id;
    public String message;

    public void setId(int id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSentTo(String sentTo) {
        this.sentTo = sentTo;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String sentTo;
    public String date;
}
