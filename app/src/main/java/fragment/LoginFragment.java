package fragment;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import caller.com.testnav.MainActivity;
import caller.com.testnav.R;
import database.DatabaseHelper;
import dialogs.Dialogs;
import helper.Utils;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import server.GetJSONCallback;
import server.ServerRequests;

import static Constants.Constants.EMPTYY;
import static Constants.Constants.MODE;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static Constants.Constants.SUCCESS;
import static database.DatabaseHelper.TABLE_LGA_STAFF;
import static database.DatabaseHelper.TABLE_ORGANISATION_STAFF;
import static database.DatabaseHelper.TABLE_USERS;
import static database.DatabaseHelper.TABLE_USER_TYPE;

public class LoginFragment extends Fragment {
    EditText mUsername, mPassword;
    Button sign_in_button;
    TextView sign_up;
    ProgressDialog pDialog;
    Context context;
    DatabaseHelper db;
    SharedPreferences sharedPreferences;
    public LoginFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        db = new DatabaseHelper(getActivity());
        context = getActivity();
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE);
        mUsername = (EditText)view.findViewById(R.id.username);
        mPassword = (EditText)view.findViewById(R.id.password);
        sign_in_button = (Button)view.findViewById(R.id.sign_in_button);
        sign_up = (TextView) view.findViewById(R.id.sign_up);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load_register_fragment();
            }
        });

        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                if(TextUtils.isEmpty(username)){
                    Utils.popup(getActivity(),"Error","Email or Phone number is required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Utils.popup(getActivity(),"Error","Password is required");
                    return;
                }
                fakeLogin(username,password);
            }
        });
        TextView forgot = (TextView)view.findViewById(R.id.forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            new Dialogs().forgot_password_popup(context);
            }
        });
        return view;
    }

    private void fakeLogin(final String username, String password){
        sign_in_button.setText("Validating....");
        pDialog = new ProgressDialog(getActivity());
        pDialog.setTitle("Log In...");
        pDialog.setMessage("Checking credentials...");
        pDialog.setCancelable(false);
        pDialog.show();
        ServerRequests serverRequests = new ServerRequests();

        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("password",password)
                .addFormDataPart("username",username)
                .build();
        serverRequests.post(request_body, "login.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            validate_user(returnedJSON);
                            return;
                        }
                        Utils.popup(getActivity(),status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                sign_in_button.setText("Log In");

            }
        });


    }
    private void validate_user(JSONObject returnedJSON){
       try{
           ContentValues contentValues = new ContentValues();
           JSONObject userObject = returnedJSON.getJSONObject("user");
           JSONObject userTypeObject = returnedJSON.getJSONObject("user_type");

           String query = "select * from "+TABLE_USERS+" where phone='"+userObject.getString("phone")+"'";
           if(db.user_data(query) != null){
               contentValues.put("user_id",userObject.getString("id"));
               db.do_edit(TABLE_USERS,contentValues,"phone",userObject.getString("phone"));
               contentValues.put("user_type",userTypeObject.getString("user_type"));
               db.do_edit(TABLE_USER_TYPE,contentValues,"phone",userObject.getString("phone"));
           }else{
               contentValues.put("title",userObject.getString("title"));
               contentValues.put("phone",userObject.getString("phone"));
               contentValues.put("first_name",userObject.getString("first_name"));
               contentValues.put("last_name",userObject.getString("last_name"));
               contentValues.put("other_names",userObject.getString("other_names"));
               contentValues.put("email",userObject.getString("email"));
               contentValues.put("password",userObject.getString("password"));
               contentValues.put("email_code",userObject.getString("email_code"));
               contentValues.put("access",userObject.getString("access"));
               contentValues.put("date",userObject.getString("date"));
               contentValues.put("user_id",userObject.getString("id"));
               db.do_insert(TABLE_USERS,contentValues);
               //create user_type

               contentValues = new ContentValues();
               contentValues.put("user_id",userObject.getString("id"));
               contentValues.put("phone",userObject.getString("phone"));
               contentValues.put("street_id",userTypeObject.getString("street_id"));
               contentValues.put("street_name",userTypeObject.getString("street_name"));
               contentValues.put("user_type",userTypeObject.getString("user_type"));
               contentValues.put("apartment_id",userTypeObject.getString("apartment_id"));
               contentValues.put("apartment_name",userTypeObject.getString("apartment_name"));
               contentValues.put("building_id",userTypeObject.getString("building_id"));
               contentValues.put("building_name",userTypeObject.getString("building_name"));
               contentValues.put("apartment_type_name",userTypeObject.getString("apartment_type_name"));
               contentValues.put("apartment_type_id",userTypeObject.getString("apartment_type_id"));
               contentValues.put("state_id",userTypeObject.getString("state_id"));
               contentValues.put("state_name",userTypeObject.getString("state_name"));
               contentValues.put("lga_id",userTypeObject.getString("lga_id"));
               contentValues.put("lga_name",userTypeObject.getString("lga_name"));
               contentValues.put("district_id",userTypeObject.getString("district_id"));
               contentValues.put("district_name",userTypeObject.getString("district_name"));
               db.do_insert(TABLE_USER_TYPE,contentValues);
           }
           JSONObject organisationStaffTypeObject = returnedJSON.getJSONObject("organisation_staff");
           query = "select * from "+TABLE_ORGANISATION_STAFF+" where user_id="+userObject.getString("id")+"";
           Cursor orgcursor = db.query(query);
           if(!organisationStaffTypeObject.getString("state").equalsIgnoreCase(EMPTYY)){
               contentValues = new ContentValues();
               contentValues.put("user_id",organisationStaffTypeObject.getString("user_id"));
               contentValues.put("organisation_id",organisationStaffTypeObject.getString("organisation_id"));
               contentValues.put("organisation",organisationStaffTypeObject.getString("organisation"));
               contentValues.put("status",organisationStaffTypeObject.getString("status"));
               contentValues.put("date",organisationStaffTypeObject.getString("date"));

               if(orgcursor.getCount() == 0){
                   if(db.do_insert(TABLE_ORGANISATION_STAFF,contentValues)){
                   //    Log.e("**ORGANISATION STAFF**","*****INSERT SUCCESSFUL***");
                   }else{
                 //      Log.e("**ORGANISATION STAFF**","*****INSERT FAILED***");
                   }
               }else{
                   db.do_edit(TABLE_ORGANISATION_STAFF,contentValues,"user_id",userObject.getString("id"));
               //    Log.e("**ORGANISATION STAFF**","*****UPDATE SUCCESSFUL***");
               }
           }else{
               if(orgcursor.getCount() > 0){
                  db.do_delete(TABLE_ORGANISATION_STAFF,"user_id",userObject.getString("id"));
               //    Log.e("**ORGANISATION STAFF**","*****DELETE SUCCESSFUL***");
               }
           }

           JSONObject lgaStaffTypeObject = returnedJSON.getJSONObject("lga_staff");
           query = "select * from "+TABLE_LGA_STAFF+" where user_id="+userObject.getString("id")+"";
           Cursor LGAcursor = db.query(query);
           if(!lgaStaffTypeObject.getString("state").equalsIgnoreCase(EMPTYY)){
               contentValues = new ContentValues();
               contentValues.put("user_id",lgaStaffTypeObject.getString("user_id"));
               contentValues.put("lga_id",lgaStaffTypeObject.getString("lga_id"));
               contentValues.put("state_id",lgaStaffTypeObject.getString("state_id"));
               contentValues.put("name",lgaStaffTypeObject.getString("name"));

               if(LGAcursor.getCount() == 0){
                   if(db.do_insert(TABLE_LGA_STAFF,contentValues)){
             //          Log.e("**LGA STAFF**","*****INSERT SUCCESSFUL***");
                   }else{
               //        Log.e("**LGA STAFF**","*****INSERT FAILED***");
                   }
               }else{
                   db.do_edit(TABLE_LGA_STAFF,contentValues,"user_id",userObject.getString("id"));
              //     Log.e("**LGA STAFF**","*****UPDATE SUCCESSFUL***");
               }
           }else{
               if(LGAcursor.getCount() > 0){
                   db.do_delete(TABLE_LGA_STAFF,"user_id",userObject.getString("id"));
              //     Log.e("**LGA STAFF**","*****DELETE SUCCESSFUL***");
               }
           }

           SharedPreferences.Editor userLocalDatabaseEditor = sharedPreferences.edit();
           userLocalDatabaseEditor.putString(SP_USER_ID,userObject.getString("id"));
           userLocalDatabaseEditor.commit();
           startActivity(new Intent(context, MainActivity.class));

       }catch (Exception e){
           e.printStackTrace();
       }
    }

    private void load_register_fragment(){
        //   getSupportActionBar().setTitle("Sign In");
        Step1Fragment fragment = new Step1Fragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

}
