package adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import java.util.List;

import Objects.Message;
import Objects.User;
import caller.com.testnav.R;
import database.DatabaseHelper;
import helper.AnimateFirstDisplayListener;
import helper.RoundedImageView;

import static Constants.Constants.EMPTYY;
import static Constants.Constants.MODE;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static database.DatabaseHelper.TABLE_CHAT;
import static database.DatabaseHelper.TABLE_USERS;
import static helper.Utils.formatTime;
import static server.ServerRequests.SERVER_ROOT;


/**
 * Created by user on 1/24/2019.
 */

public class OccupantsAdapter extends BaseAdapter {
    private Activity context;
    private JSONArray postItems;

    private int lastPosition = -1;
    DisplayImageOptions options;
    ImageLoaderConfiguration imgconfig;
    DatabaseHelper db;
    SharedPreferences sharedPreferences;
    User user;
    LayoutInflater inflater;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public OccupantsAdapter(Activity act, JSONArray arraylist){
        this.context = act;
        db = new DatabaseHelper(context);
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE);
        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"'";
        user = db.user_data(query);
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
    private Message lastMessage(String receiver_id){
        String query = "select * from "+TABLE_CHAT+" where receiver_id='"+receiver_id+"' and sender_id='"+user.user_id+"'  order by id desc limit 1";
        List<Message> chats = db.chatList(query);
        if(chats != null){
            for(int i =0;i<chats.size();i++) {
                return chats.get(i);
            }
        }
        return null;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        /*if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.row_for_contacts, null);
        }*/
        convertView= inflater.inflate(R.layout.row_for_occupant, null);
        lastPosition = position;

        try {
            JSONObject jObj = postItems.getJSONObject(position);
            TextView name = (TextView)convertView.findViewById(R.id.name);
            TextView phone = (TextView)convertView.findViewById(R.id.phone);
            TextView mStreet = (TextView)convertView.findViewById(R.id.street);
            TextView mStatus = (TextView)convertView.findViewById(R.id.status);
            TextView mBuilding = (TextView)convertView.findViewById(R.id.building);
            TextView mApartment = (TextView)convertView.findViewById(R.id.apartment);
            TextView mUser_type = (TextView)convertView.findViewById(R.id.user_type);

            name.setText(Html.fromHtml(jObj.getString("title")+" "+jObj.getString("first_name")+" "+jObj.getString("last_name")));
            phone.setText("Phone: "+jObj.getString("phone"));
            RoundedImageView top_image = (RoundedImageView)convertView.findViewById(R.id.image);
            if(!jObj.getString("thumbnail").equalsIgnoreCase("") && !jObj.getString("thumbnail").equalsIgnoreCase("null")){
                ImageLoader.getInstance().displayImage(SERVER_ROOT.replace("/android","") + jObj.getString("thumbnail"), top_image, options, animateFirstListener);
                top_image.setVisibility(View.VISIBLE);
            }
            JSONObject user_type = jObj.getJSONObject("user_type");
            String type_state = user_type.getString("state");
            if(!type_state.equalsIgnoreCase(EMPTYY)){
                mUser_type.setText("Type: "+user_type.getString("user_type"));
                if(user_type.getString("user_type").equalsIgnoreCase(context.getString(R.string.admin))){
                    mUser_type.setTextColor(Color.RED);
                }
                if(user_type.getString("user_type").equalsIgnoreCase(context.getString(R.string.caretaker))){
                    mUser_type.setTextColor(Color.BLUE);
                }

                if(user_type.getString("user_type").equalsIgnoreCase(context.getString(R.string.landlord))){
                    mUser_type.setTextColor(Color.GREEN);
                }
            }

            JSONObject street = jObj.getJSONObject("street");
            String street_state = street.getString("state");
            if(!street_state.equalsIgnoreCase(EMPTYY)){
                mStreet.setText("Street: "+street.getString("name"));
            }

            JSONObject building = jObj.getJSONObject("building");
            String building_state = building.getString("state");
            if(!building_state.equalsIgnoreCase(EMPTYY)){
                mBuilding.setText("Building: "+building.getString("name"));
            }

            JSONObject apartment = jObj.getJSONObject("apartment");
            String apartment_state = apartment.getString("state");
            if(!apartment_state.equalsIgnoreCase(EMPTYY)){
                mApartment.setText("Apartment: "+apartment.getString("name"));
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertView;
    }


}
