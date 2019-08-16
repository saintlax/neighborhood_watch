package fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.json.JSONObject;

import java.io.File;

import Objects.User;
import Objects.UserType;
import adapters.CustomPopupAdapter;
import caller.com.testnav.LoginActivity;
import caller.com.testnav.MainActivity;
import caller.com.testnav.R;
import database.DatabaseHelper;
import helper.AnimateFirstDisplayListener;
import helper.RoundedImageView;
import helper.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import server.GetJSONCallback;
import server.ServerRequests;

import static Constants.Constants.CAMERA_REQUEST;
import static Constants.Constants.MODE;
import static Constants.Constants.RESULT_LOAD_IMAGE;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static Constants.Constants.SUCCESS;
import static android.app.Activity.RESULT_OK;
import static database.DatabaseHelper.TABLE_USERS;
import static database.DatabaseHelper.TABLE_USER_TYPE;
import static helper.Utils.LoadLoginFragment;
import static helper.Utils.getMimeTtype;
import static helper.Utils.savePhotoToSDcard;


public class Step7Fragment extends Fragment {

    DisplayImageOptions options;
    ImageLoaderConfiguration imgconfig;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    RoundedImageView top_image;
    Button skipBtn;
    SharedPreferences sharedPreferences;
    User user;
    UserType userType;
    String phone;
    DatabaseHelper db;
    Context context;
    AlertDialog.Builder alertDialog;
    ProgressDialog pDialog;
    TextView mRegister_link;
    public Step7Fragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = new DatabaseHelper(getActivity());
        context = getActivity();
        ((LoginActivity)context ).getSupportActionBar().setTitle("Add Profile Picture");
        sharedPreferences = getActivity().getSharedPreferences(SP_NAME, MODE);
        phone = getArguments().getString("phone");
        String query = "select * from "+TABLE_USERS+" where phone='"+phone+"'";
        user = db.user_data(query);
        query = "select * from "+TABLE_USER_TYPE+" where phone='"+phone+"'";
        userType = db.user_type(query);
        File cacheDir = StorageUtils.getCacheDirectory(getActivity());
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_user_primary)
                .showImageForEmptyUri(R.drawable.ic_user_primary)
                .showImageOnFail(R.drawable.ic_user_primary)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.NONE)
                .build();

        imgconfig = new ImageLoaderConfiguration.Builder(getActivity())
                .build();
        ImageLoader.getInstance().init(imgconfig);
        View view = inflater.inflate(R.layout.step_7_register, container, false);


        imgconfig = new ImageLoaderConfiguration.Builder(getActivity())
                .build();
        ImageLoader.getInstance().init(imgconfig);
        skipBtn = (Button) view.findViewById(R.id.skipNext);
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(getActivity(), MainActivity.class);;
                Bundle bundle = new Bundle();
                bundle.putString("username", phone);
                intent.putExtras(bundle);
                startActivity(intent);
*/
                if(user != null && userType != null)
                    register_online(user,userType);
                else
                    Utils.popup(context,"ERROR","There is an error in registeration");
            }
        });
        top_image = (RoundedImageView)view.findViewById(R.id.top_image);
        top_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup_dialog();
            }
        });
        mRegister_link = (TextView)view.findViewById(R.id.register_link);
        mRegister_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadLoginFragment(context);
            }
        });
        return view;
    }

    public void popup_dialog(){
        alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Profile Picture");
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
            "Take a picture",
            "Choose from gallery"
        };
    int[]  menu_icons = {
            R.drawable.ic_camera_primary,
            R.drawable.ic_attach_primary
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
                default:
                    break;
        }
    }

    private void saveImage(String path){
        user.setImage(path);

        ContentValues contentValues = new ContentValues();
        //    contentValues.put("owner_phone",owner_phone);
        contentValues.put("image",user.image);
        DatabaseHelper db = new DatabaseHelper(getActivity());
        db.do_edit(TABLE_USERS,contentValues,"phone" ,phone);
        skipBtn.setText("CONTINUE TO DASHBOARD");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK && data !=null){
                    Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                    String picturePath = savePhotoToSDcard(bitmap);
                    File file = new File(picturePath);
                    Uri imageUri  = Uri.fromFile(file);
                    ImageLoader.getInstance().displayImage(imageUri.toString(), top_image, options, animateFirstListener);
                    saveImage(picturePath);
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

                    File file = new File(picturePath);
                    Uri imageUri  = Uri.fromFile(file);
                    ImageLoader.getInstance().displayImage(imageUri.toString(), top_image, options, animateFirstListener);
                    saveImage(picturePath);
                }
                break;
        }
    }

    public void register_online(final User user, UserType userType){
        ServerRequests serverRequests = new ServerRequests();
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Creating Account");
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestBody request_body = null;
        if(user.image != null){
            File f = new File(user.image);
            String content_type = getMimeTtype(f.getPath());
            String file_path = f.getAbsolutePath();
            RequestBody file_body = RequestBody.create(MediaType.parse(content_type),f);
            request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("state_id",userType.state_id)
                    .addFormDataPart("state_name",userType.state_name)
                    .addFormDataPart("lga_id",userType.lga_id)
                    .addFormDataPart("lga_name",userType.lga_name)
                    .addFormDataPart("district_id",userType.district_id)
                    .addFormDataPart("district_name",userType.district_name)
                    .addFormDataPart("apartment_type_id",userType.apartment_type_id)
                    .addFormDataPart("apartment_type_name",userType.apartment_type_name)
                    .addFormDataPart("user_type",userType.user_type)
                    .addFormDataPart("street_id",userType.street_id)
                    .addFormDataPart("apartment_id",userType.apartment_id)
                    .addFormDataPart("building_id",userType.building_id)
                    .addFormDataPart("phone",user.phone)
                    .addFormDataPart("date",user.date)
                    .addFormDataPart("title",user.title)
                    .addFormDataPart("last_name",user.last_name)
                    .addFormDataPart("first_name",user.first_name)
                    .addFormDataPart("other_names",user.other_names)
                    .addFormDataPart("uploaded_file",file_path.substring(file_path.lastIndexOf("/")+1),file_body)
                    .build();
        }else{
            request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("state_id",userType.state_id)
                    .addFormDataPart("state_name",userType.state_name)
                    .addFormDataPart("lga_id",userType.lga_id)
                    .addFormDataPart("lga_name",userType.lga_name)
                    .addFormDataPart("district_id",userType.district_id)
                    .addFormDataPart("district_name",userType.district_name)
                    .addFormDataPart("apartment_type_id",userType.apartment_type_id)
                    .addFormDataPart("apartment_type_name",userType.apartment_type_name)
                    .addFormDataPart("user_type",userType.user_type)
                    .addFormDataPart("street_id",userType.street_id)
                    .addFormDataPart("apartment_id",userType.apartment_id)
                    .addFormDataPart("building_id",userType.building_id)
                    .addFormDataPart("phone",user.phone)
                    .addFormDataPart("date",user.date)
                    .addFormDataPart("title",user.title)
                    .addFormDataPart("last_name",user.last_name)
                    .addFormDataPart("first_name",user.first_name)
                    .addFormDataPart("other_names",user.other_names)
                    .build();
        }
        serverRequests.post(request_body, "add_user.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            update_user(returnedJSON,user);
                            return;
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

    private void update_user(JSONObject returnedJSON,User user) {
        try{
            JSONObject userObject = returnedJSON.getJSONObject("user");
            ContentValues contentValues = new ContentValues();
            contentValues.put("user_id",userObject.getString("user_id"));
            db.do_edit(TABLE_USERS,contentValues,"phone",user.phone);
            db.do_edit(TABLE_USER_TYPE,contentValues,"phone",user.phone);
            //save user_id into SP

            SharedPreferences.Editor userLocalDatabaseEditor = sharedPreferences.edit();
            userLocalDatabaseEditor.putString(SP_USER_ID,userObject.getString("user_id"));
            userLocalDatabaseEditor.commit();
            startActivity(new Intent(context, MainActivity.class));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
