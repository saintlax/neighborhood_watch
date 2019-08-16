package caller.com.testnav;
import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Objects.MyLocation;
import Objects.User;
import adapters.HomeMenuAdapter;
import database.DatabaseHelper;
import dialogs.Post;
import services.BootReciever;
import services.LocationBootReciever;
import services.LocationService;
import services.SecurityService;

import static Constants.Constants.MODE;
import static Constants.Constants.PENDING;
import static Constants.Constants.SP_ACTIVE_EMERGENCY;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static database.DatabaseHelper.TABLE_USERS;
import static helper.Utils.getTimestamp;

public class LocationActivity extends AppCompatActivity implements LocationListener,GridView.OnItemClickListener{
    private TextView latitudePosition;
    private TextView longitudePosition;
    private TextView mMessage;
    private TextView currentCity;
    private LocationManager locationManager;
    private Location location;
    private final int REQUEST_LOCATION = 200;
    private static final String TAG = "LocationActivity";
    Context context;
    User user;
    DatabaseHelper db;
    SharedPreferences sharedPreferences;
    List<String> menu_name;
    List<Integer> menu_icon;
    String eventType="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        context = LocationActivity.this;
        sharedPreferences = getSharedPreferences(SP_NAME, MODE);
        db = new DatabaseHelper(this);
        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"' order by id desc";
        user = db.user_data(query);
        if(user == null){
            startActivity(new Intent(context,LoginActivity.class));
            finish();
            return;
        }
        latitudePosition = (TextView) findViewById(R.id.latitude);
        longitudePosition = (TextView) findViewById(R.id.longitude);
        currentCity = (TextView) findViewById(R.id.city);
        mMessage = (TextView) findViewById(R.id.message);
        mMessage.setTextColor(Color.BLUE);
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LocationActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 2, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (location != null) {
                latitudePosition.setText(String.valueOf(location.getLatitude()));
                longitudePosition.setText(String.valueOf(location.getLongitude()));
                MyLocation myLocation = new MyLocation();
                myLocation.setLatitude(location.getLatitude());
                myLocation.setLongitude(location.getLongitude());
                myLocation.setEvent(eventType);
                myLocation.setDate(getTimestamp());
                myLocation.setStatus(PENDING);
            //    if(!eventType.equals("")){
                    new Post().post_location(context,user, location,eventType,mMessage);
            //    }
                getAddressFromLocation(location, getApplicationContext(), new GeoCoderHandler());
            }
        } else {
            showGPSDisabledAlertToUser();
        }
        menu_icon = new ArrayList<Integer>();
        menu_name = new ArrayList<String>();





        menu_icon.add(R.drawable.ic_local_shipping_black);
        menu_name.add("Needs an Ambulance");

        menu_icon.add(R.drawable.security_logo);
        menu_name.add("Robbery");

        menu_icon.add(R.drawable.security_logo);
        menu_name.add("Kidnapping");


        menu_icon.add(R.drawable.security_logo);
        menu_name.add("Boko Haram");

        menu_icon.add(R.drawable.security_logo);
        menu_name.add("Terrorism");

        menu_icon.add(R.drawable.security_logo);
        menu_name.add("Arson");

        menu_icon.add(R.drawable.security_logo);
        menu_name.add("Riot");

        menu_icon.add(R.drawable.security_logo);
        menu_name.add("Cult Fight");


        menu_icon.add(R.drawable.security_logo);
        menu_name.add("Two fighting");


        GridView gridview = (GridView)findViewById(R.id.gridView);
        HomeMenuAdapter adapter = new HomeMenuAdapter(context,menu_name,menu_icon);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(this);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(SP_ACTIVE_EMERGENCY, SP_ACTIVE_EMERGENCY);
        setResult(MainActivity.RESULT_CODE_TRACKING, intent);
        finish();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.background);
        linearLayout.setBackgroundColor(getResources().getColor(R.color.gray_light));
        eventType = menu_name.get(i);


        //save event to sp and start LocationService
        SharedPreferences.Editor userLocalDatabaseEditor = sharedPreferences.edit();
        userLocalDatabaseEditor.putString(SP_ACTIVE_EMERGENCY,eventType);
        userLocalDatabaseEditor.commit();

        //start service
        Intent alarm = new Intent(this.context, LocationBootReciever.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(this.context, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
        if(alarmRunning == false){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context,0,alarm,0);
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), LocationService.SERVICE_TIMEOUT, pendingIntent);
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        latitudePosition.setText(String.valueOf(location.getLatitude()));
        longitudePosition.setText(String.valueOf(location.getLongitude()));
        MyLocation myLocation = new MyLocation();
        myLocation.setLatitude(location.getLatitude());
        myLocation.setLongitude(location.getLongitude());
        myLocation.setEvent(eventType);
        myLocation.setDate(getTimestamp());
        myLocation.setStatus(PENDING);
    //    if(!eventType.equals("")){
            new Post().post_location(context,user, location,eventType,mMessage);
    //    }
        getAddressFromLocation(location, getApplicationContext(), new GeoCoderHandler());
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledAlertToUser();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    public static void getAddressFromLocation(final Location location, final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> list = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1);
                    if (list != null && list.size() > 0) {
                        Address address = list.get(0);
                        // sending back first address line and locality
                        result = address.getAddressLine(0) + ", " + address.getLocality() + ", " +  address.getCountryName() ;
                    }
                    Log.e("******RESULT******","Result: "+result);
                } catch (IOException e) {
                    Log.e(TAG, "Impossible to connect to Geocoder", e);
                } finally {
                    Message msg = Message.obtain();
                    msg.setTarget(handler);
                    if (result != null) {
                        msg.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        msg.setData(bundle);
                    } else
                        msg.what = 0;
                    msg.sendToTarget();
                }
            }
        };
        thread.start();
    }
    private class GeoCoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String result;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    result = bundle.getString("address");
                    break;
                default:
                    result = null;
            }
            currentCity.setText(result);
        }
    }
}


