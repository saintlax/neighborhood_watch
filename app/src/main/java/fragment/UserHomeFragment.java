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
import caller.com.testnav.LoginActivity;
import caller.com.testnav.MainActivity;
import caller.com.testnav.R;
import database.DatabaseHelper;
import dialogs.Dialogs;

import static Constants.Constants.MODE;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static database.DatabaseHelper.TABLE_USERS;
import static helper.Utils.LoadEyeWitnessFragment;
import static helper.Utils.LoadActivityLogFragment;
import static helper.Utils.LoadHotlinesFragment;

public class UserHomeFragment extends Fragment implements  GridView.OnItemClickListener {

    List<String> menu_name;
    List<Integer> menu_icon;
    Dialogs dialogs = new Dialogs();
    Context context;
    User user;
    SharedPreferences sharedPreferences;
    DatabaseHelper db;
    public UserHomeFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
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
        menu_icon = new ArrayList<Integer>();
        menu_name = new ArrayList<String>();


        menu_icon.add(R.drawable.levies_icon);
        menu_name.add(getString(R.string.menu_levies));

        menu_icon.add(R.drawable.dues_icon);
        menu_name.add(getString(R.string.menu_dues));

        menu_icon.add(R.drawable.distress1);
        menu_name.add(getString(R.string.menu_distress));

        menu_icon.add(R.drawable.chat);
        menu_name.add(getString(R.string.menu_chat));

        menu_icon.add(R.drawable.eye_witness);
        menu_name.add(getString(R.string.menu_eye_witness));


        menu_icon.add(R.drawable.ic_history_black);
        menu_name.add(getString(R.string.menu_history));

        menu_icon.add(R.drawable.hotlines);
        menu_name.add(getString(R.string.menu_hotlines));

        menu_icon.add(R.drawable.police1);
        menu_name.add(getString(R.string.menu_agency));



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

    static final int LEVIES=0,DUES=1,DISTRESS=2,CHAT=3,EYE_WITNESS=4,HISTORY=5,HOTLINES=6;
    private void open_screen(int i){
        switch (i){
            case LEVIES:
                dialogs.user_levies_popup(context,user);
                break;
            case DUES:
                dialogs.user_dues_popup(context,user);
                break;
            case DISTRESS:
                dialogs.confirm_distress_popup(context);
                break;
            case CHAT:
                context.startActivity(new Intent(context, ContactsActivity.class));
                break;
            case EYE_WITNESS:
                LoadEyeWitnessFragment(context);
                break;
            case HISTORY:
                LoadActivityLogFragment(context);
                break;
            case HOTLINES:
                LoadHotlinesFragment(context);
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
