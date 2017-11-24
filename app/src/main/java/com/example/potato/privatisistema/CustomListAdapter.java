package com.example.potato.privatisistema;

/**
 * Created by Potato on 4/22/2017.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] skelbimuAddr;

    //Image Loader kintamieji
    private RequestQueue mRequestQueue;
    private String[] nuotraukUrl;

    private final String[] info;
    private final String[] time;

    public CustomListAdapter(Activity context, String[] itemname, String[] imgUrl, String[] inf, String[] tim) {
        super(context, R.layout.skelb_list, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.skelbimuAddr=itemname;
        this.nuotraukUrl=imgUrl;
        info = inf;
        time = tim;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.skelb_list, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.textAdresas);

        if(SkelbimuSarasas.sarasTip.contentEquals("rezervavimas")) {
            TextView txtInfo = (TextView) rowView.findViewById(R.id.textRezerv);
            txtInfo.setVisibility(View.VISIBLE);
            txtInfo.setText(info[position]);
            txtInfo = (TextView) rowView.findViewById(R.id.textTime);
            txtInfo.setVisibility(View.VISIBLE);
            txtInfo.setText(time[position]);
        }

        txtTitle.setText(skelbimuAddr[position]);

        NetworkImageView nuot = (NetworkImageView)rowView.findViewById(R.id.picNuotrauka);
        if(SkelbimuSarasas.skelbImages == null || SkelbimuSarasas.skelbImages.get(position) == null)
        {
            SkelbimuSarasas.skelbImages.put(position, loadImage());
        }

        nuot.setImageUrl(nuotraukUrl[position], SkelbimuSarasas.skelbImages.get(position));

        return rowView;

    };

    public ImageLoader loadImage() {
        mRequestQueue = Volley.newRequestQueue(context);
        return (new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);

            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        }));
    }
}
