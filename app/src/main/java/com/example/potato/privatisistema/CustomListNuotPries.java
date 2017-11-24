package com.example.potato.privatisistema;

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

/**
 * Created by Potato on 5/10/2017.
 */

public class CustomListNuotPries  extends ArrayAdapter<String> {

    private final Activity context;
    private String[] statusai;

    private String[] nuotraukUrl;

    //Image Loader kintamieji
    private RequestQueue mRequestQueue;

    public CustomListNuotPries(Activity context, String[] status, String[] nuotUrl) {
        super(context, R.layout.pries_nuot_item, status);
        // TODO Auto-generated constructor stub

        this.context = context;
        statusai = status;
        nuotraukUrl = nuotUrl;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();

        View rowView=inflater.inflate(R.layout.pries_nuot_item, null,true);

        NetworkImageView nuot = (NetworkImageView)rowView.findViewById(R.id.nuotraukaPries);
        TextView txtStatus = (TextView) rowView.findViewById(R.id.textStatusas);


        txtStatus.setText(statusai[position]);

        if(Nuotraukos.images == null || Nuotraukos.images.get(position) == null)
        {
            Nuotraukos.images.put(position, loadImage());
        }

        nuot.setImageUrl(nuotraukUrl[position], Nuotraukos.images.get(position));

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
