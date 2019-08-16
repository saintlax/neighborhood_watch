package fragment;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import Objects.User;
import Objects.UserType;
import adapters.CustomPopupAdapter;
import caller.com.testnav.R;
import database.DatabaseHelper;
import dialogs.Post;
import helper.Utils;

import static Constants.Constants.PENDING;
import static database.DatabaseHelper.TABLE_USERS;
import static helper.Utils.getTimestamp;

public class AddUserFragment extends Fragment {
    private LinearLayout mDotsLayout;
    Button continue_button;
    private TextView[] mDots;
    EditText mLastname,mFirstname,mPhone,mOther_names;
    String user_title,radioValue;
    Button mChoose_title;
    Context context;
    String apartment_id,apartment_name;
    TextView mHead_title;
    RadioGroup radioGroup;
    public AddUserFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.add_user_register, container, false);
        context = getActivity();
        apartment_id = getArguments().getString("apartment_id");
        apartment_name = getArguments().getString("apartment_name");
        mHead_title = (TextView)view.findViewById(R.id.head_title);
        mHead_title.setText("Add an Occupant to "+apartment_name);
        user_title = "Mr.";
        mChoose_title = (Button)view.findViewById(R.id.choose_title);
        mChoose_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose_title_popup();
            }
        });

        mLastname = (EditText)view.findViewById(R.id.last_name);
        mPhone = (EditText)view.findViewById(R.id.phone);
        mFirstname = (EditText)view.findViewById(R.id.first_name);
        mOther_names = (EditText)view.findViewById(R.id.other_names);
        mDotsLayout = (LinearLayout)view.findViewById(R.id.dots_layout);
        continue_button = (Button)view.findViewById(R.id.continue_button);
        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proceed();
            }
        });

        radioValue = getString(R.string.landlord);
        radioGroup = (RadioGroup)view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id=group.getCheckedRadioButtonId();
                RadioButton rb=(RadioButton)view. findViewById(id);
                radioValue = rb.getText().toString();
            }
        });
        return view;
    }



    private void proceed(){
        //   getSupportActionBar().setTitle("Sign In");
        String first_name = mFirstname.getText().toString();
        String last_name = mLastname.getText().toString();
        String phone = mPhone.getText().toString();
        String other_name = mOther_names.getText().toString();
        if(TextUtils.isEmpty(first_name)){
            Utils.popup(getActivity(),"Error","Your first name is required");
            return;
        }

        if(TextUtils.isEmpty(last_name)){
            Utils.popup(getActivity(),"Error","Your Last name is required");
            return;
        }

        if(TextUtils.isEmpty(phone)){
            Utils.popup(getActivity(),"Error","Your phone number is required");
            return;
        }

        User user = new User();
        user.setTitle(user_title);
        user.setOther_names(other_name);
        user.setFirst_name(first_name);
        user.setLast_name(last_name);
        user.setPhone(phone);
        user.setAccess(PENDING+"");
        user.setStatus(PENDING+"");
        user.setDate(getTimestamp());

        UserType userType = new UserType();
        userType.setApartment_id(apartment_id);
        userType.setApartment_name(apartment_name);
        userType.setUser_type(radioValue);
        new Post().admin_add_user(context,user,userType);
    }



    AlertDialog.Builder alertDialog;
    public void choose_title_popup(){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Choose your title");
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        final String[] menu_name = {
                "Mr.",
                "Mrs",
                "Dr.",
                "Engr.",
                "Prof.",
                "Rev.",
                "Rev. Fr.",
                "Deacon",
                "Deaconess",
                "Prophet",
        };
        int[]  menu_icons = {
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                mChoose_title.setText(menu_name[position]);
                user_title = menu_name[position];
            }
        });
    }



}
