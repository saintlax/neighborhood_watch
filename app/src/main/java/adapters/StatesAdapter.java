package adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import caller.com.testnav.R;

import static Constants.Constants.EMPTYY;


public class StatesAdapter extends BaseAdapter {
    private Activity context;
    private JSONArray postItems;

    private int lastPosition = -1;
    DisplayImageOptions options;
    ImageLoaderConfiguration imgconfig;
    LayoutInflater inflater;
    public StatesAdapter(Activity act, JSONArray arraylist){
        this.context = act;
        postItems = arraylist;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return postItems.length();
    }
    @Override
    public JSONObject getItem(int position) {
        try {
            return postItems.getJSONObject(position);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        /*if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.row_for_street, null);
        }*/
       // does not update the listView
         convertView= inflater.inflate(R.layout.row_for_state, null);

        lastPosition = position;

        try {
            JSONObject jObj = postItems.getJSONObject(position);
            TextView title = (TextView)convertView.findViewById(R.id.name);
            title.setText(jObj.getString("name"));

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertView;
    }


}
