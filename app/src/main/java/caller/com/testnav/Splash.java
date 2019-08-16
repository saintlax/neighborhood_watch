package caller.com.testnav;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import Objects.User;
import database.DatabaseHelper;

import static Constants.Constants.MODE;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static database.DatabaseHelper.TABLE_USERS;


public class Splash extends AppCompatActivity {

    User user;
    DatabaseHelper db;
    Context context;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = Splash.this;
        db = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(SP_NAME, MODE);
        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"' order by id desc";
        user = db.user_data(query);
        setContentView(R.layout.activity_splash);
//            getSupportActionBar().hide();
            Thread background = new Thread() {
                public void run() {
                    try {
                        // Thread will sleep for 5 seconds
                        sleep(5 * 100);
                        go_next();
                        // After 5 seconds redirect to another intent
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            // start thread
            background.start();


    }
    public  void go_next(){

        Intent intent = new Intent(Splash.this, LoginActivity.class);
        if(user != null){
            intent = new Intent(Splash.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }




}
