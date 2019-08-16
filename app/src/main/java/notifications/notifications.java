package notifications;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import caller.com.testnav.MainActivity;
import caller.com.testnav.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by user on 12/14/2018.
 */

public class notifications {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void notification_older( final Class<? extends Activity> ActivityToOpen,Context context, String title, String message, Bitmap bitmap, int count,Bundle bundle){
        Intent intent = new Intent(context,ActivityToOpen);
        if(bundle != null){
            intent.putExtras(bundle);
        }
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(123, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);
        nBuilder.setTicker(title);
        nBuilder.setContentTitle(title);
        nBuilder.setContentText(message);
        nBuilder.setSmallIcon(R.mipmap.ic_primaryy_logo);//@mipmap/ic_primaryy_logo
        nBuilder.setLargeIcon(bitmap);
        nBuilder.setDefaults(Notification.DEFAULT_SOUND);
        /*String vibration = sharedPreferences.getString("vibration","ON");
        if(vibration.equals("ON")){
            nBuilder.setVibrate(new long[]{50, 500, 250, 500});
        }*/
        nBuilder.setLights(Color.BLUE, 400, 400);
        nBuilder.setContentIntent(pendingIntent);
        Notification notification = nBuilder.build();
        NotificationManager nm = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);

        nm.notify(count, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void notification_newer( final Class<? extends Activity> ActivityToOpen,Context context,String title, String message,Bitmap bitmap,int count,Bundle bundle){
        Intent intent = new Intent(context,ActivityToOpen);
        if(bundle != null){
            intent.putExtras(bundle);
        }
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(123, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager mNotificationManager = (NotificationManager)context. getSystemService(NOTIFICATION_SERVICE);
        String id = "my_channel_05";
        CharSequence name = context.getString(R.string.channel_name);
        String description = context.getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(id, name,importance);
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.RED);
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

        mNotificationManager.createNotificationChannel(mChannel);

        mNotificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        String CHANNEL_ID = "my_channel_05";
        Notification notification = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_primaryy_logo)
                .setLargeIcon(bitmap)
                .setChannelId(CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .build();
        mNotificationManager.notify(count, notification);
    }

}
