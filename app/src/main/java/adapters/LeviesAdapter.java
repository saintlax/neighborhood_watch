package adapters;

import android.app.Activity;
import android.text.Html;
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

import static Constants.Constants.NAIRA;
import static helper.Utils.currencyFormat;


public class LeviesAdapter extends BaseAdapter {
    private Activity context;
    private JSONArray postItems;

    private int lastPosition = -1;
    DisplayImageOptions options;
    ImageLoaderConfiguration imgconfig;
    LayoutInflater inflater;
    public LeviesAdapter(Activity act, JSONArray arraylist){
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

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.row_for_levy, null);
        }
       // does not update the listView
        // convertView= inflater.inflate(R.layout.row_for_street, null);

        lastPosition = position;

        try {
            JSONObject jObj = postItems.getJSONObject(position);
            TextView name = (TextView)convertView.findViewById(R.id.name);
            TextView amount = (TextView)convertView.findViewById(R.id.amount);
            TextView date = (TextView)convertView.findViewById(R.id.date);

            name.setText(jObj.getString("name"));
            amount.setText(Html.fromHtml("Amount: "+NAIRA)+currencyFormat(jObj.getString("amount")));
            date.setText("Deadline Date: "+jObj.getString("deadline_date"));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertView;
    }


}
