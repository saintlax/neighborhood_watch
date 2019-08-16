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
import adapters.HomeMenuAdapter;
import caller.com.testnav.ContactsActivity;
import caller.com.testnav.LocationActivity;
import caller.com.testnav.MainActivity;
import caller.com.testnav.MapsActivity;
import caller.com.testnav.OccupantsActivity;
import caller.com.testnav.R;
import database.DatabaseHelper;
import dialogs.Dialogs;

import static Constants.Constants.MODE;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static database.DatabaseHelper.TABLE_USERS;
import static helper.Utils.LoadFamilyMembersFragment;
import static helper.Utils.LoadProfileFragment;

public class FamilyHomeFragment extends Fragment implements  GridView.OnItemClickListener {

    List<String> menu_name;
    List<Integer> menu_icon;
    Dialogs dialogs = new Dialogs();
    Context context;
    User user;
    DatabaseHelper db;
    SharedPreferences sharedPreferences;
    public FamilyHomeFragment(){

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

        menu_icon = new ArrayList<Integer>();
        menu_name = new ArrayList<String>();


        menu_icon.add(R.drawable.ic_user_primary);
        menu_name.add(getString(R.string.menu_profile));


        menu_icon.add(R.drawable.ic_user_group_primary);
        menu_name.add(getString(R.string.menu_family));


        menu_icon.add(R.drawable.distress1);
        menu_name.add(getString(R.string.menu_distress));


        menu_icon.add(R.drawable.chat);
        menu_name.add(getString(R.string.menu_chat));


        menu_icon.add(R.drawable.ic_settings_black);
        menu_name.add(getString(R.string.menu_settings));




        GridView gridview = (GridView)view.findViewById(R.id.gridView);
        HomeMenuAdapter adapter = new HomeMenuAdapter(getActivity(),menu_name,menu_icon);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(this);
        context = getActivity();
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
           open_screen(i);
    }

    static final int PROFILE=0,MY_FAMILY=1,DISTRESS=2,CHAT=3,SETTINGS=4;
    private void open_screen(int i){
        switch (i){
            case PROFILE:
                LoadProfileFragment(context);
                break;
            case MY_FAMILY:
                LoadFamilyMembersFragment(context);
                break;
            case DISTRESS:
                context.startActivity(new Intent(context, LocationActivity.class));
                break;
            case CHAT:
                context.startActivity(new Intent(context, ContactsActivity.class));
                break;
            case SETTINGS:
                break;

        }
    }


    private void load_menu_fragment(){
        ((MainActivity)getActivity() ).getSupportActionBar().setTitle("Menu");
        MenuFragment fragment = new MenuFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

}
