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

import Objects.LGAStaff;
import Objects.User;
import adapters.HomeMenuAdapter;
import caller.com.testnav.ContactsActivity;
import caller.com.testnav.ContactsSearchActivity;
import caller.com.testnav.LoginActivity;
import caller.com.testnav.MainActivity;
import caller.com.testnav.R;
import database.DatabaseHelper;
import dialogs.Dialogs;

import static Constants.Constants.MODE;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static database.DatabaseHelper.TABLE_LGA_STAFF;
import static database.DatabaseHelper.TABLE_USERS;
import static helper.Utils.LoadActivityLogFragment;
import static helper.Utils.LoadDistressHistoryFragment;
import static helper.Utils.LoadEyeWitnessFragment;
import static helper.Utils.LoadLGAStaffLoadDistrictsFragment;
import static helper.Utils.LoadListEyeWitnessFragment;

public class LGAAdminHomeFragment extends Fragment implements  GridView.OnItemClickListener {

    List<String> menu_name;
    List<Integer> menu_icon;
    Dialogs dialogs = new Dialogs();
    Context context;
    User user;
    SharedPreferences sharedPreferences;
    DatabaseHelper db;
    LGAStaff lgaStaff;
    public LGAAdminHomeFragment(){

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

        query = "select * from "+TABLE_LGA_STAFF+" where user_id="+user_id+" order by id desc limit 1";
        List<LGAStaff> lgaStaffs = db.lgaStaffList(query);
        if(lgaStaffs != null){
            for(LGAStaff lgaStafff : lgaStaffs){
                lgaStaff = lgaStafff;
            }
        }


        menu_icon = new ArrayList<Integer>();
        menu_name = new ArrayList<String>();

        menu_icon.add(R.drawable.ic_add_location_primary);
        menu_name.add(getString(R.string.menu_districts));

        menu_icon.add(R.drawable.ic_persons_primary);
        menu_name.add(getString(R.string.menu_administrators));

        menu_icon.add(R.drawable.distress1);
        menu_name.add(getString(R.string.menu_distress));

        menu_icon.add(R.drawable.eye_witness);
        menu_name.add(getString(R.string.menu_reports));

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

    static final int DISTRICTS=0,ADMINISTRATORS=1,EYE_WITNESS=3,DISTRESS=2;
    private void open_screen(int i){
        switch (i){
            case DISTRICTS:
                if(lgaStaff != null)
                LoadLGAStaffLoadDistrictsFragment(context,lgaStaff);
                break;
            case ADMINISTRATORS:
                if(lgaStaff != null){
                    Intent intent = new Intent(context, ContactsSearchActivity.class);
                    Bundle bundle = new Bundle();
                    //this query is meant for the server
                    String search_query = "select * from user_type where lga_id="+lgaStaff.lga_id+" and user_type='"+context.getString(R.string.admin)+"' order by id desc";
                    bundle.putString("search_query", search_query);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
                break;
                case DISTRESS:
                    LoadDistressHistoryFragment(context);
                    break;
                case EYE_WITNESS:
                    LoadListEyeWitnessFragment(context);
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
