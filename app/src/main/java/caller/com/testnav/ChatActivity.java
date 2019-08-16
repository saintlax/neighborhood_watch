package caller.com.testnav;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Objects.Data;
import Objects.Message;
import Objects.User;
import adapters.CustomPopupAdapter;
import database.DatabaseHelper;
import downloader.Downloader;
import downloader.GetUserCallback;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import helper.AnimateFirstDisplayListener;
import helper.RoundedImageView;
import helper.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import server.GetJSONCallback;
import server.ServerRequests;
import widgets.Widgets;

import static Constants.Constants.CAMERA_REQUEST;
import static Constants.Constants.MODE;
import static Constants.Constants.RESULT_LOAD_IMAGE;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static Constants.Constants.STORAGE_FOLDER;
import static Constants.Constants.SUCCESS;
import static database.DatabaseHelper.TABLE_CHAT;
import static database.DatabaseHelper.TABLE_USERS;
import static helper.Utils.getMimeTtype;
import static helper.Utils.saveChat;
import static helper.Utils.savePhotoToSDcard;
import static server.ServerRequests.SERVER_ROOT;


public class ChatActivity extends AppCompatActivity {

    EmojiconEditText mTxt;
    EmojiconTextView textView;
    ImageView emojiImageView;
    ImageView submitButton;
    View rootView;
    EmojIconActions emojIcon;

    public static String PENDING = "Pending";
    public static String SENT = "Sent";
    public static String RECEIVED = "Received";
    public static int OWNER = 0;
    public static int PEER = 1;

    Button mSendBtn,mCam_btn;
    //  EditText mTxt;
    Button mRecord_icon,mCameraBtn;
    LinearLayout mBtnSendLayout;
    RelativeLayout main_body;
    FrameLayout fragment_container;
    LinearLayout body;
    SharedPreferences sharedPreferences;
    DatabaseHelper db;
    User user;
    TextView mTimer;
    ScrollView mScrollView;
    public static final int TIMEOUT = 1500;
    private static Handler handler;
    ServerRequests serverRequests;
    String receiver_id = "0";
    String receiver_name = "Chika Anthony";
    String receiver_thumbnail = "";
    String receiver_phone = "";
    View title_view;
    Toolbar toolbar;
    TextView chatterName,chatterTyping,remainingMsg;
    DisplayImageOptions options;
    ImageLoaderConfiguration imgconfig;
    Context context;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        context = ChatActivity.this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LayoutInflater inflater = LayoutInflater.from(this);
        title_view = inflater.inflate(R.layout.custom_title_bar,null);
        toolbar.addView(title_view);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        receiver_id = bundle.getString("user_id");
        receiver_name = bundle.getString("fullname");
        receiver_thumbnail = bundle.getString("thumbnail");
        receiver_phone = bundle.getString("phone");
        chatterName = (TextView)title_view.findViewById(R.id.head_title);
        chatterName.setText(receiver_name);
        chatterTyping = (TextView)title_view.findViewById(R.id.head_detail);

        File cacheDir = StorageUtils.getCacheDirectory(this);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_user_white)
                .showImageForEmptyUri(R.drawable.ic_user_white)
                .showImageOnFail(R.drawable.ic_user_white)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.NONE)
                .build();

        imgconfig = new ImageLoaderConfiguration.Builder(this)
                .build();
        ImageLoader.getInstance().init(imgconfig);
        RoundedImageView top_image = (RoundedImageView)title_view.findViewById(R.id.head_image);
        try{
            if(!receiver_thumbnail.equalsIgnoreCase("NULL") && !receiver_thumbnail.equalsIgnoreCase(""))
                ImageLoader.getInstance().displayImage(SERVER_ROOT.replace("/android","") + receiver_thumbnail, top_image, options, animateFirstListener);
        }catch (Exception e){
            e.printStackTrace();
        }

        db = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(SP_NAME, MODE);
        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"'";
        user = db.user_data(query);

        rootView = findViewById(R.id.root_view);
        emojiImageView = (ImageView) findViewById(R.id.emoji_btn);
        mTxt = (EmojiconEditText) findViewById(R.id.edittext_msg);


        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (filterLongEnough()) {
                    if(mTxt.getText().toString().trim().length() > 0){
                        postTyping("typing");
                    }
                }
            }

            private boolean filterLongEnough() {
                return mTxt.getText().toString().trim().length() > 0;
            }
        };
        mTxt.addTextChangedListener(fieldValidatorTextWatcher);


        emojIcon = new EmojIconActions(this, rootView, mTxt, emojiImageView);
        emojIcon.ShowEmojIcon();

        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                scrollDown();
            }

            @Override
            public void onKeyboardClose() {

                //   Log.e(TAG, "Keyboard closed");
            }
        });

        body = (LinearLayout) findViewById(R.id.chatBody);
        main_body = (RelativeLayout) findViewById(R.id.main_body);
        fragment_container = (FrameLayout) findViewById(R.id.fragment_container);
        mBtnSendLayout = (LinearLayout)findViewById(R.id.btnSendLayout);
        mTimer = (TextView)findViewById(R.id.timer);
        mRecord_icon = (Button) findViewById(R.id.recording_icon);
        mSendBtn = (Button)findViewById(R.id.btnSend);
        mCam_btn = (Button)findViewById(R.id.cam_btn);
        mCam_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose_picture_dialog(ChatActivity.this);
            }
        });
        mScrollView = (ScrollView)findViewById(R.id.scrollView);
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = mScrollView.getScrollY();
                if(scrollY == 0){
                    previousChat();
                }

                if(scrollY == scrollY/2){
                    //       previousChat();
                }

                /*long half_height = scrollView.getHeight()/2;
                long quarter_height = scrollView.getHeight()/3;
                long half_y = scrollY/2;
                if(half_y > quarter_height && half_height >= half_y){
                    Toast.makeText(ChatActivity.this, "Height: "+half_height+" Y: "+scrollY, Toast.LENGTH_SHORT).show();
                }*/
                /*for(int j =10;j<100;j++){
                    if(scrollY == j){
                        Toast.makeText(ChatActivity.this, ""+j, Toast.LENGTH_SHORT).show();
                        break;
                    }//this won't work because the loop keeps updating
                }*/
            }
        });
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(mTxt.getText().toString())){
                    return;
                }
                Message message = new Message();
                // message.setId(1);
                message.setMessage(mTxt.getText().toString());
                message.setStatus(PENDING);
                message.setTime(Utils.getTime());
                message.setType("TXT");
                message.setReceiver_id(Integer.parseInt(receiver_id));
                message.setSender_id(user.user_id);
                message.setTimestamp(Utils.getTimestamp());
                if(!receiver_id.equalsIgnoreCase(user.user_id+"")){
                    if(saveChat(ChatActivity.this,user,message,Integer.parseInt(receiver_id))){
                        View view2 = Widgets.chatWidget(ChatActivity.this,message,OWNER);
                        body.addView(view2);
                        messageViewHashtable.put(message,view2);
                        scrollDown();
                        mTxt.requestFocus();
                        mTxt.setText("");
                    }
                }else{
                    Utils.popup(context,"Chat Permission","You cannot reply yourself!!");
                }

            }
        });
        loadChat();
        createInterval();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    private void scrollDown(){
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(mScrollView.FOCUS_DOWN);
            }
        });
    }
    private void postTyping(String state){
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("sender_id",user.user_id+"")
                .addFormDataPart("receiver_id",receiver_id)
                .addFormDataPart("sender_phone",user.phone)
                .addFormDataPart("receiver_phone",receiver_phone)
                .addFormDataPart("state",state)
                .build();
        serverRequests.post(request_body, "chat_typing.php", new GetJSONCallback() {
            @Override
            public void done(final JSONObject returnedJSON) {
                if(returnedJSON !=null){
                    try{
                        String data = returnedJSON.getString("data");
                        String last_seen = returnedJSON.getString("last_seen");
                        chatterTyping.setText(data);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        });
    }
    List<Message> message_list,seen_message_list;
    int last_message_id = 0;
    private void previousChat(){
        String query = "select * from "+TABLE_CHAT+" where receiver_id='"+receiver_id+"' and sender_id='"+user.user_id+"' and id<"+last_message_id+" order by id desc limit 2";
        List<Message> chats = db.chatList(query);
        if(chats != null){
            for(int i=0;i<chats.size();i++){
                Message message = chats.get(i);
                for(int k=0;k<seen_message_list.size();k++){
                    Message seen_message = seen_message_list.get(k);
                    if(!seen_message.equals(message)){
                        seen_message_list.add(message);
                        int id = message.id;

                        String msg = message.message;
                        String type = message.type;
                        String time = message.time;
                        String status = message.status;
                        String timestamp = message.timestamp;
                        if(status.equalsIgnoreCase(RECEIVED)){
                            switch (type){
                                case "image/png":case "image/jpeg":case "jpg":case "image/jpg":
                                    body.addView(Widgets.chatImage(ChatActivity.this,message,PEER),0);
                                    break;
                                default:
                                    body.addView(Widgets.chatWidget(ChatActivity.this,message,PEER),0);
                                    break;
                            }
                        }else{
                            switch (type){
                                case "image/png":case "image/jpeg":case "jpg":case "image/jpg":
                                    body.addView(Widgets.chatImage(ChatActivity.this,message,OWNER),0);
                                    break;
                                default:
                                    body.addView(Widgets.chatWidget(ChatActivity.this,message,OWNER),0);
                                    break;
                            }
                        }
                        last_message_id = id;
                        break;
                    }
                    break;
                }

            }
        }
    }
    Hashtable<Message,View> messageViewHashtable;
    private void loadChat(){
        messageViewHashtable = new Hashtable<>();
        message_list = new ArrayList<>();
        seen_message_list = new ArrayList<>();
        String query = "select * from "+TABLE_CHAT+" where receiver_id='"+receiver_id+"' and sender_id='"+user.user_id+"' order by id desc limit 7";
        List<Message> chats = db.chatList(query);
        if(chats != null){
            for(int i =0;i<chats.size();i++){
                Message msg = chats.get(i);
                message_list.add(msg);
                last_message_id = msg.id;
                seen_message_list.add(msg);
            }
            Collections.reverse(message_list);
            for(int i=0;i<message_list.size();i++){
                Message message = message_list.get(i);
                int id = message.id;
                String msg = message.message;
                String type = message.type;
                String time = message.time;
                String status = message.status;
                String timestamp = message.timestamp;
                if(status.equalsIgnoreCase(RECEIVED)){
                    switch (type){
                        case "image/png":case "image/jpeg":case "jpg":case "image/jpg":
                            body.addView(Widgets.chatImage(ChatActivity.this,message,PEER));
                            break;
                        default:
                            body.addView(Widgets.chatWidget(ChatActivity.this,message,PEER));
                            break;
                    }
                }else{
                    switch (type){
                        case "image/png":case "image/jpeg":case "jpg":case "image/jpg":
                            View view = Widgets.chatImage(ChatActivity.this,message,OWNER);
                            body.addView(view);
                            messageViewHashtable.put(message,view);
                            break;
                        default:
                            View view1 = Widgets.chatWidget(ChatActivity.this,message,OWNER);
                            body.addView(view1);
                            messageViewHashtable.put(message,view1);
                            break;
                    }

                }
            }
            scrollDown();
        }
    }
    private void createInterval(){
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String query = "select * from "+TABLE_CHAT+" where status='"+PENDING+"' and receiver_id='"+receiver_id+"' and sender_id='"+user.user_id+"'";
                List<Message> chats = db.chatList(query);
                if(chats != null){
                    for(int i =0;i<chats.size();i++){
                        Message message = chats.get(i);
                        Log.e("Pending Message","Message:"+message.message);
                        postChat(user,message);
                    }
                }else{
                    postChat(user,null);
                }
                postTyping("waiting");
                handler.postDelayed(this, TIMEOUT);
            }
        }, TIMEOUT);
    }

    private void postChat(User user, final Message message){
        serverRequests = new ServerRequests();
        RequestBody request_body = null;
        if(message == null){
            request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("user_id",user.user_id+"")
                    .addFormDataPart("receiver_id",receiver_id)
                    .addFormDataPart("sender_phone",user.phone)
                    .addFormDataPart("receiver_phone",receiver_phone)
                    .build();
        }else{
            request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("user_id",user.user_id+"")
                    .addFormDataPart("receiver_id",receiver_id)
                    .addFormDataPart("sender_phone",user.phone)
                    .addFormDataPart("receiver_phone",receiver_phone)
                    .addFormDataPart("message",message.message)
                    .addFormDataPart("status",message.status)
                    .addFormDataPart("time",message.time)
                    .addFormDataPart("timestamp",message.timestamp)
                    .build();
        }
        serverRequests.post(request_body, "chat.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                if(returnedJSON != null){
                    processResponse(returnedJSON,message);
                }
            }
        });
    }

    private void processResponse(JSONObject returnedJSON,Message message){
        try {
            String status = returnedJSON.getString("status");
            if(status.equalsIgnoreCase("ERROR")){
                Utils.popup(ChatActivity.this,"Error",returnedJSON.getString("message"));
                endChat();
                return;
            }

            JSONArray new_messages = returnedJSON.getJSONArray("new_message");
            if(new_messages != null){
                if(new_messages.length() > 0){
                    for(int i = 0;i<new_messages.length();i++){
                        JSONObject obj = new_messages.getJSONObject(i);
                        String msg = obj.getString("message");
                        String time = obj.getString("time");
                        String timestamp = obj.getString("timestamp");
                        String type = obj.getString("type");
                        final Message peer_message = new Message();
                        peer_message.setMessage(msg);
                        peer_message.setTime(time);
                        peer_message.setStatus(RECEIVED);
                        peer_message.setTimestamp(timestamp);
                        peer_message.setType(type);
                        peer_message.setSender_id(user.user_id);
                        peer_message.setReceiver_id(Integer.parseInt(receiver_id));
                        peer_message.setReceiver_phone(receiver_phone);
                        if(saveChat(ChatActivity.this,user,peer_message,Integer.parseInt(receiver_id))){
                            switch (type){
                                case "image/png":case "image/jpeg":case "jpg":case "image/jpg":
                                    //download the file firs and change the path to folder path
                                    String filename = "";
                                    String[] img_array = peer_message.message.split("/");
                                    if(img_array.length > 1){
                                        filename = img_array[2];
                                    }
                                    String url = SERVER_ROOT.replace("/android","")+"images/chat/";
                                    final String finalFilename = filename;
                                    new Downloader(filename, url, new GetUserCallback() {
                                        @Override
                                        public void done(Data returnedData) {
                                            if(returnedData != null){
                                                String root = Environment.getExternalStorageDirectory().toString()+"/"+STORAGE_FOLDER;
                                                String file_path = root +"/"+ finalFilename;
                                                Message new_message = peer_message;
                                                new_message.setMessage(file_path);
                                                body.addView(Widgets.chatImage(ChatActivity.this,new_message,PEER));
                                                scrollDown();
                                                sound();
                                                //i have saved this message already above but later changed the path
                                                //i need to edit the saved message
                                                ContentValues contentValues = new ContentValues();
                                                contentValues.put("message",file_path);
                                                db.do_edit(TABLE_CHAT,contentValues,"timestamp",peer_message.timestamp);
                                            }
                                        }
                                    }).execute();
                                    break;
                                default:
                                    body.addView(Widgets.chatWidget(ChatActivity.this,peer_message,PEER));
                                    break;
                            }

                        }

                    }
                    scrollDown();
                    sound();
                }
            }
            if(status.equalsIgnoreCase("LISTENING")){
                return;
            }

            JSONObject object = returnedJSON.getJSONObject("data");
            String timestamp = object.getString("timestamp");
            if(timestamp.equalsIgnoreCase(message.timestamp)){
                Log.e("UPDATING CHAT","uPDATEING.....");
                ContentValues contentValues = new ContentValues();
                contentValues.put("status",SENT);
                db.do_edit(TABLE_CHAT,contentValues,"timestamp",message.timestamp);

                Set<Map.Entry<Message, View>> entrySet = messageViewHashtable.entrySet();
                Iterator<Map.Entry<Message, View>> iterator = entrySet.iterator();
                while(iterator.hasNext()){
                    Map.Entry<Message, View> entry2 = iterator.next();
                    Message m = entry2.getKey();
                    View v = entry2.getValue();
                    if(message.timestamp.equalsIgnoreCase(m.timestamp)){
                        TextView textView = (TextView)v.findViewById(R.id.status);
                        textView.setText("SENT");
                    }
                }
            }

            if(status.equalsIgnoreCase("SENT")){
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void endChat(){
        handler.removeCallbacksAndMessages(null);
    }
    @Override
    protected void onDestroy() {
        endChat();
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        endChat();
        super.onBackPressed();
    }


    public void choose_picture_dialog(Context context){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_menu_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Send Picture");
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        String[] menu_name = {
                "Take a picture from camera",
                "Choose from gallery"
        };
        int[]  menu_icons = {
                R.drawable.ic_camera_black,
                R.drawable.ic_attach_black
        };
        CustomPopupAdapter adapter = new CustomPopupAdapter(context,menu_name,menu_icons);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ad.dismiss();
                choose_picture_menu_actions(position);

            }
        });
    }

    private void choose_picture_menu_actions(int position){
        Intent i;
        switch (position){
            case 0:
                i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i,CAMERA_REQUEST);
                break;
            case 1:
                i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
        }
    }

    private void postPicture(final String image){
        ServerRequests serverRequests = new ServerRequests();
        File f = new File(image);
        String content_type = getMimeTtype(f.getPath());
        String file_path = f.getAbsolutePath();
        RequestBody file_body = RequestBody.create(MediaType.parse(content_type),f);
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("sender_id",user.user_id+"")
                .addFormDataPart("sender_phone",user.phone)
                .addFormDataPart("receiver_phone",receiver_phone)
                .addFormDataPart("receiver_id",receiver_id)
                .addFormDataPart("status",PENDING)
                .addFormDataPart("time",Utils.getTime())
                .addFormDataPart("type",content_type)
                .addFormDataPart("timestamp",Utils.getTimestamp())
                .addFormDataPart("uploaded_file",file_path.substring(file_path.lastIndexOf("/")+1),file_body)
                .build();
        serverRequests.post(request_body, "upload_chat_file.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                if(returnedJSON != null){
                    try{
                        if(returnedJSON.getString("status").equals(SUCCESS)){
                            JSONObject object = returnedJSON.getJSONObject("data");
                            Message message = new Message();
                            message.setMessage(image);
                            message.setTime(object.getString("time"));
                            message.setTimestamp(object.getString("timestamp"));
                            message.setStatus(SENT);
                            message.setReceiver_id(Integer.parseInt(object.getString("receiver_id")));
                            message.setSender_id(Integer.parseInt(object.getString("sender_id")));
                            message.setType(object.getString("type"));
                            if(saveChat(ChatActivity.this,user,message,Integer.parseInt(receiver_id))){
                                body.addView(Widgets.chatImage(ChatActivity.this,message,OWNER));
                                scrollDown();
                            }

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    Utils.popup(ChatActivity.this,"Error",getString(R.string.error_no_response));
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            postPicture(picturePath);
        }

        switch (requestCode){
            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK && data !=null){
                    Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                    String image_path = savePhotoToSDcard(bitmap);
                    if(!image_path.equals("")){
                        postPicture(image_path);
                    }
                }
                break;


        }
    }
    MediaPlayer mediaSong;
    private void sound(){
        mediaSong = MediaPlayer.create(this, R.raw.blop);
        mediaSong.start();
    }
}
