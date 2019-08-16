package adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import java.util.List;

import caller.com.testnav.R;


/**
 * Created by user on 1/24/2019.
 */

public class PackagesAdapter extends BaseAdapter {
    private Activity context;
    private JSONArray postItems;

    private int lastPosition = -1;
    private List<String> packages;
    LayoutInflater inflater;

    public PackagesAdapter(Activity act, List<String> packages){
        this.context = act;
        this.packages = packages;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return packages.size();
    }
    @Override
    public String getItem(int position) {
        return packages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView= inflater.inflate(R.layout.row_main_packages, null);
        lastPosition = position;
        String data = packages.get(position);
        TextView textTitle = (TextView)convertView.findViewById(R.id.textTitle);
        textTitle.setText(data);
        return convertView;
    }


}
