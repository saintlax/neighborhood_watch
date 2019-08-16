package fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;

import Objects.EyeWitness;
import Objects.User;
import adapters.CustomPopupAdapter;
import caller.com.testnav.R;
import database.DatabaseHelper;
import dialogs.Dialogs;
import helper.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import server.GetJSONCallback;
import server.ServerRequests;

import static Constants.Constants.CAMERA_REQUEST;
import static Constants.Constants.MODE;
import static Constants.Constants.REQUEST_TAKE_GALLERY_VIDEO;
import static Constants.Constants.RESULT_LOAD_IMAGE;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static Constants.Constants.SUCCESS;
import static Constants.Constants.VIDEO_REQUEST;
import static android.app.Activity.RESULT_OK;
import static database.DatabaseHelper.TABLE_USERS;
import static helper.Utils.getTimestamp;
import static helper.Utils.savePhotoToSDcard;

public class AboutUsFragment extends Fragment {
    Context context;
    DatabaseHelper db;
    User user;
    SharedPreferences sharedPreferences;
    TextView textView;

    public AboutUsFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        context = getActivity();
        db = new DatabaseHelper(context);
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE);
        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"'";
        user = db.user_data(query);
        textView = (TextView)view.findViewById(R.id.message);
        textView.setText(context.getString(R.string.about_us_message));
        return view;
    }



}




