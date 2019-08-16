package helper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.webkit.MimeTypeMap;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import Objects.EyeWitness;
import Objects.LGAStaff;
import Objects.Message;
import Objects.Ratings;
import Objects.User;
import Objects.UserNeighborhood;
import Objects.UserType;
import caller.com.testnav.LoginActivity;
import caller.com.testnav.MainActivity;
import caller.com.testnav.R;
import database.DatabaseHelper;
import fragment.AboutUsFragment;
import fragment.AddUserFragment;
import fragment.AdminHomeFragment;
import fragment.CreateUserAccountFragment;
import fragment.DistressHistoryFragment;
import fragment.DuePayersFragment;
import fragment.EyeWitnessFragment;
import fragment.FamilyHomeFragment;
import fragment.FamilyMembersFragment;
import fragment.HotlinesFragment;
import fragment.JoinNeighborhoodFragment;
import fragment.LGAAdminHomeFragment;
import fragment.LGAStaffListStreetsFragment;
import fragment.LGAStaffLoadDistrictsFragment;
import fragment.LevyPayersFragment;
import fragment.ListApartmentsFragment;
import fragment.ListBuildingsFragment;
import fragment.ListDuesFragment;
import fragment.ListEyeWitnessFragment;
import fragment.ListLeviesFragment;
import fragment.ListStreetsFragment;
import fragment.LoadActivityLogFragment;
import fragment.LoadDistrictsFragment;
import fragment.LoadLGAFragment;
import fragment.LoginFragment;
import fragment.ProfileFragment;
import fragment.SettingsFragment;
import fragment.Step3Fragment;
import fragment.Step4Fragment;
import fragment.Step5Fragment;
import fragment.Step7Fragment;
import fragment.UserHomeFragment;

import static Constants.Constants.LOGGED_IN;
import static Constants.Constants.STORAGE_FOLDER;
import static database.DatabaseHelper.TABLE_CHAT;
import static database.DatabaseHelper.TABLE_EYE_WITNESS;
import static database.DatabaseHelper.TABLE_RATINGS;
import static database.DatabaseHelper.TABLE_USERS;
import static database.DatabaseHelper.TABLE_USER_NEIGHBORHOODS;
import static database.DatabaseHelper.TABLE_USER_TYPE;


/**
 * Created by user on 11/26/2018.
 */

public class Utils {

    public static String BUNDLE_PHONE = "user_phone";
    public static String BUNDLE_PASSWORD = "user_password";
    public static int HASH_LENGTH = 7;
    public static int HASH_CONSTANT = 31;
   // public static final String STORAGE_FOLDER = "pgc";
    public ProgressDialog pDialog;
    public static void popup(Context context,String title,String message){
        AlertDialog.Builder  builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    static public  String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        return sdf.format(new Date());

    }
    static public String formatTime(String date){
        String output = date;
        String[] data = date.split(":");
        if(data.length > 0){
            int hour = Integer.parseInt(data[0]);
            if(hour > 12){
                hour -= 12;
                output = hour+":"+data[1];
            }
        }
        return output;
    }
    static public String getTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm aa");
        String cur_time = sdf.format(new Date());
        return cur_time;
    }
    public static String hash(String data){
        long hash = HASH_LENGTH;
        String out = "";
        for (int i = 0; i < data.length(); i++) {
            int c = (int)(hash * HASH_CONSTANT * i);
            out +=   data.charAt(i) +""+ c;
        }
        return out;
    }
    public static String savePhotoToSDcard(Bitmap bit){
        String image_path = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String pname = sdf.format(new Date());
        String root = Environment.getExternalStorageDirectory().toString();
        File folder = new File(root+"/"+STORAGE_FOLDER);
        folder.mkdirs();

        File my_file = new File(folder,pname+".png");
        try {
            FileOutputStream stream = new FileOutputStream(my_file);
            bit.compress(Bitmap.CompressFormat.PNG,80,stream);
            stream.flush();
            stream.close();
            image_path = root+"/"+STORAGE_FOLDER+"/"+pname+".png";



        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return image_path;
    }
    public static String getMimeTtype(String path){
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return  MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    public static boolean saveUser(Context context, User user){
        DatabaseHelper db = new DatabaseHelper(context);
        ContentValues contentValues = new ContentValues();
        contentValues.put("TITLE",user.title);
        contentValues.put("FIRST_NAME",user.first_name);
        contentValues.put("LAST_NAME",user.last_name);
        contentValues.put("OTHER_NAMES",user.other_names);
        contentValues.put("PHONE",user.phone);
        contentValues.put("EMAIL",user.email);
        contentValues.put("USER_ID",user.user_id);
        contentValues.put("ACCESS",user.access);
        contentValues.put("PASSWORD",user.password);
        contentValues.put("THUMBNAIL",user.thumbnail);
        contentValues.put("EMAIL_CODE",user.email_code);
        contentValues.put("ACCESS",user.access);
        contentValues.put("IMAGE",user.image);
        contentValues.put("STATUS",LOGGED_IN);
        contentValues.put("DATE", user.date);
        String query = "select * from "+TABLE_USERS+" where user_id='"+user.user_id+"'";
        Cursor res = db.query(query);
        if(res.getCount() == 0){
            boolean status = db.do_insert(TABLE_USERS,contentValues);
            if(status){
                return true;
            }
        }else{
            db.do_edit(TABLE_USERS,contentValues,"user_id",user.user_id+"");
            return true;
        }

        return false;
    }

    public static Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {

        Bitmap bm = null;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);
        return bm;
    }
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2
            // and keeps both height and width larger than the requested
            // height and width.
            while ((halfHeight / inSampleSize) > reqHeight &&
                    (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static boolean saveUserNeighborhood(Context context, UserNeighborhood userNeighborhood){
        DatabaseHelper db = new DatabaseHelper(context);
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id",userNeighborhood.user_id);
        contentValues.put("state_id",userNeighborhood.state_id);
        contentValues.put("state_name",userNeighborhood.state_name);
        contentValues.put("street_id",userNeighborhood.street_id);
        contentValues.put("street_name",userNeighborhood.street_name);
        contentValues.put("apartment_id",userNeighborhood.apartment_id);
        contentValues.put("apartment_name",userNeighborhood.apartment_name);
        contentValues.put("building_id",userNeighborhood.building_id);
        contentValues.put("building_name",userNeighborhood.building_name);
        contentValues.put("lga_id",userNeighborhood.lga_id);
        contentValues.put("lga_name",userNeighborhood.lga_name);
        contentValues.put("district_id",userNeighborhood.district_id);
        contentValues.put("district_name",userNeighborhood.district_name);
        contentValues.put("apartment_type_id",userNeighborhood.apartment_type_id);
        contentValues.put("apartment_type_name",userNeighborhood.apartment_type_name);

        String query = "select * from "+TABLE_USER_NEIGHBORHOODS+" where user_id='"+userNeighborhood.user_id+"'";
        Cursor res = db.query(query);
        if(res.getCount() == 0) {
            if(db.do_insert(TABLE_USER_NEIGHBORHOODS,contentValues)){
                return true;
            }else{
                return false;
            }
        }else{
            if(db.do_edit(TABLE_USER_NEIGHBORHOODS,contentValues,"user_id",userNeighborhood.user_id+"")){
                return true;
            }
        }
        return false;
    }

    public static boolean saveUserType(Context context, UserType userType){
        DatabaseHelper db = new DatabaseHelper(context);
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id",userType.user_id);
        contentValues.put("street_id",userType.street_id);
        contentValues.put("street_name",userType.street_name);
        contentValues.put("apartment_id",userType.apartment_id);
        contentValues.put("apartment_name",userType.apartment_name);
        contentValues.put("building_id",userType.building_id);
        contentValues.put("building_name",userType.building_name);
        contentValues.put("lga_id",userType.lga_id);
        contentValues.put("lga_name",userType.lga_name);
        contentValues.put("district_id",userType.district_id);
        contentValues.put("district_name",userType.district_name);
        contentValues.put("apartment_type_id",userType.apartment_type_id);
        contentValues.put("apartment_type_name",userType.apartment_type_name);

        String query = "select * from "+TABLE_USER_TYPE+" where user_id='"+userType.user_id+"'";
        Cursor res = db.query(query);
        if(res.getCount() == 0) {
            if(db.do_insert(TABLE_USER_TYPE,contentValues)){
                return true;
            }else{
                return false;
            }
        }else{
            if(db.do_edit(TABLE_USER_TYPE,contentValues,"user_id",userType.user_id)){
                return true;
            }
        }
        return false;
    }

    public static boolean saveChat(Context context, User user, Message message, int receiver_id){
        DatabaseHelper db = new DatabaseHelper(context);
        ContentValues contentValues = new ContentValues();
        contentValues.put("message",message.message);
        contentValues.put("time",message.time);
        contentValues.put("receiver_id",receiver_id);
        contentValues.put("sender_id",user.user_id);
        contentValues.put("type",message.type);
        contentValues.put("timestamp",message.timestamp);
        contentValues.put("status",message.status);
        String query = "select * from "+TABLE_CHAT+ " where message='"+message.message+"' and timestamp='"+message.timestamp+"' and receiver_id='"+receiver_id+"' order by id desc";
        Cursor res = db.query(query);
        if(res.getCount() > 0){
            return true;
        }
        if(db.do_insert(TABLE_CHAT,contentValues)){
            return true;
        }
        return false;
    }


    public static String currencyFormat(String amount_){
        if(amount_.equals("") || amount_==null){
            return  "";
        }
        String number = amount_;
        double amount = Double.parseDouble(number);
        DecimalFormat formatter = new DecimalFormat("#,###");

        return formatter.format(amount);
    }


    public static void LoadStreetsFragment(Context context){
        ((MainActivity)context ).getSupportActionBar().setTitle("Streets");
        ListStreetsFragment fragment= new ListStreetsFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }


    public static void LoadLeviesFragment(Context context){
        ((MainActivity)context ).getSupportActionBar().setTitle("Levies");
        ListLeviesFragment fragment= new ListLeviesFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    public static void LoadDuesFragment(Context context){
        ((MainActivity)context ).getSupportActionBar().setTitle("Dues");
        ListDuesFragment fragment= new ListDuesFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    public static void LoadBuildingsFragment(Context context, JSONObject streetObject){
        ListBuildingsFragment fragment= new ListBuildingsFragment();
        try{
            ((MainActivity)context ).getSupportActionBar().setTitle(streetObject.getString("name"));
            Bundle data = new Bundle();//create bundle instance
            data.putString("street_id", streetObject.getString("id"));
            data.putString("street_name", streetObject.getString("name"));
            fragment.setArguments(data);
        }catch (Exception e){
            e.printStackTrace();
        }
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    public static void LoadApartmentFragment(Context context, JSONObject streetObject){
        ListApartmentsFragment fragment= new ListApartmentsFragment();
        try{
            ((MainActivity)context ).getSupportActionBar().setTitle(streetObject.getString("name"));
            Bundle data = new Bundle();//create bundle instance
            data.putString("building_id", streetObject.getString("id"));
            data.putString("apartment_name", streetObject.getString("name"));
            fragment.setArguments(data);
        }catch (Exception e){
            e.printStackTrace();
        }
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    public static void LoadStep4Fragment(Context context,User user, JSONObject streetObject){


        Step4Fragment fragment= new Step4Fragment();
        try{

            //update userType db data
            ContentValues contentValues = new ContentValues();
            contentValues.put("STREET_ID",streetObject.getString("id"));
            contentValues.put("STREET_NAME",streetObject.getString("name"));
            DatabaseHelper db = new DatabaseHelper(context);
            db.do_edit(TABLE_USER_TYPE,contentValues,"phone",user.phone);

            //      ((MainActivity)context ).getSupportActionBar().setTitle(streetObject.getString("name"));
            Bundle data = new Bundle();//create bundle instance
            data.putString("street_id", streetObject.getString("id"));
            data.putString("street_name", streetObject.getString("name"));
            data.putString("phone", user.phone);
            fragment.setArguments(data);
        }catch (Exception e){
            e.printStackTrace();
        }
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }


    public static void LoadStep5Fragment(Context context,User user, JSONObject streetObject){
        try{

            //update userType db data
            ContentValues contentValues = new ContentValues();
            contentValues.put("BUILDING_ID",streetObject.getString("id"));
            contentValues.put("BUILDING_NAME",streetObject.getString("name"));
            DatabaseHelper db = new DatabaseHelper(context);
            db.do_edit(TABLE_USER_TYPE,contentValues,"phone",user.phone);
            //      ((MainActivity)context ).getSupportActionBar().setTitle(streetObject.getString("name"));
            Step5Fragment fragment= new Step5Fragment();
            Bundle data = new Bundle();//create bundle instance
            data.putString("building_id", streetObject.getString("id"));
            data.putString("building_name", streetObject.getString("name"));
            data.putString("phone", user.phone);
            fragment.setArguments(data);

            android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void LoadStep7Fragment(Context context,User user){
        try{

            //      ((MainActivity)context ).getSupportActionBar().setTitle(streetObject.getString("name"));
            Step7Fragment fragment= new Step7Fragment();
            Bundle data = new Bundle();//create bundle instance
            data.putString("phone", user.phone);
            fragment.setArguments(data);
            android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void LoadProfileFragment(Context context){
        ((MainActivity)context ).getSupportActionBar().setTitle("Profile");
        ProfileFragment fragment= new ProfileFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    public static void LoadUserHomeFragment(Context context){
    //    ((MainActivity)context ).getSupportActionBar().setTitle("Profile");
        UserHomeFragment fragment= new UserHomeFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    public static void LoadFamilyHomeFragment(Context context){
        //    ((MainActivity)context ).getSupportActionBar().setTitle("Profile");
        FamilyHomeFragment fragment= new FamilyHomeFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    public static void LoadSettingsFragment(Context context){
        //    ((MainActivity)context ).getSupportActionBar().setTitle("Profile");
        SettingsFragment fragment= new SettingsFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }


    public static void LoadFamilyMembersFragment(Context context){
        ((MainActivity)context ).getSupportActionBar().setTitle("My Family Members");
        FamilyMembersFragment fragment= new FamilyMembersFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }


    public static void LoadAddMembersFragment(Context context){
        ((MainActivity)context ).getSupportActionBar().setTitle("Create Member Account");
        CreateUserAccountFragment fragment= new CreateUserAccountFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    public static void LoadLoginFragment(Context context){
        ((LoginActivity)context ).getSupportActionBar().setTitle("Sign In");
        LoginFragment fragment= new LoginFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    public static void LoadEyeWitnessFragment(Context context){
        ((MainActivity)context ).getSupportActionBar().setTitle("Eye witness");
        EyeWitnessFragment fragment= new EyeWitnessFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    public static void LoadAdminHomeFragment(Context context){
        //    ((MainActivity)context ).getSupportActionBar().setTitle("Profile");
        AdminHomeFragment fragment = new AdminHomeFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    public static void LoadLeviesDebtorsFragment(Context context,JSONObject object){
        try{
            ((MainActivity)context ).getSupportActionBar().setTitle(object.getString("name"));
            LevyPayersFragment fragment= new LevyPayersFragment();
            Bundle data = new Bundle();//create bundle instance
            data.putString("levy_name", object.getString("name"));
            data.putString("levy_id", object.getString("id"));
            data.putString("amount", object.getString("amount"));
            fragment.setArguments(data);
            android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void LoadDuesDebtorsFragment(Context context,JSONObject object){
        try{
            ((MainActivity)context ).getSupportActionBar().setTitle(object.getString("name"));
            DuePayersFragment fragment= new DuePayersFragment();
            Bundle data = new Bundle();//create bundle instance
            data.putString("due_name", object.getString("name"));
            data.putString("due_id", object.getString("id"));
            data.putString("amount", object.getString("amount"));
            fragment.setArguments(data);
            android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void LoadAddUserToApartmentFragment(Context context, JSONObject apartmentObject){
        try{
            AddUserFragment fragment= new AddUserFragment();
            ((MainActivity)context ).getSupportActionBar().setTitle(apartmentObject.getString("name"));
            Bundle data = new Bundle();//create bundle instance
            data.putString("apartment_id", apartmentObject.getString("id"));
            data.putString("apartment_name", apartmentObject.getString("name"));
            fragment.setArguments(data);

            android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void LoadLGAFragment(Context context,User user, JSONObject streetObject){

        try{
            LoadLGAFragment fragment= new LoadLGAFragment();
            //update userType db data
            ContentValues contentValues = new ContentValues();
            contentValues.put("STATE_ID",streetObject.getString("id"));
            contentValues.put("STATE_NAME",streetObject.getString("name"));
            DatabaseHelper db = new DatabaseHelper(context);
            db.do_edit(TABLE_USER_TYPE,contentValues,"phone",user.phone);

            //      ((MainActivity)context ).getSupportActionBar().setTitle(streetObject.getString("name"));
            Bundle data = new Bundle();//create bundle instance
            data.putString("state_id", streetObject.getString("id"));
            data.putString("state_name", streetObject.getString("name"));
            data.putString("phone", user.phone);
            fragment.setArguments(data);

            android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void LoadDistrictsFragment(Context context,User user, JSONObject lgaObject){


        LoadDistrictsFragment fragment= new LoadDistrictsFragment();
        try{
            //update userType db data
            ContentValues contentValues = new ContentValues();
            contentValues.put("LGA_ID",lgaObject.getString("id"));
            contentValues.put("LGA_NAME",lgaObject.getString("name"));
            DatabaseHelper db = new DatabaseHelper(context);
            db.do_edit(TABLE_USER_TYPE,contentValues,"phone",user.phone);

            //      ((MainActivity)context ).getSupportActionBar().setTitle(streetObject.getString("name"));
            Bundle data = new Bundle();//create bundle instance
            data.putString("lga_id", lgaObject.getString("id"));
            data.putString("lga_name", lgaObject.getString("name"));
            data.putString("phone", user.phone);
            fragment.setArguments(data);
        }catch (Exception e){
            e.printStackTrace();
        }
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }
    public static void LoadStreetsFragment(Context context,User user, JSONObject lgaObject){
        try{
            Step3Fragment fragment= new Step3Fragment();
            //update userType db data
            ContentValues contentValues = new ContentValues();
            contentValues.put("DISTRICT_ID",lgaObject.getString("id"));
            contentValues.put("DISTRICT_NAME",lgaObject.getString("name"));
            DatabaseHelper db = new DatabaseHelper(context);
            db.do_edit(TABLE_USER_TYPE,contentValues,"phone",user.phone);

            //      ((MainActivity)context ).getSupportActionBar().setTitle(streetObject.getString("name"));
            Bundle data = new Bundle();//create bundle instance
            data.putString("district_id", lgaObject.getString("id"));
            data.putString("district_name", lgaObject.getString("name"));
            data.putString("phone", user.phone);
            fragment.setArguments(data);

            android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void LoadDistressHistoryFragment(Context context){
        ((MainActivity)context ).getSupportActionBar().setTitle("Distress Calls");
        DistressHistoryFragment fragment= new DistressHistoryFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }


    public static void LoadActivityLogFragment(Context context){
        ((MainActivity)context ).getSupportActionBar().setTitle("My History");
        LoadActivityLogFragment fragment= new LoadActivityLogFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    public static boolean saveRatings(Context context, Ratings ratings){
        DatabaseHelper db = new DatabaseHelper(context);
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id",ratings.user_id);
        contentValues.put("rating",ratings.rating);
        contentValues.put("date",ratings.date);
        String query = "select * from "+TABLE_RATINGS+ " where user_id="+ratings.user_id+" order by id desc";
        Cursor res = db.query(query);
        if(res.getCount() > 0){
            db.do_edit(TABLE_RATINGS,contentValues,"user_id",ratings.user_id+"");
            Log.e("****RATING UPDATE****","****UPDATE****");
            return true;
        }
        if(db.do_insert(TABLE_RATINGS,contentValues)){
            Log.e("****RATING INSERT****","****INSERT****");
            return true;
        }
        return false;
    }

    public static boolean saveEyeWitness(Context context, EyeWitness eyeWitness){

        DatabaseHelper db = new DatabaseHelper(context);
        ContentValues contentValues = new ContentValues();
        contentValues.put("eye_witness_id",eyeWitness.eye_witness_id);
        contentValues.put("sender_id",eyeWitness.sender_id);
        contentValues.put("receiver_id",eyeWitness.receiver_id);
        contentValues.put("sender_title",eyeWitness.sender_title);
        contentValues.put("sender_first_name",eyeWitness.sender_first_name);
        contentValues.put("sender_last_name",eyeWitness.sender_last_name);
        contentValues.put("sender_other_names",eyeWitness.sender_other_names);
        contentValues.put("file_name",eyeWitness.file_name);
        contentValues.put("file_type",eyeWitness.file_type);
        contentValues.put("timestamp",eyeWitness.timestamp);
        contentValues.put("date",eyeWitness.date);
        contentValues.put("message",eyeWitness.message);
        contentValues.put("sender_image",eyeWitness.sender_image);
        contentValues.put("sender_phone",eyeWitness.sender_phone);
        contentValues.put("send_type",eyeWitness.send_type);
        String query = "select * from "+TABLE_EYE_WITNESS+ " where message='"+eyeWitness.message+"' and timestamp='"+eyeWitness.timestamp+"' and receiver_id="+eyeWitness.receiver_id+" and sender_id="+eyeWitness.sender_id+" order by id desc";
        Cursor res = db.query(query);
        if(res.getCount() > 0){
            Log.e("****LOGGED EYE****","****EXIST ALREADY****");
            db.do_delete(TABLE_EYE_WITNESS,"timestamp",eyeWitness.timestamp);
            // return true;
        }
        if(db.do_insert(TABLE_EYE_WITNESS,contentValues)){
            Log.e("****LOGGED EYE****","****LOGGED EYE WITNESS****");
            return true;
        }
        return false;
    }

    public static void LoadLGAAdminFragment(Context context,String name){
        ((MainActivity)context ).getSupportActionBar().setTitle(name);
        LGAAdminHomeFragment fragment= new LGAAdminHomeFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    public static void LoadLGAStaffLoadDistrictsFragment(Context context, LGAStaff lgaStaff){
        LGAStaffLoadDistrictsFragment fragment= new LGAStaffLoadDistrictsFragment();
        ((MainActivity)context ).getSupportActionBar().setTitle(lgaStaff.name);
        Bundle data = new Bundle();//create bundle instance
        data.putString("lga_id", lgaStaff.lga_id+"");
        data.putString("lga_name", lgaStaff.name);
        fragment.setArguments(data);
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    public static void LGAStaffListStreetsFragment(Context context,JSONObject districtObject){
        try{
            ((MainActivity)context ).getSupportActionBar().setTitle(districtObject.getString("name"));
            LGAStaffListStreetsFragment fragment= new LGAStaffListStreetsFragment();
            Bundle data = new Bundle();//create bundle instance
            data.putString("district_id", districtObject.getString("id"));
            data.putString("lga_id", districtObject.getString("lga_id"));
            data.putString("lga_name", districtObject.getString("name"));
            data.putString("state_id", districtObject.getString("state_id"));
            fragment.setArguments(data);
            android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void LoadListEyeWitnessFragment(Context context){
        ((MainActivity)context ).getSupportActionBar().setTitle("Eye witness Reports");
        ListEyeWitnessFragment fragment= new ListEyeWitnessFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    public static void LoadHotlinesFragment(Context context){
        ((MainActivity)context ).getSupportActionBar().setTitle("Hotlines");
        HotlinesFragment fragment= new HotlinesFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    public static void LoadAboutUsFragment(Context context){
        ((MainActivity)context ).getSupportActionBar().setTitle("About Us");
        AboutUsFragment fragment= new AboutUsFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    public static void LoadJoinNeighborhoodFragment(Context context){
        ((MainActivity)context ).getSupportActionBar().setTitle("Join a Neighborhood");
        JoinNeighborhoodFragment fragment= new JoinNeighborhoodFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

}
