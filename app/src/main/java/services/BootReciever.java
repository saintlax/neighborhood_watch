package services;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

/**
 * Created by user on 12/13/2018.
 */
public class BootReciever extends BroadcastReceiver {

    PowerManager pawerManager;
    public static PowerManager.WakeLock wakeLock=null;
    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        pawerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pawerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wakeLock.acquire();
        Intent background = new Intent(context,SecurityService.class);
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(background);
            else
                context.startService(background);
        }catch (Exception e){
            try{
                context.startService(background);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

}
