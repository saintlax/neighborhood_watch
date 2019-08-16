package Objects;

/**
 * Created by CHIKADIBIA on 10/28/2016.
 */
public class Data {

    public String user_id;
    public String cookie_id;
    public String extra_id;
    public String file_id;

    public String input1;
    public String input2;
    public String type;

    public Data(String user_id, String location, String file_id){
        this.user_id = user_id;
        this.location = location;
        this.file_id = file_id;
    }
    public Data(String user_id, String input1, String input2, String type){
        this.user_id = user_id;
        this.input1 = input1;
        this.input2 = input2;
        this.type = type;
    }
    //Each event data
    public String location;
    public String theme;
    public String sender_name;
    public String date;
    public String crap;

    public Data(String location, String theme, String sender_name, String date, String crap){
        this.location =location;
        this.theme=theme;
        this.sender_name=sender_name;
        this.date=date;
        this.crap=crap;
    }
}
