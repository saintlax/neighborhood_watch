package caller.com.testnav;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import Objects.User;
import Objects.UserType;
import adapters.OccupantsAdapter;
import database.DatabaseHelper;
import dialogs.Dialogs;
import helper.Utils;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import server.GetJSONCallback;
import server.ServerRequests;

import static Constants.Constants.MODE;
import static Constants.Constants.SP_CONTACTS;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static database.DatabaseHelper.TABLE_USERS;
import static database.DatabaseHelper.TABLE_USER_TYPE;


public class OccupantsActivity extends AppCompatActivity {

    DatabaseHelper db;
    User user;
    UserType userType;
    SharedPreferences sharedPreferences;
    Context context;
    Dialogs dialogs = new Dialogs();
    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_contaacts);
        context = OccupantsActivity.this;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(SP_NAME, MODE);
        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"'";
        user = db.user_data(query);
        query = "select * from "+TABLE_USER_TYPE+" where user_id='"+user_id+"' order by id desc";
        userType = db.user_type(query);

        if(user == null){
            startActivity(new Intent(OccupantsActivity.this,LoginActivity.class));
            finish();
            return;
        }
        getData();
    }

    private void loadData(JSONArray array){
        ListView listview = (ListView)findViewById(R.id.listview);
        final OccupantsAdapter adapter = new OccupantsAdapter(this, array);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject obj = adapter.getItem(position);
                dialogs.user_popup(context,user,view,obj);
            }
        });
    }

    private void getData(){
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Occupants");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        ServerRequests serverRequests = new ServerRequests();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("district_id",userType.district_id)
                .build();
        serverRequests.post(request_body, "get_occupants.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                    //    postItems = returnedJSON.getJSONArray("data");
                    loadData(returnedJSON.getJSONArray("data"));
                        //save the data into shared preference
                        SharedPreferences.Editor userLocalDatabaseEditor = sharedPreferences.edit();
                        userLocalDatabaseEditor.putString(SP_CONTACTS,returnedJSON.getJSONArray("data").toString());
                        userLocalDatabaseEditor.commit();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    //when the app cannot connect to server load data
                    String sp_students = sharedPreferences.getString(SP_CONTACTS,"");
                    if(!sp_students.equals("")){
                        try{
                            String result = "{\"response\":true,\"data\":"+sp_students+"}";
                            JSONObject jObject = new JSONObject(result);
                            JSONArray jsArray = jObject.getJSONArray("data");
                            loadData(jsArray);
                            Toast.makeText(OccupantsActivity.this, "Offline Mode", Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void searchColleagues(String query){
        ServerRequests serverRequests = new ServerRequests();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("query",query)
                .addFormDataPart("district_id",userType.district_id)
                .build();
        serverRequests.post(request_body, "search_users.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                if(returnedJSON != null){
                    try{
                        loadData(returnedJSON.getJSONArray("data"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(OccupantsActivity.this,"ERROR",getString(R.string.error_no_response));
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchColleagues(query);
            //    numBackPressed++;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if(query.length() > 2){
                    searchColleagues(query);
                  //  numBackPressed++;
                }else{
                    getData();

                }
                return false;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            /*case R.id.action_settings:
                Utils.popup(MainActivity.this,"Settings","Do you want to change your setting?");
                break;*/
            case R.id.action_search:

                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
