package dialogs;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import Constants.Constants;
import Objects.Due;
import Objects.EyeWitness;
import Objects.LGAStaff;
import Objects.Levy;
import Objects.Message;
import Objects.MyBroadcast;
import Objects.Ratings;
import Objects.User;
import Objects.UserType;
import adapters.CustomPopupAdapter;
import caller.com.testnav.ChatActivity;
import caller.com.testnav.ContactsSearchActivity;
import caller.com.testnav.DuePaymentActivity;
import caller.com.testnav.LevyPaymentActivity;
import caller.com.testnav.LocationActivity;
import caller.com.testnav.LoginActivity;
import caller.com.testnav.MapsActivity;
import caller.com.testnav.PaymentSummaryActivity;
import caller.com.testnav.R;
import caller.com.testnav.ReadEyeWitnessActivity;
import caller.com.testnav.UserLeviesHistoryActivity;
import caller.com.testnav.UserUnpaidLeviesHistoryActivity;
import database.DatabaseHelper;
import helper.Utils;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import services.BootReciever;
import services.LocationBootReciever;
import services.LocationService;
import services.SecurityService;

import static Constants.Constants.INVISIBLE;
import static Constants.Constants.SP_ACTIVE_EMERGENCY;
import static database.DatabaseHelper.TABLE_RATINGS;
import static helper.Utils.LGAStaffListStreetsFragment;
import static helper.Utils.LoadAddUserToApartmentFragment;
import static helper.Utils.LoadApartmentFragment;
import static helper.Utils.LoadBuildingsFragment;
import static helper.Utils.LoadDistrictsFragment;
import static helper.Utils.LoadDuesDebtorsFragment;
import static helper.Utils.LoadDuesFragment;
import static helper.Utils.LoadLGAFragment;
import static helper.Utils.LoadLeviesDebtorsFragment;
import static helper.Utils.LoadLeviesFragment;
import static helper.Utils.LoadStep4Fragment;
import static helper.Utils.LoadStep5Fragment;
import static helper.Utils.LoadStreetsFragment;
import static helper.Utils.decodeSampledBitmapFromUri;
import static helper.Utils.saveEyeWitness;


public class Dialogs {

    AlertDialog.Builder alertDialog;
    ProgressDialog pDialog;
    Post posts;
    public void add_street_popup(final Context context, final User user){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_add_street, null);
        final EditText street = (EditText)view.findViewById(R.id.street_name);
        alertDialog.setTitle("Add a Street/Locality/Place");
        alertDialog.setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(TextUtils.isEmpty(street.getText().toString())){
                            Utils.popup(context,"Error","A street name is required");
                            return;
                        }
                        posts = new Post();
                        posts.add_street(context,user,street.getText().toString());
                    }
                });
        alertDialog.setView(view);
        alertDialog.show();
    }
    public void streets_popup(final Context context, final User user){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Streets/Localities/Places");
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "Add a Street/Locality/Place",
                "View Streets/Localities/Places"
        };
        int[]  menu_icons = {
                R.drawable.ic_add_location_primary,
                R.drawable.ic_search_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                switch (position){
                    case 0:
                        add_street_popup(context,user);
                        break;
                    case 1:
                        LoadStreetsFragment(context);
                        break;
                }
            }
        });
    }
    public void street_options_popup(final Context context, final User user,final View listView,final JSONObject streetObject){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        try{
            alertDialog.setTitle(streetObject.getString("name"));
        }catch (Exception e){
            e.printStackTrace();
        }
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "Edit Name",
                "Delete Street",
                "Add a Building",
                "View Buildings",
                "Occupants"
        };
        int[]  menu_icons = {
                R.drawable.ic_edit_location_primary,
                R.drawable.ic_delete_primary,
                R.drawable.ic_add_circle_circle_primary,
                R.drawable.ic_search_primary,
                R.drawable.ic_persons_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                switch (position){
                    case 0:
                        edit_street_popup(context,user,listView,streetObject);
                        break;
                    case 1:
                        delete_street_popup(context,user,listView,streetObject);
                        break;
                        case 2:
                            add_building_popup(context,user,listView,streetObject);
                            break;
                    case 3:
                        LoadBuildingsFragment(context,streetObject);
                        break;
                    case 4:
                        try {
                            Intent intent = new Intent(context, ContactsSearchActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("street_id", streetObject.getString("id"));
                            bundle.putString("street_name", streetObject.getString("name"));
                            bundle.putString("district_id", streetObject.getString("district_id"));
                            bundle.putString("lga_id", streetObject.getString("lga_id"));
                            bundle.putString("state_id", streetObject.getString("state_id"));
                            //this query is meant for the server
                            String search_query = "select * from user_type where street_id="+streetObject.getString("id")+" order by id desc";
                            bundle.putString("search_query", search_query);

                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
    }
    public void add_building_popup(final Context context, final User user,final View listView,final JSONObject streetObject){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_add_building, null);
        final EditText name = (EditText)view.findViewById(R.id.name);
        final EditText landlord = (EditText)view.findViewById(R.id.landlord);
        try {
            alertDialog.setTitle("Add Building");
            name.setHint("Name or House number");
        }catch (Exception e){
            e.printStackTrace();
        }

        alertDialog.setView(view)
                .setPositiveButton("Add Building", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(TextUtils.isEmpty(name.getText().toString())){
                            Utils.popup(context,"Error","A street name is required");
                            return;
                        }
                        if(TextUtils.isEmpty(landlord.getText().toString())){
                            Utils.popup(context,"Error","Name of Owner or caretaker is required");
                            return;
                        }
                        posts = new Post();
                        posts.add_building(context,user,listView,streetObject,name.getText().toString(),landlord.getText().toString());
                    }
                });
        alertDialog.setView(view);
        alertDialog.show();
    }

    public void edit_street_popup(final Context context, final User user,final View listView,final JSONObject streetObject){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_add_street, null);
        final EditText street = (EditText)view.findViewById(R.id.street_name);
        try {
            street.setText(streetObject.getString("name"));
        }catch (Exception e){
            e.printStackTrace();
        }

        alertDialog.setTitle("Edit Street");
        alertDialog.setView(view)
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(TextUtils.isEmpty(street.getText().toString())){
                            Utils.popup(context,"Error","A street name is required");
                            return;
                        }
                        posts = new Post();
                        posts.edit_street(context,user,listView,streetObject,street.getText().toString());
                    }
                });
        alertDialog.setView(view);
        alertDialog.show();
    }
    public void delete_street_popup(final Context context,final User user,final View listView,final JSONObject streetObject){
        alertDialog = new AlertDialog.Builder(context);
        try{
            alertDialog.setTitle("Delete Street");
            alertDialog.setMessage("Do you want to delete "+streetObject.getString("name")+"?");
        }catch (Exception e){
            e.printStackTrace();
        }
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            //do nothing
            }
            });
        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        posts = new Post();
                        posts.delete_street(context,user,listView,streetObject);
                    }
                });
        alertDialog.show();
    }
    public void levies_popup(final Context context, final User user){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Levies");
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "Create a Levy",
                "View Levies"
        };
        int[]  menu_icons = {
                R.drawable.ic_dollar_circle_primary,
                R.drawable.ic_search_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                switch (position){
                    case 0:
                        add_levy_popup(context,user);
                        break;
                    case 1:
                        LoadLeviesFragment(context);
                        break;
                }
            }
        });
    }
    public void add_levy_popup(final Context context, final User user){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_add_levy, null);
        final EditText name = (EditText)view.findViewById(R.id.name);
        final EditText amount = (EditText)view.findViewById(R.id.amount);
        final EditText date = (EditText)view.findViewById(R.id.date);
        alertDialog.setTitle("Create a Levy");
        alertDialog.setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(TextUtils.isEmpty(name.getText().toString())){
                            Utils.popup(context,"Error","A name is required");
                            return;
                        }
                        if(TextUtils.isEmpty(amount.getText().toString())){
                            Utils.popup(context,"Error","An amount is required");
                            return;
                        }
                        Levy levy = new Levy();
                        levy.setAmount(amount.getText().toString());
                        levy.setName(name.getText().toString());
                        levy.setDate(date.getText().toString());
                        posts = new Post();
                        posts.add_levy(context,user,levy);
                    }
                });
        alertDialog.setView(view);
        alertDialog.show();
    }

    public void levy_options_popup(final Context context, final User user,final View listView,final JSONObject levyObject){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        try{
            alertDialog.setTitle(levyObject.getString("name"));
        }catch (Exception e){
            e.printStackTrace();
        }
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "Edit Name",
                "Delete Levy",
                "Paid Members",
                "Make Payment",
                "Summary"
        };
        int[]  menu_icons = {
                R.drawable.ic_edit_location_primary,
                R.drawable.ic_delete_primary,
                R.drawable.ic_user_group_primary,
                R.drawable.ic_search_primary,
                R.drawable.ic_assignment_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                switch (position){
                    case 0:
                        edit_levy_popup(context,user,listView,levyObject);
                        break;
                    case 1:
                        delete_levy_popup(context,user,listView,levyObject);
                        break;
                    case 2:
                        LoadLeviesDebtorsFragment(context,levyObject);
                        break;
                    case 3:
                        try{
                            Intent intent = new Intent(context, LevyPaymentActivity.class);;
                            Bundle bundle = new Bundle();
                            bundle.putString("levy_id", levyObject.getString("id"));
                            bundle.putString("amount", levyObject.getString("amount"));
                            bundle.putString("levy_name", levyObject.getString("name"));
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    case 4:
                        new Post().post_levy_summary(context,user,listView,levyObject);
                        break;
                }
            }
        });
    }
    public void edit_levy_popup(final Context context, final User user,final View listView,final JSONObject levyObject){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_add_levy, null);
        final EditText name = (EditText)view.findViewById(R.id.name);
        final EditText amount = (EditText)view.findViewById(R.id.amount);
        final EditText date = (EditText)view.findViewById(R.id.date);
        try {
            name.setText(levyObject.getString("name"));
            amount.setText(levyObject.getString("amount"));
            date.setText(levyObject.getString("deadline_date"));
        }catch (Exception e){
            e.printStackTrace();
        }

        alertDialog.setTitle("Edit Levy");
        alertDialog.setView(view)
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(TextUtils.isEmpty(name.getText().toString())){
                            Utils.popup(context,"Error","A street name is required");
                            return;
                        }
                        if(TextUtils.isEmpty(amount.getText().toString())){
                            Utils.popup(context,"Error","An amount is required");
                            return;
                        }
                        Levy levy = new Levy();
                        levy.setAmount(amount.getText().toString());
                        levy.setName(name.getText().toString());
                        levy.setDate(date.getText().toString());
                        posts = new Post();
                        posts.edit_levy(context,user,listView,levy,levyObject);
                    }
                });
        alertDialog.setView(view);
        alertDialog.show();
    }

    public void delete_levy_popup(final Context context,final User user,final View listView,final JSONObject levyObject){
        alertDialog = new AlertDialog.Builder(context);
        try{
            alertDialog.setTitle("Delete Levy");
            alertDialog.setMessage("Do you want to delete "+levyObject.getString("name")+"?");
        }catch (Exception e){
            e.printStackTrace();
        }
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });
        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                posts = new Post();
                posts.delete_levy(context,user,listView,levyObject);
            }
        });
        alertDialog.show();
    }


    public void admin_settings_options_popup(final Context context, final User user){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Streets");
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "Add Tenant Type",
                "Send Reminders"
        };
        int[]  menu_icons = {
                R.drawable.ic_home_primary,
                R.drawable.ic_phonelink_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                switch (position){
                    case 0:
                        add_tenant_popup(context,user);
                        break;
                    case 1://here
                        admin_broadcast_popup(context,user);
                        break;

                }
            }
        });
    }
    public void add_tenant_popup(final Context context, final User user){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_add_street, null);
        final EditText street = (EditText)view.findViewById(R.id.street_name);
        alertDialog.setTitle("Add TYpe Eg, Stores");
        alertDialog.setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(TextUtils.isEmpty(street.getText().toString())){
                            Utils.popup(context,"Error","A street name is required");
                            return;
                        }
                        posts = new Post();
                        posts.add_tenant_type(context,user,street.getText().toString());
                    }
                });
        alertDialog.setView(view);
        alertDialog.show();
    }

    public void building_options_popup(final Context context, final User user,final View listView,final JSONObject buildingObject){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        try{
            alertDialog.setTitle(buildingObject.getString("name"));
        }catch (Exception e){
            e.printStackTrace();
        }
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "Edit Name",
                "Delete Building",
                "Add an Apartment",
                "View Apartments"
        };
        int[]  menu_icons = {
                R.drawable.ic_edit_primary,
                R.drawable.ic_delete_primary,
                R.drawable.ic_add_circle_circle_primary,
                R.drawable.ic_search_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                switch (position){
                    case 0:
                        edit_building_popup(context,user,listView,buildingObject);
                        break;
                    case 1:
                        delete_building_popup(context,user,listView,buildingObject);
                        break;
                    case 2:
                        add_apartment_popup(context,user,listView,buildingObject);
                        break;
                    case 3:
                        LoadApartmentFragment(context,buildingObject);
                        break;
                }
            }
        });
    }
    public void edit_building_popup(final Context context, final User user,final View listView,final JSONObject buildingObject){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_add_building, null);
        final EditText name = (EditText)view.findViewById(R.id.name);
        final EditText landlord = (EditText)view.findViewById(R.id.landlord);
        try {
            name.setText(buildingObject.getString("name"));
            landlord.setText(buildingObject.getString("owner"));
        }catch (Exception e){
            e.printStackTrace();
        }

        alertDialog.setTitle("Edit Building");
        alertDialog.setView(view)
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String house = name.getText().toString();
                        String owner = landlord.getText().toString();
                        if(TextUtils.isEmpty(house)){
                            Utils.popup(context,"Error","A name is required");
                            return;
                        }
                        if(TextUtils.isEmpty(owner)){
                            Utils.popup(context,"Error","An owner or caretaker is required");
                            return;
                        }
                        posts = new Post();
                        posts.edit_building(context,user,listView,buildingObject,house,owner);
                    }
                });
        alertDialog.setView(view);
        alertDialog.show();
    }
    public void delete_building_popup(final Context context,final User user,final View listView,final JSONObject buildingObject){
        alertDialog = new AlertDialog.Builder(context);
        try{
            alertDialog.setTitle("Delete Building");
            alertDialog.setMessage("Do you want to delete "+buildingObject.getString("name")+"?");
        }catch (Exception e){
            e.printStackTrace();
        }
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });
        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                posts = new Post();
                posts.delete_building(context,user,listView,buildingObject);
            }
        });
        alertDialog.show();
    }
    public void add_apartment_popup(final Context context, final User user,final View listView,final JSONObject buildingObject){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_add_apartment, null);
        final EditText name = (EditText)view.findViewById(R.id.name);
        final EditText detail = (EditText)view.findViewById(R.id.detail);
        try {
            alertDialog.setTitle("Add Apartment");
            name.setHint("Example Mini Flat in "+buildingObject.getString("name"));
        }catch (Exception e){
            e.printStackTrace();
        }

        alertDialog.setView(view)
                .setPositiveButton("Add Apartment", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(TextUtils.isEmpty(name.getText().toString())){
                            Utils.popup(context,"Error","A name is required");
                            return;
                        }
                        /*if(TextUtils.isEmpty(detail.getText().toString())){
                            Utils.popup(context,"Error","Name of Owner or caretaker is required");
                            return;
                        }*/
                        posts = new Post();
                        posts.add_apartment(context,user,listView,buildingObject,name.getText().toString(),detail.getText().toString());
                    }
                });
        alertDialog.setView(view);
        alertDialog.show();
    }
    public void apartment_options_popup(final Context context, final User user,final View listView,final JSONObject apartmentObject){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        try{
            alertDialog.setTitle(apartmentObject.getString("name"));
        }catch (Exception e){
            e.printStackTrace();
        }
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "Edit Name",
                "Delete Apartment",
                "Contact Occupant"
              //  , "Add an Occupant"
        };
        int[]  menu_icons = {
                R.drawable.ic_edit_primary,
                R.drawable.ic_delete_primary,
                R.drawable.ic_search_primary,
                R.drawable.ic_add_user_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                switch (position){
                    case 0:
                        edit_apartment_popup(context,user,listView,apartmentObject);
                        break;
                    case 1:
                        delete_apartment_popup(context,user,listView,apartmentObject);
                        break;
                    case 2:

                        break;
                    case 3://i removed this so that admin can easily add member from a better interface
                //        LoadAddUserToApartmentFragment(context,apartmentObject);
                        break;
                }
            }
        });
    }
    public void edit_apartment_popup(final Context context, final User user,final View listView,final JSONObject apartmentObject){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_add_apartment, null);
        final EditText name = (EditText)view.findViewById(R.id.name);
        final EditText detail = (EditText)view.findViewById(R.id.detail);
        try {
            name.setText(apartmentObject.getString("name"));
            detail.setText(apartmentObject.getString("detail"));
        }catch (Exception e){
            e.printStackTrace();
        }

        alertDialog.setTitle("Edit Apartment");
        alertDialog.setView(view)
                .setPositiveButton("Edit Apartment", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String house = name.getText().toString();
                        String mdetail = detail.getText().toString();
                        if(TextUtils.isEmpty(house)){
                            Utils.popup(context,"Error","A name is required");
                            return;
                        }
                        if(TextUtils.isEmpty(mdetail)){
                            Utils.popup(context,"Error","A detail is required");
                            return;
                        }
                        posts = new Post();
                        posts.edit_apartment(context,user,listView,apartmentObject,house,mdetail);
                    }
                });
        alertDialog.setView(view);
        alertDialog.show();
    }

    public void delete_apartment_popup(final Context context,final User user,final View listView,final JSONObject apartmentObject){
        alertDialog = new AlertDialog.Builder(context);
        try{
            alertDialog.setTitle("Delete Apartment");
            alertDialog.setMessage("Do you want to delete "+apartmentObject.getString("name")+"?");
        }catch (Exception e){
            e.printStackTrace();
        }
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });
        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                posts = new Post();
                posts.delete_apartment(context,user,listView,apartmentObject);
            }
        });
        alertDialog.show();
    }

    public void choose_street_options_popup(final Context context, final User user,final View listView,final JSONObject streetObject){
        alertDialog = new AlertDialog.Builder(context);
        try{
            alertDialog.setTitle(streetObject.getString("name"));
            alertDialog.setMessage("Do you live on\n"+streetObject.getString("name")+"?");
        }catch (Exception e){
            e.printStackTrace();
        }

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        LoadStep4Fragment(context,user, streetObject);
                    }
                });
        alertDialog.show();

    }

    public void choose_building_options_popup(final Context context, final User user,final View listView,final JSONObject buildingObject){
        alertDialog = new AlertDialog.Builder(context);
        try{
            alertDialog.setTitle(buildingObject.getString("name"));
            alertDialog.setMessage("Do you live in "+buildingObject.getString("name")+"?");
        }catch (Exception e){
            e.printStackTrace();
        }

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                LoadStep5Fragment(context,user, buildingObject);
            }
        });
        alertDialog.show();

    }

    public void user_levies_popup_options(final Context context, final User user, final View listView, final JSONObject obj, final Levy levy){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        try{
            JSONObject userObject = obj.getJSONObject("user");
            alertDialog.setTitle(levy.name);
        }catch (Exception e){
            e.printStackTrace();
        }
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = null;
        int[]  menu_icons = null;
        menu_name = new String[] {
                "Make Payment",
                "View Payment"
        };
        menu_icons = new int[] {
                R.drawable.ic_account_balance_primary,
                R.drawable.ic_search_primary

        };

        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                switch (position){
                    case 0:
                        pay_levy_popup(context,user,listView,obj,levy);
                        break;
                    case 1:
                        new Post().post_get_user_levy(context,user,listView,obj,levy);
                        break;
                }
            }
        });
    }
    public  void pay_levy_popup(final Context context,final User user,final View listView,final JSONObject obj,final Levy levy){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_pay_levy, null);
        final EditText amount = (EditText)view.findViewById(R.id.amount);
        amount.setText(levy.amount);
        amount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                alertDialog.show().dismiss();
                String amt = amount.getText().toString();
                if(TextUtils.isEmpty(amt)){
                    Utils.popup(context,"Error","The Levy amount is required");
                }else{
                    new Post().post_pay_user_levy(context,user,listView,obj,levy,amt);
                }
                return true;
            }
        });
        alertDialog.setTitle("Pay "+levy.name);
        alertDialog.setView(view)
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String amt = amount.getText().toString();
                        if(TextUtils.isEmpty(amt)){
                            Utils.popup(context,"Error","The Levy amount is required");
                        }else{
                            new Post().post_pay_user_levy(context,user,listView,obj,levy,amt);
                        }
                    }
                });
        alertDialog.show();
    }

    public void confirm_keep_tracking_dialog(final Context context, final SharedPreferences sharedPreferences){
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Tracking in Progress");
        alertDialog.setMessage("We are still tracking your phone to ensure that you are safe.\nDo you want to stop this tracking?");
        alertDialog.setPositiveButton("YES, Keep tracking", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences.Editor userLocalDatabaseEditor = sharedPreferences.edit();
                userLocalDatabaseEditor.putString(SP_ACTIVE_EMERGENCY,"");
                userLocalDatabaseEditor.commit();

                Intent tracker = new Intent(context, LocationBootReciever.class);
                boolean tracking = (PendingIntent.getBroadcast(context, 0, tracker, PendingIntent.FLAG_NO_CREATE) != null);
                if(tracking == true){
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,tracker,0);
                    AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    context.stopService(new Intent(context, LocationService.class));
                }
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.show().dismiss();
            }
        });
        alertDialog.show();
    }





    public void logout_dialog(final Context context, final SharedPreferences myPrefs){
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Logout...");
        alertDialog.setMessage("Are you sure you want to Logout this App?");
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // User pressed YES button. Write Logic Here

                SharedPreferences.Editor editor = myPrefs.edit();
                editor.clear();
                editor.commit();
                Toast.makeText(context,context.getString(R.string.main_activity_logout), Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(context, LoginActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);



                Intent alarm = new Intent(context, BootReciever.class);
                boolean alarmRunning = (PendingIntent.getBroadcast(context, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
                if(alarmRunning == true){
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,alarm,0);
                    AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    context.stopService(new Intent(context, SecurityService.class));
                }

                Intent tracker = new Intent(context, LocationBootReciever.class);
                boolean tracking = (PendingIntent.getBroadcast(context, 0, tracker, PendingIntent.FLAG_NO_CREATE) != null);
                if(tracking == true){
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,tracker,0);
                    AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    context.stopService(new Intent(context, LocationService.class));
                }

                context.startActivity(intent1);
            }
        });
         alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.show().dismiss();
            }
        });
        alertDialog.show();
    }
    public void forgot_password_popup(final Context context){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_reset_password, null);
        final EditText phone = (EditText)view.findViewById(R.id.phone);
        alertDialog.setTitle("Request Password");
        alertDialog.setView(view)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(TextUtils.isEmpty(phone.getText().toString())){
                            Utils.popup(context,"Error","Your phone number or email is required");
                            return;
                        }
                        confirm_reset_password(context,phone.getText().toString());
                    }
                });
        alertDialog.setView(view);
        alertDialog.show();
    }
    public void confirm_reset_password(final Context context,final String phone){
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Reset Password");
        alertDialog.setMessage("This will reset your password and send you a new password vis SMS.\nDo you want to continue?");
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                posts = new Post();
                posts.reset_password(context,phone);
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.show().dismiss();
            }
        });
        alertDialog.show();
    }

    public void user_popup(final Context context, final User user,final View listView,final JSONObject userObject){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        try{
            alertDialog.setTitle(userObject.getString("last_name")+" "+userObject.getString("first_name"));
        }catch (Exception e){
            e.printStackTrace();
        }
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "Chat",
                "Levies History",
                "Dues History",
                "Delete Account",
                "Mark All Levies as Paid",
                "Mark All Dues as Paid"
        };
        int[]  menu_icons = {
                R.drawable.ic_message_primary,
                R.drawable.ic_history_primary,
                R.drawable.ic_history_primary,
                R.drawable.ic_delete_primary,
                R.drawable.ic_check_single_primary,
                R.drawable.ic_check_single_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                switch (position){
                    case 0:
                        try {
                            Intent intent = new Intent(context, ChatActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("user_id", userObject.getString("id"));
                            bundle.putString("fullname", userObject.getString("last_name")+" "+userObject.getString("first_name")+" "+userObject.getString("other_names"));
                            bundle.putString("image", userObject.getString("image"));
                            bundle.putString("thumbnail", userObject.getString("thumbnail"));
                            bundle.putString("phone", userObject.getString("phone"));
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                       try {
                            Intent intent = new Intent(context, UserLeviesHistoryActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("user_id", userObject.getString("id"));
                            bundle.putString("first_name", userObject.getString("first_name"));
                            bundle.putString("last_name", userObject.getString("last_name"));
                            bundle.putString("image", userObject.getString("image"));
                            bundle.putString("thumbnail", userObject.getString("thumbnail"));
                            bundle.putString("access_type", "Levies History");
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        try {
                            Intent intent = new Intent(context, UserLeviesHistoryActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("user_id", userObject.getString("id"));
                            bundle.putString("first_name", userObject.getString("first_name"));
                            bundle.putString("last_name", userObject.getString("last_name"));
                            bundle.putString("image", userObject.getString("image"));
                            bundle.putString("thumbnail", userObject.getString("thumbnail"));
                            bundle.putString("access_type", "Dues History");
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        Utils.popup(context,"Permission","You are not allowed to delete another user account");
                        //delete_user_popup(context,user,listView,userObject);
                        break;
                    case 4:
                        confirm_mark_all_levies(context,user,listView,userObject);
                        break;
                    case 5:
                        confirm_mark_all_dues(context,user,listView,userObject);
                        break;
                }
            }
        });
    }
    public void confirm_mark_all_levies(final Context context,final User user,final View listView,final JSONObject userObject){
        alertDialog = new AlertDialog.Builder(context);
        try{
            alertDialog.setTitle("Pay All Levies");
            alertDialog.setMessage("Has "+userObject.getString("title")+" "+userObject.getString("last_name")+" "+userObject.getString("first_name")+"\npaid all the levies in your neighborhood?");
        }catch (Exception e){
            e.printStackTrace();
        }
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });
        alertDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                posts = new Post();
                posts.mark_all_levies_as_paid(context,user,listView,userObject);
            }
        });
        alertDialog.show();
    }

    public void confirm_mark_all_dues(final Context context,final User user,final View listView,final JSONObject userObject){
        alertDialog = new AlertDialog.Builder(context);
        try{
            alertDialog.setTitle("Pay All Dues");
            alertDialog.setMessage("Has "+userObject.getString("title")+" "+userObject.getString("last_name")+" "+userObject.getString("first_name")+"\npaid all the dues in your neighborhood?");
        }catch (Exception e){
            e.printStackTrace();
        }
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });
        alertDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                posts = new Post();
                posts.mark_all_dues_as_paid(context,user,listView,userObject);
            }
        });
        alertDialog.show();
    }
    public void delete_user_popup(final Context context,final User user,final View listView,final JSONObject userObject){
        alertDialog = new AlertDialog.Builder(context);
        try{
            alertDialog.setTitle("Delete User Account");
            alertDialog.setMessage("Do you want to delete\n"+userObject.getString("title")+" "+userObject.getString("last_name")+" "+userObject.getString("first_name")+"'s Account?");
        }catch (Exception e){
            e.printStackTrace();
        }
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });
        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                posts = new Post();
                posts.delete_user_account(context,user,listView,userObject);
            }
        });
        alertDialog.show();
    }


    public void chatImage(final Context context,final Message message){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_chat_image, null);
        ImageView mImage = (ImageView)view.findViewById(R.id.message);
        String image_path = message.message;
        File file = new File(image_path);
        if(file.exists()){
            Bitmap photo = decodeSampledBitmapFromUri(image_path,400,400); // make image clear
            mImage.setImageBitmap(photo);
        }
        //    alertDialog.setTitle("Media");
        /*alertDialog.setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });*/
        alertDialog.setView(view);
        alertDialog.show();
    }



    public void payment_summary_popup(final Context context){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Summary");
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "Levies Summary",
                "Dues Summary"
        };
        int[]  menu_icons = {
                R.drawable.ic_edit_location_primary,
                R.drawable.ic_delete_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                Intent intent = new Intent(context, PaymentSummaryActivity.class);;
                Bundle bundle = new Bundle();
                switch (position){
                    case 0:
                        bundle.putString("payment_type", "Levies");
                        break;
                    case 1:
                        bundle.putString("payment_type", "Dues");
                        break;
                }
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }



    public void dues_popup(final Context context, final User user){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Dues");
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "Create a Due",
                "View Dues"
        };
        int[]  menu_icons = {
                R.drawable.ic_dollar_circle_primary,
                R.drawable.ic_search_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                switch (position){
                    case 0:
                        add_due_popup(context,user);
                        break;
                    case 1:
                        LoadDuesFragment(context);
                        break;
                }
            }
        });
    }

    public void add_due_popup(final Context context, final User user){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_add_levy, null);
        final EditText name = (EditText)view.findViewById(R.id.name);
        final EditText amount = (EditText)view.findViewById(R.id.amount);
        final EditText date = (EditText)view.findViewById(R.id.date);
        alertDialog.setTitle("Create a Due");
        alertDialog.setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(TextUtils.isEmpty(name.getText().toString())){
                            Utils.popup(context,"Error","A name is required");
                            return;
                        }
                        if(TextUtils.isEmpty(amount.getText().toString())){
                            Utils.popup(context,"Error","An amount is required");
                            return;
                        }
                        Due due = new Due();
                        due.setAmount(amount.getText().toString());
                        due.setName(name.getText().toString());
                        due.setDate(date.getText().toString());
                        posts = new Post();
                        posts.add_due(context,user,due);
                    }
                });
        alertDialog.setView(view);
        alertDialog.show();
    }

    public void dues_options_popup(final Context context, final User user,final View listView,final JSONObject dueObject){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        try{
            alertDialog.setTitle(dueObject.getString("name"));
        }catch (Exception e){
            e.printStackTrace();
        }
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "Edit Name",
                "Delete Due",
                "Paid Members",
                "Make Payment",
                "Summary"
        };
        int[]  menu_icons = {
                R.drawable.ic_edit_location_primary,
                R.drawable.ic_delete_primary,
                R.drawable.ic_user_group_primary,
                R.drawable.ic_search_primary,
                R.drawable.ic_assignment_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                switch (position){
                    case 0:
                        edit_due_popup(context,user,listView,dueObject);
                        break;
                    case 1:
                        delete_due_popup(context,user,listView,dueObject);
                        break;
                    case 2:
                        LoadDuesDebtorsFragment(context,dueObject);
                        break;
                    case 3:
                        try{
                            Intent intent = new Intent(context, DuePaymentActivity.class);;
                            Bundle bundle = new Bundle();
                            bundle.putString("due_id", dueObject.getString("id"));
                            bundle.putString("amount", dueObject.getString("amount"));
                            bundle.putString("due_name", dueObject.getString("name"));
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    case 4:
                        new Post().post_due_summary(context,user,listView,dueObject);
                        break;
                }
            }
        });
    }

    public void edit_due_popup(final Context context, final User user,final View listView,final JSONObject dueObject){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_add_levy, null);
        final EditText name = (EditText)view.findViewById(R.id.name);
        final EditText amount = (EditText)view.findViewById(R.id.amount);
        final EditText date = (EditText)view.findViewById(R.id.date);
        try {
            name.setText(dueObject.getString("name"));
            amount.setText(dueObject.getString("amount"));
            date.setText(dueObject.getString("deadline_date"));
        }catch (Exception e){
            e.printStackTrace();
        }

        alertDialog.setTitle("Edit Due");
        alertDialog.setView(view)
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(TextUtils.isEmpty(name.getText().toString())){
                            Utils.popup(context,"Error","A name is required");
                            return;
                        }
                        if(TextUtils.isEmpty(amount.getText().toString())){
                            Utils.popup(context,"Error","An amount is required");
                            return;
                        }
                        Due due = new Due();
                        due.setAmount(amount.getText().toString());
                        due.setName(name.getText().toString());
                        due.setDate(date.getText().toString());
                        posts = new Post();
                        posts.edit_due(context,user,listView,due,dueObject);
                    }
                });
        alertDialog.setView(view);
        alertDialog.show();
    }

    public void delete_due_popup(final Context context,final User user,final View listView,final JSONObject dueObject){
        alertDialog = new AlertDialog.Builder(context);
        try{
            alertDialog.setTitle("Delete Due?");
            alertDialog.setMessage("Do you want to delete "+dueObject.getString("name")+"?");
        }catch (Exception e){
            e.printStackTrace();
        }
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });
        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                posts = new Post();
                posts.delete_due(context,user,listView,dueObject);
            }
        });
        alertDialog.show();
    }

    public void user_dues_popup_options(final Context context, final User user, final View listView, final JSONObject obj, final Due due){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        try{
            JSONObject userObject = obj.getJSONObject("user");
            alertDialog.setTitle(due.name);
        }catch (Exception e){
            e.printStackTrace();
        }
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = null;
        int[]  menu_icons = null;
        menu_name = new String[] {
                "Make Payment",
                "View Payment"
        };
        menu_icons = new int[] {
                R.drawable.ic_account_balance_primary,
                R.drawable.ic_search_primary

        };

        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                switch (position){
                    case 0:
                        pay_due_popup(context,user,listView,obj,due);
                        break;
                    case 1:
                         new Post().post_get_user_due(context,user,listView,obj,due);
                        break;
                }
            }
        });
    }

    public  void pay_due_popup(final Context context,final User user,final View listView,final JSONObject obj,final Due due){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_pay_levy, null);
        final EditText amount = (EditText)view.findViewById(R.id.amount);
        amount.setText(due.amount);
        amount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                alertDialog.show().dismiss();
                String amt = amount.getText().toString();
                if(TextUtils.isEmpty(amt)){
                    Utils.popup(context,"Error","The Due amount is required");
                }else{
                    new Post().post_pay_user_due(context,user,listView,obj,due,amt);
                }
                return true;
            }
        });
        alertDialog.setTitle("Pay "+due.name);
        alertDialog.setView(view)
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String amt = amount.getText().toString();
                        if(TextUtils.isEmpty(amt)){
                            Utils.popup(context,"Error","The Levy amount is required");
                        }else{
                            new Post().post_pay_user_due(context,user,listView,obj,due,amt);
                        }
                    }
                });
        alertDialog.show();
    }

    public void admin_broadcast_popup(final Context context,final User user){
        final MyBroadcast myBroadcast = new MyBroadcast();
        myBroadcast.setRecipient(context.getString(R.string.landlord));
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_broadcast, null);
        final EditText message = (EditText)view.findViewById(R.id.message);
        message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                String msg = message.getText().toString();
                if(TextUtils.isEmpty(msg)){
                    Utils.popup(context,"Error","A message is required to proceed");
                }else{
                    myBroadcast.setMessage(msg);
                    new Post().post_broadcast(context,user,myBroadcast);
                }
                return true;
            }
        });

        final RadioGroup radioGroup = (RadioGroup)view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id=group.getCheckedRadioButtonId();
                RadioButton rb=(RadioButton)view. findViewById(id);
                myBroadcast.setRecipient(rb.getText().toString());
            }
        });

        alertDialog.setTitle("Broadcast Message");
        alertDialog.setView(view)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String msg = message.getText().toString();
                        if(TextUtils.isEmpty(msg)){
                            Utils.popup(context,"Error","A message is required");
                        }else{
                            myBroadcast.setMessage(msg);
                            new Post().post_broadcast(context,user,myBroadcast);
                        }
                    }
                });
        alertDialog.show();
    }

    public void user_levies_popup(final Context context, final User user){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Levies");
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "Paid",
                "Debts"
        };
        int[]  menu_icons = {
                R.drawable.ic_search_primary,
                R.drawable.ic_search_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                switch (position){
                    case 0:
                        try {
                            Intent intent = new Intent(context, UserLeviesHistoryActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("user_id", user.user_id+"");
                            bundle.putString("first_name", user.first_name);
                            bundle.putString("last_name", user.last_name);
                            bundle.putString("image", user.image);
                            bundle.putString("thumbnail", user.image);
                            bundle.putString("access_type", "Levies History");
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        try {
                            Intent intent = new Intent(context, UserUnpaidLeviesHistoryActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("user_id", user.user_id+"");
                            bundle.putString("first_name", user.first_name);
                            bundle.putString("last_name", user.last_name);
                            bundle.putString("image", user.image);
                            bundle.putString("thumbnail", user.image);
                            bundle.putString("access_type", "Unpaid Levies History");
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
    }

    public void user_dues_popup(final Context context, final User user){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Dues");
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "Paid",
                "Debts"
        };
        int[]  menu_icons = {
                R.drawable.ic_search_primary,
                R.drawable.ic_search_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                switch (position){
                    case 0:
                        try {
                            Intent intent = new Intent(context, UserLeviesHistoryActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("user_id", user.user_id+"");
                            bundle.putString("first_name", user.first_name);
                            bundle.putString("last_name", user.last_name);
                            bundle.putString("image", user.image);
                            bundle.putString("thumbnail", user.image);
                            bundle.putString("access_type", "Dues History");
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        try {
                            Intent intent = new Intent(context, UserUnpaidLeviesHistoryActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("user_id", user.user_id+"");
                            bundle.putString("first_name", user.first_name);
                            bundle.putString("last_name", user.last_name);
                            bundle.putString("image", user.image);
                            bundle.putString("thumbnail", user.image);
                            bundle.putString("access_type", "Unpaid Dues History");
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
    }
    public void choose_state_options_popup(final Context context, final User user,final View listView,final JSONObject streetObject){
        alertDialog = new AlertDialog.Builder(context);
        try{
            alertDialog.setTitle(streetObject.getString("name"));
            alertDialog.setMessage("You currently stay in\n"+streetObject.getString("name")+"?");
        }catch (Exception e){
            e.printStackTrace();
        }

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
              //  LoadStep4Fragment(context,user, streetObject);
                LoadLGAFragment(context,user, streetObject);
            }
        });
        alertDialog.show();

    }

    public void choose_lga_options_popup(final Context context, final User user,final View listView,final JSONObject lgaObject){
        alertDialog = new AlertDialog.Builder(context);
        try{
            alertDialog.setTitle(lgaObject.getString("name"));
            alertDialog.setMessage("Is this your Local Government Area?");
        }catch (Exception e){
            e.printStackTrace();
        }

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                LoadDistrictsFragment(context,user, lgaObject);
            }
        });
        alertDialog.show();

    }
    public void choose_district_options_popup(final Context context, final User user,final View listView,final JSONObject districObject){
        alertDialog = new AlertDialog.Builder(context);
        try{
            alertDialog.setTitle(districObject.getString("name"));
            alertDialog.setMessage("Do you stay on\n"+districObject.getString("name")+"?");
        }catch (Exception e){
            e.printStackTrace();
        }

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                LoadStreetsFragment(context,user, districObject);
            }
        });
        alertDialog.show();

    }
    public void family_options_popup(final Context context, final User user,final View listView,final JSONObject userObject){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        try{
            alertDialog.setTitle(userObject.getString("last_name")+" "+userObject.getString("first_name"));
        }catch (Exception e){
            e.printStackTrace();
        }
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "Chat",
                "Remove from family"
        };
        int[]  menu_icons = {
                R.drawable.ic_message_primary,
                R.drawable.ic_delete_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                switch (position){
                    case 0:
                        try {
                            Intent intent = new Intent(context, ChatActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("user_id", userObject.getString("id"));
                            bundle.putString("fullname", userObject.getString("last_name")+" "+userObject.getString("first_name")+" "+userObject.getString("other_names"));
                            bundle.putString("image", userObject.getString("image"));
                            bundle.putString("thumbnail", userObject.getString("thumbnail"));
                            bundle.putString("phone", userObject.getString("phone"));
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        delete_family_popup(context,user,listView,userObject);
                        break;

                }
            }
        });
    }
    public void delete_family_popup(final Context context,final User user,final View listView,final JSONObject userObject){
        alertDialog = new AlertDialog.Builder(context);
        try{
            alertDialog.setTitle("Delete Family Member?");
            alertDialog.setMessage("Do you want to remove "+userObject.getString("first_name")+" from your family?");
        }catch (Exception e){
            e.printStackTrace();
        }
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });
        alertDialog.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                posts = new Post();
                posts.delete_family_member(context,user,listView,userObject);
            }
        });
        alertDialog.show();
    }


    public void change_user_name_popup(final Context context, final User user, final UserType userType){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_change_name, null);
        final String[] titles = Constants.TITLES;
        int titlePosition = 0;
        for(int i=0;i<titles.length;i++){
            String titlee = titles[i];
            if(titlee.equalsIgnoreCase(user.title)){
                titlePosition = i;
                break;
            }
        }
        Spinner spinnercountry = (Spinner)view.findViewById(R.id.title_spinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, titles); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnercountry.setAdapter(spinnerArrayAdapter);
        spinnercountry.setSelection(titlePosition);

        spinnercountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                user.setTitle(titles[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final EditText first_name = (EditText)view.findViewById(R.id.first_name);
        first_name.setText(user.first_name);
        final EditText last_name = (EditText)view.findViewById(R.id.last_name);
        last_name.setText(user.last_name);
        final EditText other_names = (EditText)view.findViewById(R.id.other_names);
        other_names.setText(user.other_names);
        first_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                String fname = first_name.getText().toString();
                String lname = last_name.getText().toString();
                String oname = other_names.getText().toString();
                if(TextUtils.isEmpty(fname)){
                    Utils.popup(context,"Error","First Name is required");
                    return true;
                }
                if(TextUtils.isEmpty(lname)){
                    Utils.popup(context,"Error","Last Name is required");
                    return true;
                }
                user.setFirst_name(fname);
                user.setLast_name(lname);
                user.setOther_names(oname);
                new Post().edit_user(context,user);

                return true;
            }
        });
        alertDialog.setTitle("Change Name");
        alertDialog.setView(view)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String fname = first_name.getText().toString();
                        String lname = last_name.getText().toString();
                        String oname = other_names.getText().toString();
                        if(TextUtils.isEmpty(fname)){
                            Utils.popup(context,"Error","First Name is required");
                            return;
                        }
                        if(TextUtils.isEmpty(lname)){
                            Utils.popup(context,"Error","Last Name is required");
                            return;
                        }
                        user.setFirst_name(fname);
                        user.setLast_name(lname);
                        user.setOther_names(oname);
                        new Post().edit_user(context,user);

                    }
                });
        alertDialog.show();
    }

    public void distress_history_popup(final Context context, final User user,final View listView,final JSONObject jsonObject){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        try{
            JSONObject userObject = jsonObject.getJSONObject("user");
            alertDialog.setTitle(userObject.getString("last_name")+" "+userObject.getString("first_name"));
        }catch (Exception e){
            e.printStackTrace();
        }
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "View in Map",
                "Delete Distress"
        };
        int[]  menu_icons = {
                R.drawable.ic_edit_location_primary,
                R.drawable.ic_delete_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                switch (position){
                    case 0:
                        context.startActivity(new Intent(context, MapsActivity.class));
                        break;
                    case 1:
                        delete_distress_popup(context,user,listView,jsonObject);
                        break;
                }
            }
        });
    }

    public void delete_distress_popup(final Context context,final User user,final View listView,final JSONObject jsonObject){
        alertDialog = new AlertDialog.Builder(context);
        try{
            alertDialog.setTitle("Delete Distress Call");
            JSONObject userObject = jsonObject.getJSONObject("user");
            alertDialog.setMessage("Do you want to delete \n"+userObject.getString("last_name")+" "+userObject.getString("first_name")+"'s\nDistress call?");

         }catch (Exception e){
            e.printStackTrace();
        }
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });
        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                posts = new Post();
                posts.delete_distress_history(context,user,listView,jsonObject);
            }
        });
        alertDialog.show();
    }

    public void confirm_distress_popup(final Context context){
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Distress");
        alertDialog.setMessage("Do you have an emergency?\nIf yes, Turn ON your internet connection");

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                context.startActivity(new Intent(context, LocationActivity.class));
            }
        });
        alertDialog.show();

    }

    public void comfirm_add_more_street_popup(final Context context,final User user){
        alertDialog = new AlertDialog.Builder(context);
        try{
            alertDialog.setTitle("Successful");
            alertDialog.setMessage("A new street has been added to your district.\nDo you want to add another street?");
        }catch (Exception e){
            e.printStackTrace();
        }
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                add_street_popup(context,user);
            }
        });
        alertDialog.show();
    }

    public void feedback_popup(final Context context,final User user){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_feedback, null);
        final EditText message = (EditText)view.findViewById(R.id.message);
        message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                String msg = message.getText().toString();
                if(TextUtils.isEmpty(msg)){
                    Utils.popup(context,"Error","A message is required to proceed");
                }else{
                    new Post().post_feedback(context,user,msg);
                }
                return true;
            }
        });


        alertDialog.setTitle("Give us a feedback");
        alertDialog.setView(view)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String msg = message.getText().toString();
                        if(TextUtils.isEmpty(msg)){
                            Utils.popup(context,"Error","A message is required");
                        }else{
                            new Post().post_feedback(context,user,msg);
                        }
                    }
                });
        alertDialog.show();
    }
    public void eye_witness_sender_popup(final Context context, final User user, final User sender){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle(sender.last_name+" "+sender.first_name);
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "Witness Detail",
                "Chat with witness"
        };
        int[]  menu_icons = {
                R.drawable.ic_user_primary,
                R.drawable.ic_message_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                switch (position){
                    case 0:
                        new Post().eye_witness_detail(context,user,sender);
                        break;
                    case 1:
                        Intent intent = new Intent(context, ChatActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", sender.user_id+"");
                        bundle.putString("fullname", sender.last_name+" "+sender.first_name+" "+sender.other_names);
                        bundle.putString("image", sender.image);
                        bundle.putString("thumbnail", sender.image);
                        bundle.putString("phone", sender.phone);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                        break;
                }
            }
        });
    }
    public void lga_districts_popup(final Context context, final User user,final LGAStaff lgaStaff,final View listView,final JSONObject districtObject){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        try{
            alertDialog.setTitle(districtObject.getString("name"));
        }catch (Exception e){
            e.printStackTrace();
        }
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "View Streets",
                "View Occupants",
                "View Administrators"
        };
        int[]  menu_icons = {
                R.drawable.ic_search_primary,
                R.drawable.ic_search_primary,
                R.drawable.ic_search_primary,
                R.drawable.ic_search_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                switch (position){
                    case 0:
                        LGAStaffListStreetsFragment(context,districtObject);
                        break;
                    case 1:

                        break;
                }
            }
        });
    }
    public void eye_witness_options_popup(final Context context, final User user,final View listView,final JSONObject object){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        try{
            JSONObject userObject = object.getJSONObject("user");
            alertDialog.setTitle(userObject.getString("last_name")+" "+userObject.getString("first_name"));
        }catch (Exception e){
            e.printStackTrace();
        }
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "View Report",
                "Delete Report",
                "Chat with Reporter"
        };
        int[]  menu_icons = {
                R.drawable.ic_search_primary,
                R.drawable.ic_delete_primary,
                R.drawable.ic_message_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                try{
                    EyeWitness eyeWitness = new EyeWitness();
                    //    JSONObject notification_object = returnedJSON.getJSONObject("notification");
                    JSONObject eye_witness_object = object.getJSONObject("eye_witness");
                    JSONObject user_object = object.getJSONObject("user");
                    eyeWitness.setId(eye_witness_object.getInt("id"));
                    eyeWitness.setEye_witness_id(eye_witness_object.getInt("id"));
                    eyeWitness.setSender_id(object.getInt("sender_id"));
                    eyeWitness.setReceiver_id(object.getInt("receiver_id"));
                    eyeWitness.setMessage(eye_witness_object.getString("message"));
                    eyeWitness.setSend_type(eye_witness_object.getInt("send_type"));
                    eyeWitness.setTimestamp(eye_witness_object.getString("timestamp"));
                    eyeWitness.setDate(eye_witness_object.getString("date"));
                    eyeWitness.setFile_name(eye_witness_object.getString("file_name"));
                    eyeWitness.setFile_type(eye_witness_object.getString("file_type"));
                    eyeWitness.setSender_title(user_object.getString("title"));
                    eyeWitness.setSender_last_name(user_object.getString("last_name"));
                    eyeWitness.setSender_first_name(user_object.getString("first_name"));
                    eyeWitness.setSender_other_names(user_object.getString("other_names"));
                    eyeWitness.setSender_image(user_object.getString("image"));
                    eyeWitness.setSender_phone(user_object.getString("phone"));
                    switch (position){
                        case 0:
                            if(saveEyeWitness(context,eyeWitness)){
                                context.startActivity(new Intent(context, ReadEyeWitnessActivity.class));
                            }else{
                                Utils.popup(context,"Error","Eye witness could not persist");
                            }
                            break;
                        case 1:
                                delete_eye_witness_report_popup(context,user,listView,object,eyeWitness);
                            break;
                        case 2:
                            Intent intent = new Intent(context, ChatActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("user_id", eyeWitness.sender_id+"");
                            bundle.putString("fullname", eyeWitness.sender_first_name+" "+eyeWitness.sender_last_name+" "+eyeWitness.sender_other_names);
                            bundle.putString("image", eyeWitness.sender_image);
                            bundle.putString("thumbnail", eyeWitness.sender_image);
                            bundle.putString("phone", eyeWitness.sender_phone);
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                            break;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }
    public void delete_eye_witness_report_popup(final Context context,final User user,final View listView,final JSONObject reportObject,EyeWitness eyeWitness){
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Delete Report?");
        alertDialog.setMessage("Do you want to delete "+eyeWitness.sender_last_name+"'s report?");

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });
        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                posts = new Post();
                posts.delete_eye_witness_report(context,user,listView,reportObject);
            }
        });
        alertDialog.show();
    }
    public void rate_us_popup(final Context context,final User user){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_rate_us, null);
        alertDialog.setTitle("Rate Us");
        final RatingBar ratingBar = (RatingBar)view.findViewById(R.id.rating);
        String query = "select * from "+TABLE_RATINGS+" where user_id="+user.user_id+" order by id desc limit 1";
        Ratings ratings = new Ratings();
        List<Ratings> ratingsList = new DatabaseHelper(context).ratingsList(query);
        if(ratingsList != null){
            for(Ratings ratings1 : ratingsList){
                ratings = ratings1;
                float curRate = Float.parseFloat(ratings.rating);
                ratingBar.setRating(curRate);
            }
        }
        alertDialog.setView(view)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String rating = ""+ratingBar.getRating();
                        if(rating.equalsIgnoreCase("0.0")){
                            Utils.popup(context,"Ooops!","Hey "+user.first_name+"?!\nDon't we deserve 5 stars?");
                        }else{
                            Ratings ratings = new Ratings();
                            ratings.setUser_id(user.user_id);
                            ratings.setRating(rating);
                            new Post().rate_us(context,user,ratings);
                        }
                    }
                });
        alertDialog.show();
    }

    public void confirm_block_eye_witness_reporter_popup(final Context context,final User user,final EyeWitness eyeWitness){
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Block Reporter");
        if(eyeWitness.send_type == INVISIBLE){
            alertDialog.setMessage("Do you want to Block this reporter from sending eye witness reports?");
        }else{
            alertDialog.setMessage("Do you want to Block "+eyeWitness.sender_last_name+" from sending eye witness reports?");
        }

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });
        alertDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                block_reporter_reason_popup(context,user,eyeWitness);
            }
        });
        alertDialog.show();
    }

    public void block_reporter_reason_popup(final Context context,final User user,final EyeWitness eyeWitness){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_block_reason, null);
        final EditText message = (EditText)view.findViewById(R.id.message);
        message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                String msg = message.getText().toString();
                if(TextUtils.isEmpty(msg)){
                    Utils.popup(context,"Error","A message is required to proceed");
                }else{
                    posts = new Post();
                    posts.block_eye_witness_reporter(context,user,eyeWitness,msg);
                }
                return true;
            }
        });

        alertDialog.setTitle("Block Reason");
        alertDialog.setView(view)
                .setPositiveButton("Block", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String msg = message.getText().toString();
                        if(TextUtils.isEmpty(msg)){
                            Utils.popup(context,"Error","A message is required");
                        }else{
                            posts = new Post();
                            posts.block_eye_witness_reporter(context,user,eyeWitness,msg);
                        }
                    }
                });
        alertDialog.show();
    }


    public void hotline_popup_options(final Context context, final User user, final JSONObject obj){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        try{
            alertDialog.setTitle("Call: "+obj.getString("phone"));
        }catch (Exception e){
            e.printStackTrace();
        }
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = null;
        int[]  menu_icons = null;
        menu_name = new String[] {
                "Show my number",
                "Hide my number"
        };
        menu_icons = new int[] {
                R.drawable.ic_call_black,
                R.drawable.ic_call_red

        };

        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                switch (position){
                    case 0:
                        try{
                            Uri number = Uri.parse("tel:"+obj.getString("phone"));
                            Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                            context.startActivity(callIntent);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        try{
                            Uri number = Uri.parse("tel:#31#"+obj.getString("phone"));
                            Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                            context.startActivity(callIntent);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
    }



}
