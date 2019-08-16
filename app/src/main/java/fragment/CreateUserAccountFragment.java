package fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Constants.Constants;
import Objects.AllTypes;
import Objects.User;
import Objects.UserType;
import adapters.AllTypesPopupAdapter;
import adapters.CustomPopupAdapter;
import caller.com.testnav.LoginActivity;
import caller.com.testnav.R;
import database.DatabaseHelper;
import dialogs.Post;
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
import static Constants.Constants.PENDING;
import static Constants.Constants.RESULT_LOAD_IMAGE;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static Constants.Constants.SUCCESS;
import static android.app.Activity.RESULT_OK;
import static database.DatabaseHelper.TABLE_USERS;
import static database.DatabaseHelper.TABLE_USER_TYPE;
import static helper.Utils.getMimeTtype;
import static helper.Utils.getTimestamp;
import static helper.Utils.savePhotoToSDcard;

public class CreateUserAccountFragment extends Fragment {
    private LinearLayout mDotsLayout;
    Button continue_button;
    private TextView[] mDots;
    EditText mLastname,mFirstname,mPhone,mOther_names;
    String user_title,radioValue;
    Button mChoose_title;
    Context context;
    TextView mHead_title;
    RadioGroup radioGroup;
    User user;
    UserType userType;
    SharedPreferences sharedPreferences;
    DatabaseHelper db;

    DisplayImageOptions options;
    ImageLoaderConfiguration imgconfig;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    RoundedImageView top_image;

    public CreateUserAccountFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_create_user_account, container, false);

        context = getActivity();
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE);
        db = new DatabaseHelper(context);
        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"' order by id desc";
        user = db.user_data(query);
        if(user == null){
            startActivity(new Intent(context, LoginActivity.class));
            return null;
        }

        query = "select * from "+TABLE_USER_TYPE+" where user_id='"+user_id+"' order by id desc";
        userType = db.user_type(query);


        user_title = "Mr.";
        mChoose_title = (Button)view.findViewById(R.id.choose_title);
        mChoose_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose_title_popup();
            }
        });

        mLastname = (EditText)view.findViewById(R.id.last_name);
        mPhone = (EditText)view.findViewById(R.id.phone);
        mFirstname = (EditText)view.findViewById(R.id.first_name);
        mOther_names = (EditText)view.findViewById(R.id.other_names);
        mDotsLayout = (LinearLayout)view.findViewById(R.id.dots_layout);
        continue_button = (Button)view.findViewById(R.id.continue_button);
        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proceed();
            }
        });

        radioValue = getString(R.string.landlord);
        radioGroup = (RadioGroup)view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id=group.getCheckedRadioButtonId();
                RadioButton rb=(RadioButton)view. findViewById(id);
                radioValue = rb.getText().toString();
            }
        });






        File cacheDir = StorageUtils.getCacheDirectory(context);
        imgconfig = new ImageLoaderConfiguration.Builder(context)
                .build();
        ImageLoader.getInstance().init(imgconfig);
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

        top_image = (RoundedImageView)view.findViewById(R.id.top_image);

        top_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup_image_dialog();
            }
        });

        return view;
    }


    public void popup_image_dialog(){
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


    private void proceed(){
        //   getSupportActionBar().setTitle("Sign In");
        String first_name = mFirstname.getText().toString();
        String last_name = mLastname.getText().toString();
        String phone = mPhone.getText().toString();
        String other_name = mOther_names.getText().toString();
        if(TextUtils.isEmpty(first_name)){
            Utils.popup(getActivity(),"Error","Your first name is required");
            return;
        }

        if(TextUtils.isEmpty(last_name)){
            Utils.popup(getActivity(),"Error","Your Last name is required");
            return;
        }

        if(TextUtils.isEmpty(phone)){
            Utils.popup(getActivity(),"Error","Your phone number is required");
            return;
        }

    //    User user = new User();
        user.setTitle(user_title);
        user.setOther_names(other_name);
        user.setFirst_name(first_name);
        user.setLast_name(last_name);
        user.setPhone(phone);
        user.setAccess(PENDING+"");
        user.setStatus(PENDING+"");
        user.setDate(getTimestamp());

     //   UserType userType = new UserType();
//        userType.setApartment_id(apartment_id);
//        userType.setApartment_name(apartment_name);
        userType.setUser_type(radioValue);
    //    new Post().admin_add_user(context,user,userType);

        getData("streets",Integer.parseInt(userType.district_id));
    }



    AlertDialog.Builder alertDialog;
    public void choose_title_popup(){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Choose member title");
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        final String[] menu_name = Constants.TITLES;
        int[]  menu_icons = {
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary,
                R.drawable.ic_user_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                mChoose_title.setText(menu_name[position]);
                user_title = menu_name[position];
            }
        });
    }

    public void getData(final String data,final int id){
        ServerRequests serverRequests = new ServerRequests();
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setTitle("Loading "+data);
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        String page = "get_states.php";
        RequestBody request_body = null;
        switch(data){
            case "states":
                page = "get_states.php";
                request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("user_id","0.1")
                        .build();
                break;
            case "lgas":
                page = "get_lgas.php";
                request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("user_id","0.1")
                        .addFormDataPart("state_id",id+"")
                        .build();
                break;
            case "districts":
                page = "get_districts.php";
                request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("user_id","0.1")
                        .addFormDataPart("lga_id",id+"")
                        .build();
                break;

            case "streets":
                page = "get_streets.php";
                request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("user_id","0.1")
                        .addFormDataPart("district_id",id+"")
                        .build();
                break;
            case "buildings":
                page = "get_buildings.php";
                request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("user_id","0.1")
                        .addFormDataPart("street_id",id+"")
                        .build();
                break;
            case "apartments":
                page = "get_apartments.php";
                request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("user_id","0.1")
                        .addFormDataPart("building_id",id+"")
                        .build();
                break;
            case "tenant_type":
                page = "get_all_tenant_type.php";
                request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("user_id","0.1")//not neccessary
                        .build();
                break;
        }

        serverRequests.post(request_body, page, new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        List<AllTypes> allTypesList = new ArrayList<>();
                        JSONArray staff = returnedJSON.getJSONArray(data);
                        for(int k=0;k<staff.length();k++){
                            JSONObject object = staff.getJSONObject(k);
                            AllTypes allTypes = new AllTypes();
                            allTypes.setId(object.getInt("id"));
                            allTypes.setName(object.getString("name"));
                            allTypesList.add(allTypes);
                        }
                        allTypes_popup(allTypesList,data);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }
    public void allTypes_popup(final List<AllTypes> allTypess,final String data){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Choose "+data);
        ListView lv = (ListView) convertView.findViewById(R.id.lv);

        AllTypesPopupAdapter adapter = new AllTypesPopupAdapter(context,allTypess);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                AllTypes allTypes = allTypess.get(position);
                switch (data){
                    case "states":
                        userType.setState_id(allTypes.id+"");
                        userType.setStreet_name(allTypes.name);
                        getData("lgas",allTypes.id);
                        break;
                    case "lgas":
                        userType.setLga_id(allTypes.id+"");
                        userType.setLga_name(allTypes.name);
                        getData("districts",allTypes.id);
                        break;
                    case "districts":
                        userType.setDistrict_id(allTypes.id+"");
                        userType.setDistrict_name(allTypes.name);
                        getData("streets",allTypes.id);
                        break;
                    case "streets":
                        userType.setStreet_id(allTypes.id+"");
                        userType.setStreet_name(allTypes.name);
                        getData("buildings",allTypes.id);
                        break;
                    case "buildings":
                        userType.setBuilding_id(allTypes.id+"");
                        userType.setBuilding_name(allTypes.name);
                        getData("apartments",allTypes.id);
                        break;
                    case "apartments":

                        userType.setApartment_id(allTypes.id+"");
                        userType.setApartment_name(allTypes.name);
                        getData("tenant_type",allTypes.id);
                        break;
                    default:
                        userType.setApartment_type_id(allTypes.id+"");
                        userType.setApartment_type_name(allTypes.name);
                        confirm_account_popup(context,userType);
                        break;
                }

            }
        });
    }

    public void confirm_account_popup(final Context context,final UserType userType){
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Confirm Account");
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "Title: "+user.title,
                "First Name: "+user.first_name,
                "Last Name: "+user.last_name,
                "Other Names: "+user.other_names,
                "Phone Number: "+user.phone,
                "State: "+userType.state_name,
                "LGA: "+userType.lga_name,
                "District: "+userType.district_name,
                "Street: "+userType.street_name,
                "Building: "+userType.building_name,
                "Apartment: "+userType.apartment_name,
                "Type: "+userType.apartment_type_name,
        };
        int[]  menu_icons = {
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary,
                R.drawable.ic_edit_primary
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });
        alertDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                register_user(user,userType);
                //new Post().change_user_address(context,user,userType);
            }
        });
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
            }
        });

    }

    public void register_user(final User user, UserType userType){
        ServerRequests serverRequests = new ServerRequests();
        final ProgressDialog pDialog = new ProgressDialog(context);
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
                            mLastname.setText("");
                            mFirstname.setText("");
                            mPhone.setText("");
                            mOther_names.setText("");
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
