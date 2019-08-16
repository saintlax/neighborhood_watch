package fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONObject;

import Objects.User;
import adapters.ApartmentsAdapter;
import adapters.BuildingsAdapter;
import caller.com.testnav.R;
import database.DatabaseHelper;
import dialogs.Dialogs;
import helper.AnimateFirstDisplayListener;
import helper.RoundedImageView;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import server.GetJSONCallback;
import server.ServerRequests;

import static Constants.Constants.LOGGED_IN;
import static Constants.Constants.MODE;
import static Constants.Constants.SP_APARTMENTS;
import static Constants.Constants.SP_BUILDINGS;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static database.DatabaseHelper.TABLE_USERS;


/**
 * Created by Chikadibia on 6/18/2019.
 */

public class ListApartmentsFragment extends Fragment {
    DatabaseHelper db;
    SharedPreferences sharedPreferences;
    User user;
    Dialogs myDialogs = new Dialogs();

    DisplayImageOptions options;
    ImageLoaderConfiguration imgconfig;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    Context context;
    String building_id,apartment_name;
    public ListApartmentsFragment(){

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview, container, false);
        db = new DatabaseHelper(getActivity());
        sharedPreferences = getActivity().getSharedPreferences(SP_NAME, MODE);
        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"' and status='"+LOGGED_IN+"' ";
      //  user = db.user_data(query);
        context = getActivity();
        user = new User();
        user.setUser_id(1);
        building_id = getArguments().getString("building_id");
        apartment_name = getArguments().getString("apartment_name");

        getApartments(user,view,building_id);
        return view;
    }

    private void loadSubjects(JSONArray returnedJSON, View view){
        if(returnedJSON.length() == 0){
            LinearLayout emptyLayout = (LinearLayout)view.findViewById(R.id.empty_layout);
            emptyLayout.setVisibility(View.VISIBLE);
            TextView textView = (TextView)view.findViewById(R.id.empty_text);
            String message = "<b>There is no result for this search</b>";
            textView.setText(Html.fromHtml(message));
        }else{

            ListView listview = (ListView)view.findViewById(R.id.listview);
            final ApartmentsAdapter adapter = new ApartmentsAdapter(getActivity(), returnedJSON);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    JSONObject obj = adapter.getItem(position);
                    myDialogs.apartment_options_popup(context,user,view,obj);
                }
            });
        }
    }
    private void getApartments(User user,final View view,String building_id){
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setTitle("Buildings Apartments");
        pDialog.setMessage("Please wait....");
        pDialog.setCancelable(false);
        pDialog.show();
        ServerRequests serverRequests = new ServerRequests();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("building_id",building_id)
                .build();
        serverRequests.post(request_body, "get_apartments.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        loadSubjects(returnedJSON.getJSONArray("apartments"),view);
                        //save the student data into shared preference
                        SharedPreferences.Editor userLocalDatabaseEditor = sharedPreferences.edit();
                        userLocalDatabaseEditor.putString(SP_APARTMENTS,returnedJSON.getJSONArray("apartments").toString());
                        userLocalDatabaseEditor.commit();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    //when the app cannot connect to server load data
                    String sp_students = sharedPreferences.getString(SP_APARTMENTS,"");
                    if(!sp_students.equals("")){
                        try{
                            String result = "{\"response\":true,\"apartments\":"+sp_students+"}";
                            JSONObject jObject = new JSONObject(result);
                            JSONArray jsArray = jObject.getJSONArray("apartments");
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
