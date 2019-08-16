package widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import Objects.Message;
import caller.com.testnav.ChatActivity;
import caller.com.testnav.R;
import dialogs.Dialogs;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

import static helper.Utils.decodeSampledBitmapFromUri;


public class Widgets {
    public static View chatWidget(Context context, Message message, int type){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;
        if(type == ChatActivity.OWNER){
            view = inflater.inflate(R.layout.chat_sent_widget, null);
        }
        if(type == ChatActivity.PEER){
            view = inflater.inflate(R.layout.chat_received_widget, null);
        }
        EmojiconTextView mMessage = (EmojiconTextView)view. findViewById(R.id.message);
        mMessage.setText(Html.fromHtml(message.message));
        mMessage.setUseSystemDefault(false);
        mMessage.setEmojiconSize(60);
        TextView mTime = (TextView)view.findViewById(R.id.time);
        mTime.setText(message.time);
        TextView mStatus = (TextView)view.findViewById(R.id.status);
        mStatus.setText(message.status);
        return view;
    }
    public static View chatImage(final Context context,final Message message, int type){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;
        if(type == ChatActivity.OWNER){
            view = inflater.inflate(R.layout.chat_sent_image, null);
        }
        if(type == ChatActivity.PEER){
            view = inflater.inflate(R.layout.chat_received_image, null);
        }
        ImageView mImage = (ImageView)view.findViewById(R.id.message);
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //    loadChatMediaFragment(context,message,  fragment_container, main_body);
             new Dialogs().chatImage(context, message);
            }
        });
        TextView mTime = (TextView)view.findViewById(R.id.time);
        String image_path = message.message;
        File file = new File(image_path);
        if(file.exists()){
            Bitmap photo = decodeSampledBitmapFromUri(image_path,200,200); // make image clear
            mImage.setImageBitmap(photo);
        }
        mTime.setText(message.time);
        TextView mStatus = (TextView)view.findViewById(R.id.status);
        mStatus.setText(message.status);
        return view;
    }
}
