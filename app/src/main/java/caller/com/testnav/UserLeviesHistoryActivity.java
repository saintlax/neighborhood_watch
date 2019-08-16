package caller.com.testnav;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import Objects.User;
import Objects.UserType;
import adapters.LeviesHistoryAdapter;
import adapters.OccupantsAdapter;
import database.DatabaseHelper;
import dialogs.Dialogs;
import helper.AnimateFirstDisplayListener;
import helper.RoundedImageView;
import helper.Utils;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import server.GetJSONCallback;
import server.ServerRequests;

import static Constants.Constants.MODE;
import static Constants.Constants.SP_CONTACTS;
import static Constants.Constants.SP_LEVIES_HISTORY;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static database.DatabaseHelper.TABLE_USERS;
import static database.DatabaseHelper.TABLE_USER_TYPE;
import static server.ServerRequests.SERVER_ROOT;


public class UserLeviesHistoryActivity extends AppCompatActivity {

    DatabaseHelper db;
    User user;
    SharedPreferences sharedPreferences;
    Context context;
    Dialogs dialogs = new Dialogs();
    String member_id,first_name,last_name,thumbnail,access_type;
    View title_view;

    TextView chatterName,chatterTyping,remainingMsg;
    DisplayImageOptions options;
    ImageLoaderConfiguration imgconfig;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_contaacts);
        context = UserLeviesHistoryActivity.this;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LayoutInflater inflater = LayoutInflater.from(this);
        title_view = inflater.inflate(R.layout.custom_title_bar,null);
        toolbar.addView(title_view);

        db = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(SP_NAME, MODE);
        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"'";
        user = db.user_data(query);
        if(user == null){
            startActivity(new Intent(UserLeviesHistoryActivity.this,LoginActivity.class));
            finish();
            return;
        }
        member_id = bundle.getString("user_id");
        last_name = bundle.getString("last_name");
        first_name = bundle.getString("first_name");
        thumbnail = bundle.getString("thumbnail");
        access_type = bundle.getString("access_type");

        TextView head_title = (TextView)title_view.findViewById(R.id.head_title);
        head_title.setText(last_name+" "+first_name);
        TextView head_detail = (TextView)title_view.findViewById(R.id.head_detail);
        head_detail.setText(access_type);


        query = "select * from "+TABLE_USER_TYPE+" where user_id='"+user_id+"' order by id desc";
        UserType userType = db.user_type(query);


        File cacheDir = StorageUtils.getCacheDirectory(this);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_user_white2)
                .showImageForEmptyUri(R.drawable.ic_user_white2)
                .showImageOnFail(R.drawable.ic_user_white2)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.NONE)
                .build();

        imgconfig = new ImageLoaderConfiguration.Builder(this)
                .build();
        ImageLoader.getInstance().init(imgconfig);
        RoundedImageView top_image = (RoundedImageView)title_view.findViewById(R.id.head_image);
        try{
            if(!thumbnail.equalsIgnoreCase("NULL") && !thumbnail.equalsIgnoreCase("")){
                //show a user is viewing his history
                if(userType != null && !userType.user_type.equalsIgnoreCase(getString(R.string.admin))){
                    File file = new File(user.image);
                    Uri imageUri  = Uri.fromFile(file);
                    ImageLoader.getInstance().displayImage(imageUri.toString(), top_image, options, animateFirstListener);

                }else{
                    ImageLoader.getInstance().displayImage(SERVER_ROOT.replace("/android","") + thumbnail, top_image, options, animateFirstListener);
                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }

        getHistory(user,member_id);
    }

    private void loadSubjects(JSONArray returnedJSON){
        if(returnedJSON.length() == 0){
            LinearLayout emptyLayout = (LinearLayout)findViewById(R.id.empty_layout);
            emptyLayout.setVisibility(View.VISIBLE);
            TextView textView = (TextView)findViewById(R.id.empty_text);
            String message = "<b>Payment has not been recorded in this log</b>";
            textView.setText(Html.fromHtml(message));
        }else{
            ListView listview = (ListView)findViewById(R.id.listview);
            final LeviesHistoryAdapter adapter = new LeviesHistoryAdapter(context, returnedJSON);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    JSONObject obj = adapter.getItem(position);
                    //      myDialogs.apartment_options_popup(context,user,view,obj);
                }
            });
        }
    }
    private void getHistory(User user,String member_id){
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setTitle(access_type);
        pDialog.setMessage("Please wait....");
        pDialog.setCancelable(false);
        pDialog.show();
        ServerRequests serverRequests = new ServerRequests();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("member_id",member_id)
                .addFormDataPart("access_type",access_type)
                .build();
        serverRequests.post(request_body, "user_levies_history.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        loadSubjects(returnedJSON.getJSONArray("history"));
                        //save the student data into shared preference
                        SharedPreferences.Editor userLocalDatabaseEditor = sharedPreferences.edit();
                        userLocalDatabaseEditor.putString(SP_LEVIES_HISTORY,returnedJSON.getJSONArray("history").toString());
                        userLocalDatabaseEditor.commit();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    //when the app cannot connect to server load data
                    String sp_students = sharedPreferences.getString(SP_LEVIES_HISTORY,"");
                    if(!sp_students.equals("")){
                        try{
                            String result = "{\"response\":true,\"history\":"+sp_students+"}";
                            JSONObject jObject = new JSONObject(result);
                            JSONArray jsArray = jObject.getJSONArray("history");
                            loadSubjects(jsArray);
                            Toast.makeText(context, "Offline Mode", Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

}
