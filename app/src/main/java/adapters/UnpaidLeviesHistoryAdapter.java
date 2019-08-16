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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Objects.User;
import caller.com.testnav.R;
import database.DatabaseHelper;
import helper.AnimateFirstDisplayListener;

import static Constants.Constants.ERROR;
import static Constants.Constants.NAIRA;
import static Constants.Constants.SUCCESS;
import static helper.Utils.currencyFormat;


/**
 * Created by user on 1/24/2019.
 */

public class UnpaidLeviesHistoryAdapter extends BaseAdapter {
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

    public UnpaidLeviesHistoryAdapter(Context context, JSONArray arraylist){
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
        convertView= inflater.inflate(R.layout.row_for_unpaid_history, null);
        lastPosition = position;

        try {


            JSONObject jObj = postItems.getJSONObject(position);
            TextView title = (TextView)convertView.findViewById(R.id.title);
            TextView amount_due = (TextView)convertView.findViewById(R.id.amount_due);
            TextView deadline = (TextView)convertView.findViewById(R.id.deadline_date);

                title.setText(jObj.getString("name"));
                amount_due.setText(Html.fromHtml("Amount Due: "+NAIRA+" "+currencyFormat(jObj.getString("amount"))));
                deadline.setText("Payment Deadline: "+jObj.getString("deadline_date"));

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertView;
    }


}
