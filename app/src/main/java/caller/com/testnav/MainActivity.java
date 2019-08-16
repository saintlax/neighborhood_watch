package caller.com.testnav;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.List;

import Objects.LGAStaff;
import Objects.OrganisationStaff;
import Objects.User;
import Objects.UserType;
import database.DatabaseHelper;
import dialogs.Dialogs;
import fragment.AdminHomeFragment;
import helper.AnimateFirstDisplayListener;
import helper.RoundedImageView;
import helper.Utils;
import services.BootReciever;
import services.LocationBootReciever;
import services.SecurityService;

import static Constants.Constants.MODE;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static database.DatabaseHelper.TABLE_LGA_STAFF;
import static database.DatabaseHelper.TABLE_ORGANISATION_STAFF;
import static database.DatabaseHelper.TABLE_USERS;
import static database.DatabaseHelper.TABLE_USER_TYPE;
import static helper.Utils.LoadAboutUsFragment;
import static helper.Utils.LoadAddMembersFragment;
import static helper.Utils.LoadAdminHomeFragment;
import static helper.Utils.LoadEyeWitnessFragment;
import static helper.Utils.LoadFamilyHomeFragment;
import static helper.Utils.LoadFamilyMembersFragment;
import static helper.Utils.LoadJoinNeighborhoodFragment;
import static helper.Utils.LoadLGAAdminFragment;
import static helper.Utils.LoadProfileFragment;
import static helper.Utils.LoadSettingsFragment;
import static helper.Utils.LoadUserHomeFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences sharedPreferences;
    User user;
    UserType userType;
    DatabaseHelper db;
    Context context;
    DisplayImageOptions options;
    ImageLoaderConfiguration imgconfig;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    RoundedImageView top_image;
    Dialogs dialogs = new Dialogs();
    static final int RESULT_CODE_TRACKING = 23;
    OrganisationStaff organisationStaff;
    LGAStaff lgaStaff;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(SP_NAME, MODE);
        db = new DatabaseHelper(this);
        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"' order by id desc";
        user = db.user_data(query);

        query = "select * from "+TABLE_USER_TYPE+" where user_id='"+user_id+"' order by id desc";
        userType = db.user_type(query);

        if(user == null){
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
            return;
        }
        context = MainActivity.this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(!userType.user_type.equalsIgnoreCase(getString(R.string.admin)) && !userType.user_type.equalsIgnoreCase(getString(R.string.super_admin))){
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer_user);
        }
        Menu menu = navigationView.getMenu();

        query = "select * from "+TABLE_ORGANISATION_STAFF+" where user_id="+user_id+" order by id desc limit 1";
        List<OrganisationStaff> organisationStaffList = db.organisationsStaffList(query);
        if(organisationStaffList != null){
            for(OrganisationStaff orgStaff : organisationStaffList){
                organisationStaff = orgStaff;
            }
        }
        if(organisationStaff != null){
            menu.add(40000000,Menu.NONE,Menu.NONE,organisationStaff.organisation).setIcon(R.drawable.ic_apk_box).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Utils.popup(context,"Organisation",organisationStaff.organisation);
                    return false;
                }
            });
        }

        query = "select * from "+TABLE_LGA_STAFF+" where user_id="+user_id+" order by id desc limit 1";
        List<LGAStaff> lgaStaffs = db.lgaStaffList(query);
        if(lgaStaffs != null){
            for(LGAStaff lgaStafff : lgaStaffs){
                lgaStaff = lgaStafff;
            }
        }

        if(lgaStaff != null){
            menu.add(40000001,Menu.NONE,Menu.NONE,lgaStaff.name).setIcon(R.drawable.ic_apk_box).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    LoadLGAAdminFragment(context,lgaStaff.name);
                    return false;
                }
            });
        }

        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);


        File cacheDir = StorageUtils.getCacheDirectory(context);
        imgconfig = new ImageLoaderConfiguration.Builder(context)
                .build();
        ImageLoader.getInstance().init(imgconfig);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.user_blue)
                .showImageForEmptyUri(R.drawable.user_blue)
                .showImageOnFail(R.drawable.user_blue)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.NONE)
                .build();
        imgconfig = new ImageLoaderConfiguration.Builder(context)
                .build();
        ImageLoader.getInstance().init(imgconfig);

        top_image = (RoundedImageView)headerView.findViewById(R.id.top_image);
        if(user.image != null){
            File file = new File(user.image);
            Uri imageUri  = Uri.fromFile(file);
            ImageLoader.getInstance().displayImage(imageUri.toString(), top_image, options, animateFirstListener);
        }

        TextView owner_name = (TextView) headerView.findViewById(R.id.username);
        owner_name.setText(user.last_name+" "+user.first_name+" "+user.other_names);
        load_home_fragment();

        Intent alarm = new Intent(this.context, BootReciever.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(this.context, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
        if(alarmRunning == false){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context,0,alarm,0);
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), SecurityService.SERVICE_TIMEOUT, pendingIntent);
        }

        checkTracking();
    }


    public void checkTracking(){
        Intent tracker = new Intent(context, LocationBootReciever.class);
        boolean tracking = (PendingIntent.getBroadcast(this.context, 0, tracker, PendingIntent.FLAG_NO_CREATE) != null);
        if(tracking == true){
            dialogs.confirm_keep_tracking_dialog(context,sharedPreferences);
        }
    }

    private void load_home_fragment(){
        getSupportActionBar().setTitle(getString(R.string.app_name));
        if(userType.user_type.equalsIgnoreCase(getString(R.string.admin)) || userType.user_type.equalsIgnoreCase(getString(R.string.super_admin))){
            LoadAdminHomeFragment(context);
        }
        if(userType.user_type.equalsIgnoreCase(getString(R.string.tenant))){
            LoadUserHomeFragment(context);
        }
        if(userType.user_type.equalsIgnoreCase(getString(R.string.caretaker))){
            LoadUserHomeFragment(context);
        }
        if(userType.user_type.equalsIgnoreCase(getString(R.string.landlord))){
            LoadUserHomeFragment(context);
        }
        if(userType.user_type.equalsIgnoreCase(getString(R.string.family))){
            LoadFamilyHomeFragment(context);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            android.support.v4.app.Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if(currentFragment instanceof AdminHomeFragment){
                super.onBackPressed();
            }else{
                load_home_fragment();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(userType.user_type.equalsIgnoreCase(getString(R.string.admin)) || userType.user_type.equalsIgnoreCase(getString(R.string.super_admin))){
       //     getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(userType.user_type.equalsIgnoreCase(getString(R.string.admin)) || userType.user_type.equalsIgnoreCase(getString(R.string.super_admin))) {
            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }
            if (id == R.id.action_broadcast) {
        //        dialogs.admin_broadcast_popup(context,user);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
        //    getSupportActionBar().setTitle("Profile");
            LoadProfileFragment(context);
        } else if (id == R.id.nav_settings) {
            getSupportActionBar().setTitle("Settings");
            LoadSettingsFragment(context);
        } else if (id == R.id.nav_distress) {
            dialogs.confirm_distress_popup(context);
        } else if (id == R.id.nav_levies) {
            getSupportActionBar().setTitle("My Levies");
            dialogs.user_levies_popup(context,user);
        } else if(id == R.id.nav_dues){
            dialogs.user_dues_popup(context,user);
        } else if (id == R.id.nav_about) {
            LoadAboutUsFragment(context);
        } else if (id == R.id.nav_log_out) {
            dialogs.logout_dialog(context,sharedPreferences);
        }else if(id == R.id.nav_home){
            load_home_fragment();
        }else if(id == R.id.nav_family){
            LoadFamilyMembersFragment(context);
        }else if(id == R.id.nav_add_members){
            LoadAddMembersFragment(context);
        }else if(id == R.id.nav_feedback){
            dialogs.feedback_popup(context,user);
        }else if(id == R.id.nav_eye_witness){
            LoadEyeWitnessFragment(context);
        }else if(id == R.id.nav_rate_us){
            dialogs.rate_us_popup(context,user);
        }else if(id == R.id.nav_join_neighborhood){
            LoadJoinNeighborhoodFragment(context);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case RESULT_CODE_TRACKING:
                Toast.makeText(context, "Tracking in progress", Toast.LENGTH_SHORT).show();
                checkTracking();
                break;
        }
    }

}
