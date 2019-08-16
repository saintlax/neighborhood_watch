package adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import Objects.User;
import caller.com.testnav.R;
import database.DatabaseHelper;
import helper.AnimateFirstDisplayListener;
import helper.RoundedImageView;
import java.text.SimpleDateFormat;
import java.util.Date;

import static Constants.Constants.ERROR;
import static Constants.Constants.MODE;
import static Constants.Constants.NAIRA;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static database.DatabaseHelper.TABLE_USERS;
import static helper.Utils.currencyFormat;
import static server.ServerRequests.SERVER_ROOT;
import java.text.ParseException;


/**
 * Created by user on 1/24/2019.
 */

public class LeviesHistoryAdapter extends BaseAdapter {
    private Context context;
    private JSONArray postItems;

    private int lastPosition = -1;
    DisplayImageOptions options;
    ImageLoaderConfiguration imgconfig;
    DatabaseHelper db;
    SharedPreferences sharedPreferences;
    User user;
    LayoutInflater inflater;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public LeviesHistoryAdapter(Context context, JSONArray arraylist){
        this.context = context;

        postItems = arraylist;
        File cacheDir = StorageUtils.getCacheDirectory(context);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.user_blue)
                .showImageForEmptyUri(R.drawable.user_blue)
                .showImageOnFail(R.drawable.user_blue)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.NONE)
                .build();

        imgconfig = new ImageLoaderConfiguration.Builder(context)
                .build();
        ImageLoader.getInstance().init(imgconfig);

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
            convertView = mInflater.inflate(R.layout.row_for_history, null);
        }*/
        convertView= inflater.inflate(R.layout.row_for_history, null);
        lastPosition = position;

        try {


            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format_month = new SimpleDateFormat("MMM");
            SimpleDateFormat format_day = new SimpleDateFormat("dd");
            SimpleDateFormat format_year = new SimpleDateFormat("yyyy");

            JSONObject jObj = postItems.getJSONObject(position);
            TextView txtDate = (TextView)convertView.findViewById(R.id.date);
            TextView txtMonth = (TextView)convertView.findViewById(R.id.month);
            TextView txtYear = (TextView)convertView.findViewById(R.id.year);
            TextView title = (TextView)convertView.findViewById(R.id.title);
            TextView amount_paid = (TextView)convertView.findViewById(R.id.amount_paid);
            TextView amount_due = (TextView)convertView.findViewById(R.id.amount_due);
            TextView admin_name = (TextView)convertView.findViewById(R.id.admin_name);

            try {
                Date date = format.parse(jObj.getString("date"));
                String date_month = format_month.format(date);
                String date_day = format_day.format(date);
                String date_year = format_year.format(date);
                txtDate.setText(date_day);
                txtYear.setText(date_year);
                txtMonth.setText(date_month);

            }catch (ParseException e){
                e.printStackTrace();
            }

            JSONObject adminObject = jObj.getJSONObject("admin");
            admin_name.setText("Acknowledged By: "+adminObject.getString("last_name")+" "+adminObject.getString("first_name"));

            JSONObject levyObject = jObj.getJSONObject("levy");
            if(!levyObject.getString("state").equalsIgnoreCase(ERROR)){
                title.setText(levyObject.getString("name"));
                amount_due.setText(Html.fromHtml("Amount Due: "+NAIRA+" "+levyObject.getString("amount")));
            }
            amount_paid.setText(Html.fromHtml("Amount Paid: "+NAIRA+" "+currencyFormat(jObj.getString("amount"))));

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertView;
    }


}
