package adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import caller.com.testnav.R;


public class HomeMenuAdapter extends BaseAdapter {
    Context context;
    List<String> menu_name;
    List<Integer> menu_icon;
    LayoutInflater inflater;
    public HomeMenuAdapter(Context con, List<String> menuname, List<Integer> menuicon){
        context = con;
        menu_name = menuname;
        menu_icon = menuicon;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return menu_icon.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView= inflater.inflate(R.layout.row_main_menu2, null);
        TextView txttitle = (TextView)convertView.findViewById(R.id.textTitle);
        ImageView imglogo = (ImageView)convertView.findViewById(R.id.imageLogo);
        txttitle.setText(menu_name.get(position));

        //    txttitle.setTextColor(context.getResources().getColor(color_array.get(position)));
    //    txttitle.setTextColor(Color.parseColor("#303F9F"));
        txttitle.setTextColor(context.getResources().getColor(R.color.black));
        imglogo.setImageResource(menu_icon.get(position));
        return convertView;
    }
}