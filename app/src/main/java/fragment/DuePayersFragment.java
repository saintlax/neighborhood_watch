package fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import Objects.Levy;
import Objects.User;
import Objects.UserType;
import adapters.UserLeviesAdapter;
import caller.com.testnav.LoginActivity;
import caller.com.testnav.R;
import database.DatabaseHelper;
import dialogs.Dialogs;
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

public class DuePayersFragment extends Fragment {

    User user;
    Context context;
    DatabaseHelper db;
    UserType userType;
    SharedPreferences sharedPreferences;
    String due_id,street_id="0",due_amount,due_name;
    Dialogs dialogs = new Dialogs();
    public DuePayersFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.content_staff_contaacts, container, false);
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
        due_id = getArguments().getString("due_id");
        due_amount = getArguments().getString("amount");
        due_name = getArguments().getString("due_name");


        query = "select * from "+TABLE_USER_TYPE+" where user_id='"+user_id+"' order by id desc";
        userType = db.user_type(query);
        getAllpaidUsers(view,due_id,street_id);
        return view;
    }


    private void loadData(JSONArray array,View view){
        ListView listview = (ListView)view.findViewById(R.id.listview);
        final UserLeviesAdapter adapter = new UserLeviesAdapter(context, array);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject obj = adapter.getItem(position);
             //   Levy levy = new Levy();
             //   levy.setId(Integer.parseInt(due_id));
            //    levy.setAmount(due_amount);
            //    levy.setName(due_name);
           //     dialogs.user_levies_popup_options(context,user,view,obj,levy);
                //krjghkd
            }
        });
    }

    private void getAllpaidUsers(final View view,String due_id,String street_id){
        ServerRequests serverRequests = new ServerRequests();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("due_id",due_id)
                .addFormDataPart("street_id",street_id)
                .addFormDataPart("viewer_type","PAID")
                .build();
        serverRequests.post(request_body, "users_due_payments.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                if(returnedJSON != null){
                    try{
                        //    postItems = returnedJSON.getJSONArray("data");
                        loadData(returnedJSON.getJSONArray("users"),view);
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
                            loadData(jsArray,view);
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
