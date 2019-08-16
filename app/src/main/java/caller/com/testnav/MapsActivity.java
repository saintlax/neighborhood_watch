package caller.com.testnav;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Objects.MyLocation;
import Objects.MyMarker;
import Objects.User;
import database.DatabaseHelper;
import dialogs.Post;
import helper.AnimateFirstDisplayListener;
import helper.Utils;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import server.GetJSONCallback;
import server.ServerRequests;

import static Constants.Constants.MODE;
import static Constants.Constants.PENDING;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static Constants.Constants.SUCCESS;
import static database.DatabaseHelper.TABLE_USERS;
import static helper.Utils.getTimestamp;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    DisplayImageOptions options;
    ImageLoaderConfiguration imgconfig;
    Context context;
    HashMap<Marker, MyMarker> mDataMap = new HashMap<>();
    List<MyMarker> myMarkerList;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private static Handler handler;
    public static final int TIMEOUT = 1500;
    boolean isReady = false;
    ProgressDialog pDialog;
    User user;
    DatabaseHelper db;
    SharedPreferences sharedPreferences;
    private LocationManager locationManager;
    private Location location;
    private final int REQUEST_LOCATION = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = MapsActivity.this;
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        db = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(SP_NAME, MODE);
        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"'";
        user = db.user_data(query);


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
        createInterval();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        getMarkers(googleMap);
    }

    public void addMarkers(JSONObject object,GoogleMap googleMap){
        ImageLoader imageLoader = ImageLoader.getInstance();
        myMarkerList = new ArrayList<>();
        /*MyMarker myMarker = new MyMarker();
        myMarker.setLatLng(new LatLng(6.573120, 3.395684));
        myMarker.setTitle("Chika Place Ogudu");
        try{
        //    myMarker.setBitmap(imageLoader.loadImageSync(SERVER_ROOT.replace("/android","")+"images/profile/thumbnail/fcb9d03a29_chikadibia.jpg"));
        }catch (Exception e){
            e.printStackTrace();
        }
        myMarkerList.add(myMarker);

        MyMarker myMarker1 = new MyMarker();
        myMarker1.setLatLng(new LatLng(6.574359, 3.396620));
        myMarker1.setTitle("Chika Ogudu boys scout");
        try{
        //    myMarker1.setBitmap(imageLoader.loadImageSync(SERVER_ROOT.replace("/android","")+"images/profile/thumbnail/e670231795_chikadibia.png"));
        }catch (Exception e){
            e.printStackTrace();
        }
        myMarkerList.add(myMarker1);*/
        try{
            if(object.getString("status").equalsIgnoreCase(SUCCESS)){
                JSONArray pointsArray = object.getJSONArray("points");
                for(int i = 0;i<pointsArray.length();i++){
                    JSONObject point = pointsArray.getJSONObject(i);
                    MyMarker myMarker = new MyMarker();
                    Double lat = Double.parseDouble(point.getString("lat"));
                    Double lng = Double.parseDouble(point.getString("lng"));
                    myMarker.setLatLng(new LatLng(lat, lng));
                    myMarker.setTitle(point.getString("title"));
                    myMarker.setUser_id(point.getString("user_id"));
                    try{
                        //    myMarker.setBitmap(imageLoader.loadImageSync(SERVER_ROOT.replace("/android","")+"images/profile/thumbnail/fcb9d03a29_chikadibia.jpg"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    myMarkerList.add(myMarker);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        loadMarkers(googleMap);
    }
    public void loadMarkers(GoogleMap googleMap){
        mMap = googleMap;
        Marker m;
        for(final MyMarker object: myMarkerList){
            m = mMap.addMarker(new MarkerOptions().position(
                    object.getLatLng())
                    .title(object.getTitle())
              //      .icon(BitmapDescriptorFactory.fromBitmap(object.getBitmap()))
              //      .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_add_user_primary))
            );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(object.getLatLng(),20f));
            mDataMap.put(m, object);
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                   // int position = (int)(marker.getTag());
                    User witness = new User();
                    witness.setUser_id(Integer.parseInt(object.getUser_id()));
                    new Post().eye_witness_detail(context,user,witness);
                    return false;
                }
            });
        }

    }
    private void getMarkers(final GoogleMap googleMap){
        ServerRequests serverRequests = new ServerRequests();
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Loading Points");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .build();
        serverRequests.post(request_body, "get_markers.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    addMarkers(returnedJSON,googleMap);
                    isReady = true;
                }else{
                    Utils.popup(context,"No Points","There are no Points to track..");
                }
            }
        });
    }
    private void createInterval(){
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateMarkers();
                handler.postDelayed(this, TIMEOUT);
            }
        }, TIMEOUT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.map_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
            //    searchColleagues(query);
                //    numBackPressed++;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if(query.length() > 2){
          //          searchColleagues(query);
                    //  numBackPressed++;
                }else{
        //            getData();
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                return true;

            case R.id.hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;

            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;

            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case R.id.action_search:

                return true;
            case R.id.action_add_me:
                add_my_marker();
                return true;
            default:
                    return super.onOptionsItemSelected(item);
        }

    }

    private void comparePoints(List<MyMarker> myMarkerUpdateList) {
        Set<Map.Entry<Marker, MyMarker>> entrySet = mDataMap.entrySet();
        Iterator<Map.Entry<Marker, MyMarker>> iterator = entrySet.iterator();

        if(myMarkerUpdateList != null){
            for(MyMarker marker: myMarkerUpdateList){
                MyMarker myMarker = marker;

                while(iterator.hasNext()){
                    Map.Entry<Marker, MyMarker> entry2 = iterator.next();
                    Marker m = entry2.getKey();
                    MyMarker v = entry2.getValue();
                    if(myMarker.getUser_id().equalsIgnoreCase(v.getUser_id())){
                        m.setPosition(myMarker.getLatLng());
                        Log.e("****FOUND***","******MARKER FOUND******");
                    }
                }

            }
        }

    }
    private void endTracking(){
        handler.removeCallbacksAndMessages(null);
    }
    @Override
    public void onBackPressed() {
        endTracking();
        super.onBackPressed();
    }

    private void updateMarkers(){
        ServerRequests serverRequests = new ServerRequests();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .build();
        serverRequests.post(request_body, "get_markers.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                if(returnedJSON != null){
                    try{
                        List<MyMarker> myMarkerUpdateList;
                        myMarkerUpdateList = new ArrayList<>();
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                       if(status.equalsIgnoreCase(SUCCESS)){
                           JSONArray pointsArray = returnedJSON.getJSONArray("points");
                           for(int i = 0;i<pointsArray.length();i++){
                               JSONObject point = pointsArray.getJSONObject(i);
                               MyMarker myMarker = new MyMarker();
                               Double lat = Double.parseDouble(point.getString("lat"));
                               Double lng = Double.parseDouble(point.getString("lng"));
                               myMarker.setLatLng(new LatLng(lat, lng));
                               myMarker.setTitle(point.getString("title"));
                               myMarker.setUser_id(point.getString("user_id"));
                               myMarkerUpdateList.add(myMarker);
                           }
                           comparePoints(myMarkerUpdateList);
                       }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    Log.e("NO UPDATE DATA","********NO UPDATE DATA*********");
                    //  Utils.popup(context,"No Points","There are no Points to track..");
                }
            }
        });
    }

    private void add_my_marker(){
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 2, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (location != null) {
                add_my_own_marker(location);
            }
        } else {
            showGPSDisabledAlertToUser();
        }
    }


    boolean isUserMarkerAdded = false;
    private void add_my_own_marker(Location location){
        MyMarker myMarker1 = new MyMarker();
        myMarker1.setLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
        myMarker1.setTitle("Me");
        myMarker1.setUser_id(user.user_id+"");
        try{
            //    myMarker1.setBitmap(imageLoader.loadImageSync(SERVER_ROOT.replace("/android","")+"images/profile/thumbnail/e670231795_chikadibia.png"));
        }catch (Exception e){
            e.printStackTrace();
        }
        if(isUserMarkerAdded== false){
            Marker m = mMap.addMarker(new MarkerOptions().position(
                    myMarker1.getLatLng())
                            .title(myMarker1.getTitle())
                    //      .icon(BitmapDescriptorFactory.fromBitmap(object.getBitmap()))
                    //      .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_add_user_primary))
            );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myMarker1.getLatLng(),20f));
            mDataMap.put(m, myMarker1);//i may need this in the future

        }else{
            Set<Map.Entry<Marker, MyMarker>> entrySet = mDataMap.entrySet();
            Iterator<Map.Entry<Marker, MyMarker>> iterator = entrySet.iterator();
            while(iterator.hasNext()){
                Map.Entry<Marker, MyMarker> entry2 = iterator.next();
                Marker m = entry2.getKey();
                MyMarker v = entry2.getValue();
                if(myMarker1.getUser_id().equalsIgnoreCase(v.getUser_id())){
                    m.setPosition(myMarker1.getLatLng());
                    Log.e("****FOUND ME***","******MARKER FOUND ME******");
                }
            }

        }
        isUserMarkerAdded = true;
    }

    @Override
    public void onLocationChanged(Location location) {
        add_my_own_marker(location);
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


}
