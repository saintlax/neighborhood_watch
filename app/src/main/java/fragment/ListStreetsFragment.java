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
import adapters.StreetsAdapter;
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
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_STREETS;
import static Constants.Constants.SP_USER_ID;
import static database.DatabaseHelper.TABLE_USERS;
import static database.DatabaseHelper.TABLE_USER_TYPE;


/**
 * Created by Chikadibia on 6/18/2019.
 */

public class ListStreetsFragment extends Fragment {
    DatabaseHelper db;
    SharedPreferences sharedPreferences;
    User user;
    Dialogs myDialogs = new Dialogs();

    DisplayImageOptions options;
    ImageLoaderConfiguration imgconfig;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    TextView mTitle,mDetail;
    ImageView mBack;
    View title_view;
    RoundedImageView top_image;
    Toolbar toolbar;
    Context context;
    UserType userType;
    public ListStreetsFragment(){

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview, container, false);
        context = getActivity();
        db = new DatabaseHelper(getActivity());
        sharedPreferences = getActivity().getSharedPreferences(SP_NAME, MODE);
        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"'";
        user = db.user_data(query);

        query = "select * from "+TABLE_USER_TYPE+" where user_id='"+user_id+"' order by id desc";
        userType = db.user_type(query);



        getStreets(user,view);
        return view;
    }

    private void loadSubjects(JSONArray returnedJSON, View view){
        if(returnedJSON.length()==0){
            LinearLayout emptyLayout = (LinearLayout)view.findViewById(R.id.empty_layout);
            emptyLayout.setVisibility(View.VISIBLE);
            TextView textView = (TextView)view.findViewById(R.id.empty_text);
            String message = "<b>There is no result for this search</b>";
            textView.setText(Html.fromHtml(message));
        }else{
            ListView listview = (ListView)view.findViewById(R.id.listview);
            final StreetsAdapter adapter = new StreetsAdapter(getActivity(), returnedJSON);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    JSONObject obj = adapter.getItem(position);
                    myDialogs.street_options_popup(context,user,view,obj);
                }
            });
        }
    }
    private void getStreets(User user,final View view){
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setTitle("Streets");
        pDialog.setMessage("Loading streets in my district....");
        pDialog.setCancelable(false);
        pDialog.show();
        ServerRequests serverRequests = new ServerRequests();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("district_id",userType.district_id)
                .build();
        serverRequests.post(request_body, "get_streets.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        loadSubjects(returnedJSON.getJSONArray("streets"),view);
                        //save the student data into shared preference
                        SharedPreferences.Editor userLocalDatabaseEditor = sharedPreferences.edit();
                        userLocalDatabaseEditor.putString(SP_STREETS,returnedJSON.getJSONArray("streets").toString());
                        userLocalDatabaseEditor.commit();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    //when the app cannot connect to server load data
                    String sp_students = sharedPreferences.getString(SP_STREETS,"");
                    if(!sp_students.equals("")){
                        try{
                            String result = "{\"response\":true,\"streets\":"+sp_students+"}";
                            JSONObject jObject = new JSONObject(result);
                            JSONArray jsArray = jObject.getJSONArray("streets");
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
