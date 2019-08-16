package fragment;

import android.app.AlertDialog;
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
import android.widget.ListView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

import Objects.User;
import Objects.UserType;
import adapters.CustomPopupAdapter;
import caller.com.testnav.LoginActivity;
import caller.com.testnav.R;
import database.DatabaseHelper;
import helper.AnimateFirstDisplayListener;
import helper.RoundedImageView;

import static Constants.Constants.CAMERA_REQUEST;
import static Constants.Constants.MODE;
import static Constants.Constants.RESULT_LOAD_IMAGE;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static android.app.Activity.RESULT_OK;
import static database.DatabaseHelper.TABLE_USERS;
import static database.DatabaseHelper.TABLE_USER_TYPE;
import static helper.Utils.savePhotoToSDcard;

public class ProfileFragment extends Fragment {

    User user;
    Context context;
    DatabaseHelper db;
    String username;

    DisplayImageOptions options;
    ImageLoaderConfiguration imgconfig;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    RoundedImageView top_image;
    UserType userType;
    SharedPreferences sharedPreferences;
    AlertDialog.Builder alertDialog;


    public ProfileFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
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
        if(user.image != null){
            File file = new File(user.image);
            Uri imageUri  = Uri.fromFile(file);
            ImageLoader.getInstance().displayImage(imageUri.toString(), top_image, options, animateFirstListener);
        }
        top_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup_image_dialog();
            }
        });
        query = "select * from "+TABLE_USER_TYPE+" where user_id='"+user_id+"' order by id desc";
        userType = db.user_type(query);

        String[] menu_name = {
                "Title: "+user.title,
                "First Name: "+user.first_name,
                "Last Name: "+user.last_name,
                "Other Names: "+user.other_names,
                "Phone Number: "+user.phone,
                "Account Type: "+userType.user_type,
                "State: "+userType.state_name,
                "LGA: "+userType.lga_name,
                "District: "+userType.district_name,
                "Street Name: "+userType.street_name,
                "Building Name: "+userType.building_name,
                "Apartment Name: "+userType.apartment_name
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
                R.drawable.ic_edit_primary

        };

        ListView lv = (ListView) view.findViewById(R.id.listview);
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                }
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
        ContentValues contentValues = new ContentValues();
        contentValues.put("image",user.image);
        DatabaseHelper db = new DatabaseHelper(getActivity());
        db.do_edit(TABLE_USERS,contentValues,"user_id" ,user.user_id+"");
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


}
