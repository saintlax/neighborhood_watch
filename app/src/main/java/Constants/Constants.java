package Constants;

import android.os.Environment;


public class Constants {
    public static String SUCCESS = "SUCCESS";
    public static String ERROR = "ERROR";
    public static final int REQUEST_CAMERA = 1;
    public static final int CAMERA_REQUEST = 3;
    public static final int RESULT_LOAD_IMAGE = 2;
    public static final int REQUEST_TAKE_GALLERY_VIDEO = 66;
    public static final int VIDEO_REQUEST = 101;
    public static int LOGGED_OUT = 0;
    public static int LOGGED_IN = 1;
    public static int TYPE_ADMIN = 3;
    public static int TYPE_TEACHER = 2;
    public static int TYPE_BURSAR = 4;
    public static int TYPE_PARENTS = 5;
    public static int PENDING = 0;
    public static int SEEN = 1;
    public static int CONFIRMED = 2;
    public static final String STORAGE_FOLDER = "SchoolEngage";
    public static final String DOWNLOAD_FILE_PATH = Environment.getExternalStorageDirectory().toString()+"/"+STORAGE_FOLDER+"/download/";

    public static String EXPLODER_1 = ";:XX:;";
    public static String EXPLODER_2 = ";:YY:;";
    public static String SP_NAME = "SP_NAME";
    public static String SP_USER_ID = "SP_USER_ID";
    public static String SP_CONTACTS = "SP_CONTACTS";
    public static String SP_LEVIES = "SP_LEVIES";
    public static String SP_DUES = "SP_DUES";
    public static String SP_STREETS = "SP_STREETS";
    public static String SP_BUILDINGS = "SP_BUILDINGS";
    public static String SP_APARTMENTS = "SP_APARTMENTS";
    public static String SP_LEVIES_HISTORY = "SP_LEVIES_HISTORY";
    public static String SP_STATES = "SP_STATES";
    public static String SP_FAMILY = "SP_FAMILY";
    public static String SP_DISTRESS_HISTORY = "SP_DISTRESS_HISTORY";
    public static String SP_ACTIVE_EMERGENCY = "SP_ACTIVE_EMERGENCY";
    public static String SP_LOGS = "SP_LOGS";
    public static String SP_DISTRICTS = "SP_DISTRICTS";
    public static String SP_HOTLINES = "SP_HOTLINES";
    public static String SP_COUNTRY_STATES = "SP_COUNTRY_STATES";
    public static String EMPTYY = "EMPTYY";
    public static String BLOCK = "BLOCK";
    public static String UNBLOCK = "UNBLOCK";
    public static String SP_HOLIDAYS_CALENDAR = "SP_HOLIDAYS_CALENDAR";
    public static String PAID = "PAID";
    public static String NOT_PAID = "NOT_PAID";
    public static int MODE = 0;
    public static String ADMIN = "0";
    public static String LANDLORD = "1";
    public static String TENANT = "2";
    public static String CARETAKER = "3";
    public static int VISIBLE = 0;
    public static int INVISIBLE = 1;
    public static String NAIRA = "&#8358;";
    public static int LOCATION_INTERVAL = 1500;
    public static int FASTEST_LOCATION_INTERVAL = 1000;
    public static String[] TITLES = {
                                        "Mr.",
                                        "Mrs",
                                        "Miss",
                                        "Dr.",
                                        "Engr.",
                                        "Prof.",
                                        "Rev.",
                                        "Rev. Fr.",
                                        "Deacon",
                                        "Deaconess",
                                        "Prophet",
                                        "Prophetess",
                                        "Bishop"
                                };

}
