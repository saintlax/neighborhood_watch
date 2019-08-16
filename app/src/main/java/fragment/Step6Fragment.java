package fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Objects.Classroom;
import Objects.TenantType;
import Objects.User;
import Objects.UserType;
import adapters.CustomPopupAdapter;
import adapters.TenantTypePopupAdapter;
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
import static Constants.Constants.SP_NAME;
import static database.DatabaseHelper.TABLE_USERS;
import static database.DatabaseHelper.TABLE_USER_TYPE;
import static helper.Utils.LoadStep7Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;

public class Step6Fragment extends Fragment {
    private LinearLayout mDotsLayout;
    private TextView[] mDots;
    String phone,building_id,building_name;
    Context context;
    DatabaseHelper db;
    User user;
    UserType userType;
    SharedPreferences sharedPreferences;
    Dialogs myDialogs = new Dialogs();
    Button mContinue_link;
    public Step6Fragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.step_6_register, container, false);
        context = getActivity();
        phone = getArguments().getString("phone");
        building_id = getArguments().getString("building_id");
        building_name = getArguments().getString("building_name");
        ((LoginActivity)context ).getSupportActionBar().setTitle("Confirm Account");

        db = new DatabaseHelper(getActivity());
        sharedPreferences = getActivity().getSharedPreferences(SP_NAME, MODE);
        String query = "select * from "+TABLE_USERS+" where PHONE='"+phone+"' order by id desc";
        user = db.user_data(query);

        query = "select * from "+TABLE_USER_TYPE+" where PHONE='"+phone+"' order by id desc";
        userType = db.user_type(query);

        String[] menu_name = {
                "Title: "+user.title,
                "Fullname: "+user.first_name+" "+user.other_names+" "+" "+user.last_name,
                "Phone: "+phone,
                "State: "+userType.state_name,
                "LGA: "+userType.lga_name,
                "District: "+userType.district_name,
                "Street: "+userType.street_name,
                "Building: "+userType.building_name,
                "Apartment: "+userType.apartment_name,
                "Account: "+userType.user_type,
                "Apartment Type: "+userType.apartment_type_name

                //            "Apartment ID: "+userType.apartment_id,
  //              "Apartment Type ID: "+userType.apartment_type_id,
  //              "STATE ID: "+userType.state_id,
//                "LGA ID: "+userType.lga_id,
      //          "DISTRICT ID: "+userType.district_id,
        };
        int[]  menu_icons = {
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary
        };
        register_detail_list(view,menu_name,menu_icons);
        mContinue_link = (Button)view.findViewById(R.id.continue_link);
        mContinue_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadStep7Fragment(context,user);
            }
        });
        return view;
    }


    private void register_detail_list(View view,String[] menu_name,int[] menu_icons){
        ListView lv = (ListView) view.findViewById(R.id.listview);
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                }
            }
        });
    }


}
