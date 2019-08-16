package services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by user on 12/13/2018.
 */
public class RestartAlarm extends BroadcastReceiver {
    private AlarmManager alarm;
    @Override
    public void onReceive(Context context, Intent intent){
        alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intentcall = new Intent(context,BootReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentcall, 0);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, 0, SecurityService.SERVICE_RESTART_TIMEOUT,pendingIntent);

    }
}
