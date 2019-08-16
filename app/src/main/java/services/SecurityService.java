package services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import Objects.Data;
import Objects.EyeWitness;
import Objects.Message;
import Objects.User;
import caller.com.testnav.ChatActivity;
import caller.com.testnav.MapsActivity;
import caller.com.testnav.R;
import caller.com.testnav.ReadEyeWitnessActivity;
import database.DatabaseHelper;
import downloader.Downloader;
import downloader.GetUserCallback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import server.GetJSONCallback;
import server.ServerRequests;

import static Constants.Constants.ERROR;
import static Constants.Constants.MODE;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static Constants.Constants.STORAGE_FOLDER;
import static Constants.Constants.SUCCESS;
import static caller.com.testnav.ChatActivity.PENDING;
import static caller.com.testnav.ChatActivity.RECEIVED;
import static database.DatabaseHelper.TABLE_CHAT;
import static database.DatabaseHelper.TABLE_USERS;
import static helper.Utils.saveChat;
import static helper.Utils.saveEyeWitness;
import static notifications.notifications.notification_newer;
import static notifications.notifications.notification_older;
import static server.ServerRequests.SERVER_ROOT;


public class SecurityService extends Service {
    int count = 1;
    SharedPreferences sharedPreferences;
    User user;
    DatabaseHelper db;
    private boolean isRunning;
    public Context context;
    private Thread backgroundThread;

    public static final int SERVICE_TIMEOUT = 1000;//2500
    public static final int SERVICE_RESTART_TIMEOUT = 1000;//3000
    public SecurityService() {

    }

    MainServiceBinder mainServiceBinder = new MainServiceBinder();

    public class MainServiceBinder extends Binder {
        SecurityService getBinder(){
            return SecurityService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mainServiceBinder;
    }
    @Override
    public void onCreate() {
        this.context = this;
        db = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(SP_NAME, MODE);
        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"'";
        user = db.user_data(query);
        if(user == null){
            return;
        }
        this.isRunning= false;
        this.backgroundThread = new Thread(myTask);
    }
    private Runnable myTask = new Runnable() {

        @Override
        public void run() {
            // do something awesome
            try {

                init();
                count++;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            stopSelf();
        }
    };
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!this.isRunning){
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;//START_STICKY//START_NOT_STICKY
    }

    private void restartService(){
        Intent alarm = new Intent(this.context, BootReciever.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(this.context, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
        if(alarmRunning == false){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context,0,alarm,0);
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), SecurityService.SERVICE_TIMEOUT, pendingIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.isRunning = false;
        restartService();
    }
    private void init(){
        final ServerRequests serverRequests = new ServerRequests();
        checkPendingChat(serverRequests,user);
        pendingSentChatMessages(serverRequests,user);
        postPendingEmergency(serverRequests,user);
        postPendingEyeWitness(serverRequests,user);
    }

    private void checkPendingChat(ServerRequests serverRequests,final User user){
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("receiver_id",user.user_id+"")
                .addFormDataPart("receiver_phone",user.phone)
                .build();
        serverRequests.post(request_body, "chat_notification.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        if(status.equalsIgnoreCase(ERROR)){
                            return;
                        }
                        String title = getString(R.string.app_name);
                        JSONArray new_messages = returnedJSON.getJSONArray("new_message");
                        if(new_messages != null){
                            if(new_messages.length() > 0){
                                for(int i = 0;i<new_messages.length();i++){
                                    JSONObject obj = new_messages.getJSONObject(i);
                                    String msg = obj.getString("message");
                                    String time = obj.getString("time");
                                    String timestamp = obj.getString("timestamp");
                                    String type = obj.getString("type");
                                    //here, the sender is changed to the receiver
                                    String receiver_id = obj.getString("sender_id");
                                    title = obj.getString("fullname");
                                    final Message peer_message = new Message();
                                    peer_message.setMessage(msg);
                                    peer_message.setTime(time);
                                    peer_message.setStatus(RECEIVED);
                                    peer_message.setTimestamp(timestamp);
                                    peer_message.setType(type);
                                    peer_message.setSender_id(user.user_id);
                                    peer_message.setReceiver_id(Integer.parseInt(receiver_id));
                                    /*ContentValues contentValues = new ContentValues();
                                    contentValues.put("message",peer_message.message);
                                    contentValues.put("time",peer_message.time);
                                    contentValues.put("timestamp",peer_message.timestamp);
                                    contentValues.put("status",ChatActivity.RECEIVED);
                                    contentValues.put("type",peer_message.type);
                                    contentValues.put("sender_id",user.user_id);
                                    contentValues.put("receiver_id",receiver_id);*/
                                    boolean isFile = false;
                                    if(saveChat(context,user,peer_message,Integer.parseInt(receiver_id))){
                                        /*Log.e("MESSAGE",peer_message.message);
                                        Log.e("TIME",peer_message.time);
                                        Log.e("TIMESTAMP",peer_message.timestamp);
                                        Log.e("TYPE",peer_message.type);
                                        Log.e("RECEIVER_ID",peer_message.receiver_id+"");
                                        Log.e("SENDER_ID",peer_message.sender_id+"");
                                        Log.e("STATUS",peer_message.status+"");*/
                                        switch (type){
                                            case "image/png":case "image/jpeg":case "jpg":case "image/jpg":
                                                isFile = true;
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
                                                            //i have saved this message already above but later changed the path
                                                            //i need to edit the saved message
                                                            ContentValues contentValues = new ContentValues();
                                                            contentValues.put("message",file_path);
                                                            db.do_edit(TABLE_CHAT,contentValues,"timestamp",peer_message.timestamp);
                                                        }
                                                    }
                                                }).execute();
                                                break;
                                        }
                                        if(isFile){
                                            msg = "A picture was sent to you.";
                                        }
                                        Bundle bundle = new Bundle();
                                        bundle.putString("user_id", obj.getString("sender_id"));
                                        bundle.putString("fullname", title);
                                        bundle.putString("thumbnail", obj.getString("image"));
                                        bundle.putString("phone", obj.getString("phone"));
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            notification_newer(ChatActivity.class,SecurityService.this,title, Html.fromHtml(msg)+"",null,count,bundle);
                                        }else{
                                            notification_older(ChatActivity.class,SecurityService.this,title, Html.fromHtml(msg)+"",null,count,bundle);
                                        }
                                    }
                                }

                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        });
    }
    private void pendingSentChatMessages(ServerRequests serverRequests, final  User user){
        String query = "select * from "+TABLE_CHAT+" where status='"+PENDING+"' and sender_id='"+user.user_id+"'";
        List<Message> chats = db.chatList(query);
        if(chats != null){
            for(int i =0;i<chats.size();i++){
                Message message = chats.get(i);
                Log.e("Pending Message","Message:"+message.message);
                postPendingChat(serverRequests,user,message);
            }
        }
    }
    private void postPendingEmergency(ServerRequests serverRequests,final User user){
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .build();
        serverRequests.post(request_body, "get_emergency.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            String title = "EMERGENCY";
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                notification_newer(MapsActivity.class,SecurityService.this,title, message,null,count,null);
                            }else{
                                notification_older(MapsActivity.class,SecurityService.this,title, message,null,count,null);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private void postPendingChat(ServerRequests serverRequests,final User user, final Message message){
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("receiver_id",message.receiver_id+"")
                .addFormDataPart("sender_phone",user.phone)
                .addFormDataPart("receiver_phone",message.receiver_phone)
                .addFormDataPart("message",message.message)
                .addFormDataPart("status",message.status)
                .addFormDataPart("time",message.time)
                .addFormDataPart("timestamp",message.timestamp)
                .build();
        serverRequests.post(request_body, "chat.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                if(returnedJSON != null){
                    try{
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
                                    peer_message.setReceiver_id(message.receiver_id);
                                    peer_message.setReceiver_phone(message.receiver_phone);
                                    if(saveChat(context,user,peer_message,message.receiver_id)){
                                      Log.e("***PENDING CHAT**","SAVED");
                                    }
                                }
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private void postPendingEyeWitness(ServerRequests serverRequests,final User user){
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .build();
        serverRequests.post(request_body, "get_eye_witness.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            String title = "Eye Witness Report";
                            message = Html.fromHtml(message)+"";
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                notification_newer(ReadEyeWitnessActivity.class,SecurityService.this,title, message,null,count,null);
                            }else{
                                notification_older(ReadEyeWitnessActivity.class,SecurityService.this,title, message,null,count,null);
                            }
                            EyeWitness eyeWitness = new EyeWitness();
                            JSONObject notification_object = returnedJSON.getJSONObject("notification");
                            JSONObject eye_witness_object = returnedJSON.getJSONObject("eye_witness");
                            JSONObject user_object = returnedJSON.getJSONObject("user");
                            eyeWitness.setId(eye_witness_object.getInt("id"));
                            eyeWitness.setEye_witness_id(eye_witness_object.getInt("id"));
                            eyeWitness.setSender_id(notification_object.getInt("sender_id"));
                            eyeWitness.setReceiver_id(notification_object.getInt("receiver_id"));
                            eyeWitness.setMessage(eye_witness_object.getString("message"));
                            eyeWitness.setTimestamp(eye_witness_object.getString("timestamp"));
                            eyeWitness.setDate(eye_witness_object.getString("date"));
                            eyeWitness.setFile_name(eye_witness_object.getString("file_name"));
                            eyeWitness.setFile_type(eye_witness_object.getString("file_type"));
                            eyeWitness.setSend_type(eye_witness_object.getInt("send_type"));
                            eyeWitness.setSender_title(user_object.getString("title"));
                            eyeWitness.setSender_last_name(user_object.getString("last_name"));
                            eyeWitness.setSender_first_name(user_object.getString("first_name"));
                            eyeWitness.setSender_other_names(user_object.getString("other_names"));
                            eyeWitness.setSender_image(user_object.getString("image"));
                            eyeWitness.setSender_phone(user_object.getString("phone"));
                            saveEyeWitness(context,eyeWitness);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
