package Objects;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

public class MyMarker {
    LatLng latLng;
    String title;
    Bitmap bitmap;
    String user_id;

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public String getUser_id() {
        return user_id;
    }


    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

}
