package fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Objects.CountryState;
import Objects.User;
import Objects.UserType;
import adapters.DistressHistoryAdapter;
import adapters.HotlineAdapter;
import caller.com.testnav.R;
import database.DatabaseHelper;
import dialogs.Dialogs;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import server.GetJSONCallback;
import server.ServerRequests;

import static Constants.Constants.MODE;
import static Constants.Constants.SP_COUNTRY_STATES;
import static Constants.Constants.SP_DISTRESS_HISTORY;
import static Constants.Constants.SP_HOTLINES;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static database.DatabaseHelper.TABLE_USERS;
import static database.DatabaseHelper.TABLE_USER_TYPE;

public class HotlinesFragment extends Fragment {
    private LinearLayout mDotsLayout;
    private TextView[] mDots;
    Context context;
    DatabaseHelper db;
    User user;
    UserType userType;
    SharedPreferences sharedPreferences;
    Dialogs myDialogs = new Dialogs();

    ArrayList<String> states_array;
    List<CountryState> countryStateList;
    View view;
    int count = 0;
    String curStateId = "0";
    public HotlinesFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //final View view = inflater.inflate(R.layout.fragment_hotlines_listview, container, false);
        view = inflater.inflate(R.layout.fragment_hotlines_listview, container, false);
        context = getActivity();


        Button button = (Button)view.findViewById(R.id.btnshow);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getHotlines(user,curStateId,1);
            }
        });
        db = new DatabaseHelper(context);
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE);

        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"'";
        user = db.user_data(query);

        query = "select * from "+TABLE_USER_TYPE+" where user_id='"+user_id+"' order by id desc";
        userType = db.user_type(query);
        curStateId = userType.state_id;
        getHotlines(user,curStateId,0);
        return view;
    }
    LinearLayout emptyLayout;
    TextView textView;
    ListView listview;
    private void loadHotlines(JSONArray returnedJSON,View view){
     //   Log.e("***HOTLINE DUMP**","LENGTH: "+returnedJSON.length()+" DUMP: "+returnedJSON.toString());
    //    Toast.makeText(context, ""+returnedJSON.length(), Toast.LENGTH_SHORT).show();

        listview = (ListView)view.findViewById(R.id.listview);
        final HotlineAdapter adapter = new HotlineAdapter(getActivity(), returnedJSON);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject obj = adapter.getItem(position);
                myDialogs.hotline_popup_options(context,user,obj);
            }
        });



    }

    private void loadStates(JSONArray array){
        states_array = new ArrayList<>();
        countryStateList = new ArrayList<>();
        //first add your own state as the default state
        CountryState myState = new CountryState();
        myState.setId(Integer.parseInt(userType.state_id));
        myState.setName(userType.state_name);
        countryStateList.add(myState);
        states_array.add(userType.state_name);

        try{
            for(int k=0;k<array.length();k++){
                JSONObject stateObject = array.getJSONObject(k);
                CountryState countryState = new CountryState();
                countryState.setId(stateObject.getInt("id"));
                countryState.setName(stateObject.getString("name"));
                countryStateList.add(countryState);
                states_array.add(stateObject.getString("name"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        Spinner spinnermonth = (Spinner)view.findViewById(R.id.spinner_states);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, states_array); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnermonth.setAdapter(spinnerArrayAdapter);
      //  spinnermonth.setSelection(current_month - 1);
        spinnermonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CountryState countryState = countryStateList.get(position);
                curStateId = countryState.id+"";
                if(count > 1){
                    getHotlines(user,countryState.id+"",1);
                }
                count++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void getHotlines(User user, String state_id, final int viewState){
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setTitle("Hotlines");
        pDialog.setMessage("Loading Hotlines....");
        pDialog.setCancelable(false);
        pDialog.show();
        ServerRequests serverRequests = new ServerRequests();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("state_id",state_id)
                .build();
        serverRequests.post(request_body, "get_hotlines.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        count++;
                        //if zero, load the states else don't
                        if(viewState==0){
                            loadStates(returnedJSON.getJSONArray("states"));
                        }
                        loadHotlines(returnedJSON.getJSONArray("hotlines"),view);
                        //save the student data into shared preference
                        SharedPreferences.Editor userLocalDatabaseEditor = sharedPreferences.edit();
                        userLocalDatabaseEditor.putString(SP_HOTLINES,returnedJSON.getJSONArray("hotlines").toString());
                        userLocalDatabaseEditor.commit();

                        userLocalDatabaseEditor.putString(SP_COUNTRY_STATES,returnedJSON.getJSONArray("states").toString());
                        userLocalDatabaseEditor.commit();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    //when the app cannot connect to server load data
                    String sp_students = sharedPreferences.getString(SP_DISTRESS_HISTORY,"");

                    if(!sp_students.equals("")){
                        try{
                            String result = "{\"response\":true,\"hotlines\":"+sp_students+"}";
                            JSONObject jObject = new JSONObject(result);
                            JSONArray jsArray = jObject.getJSONArray("hotlines");
                            loadHotlines(jsArray,view);

                            Toast.makeText(getActivity(), "Offline Mode", Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    String sp_states = sharedPreferences.getString(SP_COUNTRY_STATES,"");
                    if(!sp_states.equalsIgnoreCase("")){
                       try {
                           String result = "{\"response\":true,\"states\":"+sp_states+"}";
                           JSONObject jObject1 = new JSONObject(result);
                           JSONArray jsArray1 = jObject1.getJSONArray("states");
                           loadStates(jsArray1);
                       }catch (Exception e){
                           e.printStackTrace();
                       }
                    }


                }
            }
        });
    }

}
