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
import adapters.BuildingsAdapter;
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
import static Constants.Constants.SP_NAME;
import static database.DatabaseHelper.TABLE_USERS;

public class LoadLGAFragment extends Fragment {
    private LinearLayout mDotsLayout;
    private TextView[] mDots;
    String phone,state_id,state_name;
    Context context;
    DatabaseHelper db;
    User user;
    SharedPreferences sharedPreferences;
    Dialogs myDialogs = new Dialogs();
    public LoadLGAFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.step_4_register, container, false);
        context = getActivity();
        phone = getArguments().getString("phone");
        state_id = getArguments().getString("state_id");
        state_name = getArguments().getString("state_name");

        ((LoginActivity)context ).getSupportActionBar().setTitle(state_name);
        db = new DatabaseHelper(getActivity());

        sharedPreferences = getActivity().getSharedPreferences(SP_NAME, MODE);
        mDotsLayout = (LinearLayout)view.findViewById(R.id.dots_layout);
        addDotsIndicator(2);
        String query = "select * from "+TABLE_USERS+" where PHONE='"+phone+"' order by id desc";
        user = db.user_data(query);
        if(user != null){
            user.setUser_id(1);
            getLGAs(user,view,state_id);
        }else{
            Utils.popup(context,"Error","User could not be created");
        }
        return view;
    }


    private void loadSubjects(JSONArray returnedJSON, View view){
        if(returnedJSON.length() == 0){

            LinearLayout emptyLayout = (LinearLayout)view.findViewById(R.id.empty_layout);
            emptyLayout.setVisibility(View.VISIBLE);
            TextView textView = (TextView)view.findViewById(R.id.empty_text);
            String message = "<b>There are no Local Governments registered to "+state_name+" yet</b>";
            textView.setText(Html.fromHtml(message));
        }else{
            ListView listview = (ListView)view.findViewById(R.id.listview);
            final StatesAdapter adapter = new StatesAdapter(getActivity(), returnedJSON);;
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    JSONObject obj = adapter.getItem(position);
                    if(user !=null){
                        myDialogs.choose_lga_options_popup(context,user,view,obj);
                    }else{
                        Utils.popup(context,"NO PHONE","NO user created");

                    }
                }
            });
        }
    }
    private void getLGAs(User user,final View view,String state_id){
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setTitle("Local Governments");
        pDialog.setMessage("Please wait....");
        pDialog.setCancelable(false);
        pDialog.show();
        ServerRequests serverRequests = new ServerRequests();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("state_id",state_id)
                .build();
        serverRequests.post(request_body, "get_lgas.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        loadSubjects(returnedJSON.getJSONArray("lgas"),view);
                        //save the student data into shared preference
                        SharedPreferences.Editor userLocalDatabaseEditor = sharedPreferences.edit();
                        userLocalDatabaseEditor.putString(SP_BUILDINGS,returnedJSON.getJSONArray("lgas").toString());
                        userLocalDatabaseEditor.commit();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    //when the app cannot connect to server load data
                    String sp_students = sharedPreferences.getString(SP_BUILDINGS,"");
                    if(!sp_students.equals("")){
                        try{
                            String result = "{\"response\":true,\"lgas\":"+sp_students+"}";
                            JSONObject jObject = new JSONObject(result);
                            JSONArray jsArray = jObject.getJSONArray("lgas");
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
        LoadLGAFragment fragment = new LoadLGAFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }
}
