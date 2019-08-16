package dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import Objects.Due;
import Objects.EyeWitness;
import Objects.Levy;
import Objects.MyBroadcast;
import Objects.MyLocation;
import Objects.Ratings;
import Objects.User;
import Objects.UserNeighborhood;
import Objects.UserType;
import caller.com.testnav.R;
import helper.Utils;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import server.GetJSONCallback;
import server.ServerRequests;

import static Constants.Constants.NAIRA;
import static Constants.Constants.SUCCESS;
import static helper.Utils.currencyFormat;
import static helper.Utils.getTimestamp;
import static helper.Utils.saveRatings;
import static helper.Utils.saveUser;
import static helper.Utils.saveUserNeighborhood;
import static helper.Utils.saveUserType;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;


public class Post extends Dialogs{
    ServerRequests serverRequests = new ServerRequests();
    ProgressDialog pDialog;
    public void add_street(final Context context, final User user, String name){
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Adding Street");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("name",name)
                .build();
        serverRequests.post(request_body, "add_street.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            new Dialogs().comfirm_add_more_street_popup(context,user);
                            return;
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }
    public void edit_street(final Context context, User user, final View listView,final JSONObject object,final String name){
        String id = "0";
        try {
            id = object.getString("id");
        }catch (Exception e){
            e.printStackTrace();
        }
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Editing Street");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("name",name)
                .addFormDataPart("id",id)
                .build();
        serverRequests.post(request_body, "edit_street.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            TextView textView = (TextView)listView.findViewById(R.id.textTitle);
                            textView.setText(name);
                            textView.setTextColor(Color.BLUE);
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }
    public void delete_street(final Context context, User user, final View listView,final JSONObject object){
        String id = "0";
        try {
            id = object.getString("id");
        }catch (Exception e){
            e.printStackTrace();
        }
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Deleting Street");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("id",id)
                .build();
        serverRequests.post(request_body, "delete_street.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            TextView textView = (TextView)listView.findViewById(R.id.textTitle);
                            textView.setText("THIS STREET HAS BEEN DELETED");
                            textView.setTextColor(Color.RED);
                            Toast.makeText(context, "DELETED", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }
    public void add_levy(final Context context,final User user,final Levy levy){
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Adding Levy");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("name",levy.name)
                .addFormDataPart("amount",levy.amount)
                .addFormDataPart("deadline_date",levy.date)
                .build();
        serverRequests.post(request_body, "add_levy.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }
    public void edit_levy(final Context context,final User user,final View listView,final Levy levy,JSONObject levyObject){
        String id = "0";
        try{
            id = levyObject.getString("id");
        }catch (Exception e){
            e.printStackTrace();
        }
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Editing Levy");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("name",levy.name)
                .addFormDataPart("amount",levy.amount)
                .addFormDataPart("deadline_date",levy.date)
                .addFormDataPart("id",id)
                .build();
        serverRequests.post(request_body, "edit_levy.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            TextView name = (TextView)listView.findViewById(R.id.name);
                            TextView amount = (TextView)listView.findViewById(R.id.amount);
                            TextView date = (TextView)listView.findViewById(R.id.date);
                            name.setText(levy.name);
                            amount.setText(levy.amount);
                            date.setText(levy.date);
                            name.setTextColor(Color.BLUE);
                            amount.setTextColor(Color.BLUE);
                            date.setTextColor(Color.BLUE);
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }
    public void delete_levy(final Context context, User user, final View listView,final JSONObject object){
        String id = "0";
        try {
            id = object.getString("id");
        }catch (Exception e){
            e.printStackTrace();
        }
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Deleting Levy");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("id",id)
                .build();
        serverRequests.post(request_body, "delete_levy.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){

                            TextView name = (TextView)listView.findViewById(R.id.name);
                            TextView amount = (TextView)listView.findViewById(R.id.amount);
                            TextView date = (TextView)listView.findViewById(R.id.date);
                            name.setText("DELETED");
                            amount.setText("DELETED");
                            date.setText("DELETED");
                            name.setTextColor(Color.BLUE);
                            amount.setTextColor(Color.BLUE);
                            date.setTextColor(Color.BLUE);
                            Toast.makeText(context, "DELETED", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }


    public void add_tenant_type(final Context context, User user,String name){
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Adding Type");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
              //  .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("name",name)
                .build();
        serverRequests.post(request_body, "add_tenant_type.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void add_building(final Context context, User user, final View listView,final JSONObject object,final String name,final String owner){
        String id = "0";
        try {
            id = object.getString("id");
        }catch (Exception e){
            e.printStackTrace();
        }
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Editing Street");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("name",name)
                .addFormDataPart("owner",owner)
                .addFormDataPart("street_id",id)
                .build();
        serverRequests.post(request_body, "add_building.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            TextView textView = (TextView)listView.findViewById(R.id.textDescription);
                            textView.setText(message);
                            textView.setTextColor(Color.BLUE);
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }
    public void edit_building(final Context context, User user, final View listView,final JSONObject object,final String name,final String owner){
        String id = "0";
        try {
            id = object.getString("id");
        }catch (Exception e){
            e.printStackTrace();
        }
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Editing Building");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("name",name)
                .addFormDataPart("owner",owner)
                .addFormDataPart("id",id)//building_id
                .build();
        serverRequests.post(request_body, "edit_building.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            TextView title = (TextView)listView.findViewById(R.id.textTitle);
                            TextView desc = (TextView)listView.findViewById(R.id.textDescription);
                            title.setText(name);
                            title.setTextColor(Color.BLUE);
                            desc.setText("Owner / Caretaker: "+owner);
                            desc.setTextColor(Color.BLUE);
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }
    public void delete_building(final Context context, User user, final View listView,final JSONObject object){
        String id = "0";
        try {
            id = object.getString("id");
        }catch (Exception e){
            e.printStackTrace();
        }
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Deleting Building");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("id",id)
                .build();
        serverRequests.post(request_body, "delete_building.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            TextView textView = (TextView)listView.findViewById(R.id.textTitle);
                            TextView textDescription = (TextView)listView.findViewById(R.id.textDescription);
                            TextView sub_name = (TextView)listView.findViewById(R.id.sub_name);
                            textView.setText("THIS BUILDING HAS BEEN DELETED");
                            textDescription.setText("THIS BUILDING HAS BEEN DELETED");
                            sub_name.setText("THIS BUILDING HAS BEEN DELETED");
                            textView.setTextColor(Color.RED);
                            textDescription.setTextColor(Color.RED);
                            sub_name.setTextColor(Color.RED);
                            Toast.makeText(context, "DELETED", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void add_apartment(final Context context, User user, final View listView,final JSONObject object,final String name,final String detail){
        String id = "0";
        try {
            id = object.getString("id");
        }catch (Exception e){
            e.printStackTrace();
        }
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Adding Apartment");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("name",name)
                .addFormDataPart("detail",detail)
                .addFormDataPart("building_id",id)
                .build();
        serverRequests.post(request_body, "add_apartment.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            TextView textView = (TextView)listView.findViewById(R.id.textDescription);
                            textView.setText(message);
                            textView.setTextColor(Color.BLUE);
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void edit_apartment(final Context context, User user, final View listView,final JSONObject object,final String name,final String detail){
        String id = "0";
        try {
            id = object.getString("id");
        }catch (Exception e){
            e.printStackTrace();
        }
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Editing Apartment");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("name",name)
                .addFormDataPart("detail",detail)
                .addFormDataPart("id",id)//apartment_id
                .build();
        serverRequests.post(request_body, "edit_apartment.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            TextView title = (TextView)listView.findViewById(R.id.textTitle);
                            TextView desc = (TextView)listView.findViewById(R.id.textDescription);
                            title.setText(name);
                            desc.setText(detail);
                            title.setTextColor(Color.BLUE);
                            desc.setTextColor(Color.BLUE);
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void delete_apartment(final Context context, User user, final View listView,final JSONObject object){
        String id = "0";
        try {
            id = object.getString("id");
        }catch (Exception e){
            e.printStackTrace();
        }
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Deleting apartment");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("id",id)
                .build();
        serverRequests.post(request_body, "delete_apartment.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            TextView textView = (TextView)listView.findViewById(R.id.textTitle);
                            TextView textDescription = (TextView)listView.findViewById(R.id.textDescription);
                            TextView sub_name = (TextView)listView.findViewById(R.id.sub_name);
                            textView.setText("THIS BUILDING HAS BEEN DELETED");
                            textDescription.setText("THIS BUILDING HAS BEEN DELETED");
                            sub_name.setText("THIS BUILDING HAS BEEN DELETED");
                            textView.setTextColor(Color.RED);
                            textDescription.setTextColor(Color.RED);
                            sub_name.setTextColor(Color.RED);
                            Toast.makeText(context, "DELETED", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void post_pay_user_levy(final Context context, User user,final View listView,JSONObject object,Levy levy,String amount){
        String member_id = "0";
        String member_name = "Chikadibia";
        try{
            JSONObject userObject = object.getJSONObject("user");
            member_id = userObject.getString("id");
            member_name = userObject.getString("last_name");
        }catch (Exception e){
            e.printStackTrace();
        }
        ServerRequests serverRequests = new ServerRequests();
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Sending Payment");
        pDialog.setMessage("Checking credentials for "+member_name);
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("amount",amount)
                .addFormDataPart("member_id",member_id)
                .addFormDataPart("levy_id",levy.id+"")
                .addFormDataPart("levy_name",levy.name)
                .build();
        serverRequests.post(request_body, "pay_levy.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            TextView detail = (TextView)listView.findViewById(R.id.detail);
                            detail.setText(message);
                            detail.setTextColor(Color.BLUE);
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }
    public void post_get_user_levy(final Context context, User user,final View listView,JSONObject object,Levy levy){
        String member_id = "0";
        String member_name = "Chikadibia";
        try{
            JSONObject userObject = object.getJSONObject("user");
            member_id = userObject.getString("id");
            member_name = userObject.getString("last_name");
        }catch (Exception e){
            e.printStackTrace();
        }
        ServerRequests serverRequests = new ServerRequests();
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Fetching Payment");
        pDialog.setMessage("Checking credentials for "+member_name);
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("member_id",member_id)
                .addFormDataPart("levy_id",levy.id+"")
                .addFormDataPart("levy_name",levy.name)
                .build();
        serverRequests.post(request_body, "get_levy.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            status = "Payment Detail";//here
                            JSONObject payment = returnedJSON.getJSONObject("payment");
                            message = "Amount Paid: "+NAIRA+" <b>"+currencyFormat(payment.getString("amount"))+"</b><br>";
                            message += "Date of Payment: "+payment.getString("date")+"<br>";
                            JSONObject admin_user = returnedJSON.getJSONObject("admin_user");
                            message += "Acknowledged By: "+admin_user.getString("last_name")+"<br>";

                        }
                        Utils.popup(context,status, Html.fromHtml(message)+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }
    public void post_levy_summary(final Context context, User user,final View listView,JSONObject object){
        String levy_id = "0";
        try{
            levy_id = object.getString("id");
        }catch (Exception e){
            e.printStackTrace();
        }
        ServerRequests serverRequests = new ServerRequests();
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Fetching Payments");
        pDialog.setMessage("Checking credentials for Summary");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("levy_id",levy_id)
                .build();
        serverRequests.post(request_body, "levy_summary.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            status = "Summary";//here
                            message = "Total Revenue: "+NAIRA+" <b>"+currencyFormat(returnedJSON.getString("revenue"))+"</b><br>";
                      //      message += returnedJSON.getString("revenue_from")+"<br>";
                            message += "Total Outstanding: "+NAIRA+" <b>"+currencyFormat(returnedJSON.getString("outstanding"))+"</b><br>";
                      //      message += returnedJSON.getString("outstanding_from")+"<br>";
                        }
                        Utils.popup(context,status, Html.fromHtml(message)+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }
    public void post_location(final Context context, User user, Location location, String event, final TextView textView){
        ServerRequests serverRequests = new ServerRequests();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("lat",location.getLatitude()+"")
                .addFormDataPart("lng",location.getLongitude()+"")
                .addFormDataPart("timestamp",getTimestamp()+"")
                .addFormDataPart("event",event)
                .build();
        serverRequests.post(request_body, "tracker.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            if(textView != null){
                                textView.setText(message);
                            }
                        }
                   //     Utils.popup(context,status, Html.fromHtml(message)+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public void reset_password(final Context context, final String phone){
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Reset Password");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("phone",phone)
                .build();
        serverRequests.post(request_body, "reset_password.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void admin_add_user(final Context context, User user, UserType userType){
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Add Occupant");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("phone",user.phone)
                .addFormDataPart("title",user.title)
                .addFormDataPart("last_name",user.last_name)
                .addFormDataPart("first_name",user.first_name)
                .addFormDataPart("other_names",user.other_names)
                .addFormDataPart("apartment_id",userType.apartment_id)
                .addFormDataPart("user_type",userType.user_type)
                .build();
        serverRequests.post(request_body, "admin_add_user.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void delete_user_account(final Context context, User user, final View listView,final JSONObject object){
        String id = "0";
        try {
            id = object.getString("id");
        }catch (Exception e){
            e.printStackTrace();
        }
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Deleting Account");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("id",id)
                .build();
        serverRequests.post(request_body, "delete_user_account.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){


                            TextView name = (TextView)listView.findViewById(R.id.name);
                            TextView phone = (TextView)listView.findViewById(R.id.phone);
                            TextView mStreet = (TextView)listView.findViewById(R.id.street);
                            TextView mStatus = (TextView)listView.findViewById(R.id.status);
                            TextView mBuilding = (TextView)listView.findViewById(R.id.building);
                            TextView mApartment = (TextView)listView.findViewById(R.id.apartment);
                            TextView mUser_type = (TextView)listView.findViewById(R.id.user_type);
                            name.setText("THIS DELETED");
                            name.setTextColor(Color.RED);

                            phone.setText("THIS DELETED");
                            phone.setTextColor(Color.RED);

                            mStatus.setText("THIS DELETED");
                            mStreet.setTextColor(Color.RED);

                            mBuilding.setText("THIS DELETED");
                            mBuilding.setTextColor(Color.RED);

                            mApartment.setText("THIS DELETED");
                            mApartment.setTextColor(Color.RED);

                            mUser_type.setText("THIS DELETED");
                            mUser_type.setTextColor(Color.RED);
                            Toast.makeText(context, "DELETED", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void add_due(final Context context,final User user,final Due due){
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Adding Due");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("name",due.name)
                .addFormDataPart("amount",due.amount)
                .addFormDataPart("deadline_date",due.date)
                .build();
        serverRequests.post(request_body, "add_due.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void edit_due(final Context context,final User user,final View listView,final Due due,JSONObject dueObject){
        String id = "0";
        try{
            id = dueObject.getString("id");
        }catch (Exception e){
            e.printStackTrace();
        }
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Editing Due");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("name",due.name)
                .addFormDataPart("amount",due.amount)
                .addFormDataPart("deadline_date",due.date)
                .addFormDataPart("id",id)
                .build();
        serverRequests.post(request_body, "edit_due.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            TextView name = (TextView)listView.findViewById(R.id.name);
                            TextView amount = (TextView)listView.findViewById(R.id.amount);
                            TextView date = (TextView)listView.findViewById(R.id.date);
                            name.setText(due.name);
                            amount.setText(due.amount);
                            date.setText(due.date);
                            name.setTextColor(Color.BLUE);
                            amount.setTextColor(Color.BLUE);
                            date.setTextColor(Color.BLUE);
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void delete_due(final Context context, User user, final View listView,final JSONObject object){
        String id = "0";
        try {
            id = object.getString("id");
        }catch (Exception e){
            e.printStackTrace();
        }
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Deleting Due");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("id",id)
                .build();
        serverRequests.post(request_body, "delete_due.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){

                            TextView name = (TextView)listView.findViewById(R.id.name);
                            TextView amount = (TextView)listView.findViewById(R.id.amount);
                            TextView date = (TextView)listView.findViewById(R.id.date);
                            name.setText("DELETED");
                            amount.setText("DELETED");
                            date.setText("DELETED");
                            name.setTextColor(Color.BLUE);
                            amount.setTextColor(Color.BLUE);
                            date.setTextColor(Color.BLUE);
                            Toast.makeText(context, "DELETED", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void post_pay_user_due(final Context context, User user,final View listView,JSONObject object,Due due,String amount){
        String member_id = "0";
        String member_name = "Chikadibia";
        try{
            JSONObject userObject = object.getJSONObject("user");
            member_id = userObject.getString("id");
            member_name = userObject.getString("last_name");
        }catch (Exception e){
            e.printStackTrace();
        }
        ServerRequests serverRequests = new ServerRequests();
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Sending Payment");
        pDialog.setMessage("Checking credentials for "+member_name);
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("amount",amount)
                .addFormDataPart("member_id",member_id)
                .addFormDataPart("due_id",due.id+"")
                .addFormDataPart("due_name",due.name)
                .build();
        serverRequests.post(request_body, "pay_due.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            TextView detail = (TextView)listView.findViewById(R.id.detail);
                            detail.setText(message);
                            detail.setTextColor(Color.BLUE);
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }


    public void post_get_user_due(final Context context, User user,final View listView,JSONObject object,Due due){
        String member_id = "0";
        String member_name = "Chikadibia";
        try{
            JSONObject userObject = object.getJSONObject("user");
            member_id = userObject.getString("id");
            member_name = userObject.getString("last_name");
        }catch (Exception e){
            e.printStackTrace();
        }
        ServerRequests serverRequests = new ServerRequests();
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Fetching Payment");
        pDialog.setMessage("Checking credentials for "+member_name);
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("member_id",member_id)
                .addFormDataPart("due_id",due.id+"")
                .addFormDataPart("due_name",due.name)
                .build();
        serverRequests.post(request_body, "get_due.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            status = "Payment Detail";//here
                            JSONObject payment = returnedJSON.getJSONObject("payment");
                            message = "Amount Paid: "+NAIRA+" <b>"+currencyFormat(payment.getString("amount"))+"</b><br>";
                            message += "Date of Payment: "+payment.getString("date")+"<br>";
                            JSONObject admin_user = returnedJSON.getJSONObject("admin_user");
                            message += "Acknowledged By: "+admin_user.getString("last_name")+"<br>";

                        }
                        Utils.popup(context,status, Html.fromHtml(message)+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void post_due_summary(final Context context, User user,final View listView,JSONObject object){
        String due_id = "0";
        try{
            due_id = object.getString("id");
        }catch (Exception e){
            e.printStackTrace();
        }
        ServerRequests serverRequests = new ServerRequests();
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Fetching Payments");
        pDialog.setMessage("Checking credentials for Summary");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("due_id",due_id)
                .build();
        serverRequests.post(request_body, "due_summary.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            status = "Summary";//here
                            message = "Total Revenue: "+NAIRA+" <b>"+currencyFormat(returnedJSON.getString("revenue"))+"</b><br>";
                            //      message += returnedJSON.getString("revenue_from")+"<br>";
                            message += "Total Outstanding: "+NAIRA+" <b>"+currencyFormat(returnedJSON.getString("outstanding"))+"</b><br>";
                            //      message += returnedJSON.getString("outstanding_from")+"<br>";
                        }
                        Utils.popup(context,status, Html.fromHtml(message)+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }
    public void post_broadcast(final Context context, User user, MyBroadcast myBroadcast){
        ServerRequests serverRequests = new ServerRequests();
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Broadcast");
        pDialog.setMessage("Publishing... Please wait..");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("message",myBroadcast.message)
                .addFormDataPart("recipient",myBroadcast.recipient)
                .build();
        serverRequests.post(request_body, "broadcast.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){

                        }
                        Utils.popup(context,status, Html.fromHtml(message)+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void delete_family_member(final Context context, User user, final View listView,final JSONObject object){
        String id = "0";
        try {
            id = object.getString("id");
        }catch (Exception e){
            e.printStackTrace();
        }
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Deleting family Member");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("member_id",id)
                .build();
        serverRequests.post(request_body, "delete_family_member.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            TextView textView = (TextView)listView.findViewById(R.id.name);
                            TextView phone = (TextView)listView.findViewById(R.id.phone);
                            TextView detail = (TextView)listView.findViewById(R.id.detail);
                            textView.setText(" DELETED");
                            phone.setText("DELETED");
                            detail.setText("DELETED");
                            textView.setTextColor(Color.RED);
                            phone.setTextColor(Color.RED);
                            detail.setTextColor(Color.RED);
                            Toast.makeText(context, "DELETED", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void change_user_address(final Context context,final User user,final UserType userType){
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Change Address");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("street_id",userType.street_id)
                .addFormDataPart("user_type",userType.user_type)
        //        .addFormDataPart("street_name",userType.street_name)
                .addFormDataPart("apartment_id",userType.apartment_id)
        //        .addFormDataPart("apartment_name",userType.apartment_name)
                .addFormDataPart("building_id",userType.building_id)
        //        .addFormDataPart("building_name",userType.building_name)
                .addFormDataPart("lga_id",userType.lga_id)
                .addFormDataPart("lga_name",userType.lga_name)
                .addFormDataPart("district_id",userType.district_id)
                .addFormDataPart("district_name",userType.district_name)
                .addFormDataPart("apartment_type_id",userType.apartment_type_id)
                .addFormDataPart("apartment_type_name",userType.apartment_type_name)
                .build();
        serverRequests.post(request_body, "update_user_type.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            if(saveUserType(context,userType)){
                                message = "Your account updated successfully";
                            }
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void edit_user(final Context context,final User user){
        ServerRequests serverRequests = new ServerRequests();
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Edit User");
        pDialog.setMessage("Checking credentials...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("last_name",user.last_name)
                .addFormDataPart("first_name",user.first_name)
                .addFormDataPart("other_names",user.other_names)
                .addFormDataPart("title",user.title)
                .build();
        serverRequests.post(request_body, "edit_user.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            saveUser(context,user);
                        }
                        Utils.popup(context,status, Html.fromHtml(message)+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void delete_distress_history(final Context context, User user, final View view,final JSONObject object){
        String id = "0";
        try {
            //i had to add the id manually in the php array because the row is from tracker table
            id = object.getString("emergency_id");
        }catch (Exception e){
            e.printStackTrace();
        }
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Deleting Distress");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("id",id)
                .build();
        serverRequests.post(request_body, "delete_emergency.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            TextView name = (TextView)view.findViewById(R.id.name);
                            TextView phone = (TextView)view.findViewById(R.id.phone);
                            TextView mEvent = (TextView)view.findViewById(R.id.event);
                            TextView mStatus = (TextView)view.findViewById(R.id.status);
                            TextView mLat = (TextView)view.findViewById(R.id.lat);
                            TextView mLng = (TextView)view.findViewById(R.id.lng);
                            TextView mState = (TextView)view.findViewById(R.id.state);
                            TextView mDate = (TextView)view.findViewById(R.id.date);
                            name.setText("DELETED!");
                            phone.setText("DELETED!");
                            mEvent.setText("DELETED!");
                            mLat.setText("DELETED!");
                            mLng.setText("DELETED!");
                            mDate.setText("DELETED!");
                            name.setTextColor(Color.RED);
                            phone.setTextColor(Color.RED);
                            mEvent.setTextColor(Color.RED);
                            mLat.setTextColor(Color.RED);
                            mLng.setTextColor(Color.RED);
                            mState.setTextColor(Color.RED);
                            mDate.setTextColor(Color.RED);
                            Toast.makeText(context, "DELETED", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void post_feedback(final Context context, User user, String message){
        ServerRequests serverRequests = new ServerRequests();
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Broadcast");
        pDialog.setMessage("Publishing... Please wait..");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("message",message)
                .build();
        serverRequests.post(request_body, "send_feedback.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        Utils.popup(context,status, Html.fromHtml(message)+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void eye_witness_detail(final Context context, User user,User witness){
        ServerRequests serverRequests = new ServerRequests();
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Eye Witness");
        pDialog.setMessage("Loading Data..");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("witness_id",witness.user_id+"")
                .build();
        serverRequests.post(request_body, "get_eye_witness_detail.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        Utils.popup(context,status, Html.fromHtml(message)+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void delete_eye_witness_report(final Context context, User user, final View listView,final JSONObject object){
        String id = "0";
        try {
            id = object.getString("id");
        }catch (Exception e){
            e.printStackTrace();
        }
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Deleting Report");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("id",id)
                .build();
        serverRequests.post(request_body, "delete_eye_witness_report.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            LinearLayout general = (LinearLayout)listView.findViewById(R.id.general);
                            general.setVisibility(View.GONE);
                            /*TextView textView = (TextView)listView.findViewById(R.id.textTitle);
                            TextView textDescription = (TextView)listView.findViewById(R.id.textDescription);
                            TextView sub_name = (TextView)listView.findViewById(R.id.sub_name);
                            textView.setText("THIS BUILDING HAS BEEN DELETED");
                            textDescription.setText("THIS BUILDING HAS BEEN DELETED");
                            sub_name.setText("THIS BUILDING HAS BEEN DELETED");
                            textView.setTextColor(Color.RED);
                            textDescription.setTextColor(Color.RED);
                            sub_name.setTextColor(Color.RED);*/
                            Toast.makeText(context, "DELETED", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void rate_us(final Context context, User user, Ratings ratings){

        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Rating Us");
        pDialog.setMessage("Please wait ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("rating",ratings.rating)
                .build();
        serverRequests.post(request_body, "rate_us.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            JSONObject rate_us = returnedJSON.getJSONObject("rate_us");
                            Ratings ratings = new Ratings();
                            ratings.setUser_id(rate_us.getInt("user_id"));
                            ratings.setRating(rate_us.getString("rating"));
                            ratings.setDate(rate_us.getString("date"));
                            saveRatings(context,ratings);
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }


    public void join_neighborhood(final Context context,final User user,final UserNeighborhood userNeighborhood){
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Joining Neighborhood");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("state_id",userNeighborhood.state_id+"")
                .addFormDataPart("state_name",userNeighborhood.state_name)
                .addFormDataPart("street_id",userNeighborhood.street_id+"")
                .addFormDataPart("street_name",userNeighborhood.street_name)
                .addFormDataPart("apartment_id",userNeighborhood.apartment_id+"")
                .addFormDataPart("apartment_name",userNeighborhood.apartment_name)
                .addFormDataPart("building_id",userNeighborhood.building_id+"")
                .addFormDataPart("building_name",userNeighborhood.building_name)
                .addFormDataPart("lga_id",userNeighborhood.lga_id+"")
                .addFormDataPart("lga_name",userNeighborhood.lga_name)
                .addFormDataPart("district_id",userNeighborhood.district_id+"")
                .addFormDataPart("district_name",userNeighborhood.district_name)
                .addFormDataPart("apartment_type_id",userNeighborhood.apartment_type_id+"")
                .addFormDataPart("apartment_type_name",userNeighborhood.apartment_type_name)
                .addFormDataPart("user_type",userNeighborhood.user_type)
                .build();
        serverRequests.post(request_body, "join_neighborhood.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            if(saveUserNeighborhood(context,userNeighborhood)){
                                message = "You have added a new neighborhood watch to your account";
                            }
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void block_eye_witness_reporter(final Context context, User user, EyeWitness eyeWitness,String reason){

        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Blocking Reporter");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("sender_id",eyeWitness.sender_id+"")
                .addFormDataPart("reason",reason)
                .build();
        serverRequests.post(request_body, "block_reporter.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            Toast.makeText(context, "BLOCKED!!!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Utils.popup(context,status,message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void mark_all_levies_as_paid(final Context context, User user, final View listView,final JSONObject object){
        String member_id = "0";
        try {
            member_id = object.getString("id");
        }catch (Exception e){
            e.printStackTrace();
        }
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Marking all Levies");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("member_id",member_id)
                .build();
        serverRequests.post(request_body, "mark_all_levies_as_paid.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            Toast.makeText(context, "Paid All Levies", Toast.LENGTH_SHORT).show();
                        }
                        Utils.popup(context,status,Html.fromHtml(message)+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }

    public void mark_all_dues_as_paid(final Context context, User user, final View listView,final JSONObject object){
        String member_id = "0";
        try {
            member_id = object.getString("id");
        }catch (Exception e){
            e.printStackTrace();
        }
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Marking all Dues");
        pDialog.setMessage("Checking credentials ...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",user.user_id+"")
                .addFormDataPart("member_id",member_id)
                .build();
        serverRequests.post(request_body, "mark_all_dues_as_paid.php", new GetJSONCallback() {
            @Override
            public void done(JSONObject returnedJSON) {
                pDialog.cancel();
                if(returnedJSON != null){
                    try{
                        String status = returnedJSON.getString("status");
                        String message = returnedJSON.getString("message");
                        if(status.equalsIgnoreCase(SUCCESS)){
                            Toast.makeText(context, "Paid All Levies", Toast.LENGTH_SHORT).show();
                        }
                        Utils.popup(context,status,Html.fromHtml(message)+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Utils.popup(context,"Error",context.getString(R.string.error_no_response));
                }
            }
        });
    }


}
