package caller.com.testnav;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import android.widget.MediaController;
import Objects.EyeWitness;
import Objects.MyMarker;
import Objects.User;
import database.DatabaseHelper;
import dialogs.Dialogs;
import helper.AnimateFirstDisplayListener;
import helper.RoundedImageView;
import helper.Utils;
import services.MyLocation;

import static Constants.Constants.INVISIBLE;
import static Constants.Constants.MODE;
import static Constants.Constants.SP_NAME;
import static Constants.Constants.SP_USER_ID;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static database.DatabaseHelper.TABLE_EYE_WITNESS;
import static database.DatabaseHelper.TABLE_USERS;
import static server.ServerRequests.SERVER_ROOT;

public class ReadEyeWitnessActivity extends AppCompatActivity {
    Context context;
    DatabaseHelper db;
    SharedPreferences sharedPreferences;
    User user;
    EyeWitness eyeWitnessData;
    ImageView imageView;
    TextView textView;

    DisplayImageOptions options;
    ImageLoaderConfiguration imgconfig;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    SimpleExoPlayerView exoPlayerView;
    SimpleExoPlayer exoPlayer;

    BandwidthMeter bandwidthMeter;
    TrackSelector trackSelector;
    DefaultHttpDataSourceFactory dataSourceFactory;
    ExtractorsFactory extractorsFactory;
    MediaSource mediaSource;

    View title_view;
    Toolbar toolbar;
    TextView chatterName,chatterTyping,remainingMsg,block_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_eye_witness);
        context = ReadEyeWitnessActivity.this;
        db = new DatabaseHelper(context);
        sharedPreferences = context.getSharedPreferences(SP_NAME, MODE);
        String user_id = sharedPreferences.getString(SP_USER_ID,"");
        String query = "select * from "+TABLE_USERS + " where user_id='"+user_id+"'";
        user = db.user_data(query);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LayoutInflater inflater = LayoutInflater.from(this);
        title_view = inflater.inflate(R.layout.custom_title_bar,null);
        toolbar.addView(title_view);


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


        imageView = (ImageView)findViewById(R.id.image);
        textView = (TextView)findViewById(R.id.text);
        block_link = (TextView)findViewById(R.id.block_link);
        exoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exo_player_view);

        query = "select * from "+TABLE_EYE_WITNESS+" where receiver_id="+user.user_id+" order by id desc limit 1";
        for(EyeWitness eyeWitness: db.eyeWitnessList(query)){
            eyeWitnessData = eyeWitness;
        }
        if(eyeWitnessData != null){
            textView.setText(Html.fromHtml(eyeWitnessData.message));
          //  getSupportActionBar().setTitle(eyeWitnessData.sender_title+" "+eyeWitnessData.sender_last_name+" "+eyeWitnessData.sender_first_name);
            String sender_name = eyeWitnessData.sender_title+" "+eyeWitnessData.sender_last_name+" "+eyeWitnessData.sender_first_name;
            chatterName = (TextView)title_view.findViewById(R.id.head_title);
            chatterName.setText(sender_name);
            if(eyeWitnessData.send_type == INVISIBLE){
                chatterName.setText("ANONYMOUS REPORTER");
            }
            chatterTyping = (TextView)title_view.findViewById(R.id.head_detail);
            chatterTyping.setText("Eye witness");

            RoundedImageView top_image = (RoundedImageView)title_view.findViewById(R.id.head_image);
            try{
                if(!eyeWitnessData.sender_image.equalsIgnoreCase("NULL") && !eyeWitnessData.sender_image.equalsIgnoreCase(""))
                    ImageLoader.getInstance().displayImage(SERVER_ROOT.replace("/android","") + eyeWitnessData.sender_image, top_image, options, animateFirstListener);
            }catch (Exception e){
                e.printStackTrace();
            }
            title_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(eyeWitnessData != null){
                        User sender = new User();
                        sender.setUser_id(eyeWitnessData.sender_id);
                        sender.setTitle(eyeWitnessData.sender_title);
                        sender.setLast_name(eyeWitnessData.sender_last_name);
                        sender.setOther_names(eyeWitnessData.sender_other_names);
                        sender.setPhone(eyeWitnessData.sender_phone);
                        sender.setFirst_name(eyeWitnessData.sender_first_name);
                        sender.setImage(eyeWitnessData.sender_image);
                        if(eyeWitnessData.send_type != INVISIBLE)
                        new Dialogs().eye_witness_sender_popup(context,user,sender);
                    }
                }
            });

            if(!eyeWitnessData.file_type.equalsIgnoreCase("") && eyeWitnessData.file_type != null){
                switch (eyeWitnessData.file_type){
                    case "png": case "jpg": case "jpeg": case "JPEG":
                        exoPlayerView.setVisibility(View.GONE);
                        ImageLoader imageLoader = ImageLoader.getInstance();
                        try{
                            String imageUri = SERVER_ROOT.replace("/android","")+eyeWitnessData.file_name;
                            imageLoader.displayImage(imageUri, imageView);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    case "mp4": case "3gp": case "MP4": case "3GP":

                        imageView.setVisibility(View.GONE);
                        try {


                            bandwidthMeter = new DefaultBandwidthMeter();
                            trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
                            String videoUri = SERVER_ROOT.replace("/android","")+eyeWitnessData.file_name;
                            Uri videoURI = Uri.parse(videoUri);

                            dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
                            extractorsFactory = new DefaultExtractorsFactory();
                            mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);

                            exoPlayerView.setPlayer(exoPlayer);
                            exoPlayer.prepare(mediaSource);
                            exoPlayer.setPlayWhenReady(true);
                        }catch (Exception e){
                            Log.e("MainAcvtivity"," exoplayer error "+ e.toString());
                        }
                        break;
                        default:
                            exoPlayerView.setVisibility(View.GONE);
                            imageView.setVisibility(View.GONE);
                            break;
                }
            }else{
                exoPlayerView.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
            }
        }else{
            Utils.popup(context,"Error","There is no available Data");
        }

        block_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(eyeWitnessData != null)
                new Dialogs().confirm_block_eye_witness_reporter_popup(context,user,eyeWitnessData);
            }
        });
    }

    private void releasePlayer(){
        if(exoPlayer != null){
            exoPlayer.seekTo(0);
            exoPlayer.stop();
            exoPlayer.release();
            bandwidthMeter = null;
            trackSelector = null;
            dataSourceFactory = null;
            extractorsFactory = null;
            mediaSource = null;
        }
    }
    @Override
    public void onBackPressed() {
        releasePlayer();
        super.onBackPressed();
    }
}
