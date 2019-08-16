package fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import Objects.User;
import adapters.ActivityLogAdapter;
import adapters.StatesAdapter;
import caller.com.testnav.LoginActivity;
import caller.com.testnav.R;
import database.DatabaseHelper;
import dialogs.Dialogs;
import helper.Utils;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import server.GetJSONCallback;
import server.ServerRequests;

import static Constants.Constants.MODE;
import static Constants.Constants.SP_BUILDINGS;
import static Constants.Constants.SP_LOGS;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static database.DatabaseHelper.TABLE_USERS;

public class LoadActivityLogFragment extends Fragment {
    private LinearLayout mDotsLayout;
    private TextView[] mDots;
    Context context;
    DatabaseHelper db;
    User user;
    SharedPreferences sharedPreferences;
    Dialogs myDialogs = new Dialogs();
    public LoadActivityLogFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_activity_log, container, false);
        context = getActivity();

        db = new DatabaseHelper(getActivity());
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE);
        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"'";
        user = db.user_data(query);
        getActivityLog(user,view);
        return view;
    }


    private void loadSubjects(JSONArray returnedJSON, View view){
        if(returnedJSON.length() == 0){

            LinearLayout emptyLayout = (LinearLayout)view.findViewById(R.id.empty_layout);
            emptyLayout.setVisibility(View.VISIBLE);
            TextView textView = (TextView)view.findViewById(R.id.empty_text);
            String message = "<b>Nothing has been logged into your account yet</b>";
            textView.setText(Html.fromHtml(message));
        }else{
            ListView listview = (ListView)view.findViewById(R.id.listview);
            final ActivityLogAdapter adapter = new ActivityLogAdapter(getActivity(), returnedJSON);;
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    JSONObject obj = adapter.getItem(position);
//                    myDialogs.choose_lga_options_popup(context,user,view,obj);

                }
            });
        }
    }
    private void getActivityLog(User user,final View view){
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setTitle("My History");
        pDialog.setMessage("Please wait....");
        pDialog.setCancelable(false);
        pDialog.show();
        ServerRequests serverRequests = new ServerRequests();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .build();
        serverRequests.post(request_body, "get_activity_logs.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        loadSubjects(returnedJSON.getJSONArray("logs"),view);
                        //save the student data into shared preference
                        SharedPreferences.Editor userLocalDatabaseEditor = sharedPreferences.edit();
                        userLocalDatabaseEditor.putString(SP_LOGS,returnedJSON.getJSONArray("logs").toString());
                        userLocalDatabaseEditor.commit();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    //when the app cannot connect to server load data
                    String sp_students = sharedPreferences.getString(SP_LOGS,"");
                    if(!sp_students.equals("")){
                        try{
                            String result = "{\"response\":true,\"logs\":"+sp_students+"}";
                            JSONObject jObject = new JSONObject(result);
                            JSONArray jsArray = jObject.getJSONArray("logs");
                            loadSubjects(jsArray,view);
                            Toast.makeText(getActivity(), "Offline Mode", Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void addDotsIndicator(int position){
        mDots = new TextView[5];
        mDotsLayout.removeAllViews();
        for(int i =0;i<4;i++){
            mDots[i] = new TextView(getActivity());
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.transparentWhite));
            mDotsLayout.addView(mDots[i]);
        }
//        mDotsLayout.setPadding(0,getResources().getDimensionPixelSize(R.dimen.pad_20dp),0,0);

        if(mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.white));
        }

    }


    private void load_next_fragment(){
        //   getSupportActionBar().setTitle("Sign In");
        LoadActivityLogFragment fragment = new LoadActivityLogFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }
}
