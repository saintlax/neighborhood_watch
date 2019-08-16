package fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Objects.EyeWitness;
import Objects.User;
import Objects.UserType;
import adapters.CustomPopupAdapter;
import adapters.DistressHistoryAdapter;
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
import static Constants.Constants.INVISIBLE;
import static Constants.Constants.MODE;
import static Constants.Constants.REQUEST_TAKE_GALLERY_VIDEO;
import static Constants.Constants.RESULT_LOAD_IMAGE;
import static Constants.Constants.SP_DISTRESS_HISTORY;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static Constants.Constants.SUCCESS;
import static Constants.Constants.VIDEO_REQUEST;
import static Constants.Constants.VISIBLE;
import static android.app.Activity.RESULT_OK;
import static database.DatabaseHelper.TABLE_USERS;
import static helper.Utils.getMimeTtype;
import static helper.Utils.getTimestamp;
import static helper.Utils.savePhotoToSDcard;

public class EyeWitnessFragment extends Fragment {
    Context context;
    DatabaseHelper db;
    User user;
    SharedPreferences sharedPreferences;
    Dialogs myDialogs = new Dialogs();
    EditText mMessage;
    TextView mAddMedia;
    Button mSubmit;
    CheckBox send_type;
    AlertDialog.Builder alertDialog;
    EyeWitness eyeWitness;


    public EyeWitnessFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_eye_witness, container, false);
        context = getActivity();
        db = new DatabaseHelper(context);
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE);
        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"'";
        user = db.user_data(query);

        eyeWitness = new EyeWitness();
        eyeWitness.setSend_type(VISIBLE);
        send_type = (CheckBox)view.findViewById(R.id.send_type);
        send_type.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    Toast.makeText(context, "Your identity is protected", Toast.LENGTH_LONG).show();
                    eyeWitness.setSend_type(INVISIBLE);
                }else{
                    eyeWitness.setSend_type(VISIBLE);
                }

            }
        });
        mMessage = (EditText)view.findViewById(R.id.message);
        mAddMedia = (TextView)view.findViewById(R.id.add_media);
        mAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup_choose_media();
            }
        });
        mSubmit = (Button)view.findViewById(R.id.submit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = mMessage.getText().toString();
                if(TextUtils.isEmpty(message)){
                    Utils.popup(context,"Error","A message is required to send an eye witness report");
                    return;
                }
                eyeWitness.setMessage(message);
                eyeWitness.setDate(getTimestamp());
                upload_eyeWitness(user,eyeWitness);
            }
        });
        return view;
    }


    public void popup_choose_media(){
        alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Add Media");
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "Take a picture with camera",
                "Choose a picture from gallery",
                "Record a Video",
                "Choose a video from Gallery"

        };
        int[]  menu_icons = {
                R.drawable.ic_camera_primary,
                R.drawable.ic_attach_primary,
                R.drawable.ic_record_red,
                R.drawable.ic_play_primary
        };

        CustomPopupAdapter adapter = new CustomPopupAdapter(getActivity(),menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                custom_menu_actions(position);
            }
        });
    }
    private void custom_menu_actions(int position){
        switch (position){
            case 0:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAMERA_REQUEST);
                break;
            case 1:
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
            case 2:
                Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if(videoIntent.resolveActivity(context.getPackageManager()) != null){
                    startActivityForResult(videoIntent,VIDEO_REQUEST);
                }
                break;
            case 3:
                Intent intent2 = new Intent();
                intent2.setType("video/*");
                intent2.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent2,"Select Video"),REQUEST_TAKE_GALLERY_VIDEO);

                break;
            default:
                break;
        }
    }

    private void saveFile(String path){
        eyeWitness.setFile_name(path);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK && data !=null){
                    Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                    String picturePath = savePhotoToSDcard(bitmap);
                    mAddMedia.setText("Camera Image has been taken");
                    mAddMedia.setTextColor(Color.BLUE);
                    saveFile(picturePath);
                }
                break;

            case RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK && null != data){
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    mAddMedia.setText("Image has been selected from Gallery");
                    mAddMedia.setTextColor(Color.BLUE);
                    saveFile(picturePath);
                }
                break;
            case REQUEST_TAKE_GALLERY_VIDEO:
                if (resultCode == RESULT_OK && null != data){
                    String[] projection = { MediaStore.Video.Media.DATA };
                    Uri uri = data.getData();
                    Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
                    if (cursor != null) {
                        int column_index = cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                        cursor.moveToFirst();
                        String videoPath = cursor.getString(column_index);
                        mAddMedia.setText("Video has been chosen from gallery");
                        mAddMedia.setTextColor(Color.BLUE);
                        saveFile(videoPath);
                    }
                }
                break;
            case VIDEO_REQUEST:
                if(resultCode == RESULT_OK && null != data){
                    String[] projection = { MediaStore.Video.Media.DATA };
                    Uri uri = data.getData();
                    Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
                    if (cursor != null) {
                        int column_index = cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                        cursor.moveToFirst();
                        String videoPath = cursor.getString(column_index);
                        mAddMedia.setText("Video recording is ready for sending...");
                        mAddMedia.setTextColor(Color.BLUE);
                        saveFile(videoPath);
                    }
                }
                break;
        }
    }

    public void upload_eyeWitness(final User user, EyeWitness eyeWitness){
        ServerRequests serverRequests = new ServerRequests();
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setTitle("Eye Witness");
        pDialog.setMessage("This will take a moment...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestBody request_body = null;
        if(eyeWitness.file_name != null){
            File f = new File(eyeWitness.file_name);
        //    String content_type = getMimeTtype(f.getPath());
            String content_type = "image/png";
            String file_path = f.getAbsolutePath();


            String extension = file_path.substring(file_path.lastIndexOf("."));
            switch (extension){
                case "png":case "PNG":case "JPG":case "JPEG":
                    content_type = "image/"+extension;
                    break;
                case "3gp": case "mp4": case "3GP": case "MP4":
                    content_type = "video/"+extension;
                    break;
            }

            RequestBody file_body = RequestBody.create(MediaType.parse(content_type),f);
            request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("user_id",user.user_id+"")
                    .addFormDataPart("timestamp",eyeWitness.date)
                    .addFormDataPart("message",eyeWitness.message)
                    .addFormDataPart("send_type",eyeWitness.send_type+"")
                    .addFormDataPart("uploaded_file",file_path.substring(file_path.lastIndexOf("/")+1),file_body)
                    .build();
        }else{
            request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("user_id",user.user_id+"")
                    .addFormDataPart("timestamp",eyeWitness.date)
                    .addFormDataPart("message",eyeWitness.message)
                    .addFormDataPart("send_type",eyeWitness.send_type+"")
                    .build();
        }
        serverRequests.post(request_body, "add_eye_witness.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            mMessage.setText("");
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

}




