package fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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
import java.util.ArrayList;
import java.util.List;

import Objects.AllTypes;
import Objects.TenantType;
import Objects.User;
import Objects.UserType;
import adapters.AllTypesPopupAdapter;
import adapters.CustomPopupAdapter;
import adapters.TenantTypePopupAdapter;
import caller.com.testnav.LoginActivity;
import caller.com.testnav.R;
import database.DatabaseHelper;
import dialogs.Dialogs;
import dialogs.Post;
import helper.AnimateFirstDisplayListener;
import helper.RoundedImageView;
import helper.Utils;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import server.GetJSONCallback;
import server.ServerRequests;

import static Constants.Constants.CAMERA_REQUEST;
import static Constants.Constants.MODE;
import static Constants.Constants.RESULT_LOAD_IMAGE;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static android.app.Activity.RESULT_OK;
import static database.DatabaseHelper.TABLE_USERS;
import static database.DatabaseHelper.TABLE_USER_TYPE;
import static helper.Utils.LoadStreetsFragment;
import static helper.Utils.savePhotoToSDcard;

public class SettingsFragment extends Fragment {

    User user;
    Context context;
    DatabaseHelper db;
    UserType userType;
    SharedPreferences sharedPreferences;
    AlertDialog.Builder alertDialog;
    Dialogs dialogs = new Dialogs();
    public SettingsFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);
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

        query = "select * from "+TABLE_USER_TYPE+" where user_id='"+user_id+"' order by id desc";
        userType = db.user_type(query);

        String[] menu_name = {
                "Change address",
                "Notifications",
                "Change name",
                "Change user type",

        };
        int[]  menu_icons = {
                R.drawable.ic_edit_location_primary,
                R.drawable.ic_notifications_black,
                R.drawable.ic_user_primary,
                R.drawable.ic_edit_primary
        };

        ListView lv = (ListView) view.findViewById(R.id.listview);
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        change_address_popup(context,user);
                        break;
                    case 1:
                        break;
                        case 2:
                            dialogs.change_user_name_popup(context,user,userType);
                            break;
                    case 3:
                        change_user_type_popup(context,user,userType);
                        break;
                }
            }
        });
        return view;
    }

    public void change_address_popup(final Context context, final User user){
        alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("Change Address");
        alertDialog.setMessage("Do you want to change your address?");

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                getData("states",0);
            }
        });
        alertDialog.show();

    }
    public void getData(final String data,final int id){
        ServerRequests serverRequests = new ServerRequests();
        final ProgressDialog  pDialog = new ProgressDialog(context);
        pDialog.setTitle("Loading "+data);
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        String page = "get_states.php";
        RequestBody request_body = null;
        switch(data){
            case "states":
                page = "get_states.php";
                request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("user_id","0.1")
                        .build();
                break;
            case "lgas":
                page = "get_lgas.php";
                request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("user_id","0.1")
                        .addFormDataPart("state_id",id+"")
                        .build();
                break;
            case "districts":
                page = "get_districts.php";
                request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("user_id","0.1")
                        .addFormDataPart("lga_id",id+"")
                        .build();
                break;

            case "streets":
                page = "get_streets.php";
                request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("user_id","0.1")
                        .addFormDataPart("district_id",id+"")
                        .build();
                break;
            case "buildings":
                page = "get_buildings.php";
                request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("user_id","0.1")
                        .addFormDataPart("street_id",id+"")
                        .build();
                break;
                case "apartments":
                page = "get_apartments.php";
                request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("user_id","0.1")
                        .addFormDataPart("building_id",id+"")
                        .build();
                break;
            case "tenant_type":
                page = "get_all_tenant_type.php";
                request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("user_id","0.1")//not neccessary
                        .build();
                break;
        }

        serverRequests.post(request_body, page, new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        List<AllTypes> allTypesList = new ArrayList<>();
                        JSONArray staff = returnedJSON.getJSONArray(data);
                        for(int k=0;k<staff.length();k++){
                            JSONObject object = staff.getJSONObject(k);
                            AllTypes allTypes = new AllTypes();
                            allTypes.setId(object.getInt("id"));
                            allTypes.setName(object.getString("name"));
                            allTypesList.add(allTypes);
                        }
                        allTypes_popup(allTypesList,data);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }
    public void allTypes_popup(final List<AllTypes> allTypess,final String data){
       final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Choose your "+data);
        ListView lv = (ListView) convertView.findViewById(R.id.lv);

        AllTypesPopupAdapter adapter = new AllTypesPopupAdapter(context,allTypess);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                AllTypes allTypes = allTypess.get(position);
                switch (data){
                    case "states":
                        userType.setState_id(allTypes.id+"");
                        userType.setState_name(allTypes.name);
                        getData("lgas",allTypes.id);
                        break;
                    case "lgas":
                        userType.setLga_id(allTypes.id+"");
                        userType.setLga_name(allTypes.name);
                        getData("districts",allTypes.id);
                        break;
                    case "districts":
                        userType.setDistrict_id(allTypes.id+"");
                        userType.setDistrict_name(allTypes.name);
                        getData("streets",allTypes.id);
                        break;
                        case "streets":
                            userType.setStreet_id(allTypes.id+"");
                            userType.setStreet_name(allTypes.name);
                            getData("buildings",allTypes.id);
                        break;
                        case "buildings":
                            userType.setBuilding_id(allTypes.id+"");
                            userType.setBuilding_name(allTypes.name);
                            getData("apartments",allTypes.id);
                        break;
                    case "apartments":

                        userType.setApartment_id(allTypes.id+"");
                        userType.setApartment_name(allTypes.name);
                        getData("tenant_type",allTypes.id);
                        break;
                        default:
                            userType.setApartment_type_id(allTypes.id+"");
                            userType.setApartment_type_name(allTypes.name);
                            change_address_details_popup(context,userType);
                            break;
                }
            }
        });
    }
    public void change_address_details_popup(final Context context,final UserType userType){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Confirm new address");
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "State: "+userType.state_name,
                "LGA: "+userType.lga_name,
                "District: "+userType.district_name,
                "Street: "+userType.street_name,
                "Building: "+userType.building_name,
                "Apartment: "+userType.apartment_name,
                "Type: "+userType.apartment_type_name,
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
                R.drawable.ic_edit_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });
        alertDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                new Post().change_user_address(context,user,userType);
            }
        });
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
            }
        });

    }

    public void change_user_type_popup(final Context context,final User user,final UserType userType){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Change user type");
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        final String[] menu_name = {
                getString(R.string.landlord),
                getString(R.string.caretaker),
                getString(R.string.tenant),
                getString(R.string.family),
                getString(R.string.admin)
        };
        int[]  menu_icons = {
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                userType.setUser_type(menu_name[position]);
                confirm_change_user_type_popup(context,user,userType);
            }
        });

    }
    public void confirm_change_user_type_popup(final Context context, final User user,final UserType userType){
        alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("Confirm change");
        alertDialog.setMessage("Do you want to switch to\n"+userType.user_type+"?");

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
             new Post().change_user_address(context,user,userType);
            }
        });
        alertDialog.show();

    }




}
