package server;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerRequests {
    public static String SERVER_ROOT = "http://10.0.2.2/security/android/";
   // public static String SERVER_ROOT = "http://192.168.43.34/security/android/";
    //public static String SERVER_ROOT = "http://baffstravels.com/security/android/";

    public ServerRequests(){

    }

    public void post(RequestBody request_body, String page, GetJSONCallback JSONCallBack) {
        new OKHTTPAsyncTask(request_body, page, JSONCallBack).execute();
    }
    public class OKHTTPAsyncTask extends AsyncTask<Void, Void, JSONObject> {

        RequestBody request_body;
        GetJSONCallback JSONCallBack;
        String page;
        public OKHTTPAsyncTask(RequestBody request_body,String page, GetJSONCallback callBack) {
            this.JSONCallBack = callBack;
            this.request_body = request_body;
            this.page = page;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            JSONObject returnedData = null;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(SERVER_ROOT+page)
                    .post(request_body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                final String result = response.body().string();
                Log.e("******RESULT****",result);
                JSONObject jObject = new JSONObject(result);
                if(jObject != null){
                    returnedData = jObject;
                }
                response.close();
                if(!response.isSuccessful()){
                    throw new IOException("Error"+response);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return  returnedData;
        }

        @Override
        protected void onPostExecute(JSONObject returnedData) {
            super.onPostExecute(returnedData);
            JSONCallBack.done(returnedData);
        }
    }

}