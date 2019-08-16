package adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

import caller.com.testnav.R;
import helper.AnimateFirstDisplayListener;
import helper.RoundedImageView;

import static Constants.Constants.EMPTYY;
import static server.ServerRequests.SERVER_ROOT;


public class EyeWitnessAdapter extends BaseAdapter {
    private Activity context;
    private JSONArray postItems;

    private int lastPosition = -1;
    DisplayImageOptions options;
    ImageLoaderConfiguration imgconfig;
    LayoutInflater inflater;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    public EyeWitnessAdapter(Activity act, JSONArray arraylist){
        this.context = act;
        postItems = arraylist;
        inflater = LayoutInflater.from(context);
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
            convertView = mInflater.inflate(R.layout.row_for_eye_witness_report, null);
        }
       // does not update the listView
       //  convertView= inflater.inflate(R.layout.row_for_eye_witness_report, null);

        lastPosition = position;

        try {
            JSONObject jObj = postItems.getJSONObject(position);
            TextView name = (TextView)convertView.findViewById(R.id.name);
            TextView mStatus = (TextView)convertView.findViewById(R.id.status);
            TextView phone = (TextView)convertView.findViewById(R.id.phone);
            TextView message = (TextView)convertView.findViewById(R.id.message);
            TextView date = (TextView)convertView.findViewById(R.id.date);

            JSONObject userObject = jObj.getJSONObject("user");
            JSONObject eyeWitnessObject = jObj.getJSONObject("eye_witness");

            String status = "Text";
            switch (eyeWitnessObject.getString("file_type")){
                case "png": case "jpg": case "jpeg":
                    status = "Picture";
                    break;
                case "3gp": case "mp4": case "wmv": case "avi": case "mkv":
                    status = "Video";
                    break;
                    default:
                        status = "Undefined";
                        break;
            }
            mStatus.setText(status);
            name.setText(userObject.getString("first_name")+" "+userObject.getString("last_name"));
            phone.setText(userObject.getString("phone"));
            message.setText(eyeWitnessObject.getString("message"));
            date.setText(eyeWitnessObject.getString("date"));
            RoundedImageView top_image = (RoundedImageView)convertView.findViewById(R.id.image);
            if(!userObject.getString("thumbnail").equalsIgnoreCase("") && !userObject.getString("thumbnail").equalsIgnoreCase("null")){
                ImageLoader.getInstance().displayImage(SERVER_ROOT.replace("/android","") + userObject.getString("thumbnail"), top_image, options, animateFirstListener);
                top_image.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertView;
    }


}
