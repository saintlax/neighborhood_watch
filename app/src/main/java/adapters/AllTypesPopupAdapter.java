package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.List;

import Objects.AllTypes;
import Objects.TenantType;
import caller.com.testnav.R;
import helper.AnimateFirstDisplayListener;

/**
 * Created by user on 4/28/2019.
 */

public class AllTypesPopupAdapter extends BaseAdapter {
    Context context;
    List<AllTypes> menu_name;
    DisplayImageOptions options;
    ImageLoaderConfiguration imgconfig;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    public AllTypesPopupAdapter(Context act, List<AllTypes> menu_name){
        this.context = act;
        this.menu_name = menu_name;
        File cacheDir = StorageUtils.getCacheDirectory(context);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_flag_primary)
                .showImageForEmptyUri(R.drawable.ic_flag_primary)
                .showImageOnFail(R.drawable.ic_flag_primary)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.NONE)
                .build();

        imgconfig = new ImageLoaderConfiguration.Builder(context)
                .build();
        ImageLoader.getInstance().init(imgconfig);
    }
    @Override
    public int getCount() {
        return menu_name.size();
    }

    @Override
    public String getItem(int position) {
        try{
            //  return menu_name[position-1];
            AllTypes allTypes = menu_name.get(position-1);
            return allTypes.name;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.row_for_tenant_type_popup,viewGroup,false);
        //    ImageView icon = (ImageView)row.findViewById(R.id.menu_icon);
        //    icon.setImageResource(menu_name.get(i).image);
       // RoundedImageView top_image = (RoundedImageView)row.findViewById(R.id.menu_icon);


        TextView name =(TextView)row.findViewById(R.id.menu_name);
        name.setText(menu_name.get(i).name);
        return row;
    }
}
