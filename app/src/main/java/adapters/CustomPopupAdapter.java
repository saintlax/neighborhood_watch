package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import caller.com.testnav.R;


public class CustomPopupAdapter extends BaseAdapter {
    Context context;


    String[] menu_name;
    int[]  menu_icons;
    public CustomPopupAdapter(Context act, String[] menu_name, int[] menu_icons){
        this.context = act;
        this.menu_name = menu_name;
        this.menu_icons = menu_icons;
    }
    @Override
    public int getCount() {
        return menu_name.length;
    }

    @Override
    public String getItem(int position) {
        try{
            return menu_name[position-1];
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.row_for_custom_menu,viewGroup,false);
        ImageView icon = (ImageView)row.findViewById(R.id.menu_icon);
        icon.setImageResource(menu_icons[i]);
        TextView name =(TextView)row.findViewById(R.id.menu_name);
        name.setText(menu_name[i]);
        return row;
    }
}
