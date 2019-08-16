package database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import Objects.Birthdays;
import Objects.EyeWitness;
import Objects.LGAStaff;
import Objects.LessonNote;
import Objects.Message;
import Objects.OrganisationStaff;
import Objects.Ratings;
import Objects.ReportCard;
import Objects.SchoolEvent;
import Objects.StudentTeacher;
import Objects.User;
import Objects.UserType;

/**
 * Created by user on 11/26/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "users.db";
    public static String TABLE_USERS = "users";
    public static String TABLE_CHAT = "chat";
    public static String TABLE_STUDENTS = "students";
    public static String TABLE_STUDENT_TEACHER = "student_teacher";
    public static String TABLE_EVENTS = "events";
    public static String TABLE_PLAYED_ALARMS = "played_alarms";
    public static String TABLE_REPORT_CARD = "report_card";
    public static String TABLE_USER_TYPE = "user_type";
    public static String TABLE_EYE_WITNESS = "eye_witness";
    public static String TABLE_ORGANISATION_STAFF = "organisation_staff";
    public static String TABLE_LGA_STAFF = "lga_staff";
    public static String TABLE_RATINGS = "ratings";
    public static String TABLE_USER_NEIGHBORHOODS = "user_neighborhoods";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table "+TABLE_USERS +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "USER_ID TEXT," +
                "TITLE TEXT," +
                "FIRST_NAME TEXT," +
                "LAST_NAME TEXT," +
                "OTHER_NAMES TEXT," +
                "PHONE TEXT," +
                "EMAIL TEXT," +
                "EMAIL_CODE TEXT," +
                "PASSWORD TEXT," +
                "ACCESS TEXT," +
                "IMAGE TEXT," +
                "THUMBNAIL TEXT," +
                "STATUS TEXT," +
                "DATE TEXT)";
        db.execSQL(query);

        String user_type = "create table "+TABLE_USER_TYPE +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "USER_ID TEXT," +
                "PHONE TEXT," +
                "USER_TYPE TEXT," +
                "STREET_ID TEXT," +
                "STREET_NAME TEXT," +
                "BUILDING_ID TEXT," +
                "BUILDING_NAME TEXT," +
                "APARTMENT_ID TEXT," +
                "APARTMENT_NAME TEXT," +
                "APARTMENT_TYPE_ID TEXT," +
                "APARTMENT_TYPE_NAME TEXT," +
                "STATE_ID TEXT," +
                "STATE_NAME TEXT," +
                "LGA_ID TEXT," +
                "LGA_NAME TEXT," +
                "DISTRICT_ID TEXT," +
                "DISTRICT_NAME TEXT)";
        db.execSQL(user_type);

         query = "create table "+TABLE_STUDENT_TEACHER +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "STUDENT_ID INTEGER," +
                "TEACHER_ID INTEGER," +
                "STUDENT_NAME TEXT," +
                "STUDENT_IMAGE TEXT," +
                "TEACHER_NAME TEXT," +
                "TEACHER_IMAGE TEXT," +
                "CLASSROOM_ID INTEGER," +
                "CLASSROOM_NAME TEXT)";
        db.execSQL(query);

        String chat = "create table "+TABLE_CHAT+
                " (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "message TEXT," +
                "receiver_id INTEGER," +
                "sender_id INTEGER," +
                "time TEXT," +
                "type TEXT," +
                "status TEXT," +
                "timestamp TEXT," +
                "receiver_phone TEXT)";
        db.execSQL(chat);


        String students = "create table "+TABLE_STUDENTS +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "USER_ID INTEGER," +
                "TEACHER_ID INTEGER," +
                "CLASSROOM_ID INTEGER," +
                "FULLNAME TEXT," +
                "PHONE TEXT," +
                "EMAIL TEXT," +
                "PASSWORD TEXT," +
                "USER_TYPE INTEGER," +
                "IMAGE TEXT," +
                "THUMBNAIL TEXT," +
                "STATUS TEXT," +
                "BIRTHDATE TEXT," +
                "BATCH TEXT," +
                "SCHOOL_GROUP TEXT," +
                "SEMESTER TEXT," +
                "EXTRA_CURRICULAR TEXT," +
                "ENROLLMENT_NO TEXT," +
                "ROLL_NO TEXT," +
                "PARENT_PHONE TEXT," +
                "PARENT_EMAIL TEXT," +
                "PARENT_NAME TEXT," +
                "PARENT_IMAGE TEXT," +
                "PARENT_THUMBNAIL TEXT," +
                "ADDRESS TEXT," +
                "CITY TEXT," +
                "DATE TEXT)";
        db.execSQL(students);


        String events = "create table "+TABLE_EVENTS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id TEXT," +
                "sender_name TEXT," +
                "sender_phone TEXT," +
                "sender_image TEXT," +
                "event_date TEXT," +
                "on_date TEXT," +
                "status TEXT," +
                "date TEXT," +
                "event_time TEXT," +
                "theme TEXT," +
                "location TEXT," +
                "hr TEXT," +
                "min TEXT," +
                "mnth TEXT," +
                "day TEXT," +
                "ano TEXT," +
                "yr TEXT," +
                "etype TEXT," +
                "image TEXT," +
                "thumbnail TEXT," +
                "sendTo TEXT)";
        db.execSQL(events);

        String report_card = "create table "+TABLE_REPORT_CARD+
                " (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "student_id INTEGER," +
                "student_name TEXT," +
                "teacher_id INTEGER," +
                "teacher_name TEXT," +
                "report_card TEXT," +
                "status INTEGER," +
                "date TEXT)";
        db.execSQL(report_card);

        String played_alarm = "create table "+TABLE_PLAYED_ALARMS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "event_id TEXT," +
                "time TEXT," +
                "status TEXT," +
                "date TEXT)";
        db.execSQL(played_alarm);

        String eye_witness = "create table "+TABLE_EYE_WITNESS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "eye_witness_id INTEGER," +
                "sender_id INTEGER," +
                "receiver_id INTEGER," +
                "message TEXT," +
                "sender_title TEXT," +
                "sender_first_name TEXT," +
                "sender_last_name TEXT," +
                "sender_other_names TEXT," +
                "file_name TEXT," +
                "file_type TEXT," +
                "timestamp TEXT," +
                "date TEXT," +
                "sender_image TEXT," +
                "sender_phone TEXT," +
                "send_type INTEGER)";
        db.execSQL(eye_witness);


        String org_staff = "create table "+TABLE_ORGANISATION_STAFF +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "organisation_id INTEGER," +
                "organisation TEXT," +
                "status TEXT," +
                "date TEXT)";
        db.execSQL(org_staff);


        String lga_staff = "create table "+TABLE_LGA_STAFF +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "lga_id INTEGER," +
                "name TEXT," +
                "state_id INTEGER)";
        db.execSQL(lga_staff);

        String ratings = "create table "+TABLE_RATINGS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "rating TEXT," +
                "date TEXT)";
        db.execSQL(ratings);

        String user_neighborhoods = "create table "+TABLE_USER_NEIGHBORHOODS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "state_name TEXT," +
                "state_id INTEGER," +
                "lga_name TEXT," +
                "lga_id INTEGER," +
                "district_name TEXT," +
                "district_id INTEGER," +
                "street_name TEXT," +
                "street_id INTEGER," +
                "building_name TEXT," +
                "building_id INTEGER," +
                "apartment_name TEXT," +
                "apartment_id INTEGER," +
                "apartment_type_name TEXT," +
                "apartment_type_id INTEGER," +
                "user_type TEXT," +
                "date TEXT)";
        db.execSQL(user_neighborhoods);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String query = "DROP TABLE IF EXISTS "+ TABLE_USERS;
        db.execSQL(query);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_CHAT);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_ORGANISATION_STAFF);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_STUDENT_TEACHER);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_REPORT_CARD);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PLAYED_ALARMS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_USER_TYPE);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_EYE_WITNESS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_LGA_STAFF);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_RATINGS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_USER_NEIGHBORHOODS);
        onCreate(db);
    }
    public boolean do_insert(String table, ContentValues contentValues){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert(table,null,contentValues);
        if(result != -1){
            return true;
        }

        return false;
    }

    public boolean do_edit(String table, ContentValues contentValues,String dbvar ,String param){
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(table,contentValues,dbvar +" = ?",new String[]{param});
        return true;
    }


    public Integer do_delete(String table,String dbvar,String param){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(table,dbvar+" = ?", new String[]{param});
    }
    public Cursor query(String query){
        SQLiteDatabase db = this.getWritableDatabase();
        //    String query = "select * from "+table;
        Cursor res =  db.rawQuery(query,null);
        return res;
    }

    public User user_data(String query){
        User user = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery(query,null);
        if(res.getCount() == 0){
            return null;
        }

        while(res.moveToNext()){
            int id = Integer.parseInt(res.getString(0));
            int user_id = Integer.parseInt(res.getString(1));
            String title = res.getString(2);
            String first_name = res.getString(3);
            String last_name = res.getString(4);
            String other_names = res.getString(5);
            String phone = res.getString(6);
            String email = res.getString(7);
            String email_code = res.getString(8);
            String password = res.getString(9);
            String access = res.getString(10);
            String image = res.getString(11);
            String thumbnail = res.getString(12);
            String status = res.getString(13);
            String date = res.getString(14);
            user = new User();
            user.setId(id);
            user.setUser_id(user_id);
            user.setEmail_code(email_code);
            user.setThumbnail(thumbnail);
            user.setAccess(access);
            user.setStatus(status);
            user.setTitle(title);
            user.setLast_name(last_name);
            user.setFirst_name(first_name);
            user.setOther_names(other_names);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(password);
            user.setImage(image);
            user.setDate(date);
        }
        res.close();
        return user;
    }

    public List<Ratings> ratingsList(String query){
        List<Ratings> ratingsList =new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery(query,null);
        if(res.getCount() == 0){
            return null;
        }
        while(res.moveToNext()) {
            int id = res.getInt(0);
            int user_id = res.getInt(1);
            String rating = res.getString(2);
            String date = res.getString(3);
            Ratings ratings = new Ratings();
            ratings.setId(id);
            ratings.setUser_id(user_id);
            ratings.setRating(rating);
            ratings.setDate(date);
            ratingsList.add(ratings);
        }
        return ratingsList;
    }

    public List<LGAStaff> lgaStaffList(String query){
        List<LGAStaff> lgaStaffs = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery(query,null);
        if(res.getCount() == 0){
            return null;
        }
        while(res.moveToNext()) {
            int id = res.getInt(0);
            int user_id = res.getInt(1);
            int lga_id = res.getInt(2);
            String name = res.getString(3);
            int state_id = res.getInt(4);
            LGAStaff lgaStaff = new LGAStaff();
            lgaStaff.setId(id);
            lgaStaff.setUser_id(user_id);
            lgaStaff.setLga_id(lga_id);
            lgaStaff.setName(name);
            lgaStaff.setState_id(state_id);
            lgaStaffs.add(lgaStaff);
        }
        return lgaStaffs;
    }

    public List<OrganisationStaff> organisationsStaffList(String query){
        List<OrganisationStaff> organisationStaffs  = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery(query,null);
        if(res.getCount() == 0){
            return null;
        }
        while(res.moveToNext()) {
            int id = res.getInt(0);
            int user_id = res.getInt(1);
            int organisation_id = res.getInt(2);
            String organisation = res.getString(3);
            String status = res.getString(4);
            String date = res.getString(5);
            OrganisationStaff org = new OrganisationStaff();
            org.setId(id);
            org.setUser_id(user_id);
            org.setOrganisation_id(organisation_id);
            org.setOrganisation(organisation);
            org.setStatus(status);
            org.setDate(date);
            organisationStaffs.add(org);
        }
        return organisationStaffs;
    }

    public UserType user_type(String query){
        UserType userType = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery(query,null);
        if(res.getCount() == 0){
            return null;
        }

        while(res.moveToNext()){
            int id = Integer.parseInt(res.getString(0));
            String user_id = res.getString(1);
            String phone = res.getString(2);
            String type = res.getString(3);
            String street_id = res.getString(4);
            String street_name = res.getString(5);
            String building_id = res.getString(6);
            String building_name = res.getString(7);
            String apartment_id = res.getString(8);
            String apartment_name = res.getString(9);
            String apartment_type_id = res.getString(10);
            String apartment_type_name = res.getString(11);
            String state_id = res.getString(12);
            String state_name = res.getString(13);
            String lga_id = res.getString(14);
            String lga_name = res.getString(15);
            String district_id = res.getString(16);
            String district_name = res.getString(17);

            userType = new UserType();
            userType.setId(id);
            userType.setUser_id(user_id);
            userType.setPhone(phone);
            userType.setUser_type(type);
            userType.setStreet_id(street_id);
            userType.setStreet_name(street_name);
            userType.setBuilding_id(building_id);
            userType.setBuilding_name(building_name);
            userType.setApartment_id(apartment_id);
            userType.setApartment_name(apartment_name);
            userType.setApartment_type_id(apartment_type_id);
            userType.setApartment_type_name(apartment_type_name);
            userType.setState_id(state_id);
            userType.setState_name(state_name);
            userType.setLga_id(lga_id);
            userType.setLga_name(lga_name);
            userType.setDistrict_id(district_id);
            userType.setDistrict_name(district_name);
        }


        res.close();
        return userType;
    }


    public List<Message> chatList(String query){
        List<Message> chats = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery(query,null);
        if(res.getCount() == 0){
            return null;
        }

        while(res.moveToNext()){
            int id = Integer.parseInt(res.getString(0));
            String msg = res.getString(1);
            int receiver_id = res.getInt(2);
            int sender_id = res.getInt(3);
            String time = res.getString(4);
            String type = res.getString(5);
            String status = res.getString(6);
            String timestamp = res.getString(7);
            String receiver_phone = res.getString(8);
            Message message = new Message();
            message.setId(id);
            message.setReceiver_id(receiver_id);
            message.setSender_id(sender_id);
            message.setMessage(msg);
            message.setTime(time);
            message.setType(type);
            message.setStatus(status);
            message.setTimestamp(timestamp);
            message.setReceiver_phone(receiver_phone);
            chats.add(message);
        }
        res.close();
        return chats;
    }

    public List<Birthdays> birthdaysList(String query){
        List<Birthdays> birthdays = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery(query,null);
        if(res.getCount() == 0){
            return null;
        }

        while(res.moveToNext()){
            int id = res.getInt(0);
            int sender_id = res.getInt(1);
            int receiver_id = res.getInt(2);
            String message = res.getString(3);
            int status = res.getInt(4);
            String date = res.getString(5);

            Birthdays birthday = new Birthdays();
            birthday.setId(id);
            birthday.setSender_id(sender_id);
            birthday.setReceiver_id(receiver_id);
            birthday.setMessage(message);
            birthday.setStatus(status);
            birthday.setDate(date);
            birthdays.add(birthday);
        }
        res.close();
        return birthdays;
    }



    public List<LessonNote> LessonNoteList(String query){
        List<LessonNote> lessonNotes = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery(query,null);
        if(res.getCount() == 0){
            return null;
        }

        while(res.moveToNext()){
            int id = Integer.parseInt(res.getString(0));
            int note_id = Integer.parseInt(res.getString(1));
            int exam_id = res.getInt(2);
            int teacher_id = res.getInt(3);
            String exam_title = res.getString(4);
            String notee = res.getString(5);
            int status = Integer.parseInt(res.getString(6));
            String timestamp = res.getString(7);
            LessonNote note = new LessonNote();
            note.setId(id);
            note.setNote_id(note_id);
            note.setExam_id(exam_id);
            note.setTeacher_id(teacher_id);
            note.setExam_title(exam_title);
            note.setNote(notee);
            note.setStatus(status);
            note.setDate(timestamp);
            lessonNotes.add(note);
        }
        res.close();
        return lessonNotes;
    }

    public List<ReportCard> reportCardList(String query){
        List<ReportCard> reportCards = new ArrayList<>();
        Cursor res = query(query);
        if(res.getCount()==0){
            return null;
        }
        if(res.getCount() > 0){
            while(res.moveToNext()){
                int id = res.getInt(0);
                int student_id = res.getInt(1);
                String student_name = res.getString(2);
                int teacher_id = res.getInt(3);
                String teacher_name = res.getString(4);
                String report_card = res.getString(5);
                int status = res.getInt(6);
                String date = res.getString(7);

                ReportCard reportCard = new ReportCard();
                reportCard.setId(id);
                reportCard.setStudent_id(student_id);
                reportCard.setStudent_name(student_name);
                reportCard.setTeacher_id(teacher_id);
                reportCard.setTeacher_name(teacher_name);
                reportCard.setReport_card(report_card);
                reportCard.setStatus(status);
                reportCard.setDate(date);
                reportCards.add(reportCard);
            }
        }
        return reportCards;
    }


    public List<EyeWitness> eyeWitnessList(String query){
        List<EyeWitness> eyeWitnessList = new ArrayList<>();
        Cursor res = query(query);
        if(res.getCount()==0){
            return null;
        }

        if(res.getCount() > 0){
            while(res.moveToNext()){
                int id = res.getInt(0);
                int eye_witness_id = res.getInt(1);
                int sender_id = res.getInt(2);
                int receiver_id = res.getInt(3);
                String message = res.getString(4);
                String sender_title = res.getString(5);
                String sender_first_name = res.getString(6);
                String sender_last_name = res.getString(7);
                String sender_other_names = res.getString(8);
                String file_name = res.getString(9);
                String file_type = res.getString(10);
                String timestamp = res.getString(11);
                String date = res.getString(12);
                String sender_image = res.getString(13);
                String sender_phone = res.getString(14);
                int send_type = res.getInt(15);
                EyeWitness eyeWitness = new EyeWitness();
                eyeWitness.setId(id);
                eyeWitness.setEye_witness_id(eye_witness_id);
                eyeWitness.setSender_id(sender_id);
                eyeWitness.setReceiver_id(receiver_id);
                eyeWitness.setMessage(message);
                eyeWitness.setSender_title(sender_title);
                eyeWitness.setSender_first_name(sender_first_name);
                eyeWitness.setSender_last_name(sender_last_name);
                eyeWitness.setSender_other_names(sender_other_names);
                eyeWitness.setFile_name(file_name);
                eyeWitness.setFile_type(file_type);
                eyeWitness.setTimestamp(timestamp);
                eyeWitness.setDate(date);
                eyeWitness.setSender_image(sender_image);
                eyeWitness.setSender_phone(sender_phone);
                eyeWitness.setSend_type(send_type);
                eyeWitnessList.add(eyeWitness);
            }
        }

        return eyeWitnessList;
    }

public List<StudentTeacher> listStudentTeacher(){
    List<StudentTeacher> studentTeachers = new ArrayList<>();
    String query = "select * from "+TABLE_STUDENT_TEACHER+" order by id desc";
    Cursor res = query(query);
    if(res.getCount()==0){
        return null;
    }
    if(res.getCount() > 0){
        while(res.moveToNext()){
            int id = Integer.parseInt(res.getString(0));
            int student_id = Integer.parseInt(res.getString(1));
            int teacher_id = Integer.parseInt(res.getString(2));
            String student_name = res.getString(3);
            String student_image = res.getString(4);
            String teacher_name = res.getString(5);
            String teacher_image = res.getString(6);
            int classroom_id = res.getInt(7);
            String classroom_name = res.getString(8);
            StudentTeacher st = new StudentTeacher();
            st.setId(id);
            st.setStudent_id(student_id);
            st.setTeacher_id(teacher_id);
            st.setStudent_name(student_name);
            st.setStudent_image(student_image);
            st.setTeacher_name(teacher_name);
            st.setTeacher_image(teacher_image);
            st.setClassroom_id(classroom_id);
            st.setClassroom_name(classroom_name);
            studentTeachers.add(st);
        }
    }
    return studentTeachers;
}




    public List<SchoolEvent> eventsList(String query){
        List<SchoolEvent> schoolEvents = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery(query,null);
        if(res.getCount() == 0){
            return null;
        }

        while(res.moveToNext()){
            int id = Integer.parseInt(res.getString(0));
            int user_id = Integer.parseInt(res.getString(1));
            String sender_name = res.getString(2);
            String sender_phone = res.getString(3);
            String sender_image = res.getString(4);
            String event_date = res.getString(5);
            String on_date = res.getString(6);
            String status = res.getString(7);
            String date = res.getString(8);
            String event_time = res.getString(9);
            String theme = res.getString(10);
            String location = res.getString(11);
            int hr = Integer.parseInt(res.getString(12));
            int min = Integer.parseInt(res.getString(13));
            String mnth = res.getString(14);
            int day = Integer.parseInt(res.getString(15));
            String ano = res.getString(16);
            int yr = Integer.parseInt(res.getString(17));
            String etype = res.getString(18);
            String image = res.getString(19);
            String thumbnail = res.getString(20);
            String sendTo = res.getString(21);
            SchoolEvent event = new SchoolEvent();
            event.setUser_id(user_id);
            event.setSender_name(sender_name);
            event.setSender_phone(sender_phone);
            event.setImage(sender_image);
            event.setEvent_date(event_date);
            event.setDate(date);
            event.setOn_date(on_date);
            event.setStatus(status);
            event.setTime(event_time);
            event.setTheme(theme);
            event.setLocation(location);
            event.setHour(hr);
            event.setMinute(min);
            event.setMonth(mnth);
            event.setDay(day);
            event.setAno(ano);
            event.setYear(yr);
            event.setEvent_type(etype);
            event.setImage(image);
            event.setThumbnail(thumbnail);
            event.setSendTo(sendTo);
            schoolEvents.add(event);
        }
        res.close();
        return schoolEvents;
    }





}
