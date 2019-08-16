package services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by user on 12/13/2018.
 */
public class LocationBootReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Intent background = new Intent(context,LocationService.class);
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
