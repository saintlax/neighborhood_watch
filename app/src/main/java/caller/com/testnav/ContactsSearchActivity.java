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
import org.json.JSONException;
import org.json.JSONObject;

import Objects.User;
import Objects.UserType;
import adapters.ContactsAdapter;
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
import static Constants.Constants.SUCCESS;
import static database.DatabaseHelper.TABLE_USERS;
import static database.DatabaseHelper.TABLE_USER_TYPE;


public class ContactsSearchActivity extends AppCompatActivity {

    DatabaseHelper db;
    User user;
    UserType userType;
    SharedPreferences sharedPreferences;
    Context context;
    ProgressDialog pDialog;
    String search_query;
    Dialogs dialogs = new Dialogs();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Contacts Search");
        db = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(SP_NAME, MODE);
        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"'";
        user = db.user_data(query);

        query = "select * from "+TABLE_USER_TYPE+" where user_id='"+user_id+"' order by id desc";
        userType = db.user_type(query);

        if(user == null){
            startActivity(new Intent(ContactsSearchActivity.this,LoginActivity.class));
            finish();
            return;
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        search_query = bundle.getString("search_query");
        getData(search_query);
    }

    private void loadData(JSONArray array){
        ListView listview = (ListView)findViewById(R.id.listview);
        final OccupantsAdapter adapter = new OccupantsAdapter(this, array);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject obj = adapter.getItem(position);
                /*try {
                    Intent intent = new Intent(ContactsSearchActivity.this, ChatActivity.class);;
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", userObject.getString("id"));
                    bundle.putString("fullname", userObject.getString("last_name")+" "+userObject.getString("first_name")+" "+userObject.getString("other_names"));
                    bundle.putString("image", userObject.getString("image"));
                    bundle.putString("thumbnail", userObject.getString("thumbnail"));
                    bundle.putString("phone", userObject.getString("phone"));
                    intent.putExtras(bundle);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                dialogs.user_popup(context,user,view,obj);
            }
        });
    }

    private void getData(String search_query){
        /**
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Contacts");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        **/
        ServerRequests serverRequests = new ServerRequests();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("search_query",search_query)
                .build();
        serverRequests.post(request_body, "search_user_type_by_query.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
           //     pDialog.cancel();
                if(returnedJSON != null){


                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){

                            loadData(returnedJSON.getJSONArray("data"));
                            //save the data into shared preference
                            SharedPreferences.Editor userLocalDatabaseEditor = sharedPreferences.edit();
                            userLocalDatabaseEditor.putString(SP_CONTACTS,returnedJSON.getJSONArray("data").toString());
                            userLocalDatabaseEditor.commit();
                        }else{
                            Utils.popup(context,"No Result",message);
                        }

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
                            Toast.makeText(context, "Offline Mode", Toast.LENGTH_SHORT).show();
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
                    Utils.popup(ContactsSearchActivity.this,"ERROR",getString(R.string.error_no_response));
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
           //     searchColleagues(query);
            //    numBackPressed++;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if(query.length() > 2){
             //       searchColleagues(query);
                  //  numBackPressed++;
                }else{
               //     getData(search_query);

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
