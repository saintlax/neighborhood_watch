package widgets;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import Objects.Message;
import Objects.User;
import caller.com.testnav.ChatActivity;
import database.DatabaseHelper;

import static caller.com.testnav.ChatActivity.OWNER;
import static caller.com.testnav.ChatActivity.PEER;
import static caller.com.testnav.ChatActivity.RECEIVED;
import static database.DatabaseHelper.TABLE_CHAT;

public class LoadChat extends Thread{


    /*The usage is here
    *
    *
    * new LoadChat(context,user,receiver_id,db,body,messageViewHashtable,  new MessageViewHashtableCallback(){
            @Override
            public void complete(Hashtable<Message, View> hashtable) {

            }
        }).start();
    *
    * */



    User user;
    DatabaseHelper db;
    MessageViewHashtableCallback messageViewHashtableCallback;
    LinearLayout body;
    String receiver_id;
    Hashtable<Message, View> messageViewHashtable;
    Context context;

    public LoadChat(Context context,User user,String receiver_id,DatabaseHelper databaseHelper,LinearLayout body,Hashtable<Message, View> messageViewHashtable,MessageViewHashtableCallback messageViewHashtableCallback){
        this.user = user;
        this.db = databaseHelper;
        this.body = body;
        this.receiver_id = receiver_id;
        this.messageViewHashtableCallback = messageViewHashtableCallback;
        this.messageViewHashtable = messageViewHashtable;
        this.context = context;
    }
    List<Message> message_list,seen_message_list;
    @Override
    public void run() {
        messageViewHashtable = new Hashtable<>();
        message_list = new ArrayList<>();
        seen_message_list = new ArrayList<>();
        String query = "select * from "+TABLE_CHAT+" where receiver_id='"+receiver_id+"' and sender_id='"+user.user_id+"' order by id desc limit 7";
        List<Message> chats = db.chatList(query);
        if(chats != null){
            for(int i =0;i<chats.size();i++){
                Message msg = chats.get(i);
                message_list.add(msg);
              //  last_message_id = msg.id;
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
                            body.addView(Widgets.chatImage(context,message,PEER));
                            break;
                        default:
                            body.addView(Widgets.chatWidget(context,message,PEER));
                            break;
                    }
                }else{
                    switch (type){
                        case "image/png":case "image/jpeg":case "jpg":case "image/jpg":
                            View view = Widgets.chatImage(context,message,OWNER);
                            body.addView(view);
                            messageViewHashtable.put(message,view);
                            break;
                        default:
                            View view1 = Widgets.chatWidget(context,message,OWNER);
                            body.addView(view1);
                            messageViewHashtable.put(message,view1);
                            break;
                    }

                }
            }
          //  scrollDown();
            messageViewHashtableCallback.complete(messageViewHashtable);
        }
    }
}
