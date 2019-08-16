package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import adapters.HomeMenuAdapter;
import caller.com.testnav.MainActivity;
import caller.com.testnav.R;

public class HomeFragment extends Fragment implements  GridView.OnItemClickListener {

    List<String> menu_name;
    List<Integer> menu_icon;
    public HomeFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        menu_icon = new ArrayList<Integer>();
        menu_name = new ArrayList<String>();


        menu_icon.add(R.drawable.one1);
        menu_name.add("Closest Restaurants");


        menu_icon.add(R.drawable.one2);
        menu_name.add("Favorite");

        menu_icon.add(R.drawable.one3);
        menu_name.add("Five Star");

        menu_icon.add(R.drawable.one4);
        menu_name.add("Local Cafeteria");

        menu_icon.add(R.drawable.one5);
        menu_name.add("Top Ten Restaurants");

        menu_icon.add(R.drawable.one6);
        menu_name.add("Small Chops");

        menu_icon.add(R.drawable.one7);
        menu_name.add("Continental Dishes");

        menu_icon.add(R.drawable.one8);
        menu_name.add("Pasta and Ice");


        GridView gridview = (GridView)view.findViewById(R.id.gridView);
        HomeMenuAdapter adapter = new HomeMenuAdapter(getActivity(),menu_name,menu_icon);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(this);


        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        load_menu_fragment();
        //   open_screen(i);
    }

    private void load_menu_fragment(){
        ((MainActivity)getActivity() ).getSupportActionBar().setTitle("Menu");
        MenuFragment fragment = new MenuFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

}
