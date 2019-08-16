package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import caller.com.testnav.R;

public class MenuFragment extends Fragment {

    ImageView minus,plus;
    TextView number;
    int count = 0;
    public MenuFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.menu_fragment, container, false);
        minus = (ImageView)view.findViewById(R.id.minus);
        plus = (ImageView)view.findViewById(R.id.plus);
        number = (TextView)view.findViewById(R.id.number);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count != 0){
                    count --;
                    number.setText(count+"");
                }
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    count ++;
                    number.setText(count+"");

            }
        });
        return view;
    }


}
