package downloader;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import Objects.Data;

import static Constants.Constants.STORAGE_FOLDER;


/**
 * Created by CHIKADIBIA on 2/27/2017.
 */

public class Downloader extends AsyncTask<Void, Void, Data> {
    String fileName;
    String DownloadUrl;
    Data data;
    GetUserCallback userCallBack;
    public Downloader(Context context){

    }
    public boolean downloader(String filename, String Address){
        try{
            Log.e("***PATH**",Address+filename);
            URL u = new URL(Address+filename);
            InputStream is = u.openStream();
            DataInputStream dis = new DataInputStream(is);
            byte[] buffer = new byte[1024];
            int length;

            /*File new_folder = new File(DOWNLOAD_FILE_PATH);
            if(!new_folder.exists()){
               Boolean state = new_folder.mkdir();
               if(state){
                   Log.e("folder","CREATED");
               }else {
                   Log.e("folder","NOT CREATED");
               }
            }*/
            String root = Environment.getExternalStorageDirectory().toString();
            File new_folder = new File(root+"/"+STORAGE_FOLDER);
            new_folder.mkdirs();
            File new_file = new File(new_folder,filename);
            if(!new_file.exists()) {
                Log.e("Download Path", "Saving to: " + new_folder);
                FileOutputStream fos = new FileOutputStream(new_file);
                while ((length = dis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                fos.close();
                return true;
            }else{
                Log.e("File Exist", "This file already exist");
                return true;
            }
        }catch(MalformedURLException mue){
                mue.printStackTrace();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }catch(SecurityException e){
            e.printStackTrace();
        }
        return false;
    }
    public Downloader(String fileName, String DownloadUrl,GetUserCallback userCallBack){
        this.fileName = fileName;
        this.DownloadUrl = DownloadUrl;
        this.data = data;
        this.userCallBack = userCallBack;
    }
    @Override
    protected Data doInBackground(Void... params){
        Data returnedData = null;
        boolean state = downloader(fileName,DownloadUrl);
        if(state==true){
            returnedData = new Data("true","true","true","");
        }
        return returnedData;

    }

    @Override
    protected void onPostExecute(Data returnedData){
        super.onPostExecute(returnedData);
        userCallBack.done(returnedData);

    }

}
