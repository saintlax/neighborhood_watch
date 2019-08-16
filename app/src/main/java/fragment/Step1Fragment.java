package fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.Toast;

import org.json.JSONObject;

import Constants.Constants;
import Objects.User;
import adapters.CustomPopupAdapter;
import caller.com.testnav.LoginActivity;
import caller.com.testnav.R;
import database.DatabaseHelper;
import helper.Utils;

import static Constants.Constants.LOGGED_IN;
import static Constants.Constants.PENDING;
import static database.DatabaseHelper.TABLE_USERS;
import static helper.Utils.getTimestamp;

public class Step1Fragment extends Fragment {
    private LinearLayout mDotsLayout;
    Button continue_button;
    private TextView[] mDots;
    EditText mLastname,mFirstname,mPhone,mOther_names;
    String radioValue;
    Button mChoose_title;
    Context context;
    User curUser;
    DatabaseHelper db;
    public Step1Fragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.step_1_register, container, false);
        context = getActivity();
        db = new DatabaseHelper(context);
        ((LoginActivity)context ).getSupportActionBar().setTitle("Step 1");
        String query = "select * from "+TABLE_USERS+" order by id desc limit 1";
        curUser = db.user_data(query);
        radioValue = (curUser != null && curUser.title !=null)? curUser.title : "Mr.";
        mChoose_title = (Button)view.findViewById(R.id.choose_title);
        if(curUser != null && curUser.title !=null){
            mChoose_title.setText(curUser.title);
        }
        mChoose_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose_title_popup();
            }
        });


        mLastname = (EditText)view.findViewById(R.id.last_name);
        mLastname.setText((curUser != null && curUser.last_name != null)? curUser.last_name : "");
        mPhone = (EditText)view.findViewById(R.id.phone);
        mPhone.setText((curUser != null && curUser.phone != null)? curUser.phone : "");
        mFirstname = (EditText)view.findViewById(R.id.first_name);
        mFirstname.setText((curUser != null && curUser.first_name != null)? curUser.first_name : "");
        mOther_names = (EditText)view.findViewById(R.id.other_names);
        mOther_names.setText((curUser != null && curUser.other_names != null)? curUser.other_names : "");
        mDotsLayout = (LinearLayout)view.findViewById(R.id.dots_layout);
        continue_button = (Button)view.findViewById(R.id.continue_button);
        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load_next_fragment();
            }
        });
        addDotsIndicator(0);
        return view;
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

        if(mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.white));
        }

    }

    private void load_next_fragment(){
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

        first_name = first_name.substring(0, 1).toUpperCase() + first_name.substring(1);
        last_name = last_name.substring(0, 1).toUpperCase() + last_name.substring(1);
        if(!TextUtils.isEmpty(other_name)){
            other_name = other_name.substring(0, 1).toUpperCase() + other_name.substring(1);
        }

        if(phone.length() < 12){
            Utils.popup(getActivity(),"Phone Number Error","A valid phone number is required");
            return;
        }


        /*if(phone.substring(0, 4).equalsIgnoreCase("234") || phone.substring(0, 4).equalsIgnoreCase("+234")){
            ;
        }*/
        if(phone.startsWith("+234")){
            phone = phone.replace("+234","0");
            Toast.makeText(context, "Starts with +234", Toast.LENGTH_SHORT).show();
        }
        if(phone.startsWith("234")){
            phone = phone.replaceAll("234","0");
            Toast.makeText(context, "Starts with 234", Toast.LENGTH_SHORT).show();
        }

        if(phone.length() > 13){
            Utils.popup(getActivity(),"Phone Number Error","A valid phone number is required");
            return;
        }
        Log.e("****PHONE***",phone);
        User user = new User();
        user.setTitle(radioValue);
        user.setOther_names(other_name);
        user.setFirst_name(first_name);
        user.setLast_name(last_name);
        user.setPhone(phone);
        user.setAccess(PENDING+"");
        user.setStatus(PENDING+"");
        user.setDate(getTimestamp());
        ContentValues contentValues = new ContentValues();
        contentValues.put("TITLE",user.title);
        contentValues.put("PHONE",user.phone);
        contentValues.put("USER_ID",user.user_id);
        contentValues.put("ACCESS",user.access);
        contentValues.put("FIRST_NAME",user.first_name);
        contentValues.put("LAST_NAME",user.last_name);
        contentValues.put("OTHER_NAMES",user.other_names);
        contentValues.put("STATUS",user.status);
        contentValues.put("DATE", user.date);
        boolean status = new DatabaseHelper(getActivity()).do_insert(TABLE_USERS,contentValues);
        if(status){
            step_2_fragment(user);
        }else{
            Utils.popup(getActivity(),"Error","LOCAL PERSISTENCE ERROR");
        }
    }
    private void step_2_fragment(User user){
        Step2Fragment fragment = new Step2Fragment();
        Bundle data = new Bundle();//create bundle instance
        data.putString("user_id", user.user_id+"");
        data.putString("phone", user.phone);
        fragment.setArguments(data);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }





    AlertDialog.Builder alertDialog;
    public void choose_title_popup(){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Choose your title");
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        final String[] menu_name = Constants.TITLES;
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
                mChoose_title.setText(menu_name[position]);
                radioValue = menu_name[position];
            }
        });
    }



}
