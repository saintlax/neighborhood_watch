package fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
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

import java.util.ArrayList;
import java.util.List;

import Objects.TenantType;
import Objects.User;
import adapters.ApartmentsAdapter;
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
import static Constants.Constants.SP_APARTMENTS;
import static Constants.Constants.SP_NAME;
import static database.DatabaseHelper.TABLE_USERS;
import static database.DatabaseHelper.TABLE_USER_TYPE;

public class Step5Fragment extends Fragment {
    private LinearLayout mDotsLayout;
    private TextView[] mDots;
    String phone,building_id,building_name;
    Context context;
    DatabaseHelper db;
    User user;
    SharedPreferences sharedPreferences;
    Dialogs myDialogs = new Dialogs();
    public Step5Fragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.step_4_register, container, false);
        phone = getArguments().getString("phone");
        building_id = getArguments().getString("building_id");
        building_name = getArguments().getString("building_name");
        context = getActivity();
        ((LoginActivity)context ).getSupportActionBar().setTitle(building_name);

        db = new DatabaseHelper(getActivity());

        sharedPreferences = getActivity().getSharedPreferences(SP_NAME, MODE);
        mDotsLayout = (LinearLayout)view.findViewById(R.id.dots_layout);
        addDotsIndicator(2);
        String query = "select * from "+TABLE_USERS+" where PHONE='"+phone+"' order by id desc";
        user = db.user_data(query);
        user.setUser_id(1);
        getApartments(user,view,building_id);
        return view;
    }


    private void loadSubjects(JSONArray returnedJSON, View view){
        if(returnedJSON.length() == 0){

            LinearLayout emptyLayout = (LinearLayout)view.findViewById(R.id.empty_layout);
            emptyLayout.setVisibility(View.VISIBLE);
            TextView textView = (TextView)view.findViewById(R.id.empty_text);
            String message = "<b>There are no apartments added to "+building_name+"</b>";
            textView.setText(Html.fromHtml(message));
        }else{
            ListView listview = (ListView)view.findViewById(R.id.listview);
            final ApartmentsAdapter adapter = new ApartmentsAdapter(getActivity(), returnedJSON);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    JSONObject obj = adapter.getItem(position);
                    getApartmentTypes(obj);
                    //load_next_fragment(obj);
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


    private void load_next_fragment(JSONObject apartmentObject){
        try {
            Step6Fragment fragment = new Step6Fragment();
            //update userType db data
            ContentValues contentValues = new ContentValues();
            contentValues.put("APARTMENT_ID",apartmentObject.getString("id"));
            contentValues.put("APARTMENT_NAME",apartmentObject.getString("name"));
            contentValues.put("APARTMENT_TYPE_ID",apartment_type_id);
            contentValues.put("APARTMENT_TYPE_NAME",apartment_type_name);
            DatabaseHelper db = new DatabaseHelper(context);
            db.do_edit(TABLE_USER_TYPE,contentValues,"phone",user.phone);


            Bundle data = new Bundle();//create bundle instance
            data.putString("building_id", building_id);
            data.putString("building_name", building_name);
            data.putString("phone", phone);
            fragment.setArguments(data);
            android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    ProgressDialog pDialog;
    AlertDialog.Builder builder;
    int apartment_type_id = 0;
    String apartment_type_name = "No Listed";
    public void getApartmentTypes(final JSONObject apartmentObject){
        ServerRequests serverRequests = new ServerRequests();
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Loading Type");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id","0.1")
                .build();
        serverRequests.post(request_body, "get_all_tenant_type.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        List<TenantType> tenantTypeList = new ArrayList<>();
                        JSONArray staff = returnedJSON.getJSONArray("tenant_type");
                        for(int k=0;k<staff.length();k++){
                            JSONObject object = staff.getJSONObject(k);
                            TenantType tenantType = new TenantType();
                            tenantType.setId(object.getInt("id"));
                            tenantType.setName(object.getString("name"));
                            tenantTypeList.add(tenantType);
                        }
                        tenantType_popup(tenantTypeList,apartmentObject);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }
    public void tenantType_popup(final List<TenantType> tenantTypes,final JSONObject apartmentObject){
        builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        builder.setView(convertView);
        builder.setTitle("Choose your Apartment Type");
        ListView lv = (ListView) convertView.findViewById(R.id.lv);

        TenantTypePopupAdapter adapter = new TenantTypePopupAdapter(context,tenantTypes);
        lv.setAdapter(adapter);
        final AlertDialog ad = builder.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                TenantType tenantType = tenantTypes.get(position);
                if(tenantType != null){
                    apartment_type_id = tenantType.id;
                    apartment_type_name = tenantType.name;
                    load_next_fragment(apartmentObject);
                }
            }
        });
    }
}
