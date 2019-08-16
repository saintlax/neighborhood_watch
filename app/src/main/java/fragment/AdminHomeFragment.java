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
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import Objects.User;
import Objects.UserType;
import adapters.HomeMenuAdapter;
import caller.com.testnav.ContactsActivity;
import caller.com.testnav.OccupantsActivity;
import caller.com.testnav.R;
import database.DatabaseHelper;
import dialogs.Dialogs;

import static Constants.Constants.MODE;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static database.DatabaseHelper.TABLE_USERS;
import static database.DatabaseHelper.TABLE_USER_TYPE;
import static helper.Utils.LoadActivityLogFragment;
import static helper.Utils.LoadDistressHistoryFragment;
import static helper.Utils.LoadHotlinesFragment;

public class AdminHomeFragment extends Fragment implements  GridView.OnItemClickListener {

    List<String> menu_name;
    List<Integer> menu_icon;
    Dialogs dialogs = new Dialogs();
    Context context;
    User user;
    DatabaseHelper db;
    SharedPreferences sharedPreferences;
    UserType userType;
    public AdminHomeFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        db = new DatabaseHelper(context);
        sharedPreferences = getActivity().getSharedPreferences(SP_NAME, MODE);
        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"'";
        user = db.user_data(query);

        query = "select * from "+TABLE_USER_TYPE+" where user_id='"+user_id+"' order by id desc";
        userType = db.user_type(query);


        menu_icon = new ArrayList<Integer>();
        menu_name = new ArrayList<String>();


        menu_icon.add(R.drawable.street);
        menu_name.add(getString(R.string.menu_streets));


        menu_icon.add(R.drawable.users);
        menu_name.add(getString(R.string.menu_occupants));

        menu_icon.add(R.drawable.levies_icon);
        menu_name.add(getString(R.string.menu_levies));

        menu_icon.add(R.drawable.dues_icon);
        menu_name.add(getString(R.string.menu_dues));


        menu_icon.add(R.drawable.broadcaster);
        menu_name.add(getString(R.string.menu_broadcast));


        menu_icon.add(R.drawable.chat);
        menu_name.add(getString(R.string.menu_chat));


        menu_icon.add(R.drawable.summary);
        menu_name.add(getString(R.string.menu_payment_summary));


        menu_icon.add(R.drawable.distress1);
        menu_name.add(getString(R.string.menu_distress));


        menu_icon.add(R.drawable.hotlines);
        menu_name.add(getString(R.string.menu_hotlines));


        menu_icon.add(R.drawable.ic_history_black);
        menu_name.add(getString(R.string.menu_history));


        GridView gridview = (GridView)view.findViewById(R.id.gridView);
        HomeMenuAdapter adapter = new HomeMenuAdapter(getActivity(),menu_name,menu_icon);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
           open_screen(i);
    }

    static final int STREET=0,OCCUPANTS=1,LEVIES=2,DUES=3,DISTRESS=7,HOTLINES=8,HISTORY=9,BROADCAST=4,PAYMENT_SUMMARY=6,CHAT=5;
    private void open_screen(int i){
        switch (i){
            case STREET:
                dialogs.streets_popup(context,user);
                break;
            case OCCUPANTS:
                context.startActivity(new Intent(context, OccupantsActivity.class));
                break;
            case LEVIES:
                dialogs.levies_popup(context,user);
                break;
            case DUES:
                dialogs.dues_popup(context,user);
                break;
            case BROADCAST:
                if(userType.user_type.equalsIgnoreCase(getString(R.string.admin))){
                    dialogs.admin_broadcast_popup(context,user);
                }else{
                    dialogs.admin_settings_options_popup(context, user);
                }
                break;
            case PAYMENT_SUMMARY:
                dialogs.payment_summary_popup(context);
                break;
            case CHAT:
                context.startActivity(new Intent(context, ContactsActivity.class));
                break;
            case DISTRESS:
                LoadDistressHistoryFragment(context);
                break;
            case HOTLINES:
                LoadHotlinesFragment(context);
                break;
            case HISTORY:
                LoadActivityLogFragment(context);
                break;

        }
    }


}
