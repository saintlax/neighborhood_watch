package fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Objects.AllTypes;
import Objects.User;
import Objects.UserNeighborhood;
import Objects.UserType;
import adapters.AllTypesPopupAdapter;
import adapters.CustomPopupAdapter;
import caller.com.testnav.R;
import database.DatabaseHelper;
import dialogs.Dialogs;
import dialogs.Post;
import helper.Utils;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import server.GetJSONCallback;
import server.ServerRequests;

import static Constants.Constants.MODE;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static database.DatabaseHelper.TABLE_USERS;

public class JoinNeighborhoodFragment extends Fragment {
    Context context;
    DatabaseHelper db;
    User user;
    SharedPreferences sharedPreferences;
    Dialogs myDialogs = new Dialogs();
    Button join;
    UserNeighborhood userNeighborhood;
    AlertDialog.Builder alertDialog;
    public JoinNeighborhoodFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_join_neighborhood, container, false);
        context = getActivity();
        db = new DatabaseHelper(context);
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE);
        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"'";
        user = db.user_data(query);
        userNeighborhood = new UserNeighborhood();
        join = (Button)view.findViewById(R.id.join);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData("states",0);
            }
        });
        return view;
    }

    public void getData(final String data,final int id){
        ServerRequests serverRequests = new ServerRequests();
        final ProgressDialog pDialog = new ProgressDialog(context);
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
                        userNeighborhood.setState_id(allTypes.id);
                        userNeighborhood.setState_name(allTypes.name);
                        getData("lgas",allTypes.id);
                        break;
                    case "lgas":
                        userNeighborhood.setLga_id(allTypes.id);
                        userNeighborhood.setLga_name(allTypes.name);
                        getData("districts",allTypes.id);
                        break;
                    case "districts":
                        userNeighborhood.setDistrict_id(allTypes.id);
                        userNeighborhood.setDistrict_name(allTypes.name);
                        getData("streets",allTypes.id);
                        break;
                    case "streets":
                        userNeighborhood.setStreet_id(allTypes.id);
                        userNeighborhood.setStreet_name(allTypes.name);
                        getData("buildings",allTypes.id);
                        break;
                    case "buildings":
                        userNeighborhood.setBuilding_id(allTypes.id);
                        userNeighborhood.setBuilding_name(allTypes.name);
                        getData("apartments",allTypes.id);
                        break;
                    case "apartments":
                        userNeighborhood.setApartment_id(allTypes.id);
                        userNeighborhood.setApartment_name(allTypes.name);
                        getData("tenant_type",allTypes.id);
                        break;
                        default:
                            userNeighborhood.setApartment_type_id(allTypes.id);
                            userNeighborhood.setApartment_type_name(allTypes.name);
                            userNeighborhood.setUser_id(user.user_id);
           //                 join_neighborhood_details_popup(context,userNeighborhood);
                            choose_user_type_popup(context,userNeighborhood);
                            break;

                }
            }
        });
    }

    public void join_neighborhood_details_popup(final Context context,final UserNeighborhood userNeighborhood){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Confirm Neighborhood");
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "State: "+userNeighborhood.state_name,
                "LGA: "+userNeighborhood.lga_name,
                "District: "+userNeighborhood.district_name,
                "Street: "+userNeighborhood.street_name,
                "Building: "+userNeighborhood.building_name,
                "Apartment: "+userNeighborhood.apartment_name,
                "Apartment type: "+userNeighborhood.apartment_type_name,
                "Type: "+userNeighborhood.user_type
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
                new Post().join_neighborhood(context,user,userNeighborhood);
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

    public void choose_user_type_popup(final Context context,final UserNeighborhood userNeighborhood){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("CHOOSE YOUR TYPE");
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        final String[] menu_name = {
                context.getString(R.string.admin),
                context.getString(R.string.landlord),
                context.getString(R.string.tenant),
                context.getString(R.string.family),
                context.getString(R.string.caretaker)
        };
        int[]  menu_icons = {
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary

        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                userNeighborhood.setUser_type(menu_name[position]);
                join_neighborhood_details_popup(context,userNeighborhood);
            }
        });

    }

}




