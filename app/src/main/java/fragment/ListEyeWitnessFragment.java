package fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import Objects.LGAStaff;
import Objects.User;
import adapters.ApartmentsAdapter;
import adapters.EyeWitnessAdapter;
import caller.com.testnav.LoginActivity;
import caller.com.testnav.R;
import database.DatabaseHelper;
import dialogs.Dialogs;
import helper.AnimateFirstDisplayListener;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import server.GetJSONCallback;
import server.ServerRequests;

import static Constants.Constants.LOGGED_IN;
import static Constants.Constants.MODE;
import static Constants.Constants.SP_APARTMENTS;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static database.DatabaseHelper.TABLE_LGA_STAFF;
import static database.DatabaseHelper.TABLE_USERS;


/**
 * Created by Chikadibia on 6/18/2019.
 */

public class ListEyeWitnessFragment extends Fragment {
    DatabaseHelper db;
    SharedPreferences sharedPreferences;
    User user;
    Dialogs myDialogs = new Dialogs();

    DisplayImageOptions options;
    ImageLoaderConfiguration imgconfig;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    Context context;
    LGAStaff lgaStaff;
    public ListEyeWitnessFragment(){

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview, container, false);
        context = getActivity();
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE);
        db = new DatabaseHelper(context);
        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"' order by id desc";
        user = db.user_data(query);
        if(user == null){
            startActivity(new Intent(context, LoginActivity.class));
            return null;
        }

        query = "select * from "+TABLE_LGA_STAFF+" where user_id="+user_id+" order by id desc limit 1";
        List<LGAStaff> lgaStaffs = db.lgaStaffList(query);
        if(lgaStaffs != null){
            for(LGAStaff lgaStafff : lgaStaffs){
                lgaStaff = lgaStafff;
            }
        }
        getReports(user,view);
        return view;
    }

    private void loadSubjects(JSONArray returnedJSON, View view){
        if(returnedJSON.length() == 0){
            LinearLayout emptyLayout = (LinearLayout)view.findViewById(R.id.empty_layout);
            emptyLayout.setVisibility(View.VISIBLE);
            TextView textView = (TextView)view.findViewById(R.id.empty_text);
            String message = "<b>There are no eye witness reports</b>";
            textView.setText(Html.fromHtml(message));
        }else{

            ListView listview = (ListView)view.findViewById(R.id.listview);
            final EyeWitnessAdapter adapter = new EyeWitnessAdapter(getActivity(), returnedJSON);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    JSONObject obj = adapter.getItem(position);
                    myDialogs.eye_witness_options_popup(context,user,view,obj);
                }
            });
        }
    }
    private void getReports(User user,final View view){
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setTitle("Eye witness Reports");
        pDialog.setMessage("Please wait....");
        pDialog.setCancelable(false);
        pDialog.show();
        ServerRequests serverRequests = new ServerRequests();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .build();
        serverRequests.post(request_body, "all_eye_witness_reports.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        loadSubjects(returnedJSON.getJSONArray("eye_witness_report"),view);
                        //save the student data into shared preference
                        SharedPreferences.Editor userLocalDatabaseEditor = sharedPreferences.edit();
                        userLocalDatabaseEditor.putString(SP_APARTMENTS,returnedJSON.getJSONArray("eye_witness_report").toString());
                        userLocalDatabaseEditor.commit();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    //when the app cannot connect to server load data
                    String sp_students = sharedPreferences.getString(SP_APARTMENTS,"");
                    if(!sp_students.equals("")){
                        try{
                            String result = "{\"response\":true,\"eye_witness_report\":"+sp_students+"}";
                            JSONObject jObject = new JSONObject(result);
                            JSONArray jsArray = jObject.getJSONArray("eye_witness_report");
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

}
