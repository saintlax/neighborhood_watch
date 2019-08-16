package caller.com.testnav;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import Objects.SchoolEvent;
import Objects.User;
import adapters.UserLeviesAdapter;
import database.DatabaseHelper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import server.GetJSONCallback;
import server.ServerRequests;

import static Constants.Constants.MODE;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static database.DatabaseHelper.TABLE_USERS;
import android.app.ProgressDialog;
import android.widget.TextView;


public class PaymentSummaryActivity   extends AppCompatActivity {
    ArrayList<String> month_array;
    ArrayList<String> year_array;
    ArrayList<String> note_array;
    ArrayList<String> dates_array;
    ArrayList<SchoolEvent> event_array;
    int current_year;
    int current_month;
    int max_days;
    int current_date;
    SharedPreferences sharedPreferences;
    private Context context;
    User user;
    DatabaseHelper db;
    String payment_type;
    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = PaymentSummaryActivity.this;
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

        setContentView(R.layout.activity_payment_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //    window.setStatusBarColor(getResources().getColor(R.color.color_21));
     //       window.setStatusBarColor(Color.parseColor("#2a328a"));
        }

        note_array = new ArrayList<String>();
        dates_array = new ArrayList<String>();
        event_array = new ArrayList<>();

        month_array = new ArrayList<String>();
        month_array.add("January");
        month_array.add("February");
        month_array.add("March");
        month_array.add("April");
        month_array.add("May");
        month_array.add("June");
        month_array.add("July");
        month_array.add("August");
        month_array.add("September");
        month_array.add("October");
        month_array.add("November");
        month_array.add("December");

        year_array = new ArrayList<String>();

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        current_year = c.get(Calendar.YEAR);
        SimpleDateFormat sformator = new SimpleDateFormat("MM");

        current_month =  Integer.parseInt(sformator.format(c.getTime())); // c.get(Calendar.MONTH);

        for (int i = 2016; i <= year ; i++){
            year_array.add(String.valueOf(i));
        }

        max_days = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        current_date = c.get(Calendar.DATE);

        Spinner spinnermonth = (Spinner)findViewById(R.id.spinnermonth);
        Spinner spinneryear = (Spinner)findViewById(R.id.spinneryear);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, month_array); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnermonth.setAdapter(spinnerArrayAdapter);
        spinnermonth.setSelection(current_month - 1);
        spinnermonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                current_month = position + 1;
                getData(payment_type,current_month,current_year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, year_array); //selected item will look like a spinner set from XML
        spinnerArrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinneryear.setAdapter(spinnerArrayAdapter2);
        spinneryear.setSelection(year_array.indexOf(String.valueOf(current_year)));
        spinneryear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                current_year = Integer.parseInt(year_array.get(position));
                getData(payment_type,current_month,current_year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Button btnShow = (Button)findViewById(R.id.btnshow);
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(payment_type,current_month,current_year);
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        payment_type = bundle.getString("payment_type");
        getSupportActionBar().setTitle(payment_type);
        getData(payment_type,current_month,current_year);
    }


    private void loadData(JSONArray array){
        if(array.length()==0){
            LinearLayout emptyLayout = (LinearLayout)findViewById(R.id.empty_layout);
            emptyLayout.setVisibility(View.VISIBLE);
            TextView textView = (TextView)findViewById(R.id.empty_text);
            String message = "<b>There is no result for this search</b>";
            textView.setText(Html.fromHtml(message));
        }else{
            ListView listview = (ListView)findViewById(R.id.listview);
            final UserLeviesAdapter adapter = new UserLeviesAdapter(this, array);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    JSONObject obj = adapter.getItem(position);
                    //  dialogs.user_popup(context,user,view,obj);
                }
            });
        }
    }

    private void getData(String payment_type,int current_month,int current_year){
        /*pDialog = new ProgressDialog(context);
        pDialog.setTitle("Summary");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();*/
        ServerRequests serverRequests = new ServerRequests();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("payment_type",payment_type)
                .addFormDataPart("year",current_year+"")
                .addFormDataPart("month",current_month+"")
                .build();
        serverRequests.post(request_body, "monthly_payments.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
             //   pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        loadData(returnedJSON.getJSONArray("users"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
