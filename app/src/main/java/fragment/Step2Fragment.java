package fragment;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import caller.com.testnav.LoginActivity;
import caller.com.testnav.R;
import database.DatabaseHelper;
import helper.Utils;

import static database.DatabaseHelper.TABLE_USER_TYPE;

public class Step2Fragment extends Fragment {
    private LinearLayout mDotsLayout;
    private TextView[] mDots;
    Button next_button;
    String user_id,phone;
    RadioGroup radioGroup;
    String radioValue;
    Context context;
    public Step2Fragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.step_2_register, container, false);
        context = getActivity();
        ((LoginActivity)context ).getSupportActionBar().setTitle("Step 2");
        user_id = getArguments().getString("user_id");
        phone = getArguments().getString("phone");
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
        mDotsLayout = (LinearLayout)view.findViewById(R.id.dots_layout);
        next_button = (Button)view.findViewById(R.id.next_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load_next_fragment();
            }
        });
        addDotsIndicator(1);
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
//        mDotsLayout.setPadding(0,getResources().getDimensionPixelSize(R.dimen.pad_20dp),0,0);

        if(mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.white));
        }
    }


    private void load_next_fragment(){
        //   getSupportActionBar().setTitle("Sign In");

        ContentValues contentValues = new ContentValues();
        contentValues.put("PHONE",phone);
        contentValues.put("USER_ID",user_id);
        contentValues.put("USER_TYPE",radioValue);
        DatabaseHelper db = new DatabaseHelper(getActivity());
        boolean status = db.do_insert(TABLE_USER_TYPE,contentValues);
        if(status){
          //  Step3Fragment fragment = new Step3Fragment();
            LoadStatesFragment fragment = new LoadStatesFragment();
            Bundle data = new Bundle();
            data.putString("phone", phone);
            fragment.setArguments(data);
            android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();
        }else{
            Utils.popup(getActivity(),"Error","LOCAL PERSISTENCE ERROR ON USER TYPE");
        }
    }
}
