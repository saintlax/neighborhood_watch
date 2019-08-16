package caller.com.testnav;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import Objects.Due;
import Objects.Levy;
import Objects.User;
import Objects.UserType;
import adapters.UserLeviesAdapter;
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


public class DuePaymentActivity extends AppCompatActivity {

    DatabaseHelper db;
    User user;
    SharedPreferences sharedPreferences;
    String due_id,street_id="0",due_amount,due_name;
    Context context;
    Dialogs dialogs = new Dialogs();
    UserType userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_contaacts);
        context = DuePaymentActivity.this;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        due_id = bundle.getString("due_id");
        due_amount = bundle.getString("amount");
        due_name = bundle.getString("due_name");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pay Due");
        db = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(SP_NAME, MODE);
        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"'";
        user = db.user_data(query);
        if(user == null){
            startActivity(new Intent(context,LoginActivity.class));
            finish();
            return;
        }

        query = "select * from "+TABLE_USER_TYPE+" where user_id='"+user_id+"' order by id desc";
        userType = db.user_type(query);
        if(userType != null){
            street_id = userType.street_id;
        }
        getAllUnpaidUsers(due_id,street_id);
    }

    private void loadData(JSONArray array){
        if(array.length() == 0){
            LinearLayout emptyLayout = (LinearLayout)findViewById(R.id.empty_layout);
            emptyLayout.setVisibility(View.VISIBLE);
            TextView textView = (TextView)findViewById(R.id.empty_text);
            String message = "<b>There is no result for this search</b>";
            textView.setText(Html.fromHtml(message));
        }else{
            ListView listview = (ListView)findViewById(R.id.listview);
            final UserLeviesAdapter adapter = new UserLeviesAdapter(context, array);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    JSONObject obj = adapter.getItem(position);
                    Due due = new Due();
                    due.setId(Integer.parseInt(due_id));
                    due.setAmount(due_amount);
                    due.setName(due_name);
                    dialogs.user_dues_popup_options(context,user,view,obj,due);
                }
            });
        }
    }

    private void getAllUnpaidUsers(String due_id,String street_id){
        ServerRequests serverRequests = new ServerRequests();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("due_id",due_id)
                .addFormDataPart("street_id",street_id)
                .addFormDataPart("viewer_type","UNPAID")
                .build();
        serverRequests.post(request_body, "users_due_payments.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                if(returnedJSON != null){
                    try{
                        //    postItems = returnedJSON.getJSONArray("data");
                        loadData(returnedJSON.getJSONArray("users"));
                        //save the data into shared preference
                        SharedPreferences.Editor userLocalDatabaseEditor = sharedPreferences.edit();
                        userLocalDatabaseEditor.putString(SP_CONTACTS,returnedJSON.getJSONArray("users").toString());
                        userLocalDatabaseEditor.commit();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    //when the app cannot connect to server load data
                    String sp_students = sharedPreferences.getString(SP_CONTACTS,"");
                    if(!sp_students.equals("")){
                        try{
                            String result = "{\"response\":true,\"users\":"+sp_students+"}";
                            JSONObject jObject = new JSONObject(result);
                            JSONArray jsArray = jObject.getJSONArray("users");
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
                .build();
        serverRequests.post(request_body, "search_user.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                if(returnedJSON != null){
                    try{
                        loadData(returnedJSON.getJSONArray("user"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"ERROR",getString(R.string.error_no_response));
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
                    getAllUnpaidUsers(due_id,street_id);

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