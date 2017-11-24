package com.example.potato.privatisistema;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class PadidintiNuotrauka extends AppCompatActivity {

    public static String imgUrl;
    public static String nuotId;

    //Image Loader kintamieji
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_padidinti_nuotrauka);

        if(SkelbimuSarasas.sarasTip.contentEquals("redagavimas") || SkelbimuSarasas.sarasTip.contentEquals("naujas")) {
            if (NuotraukosDaug.nuotTipas.contentEquals("nauja") || NuotraukosDaug.nuotTipas.contentEquals("redaguojama")) {
                ImageView nuot = (ImageView) findViewById(R.id.fullScreenNorm);
                nuot.setVisibility(View.VISIBLE);
                nuot.setImageBitmap(NuotraukosDaug.naujNuot.get(NuotraukosDaug.nuotPos));
                NetworkImageView netNuot = (NetworkImageView) findViewById(R.id.fullScreen);
                netNuot.setVisibility(View.GONE);
                //Aktivuojam pagrindinius ir pasalinimo mygtukus redagavime
                Button but = (Button) findViewById(R.id.butPasalinti);
                but.setVisibility(View.VISIBLE);
                but = (Button) findViewById(R.id.butPagr);
                but.setVisibility(View.VISIBLE);
            } else {
                NetworkImageView nuot = (NetworkImageView) findViewById(R.id.fullScreen);
                nuot.setImageUrl(imgUrl, loadImage());
            }
        }else
        {
            NetworkImageView nuot = (NetworkImageView) findViewById(R.id.fullScreen);
            nuot.setImageUrl(imgUrl, loadImage());
        }

        if(SkelbimuSarasas.sarasTip.contentEquals("nuomot") && Nuotraukos.images.size()>0)
        {
            Button but = (Button) findViewById(R.id.butPat);
            but.setVisibility(View.VISIBLE);
            but = (Button) findViewById(R.id.butAtm);
            but.setVisibility(View.VISIBLE);
        }
    }

    public void patNuot(View view)
    {
        siustiStatusa("patvirtinta");
    }

    public void atmNuot(View view)
    {
        siustiStatusa("atmesta");
    }

    public void pasalint(View view)
    {
        if(NuotraukosDaug.nuotPos == NuotraukosDaug.pagrNuot)
        {
            NuotraukosDaug.pagrNuot = 1;
        }else if(NuotraukosDaug.nuotPos < NuotraukosDaug.pagrNuot)
        {
            NuotraukosDaug.pagrNuot--;
        }

        if(NuotraukosDaug.nuotTipas.contentEquals("nauja")) {
            NuotraukosDaug.naujNuot.remove(NuotraukosDaug.nuotPos);
            finish();
        }else if(NuotraukosDaug.nuotTipas.contentEquals("redaguojama"))
        {
            NuotraukosDaug.naujNuot.remove(NuotraukosDaug.nuotPos);
            if(NuotraukosDaug.nuotPos <= NuotraukosDaug.existNuot.size())
            {
                NuotraukosDaug.pasalNuot.add(NuotraukosDaug.existNuot.get(NuotraukosDaug.nuotPos-1));
                NuotraukosDaug.existNuot.remove(NuotraukosDaug.nuotPos-1);
            }
            finish();
        }
    }

    //Nustatoma kuri bus pagrindine nuotrauka
    public void pagrindine(View view)
    {
        NuotraukosDaug.pagrNuot = NuotraukosDaug.nuotPos;
        Toast.makeText(PadidintiNuotrauka.this, "Nuotrauka nustatyta kaip pagrindinė", Toast.LENGTH_LONG).show();
        finish();
    }

    public void siustiStatusa(final String statusas)
    {
        String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/postNuotStat";

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        //SITA VIETA KEICIAS
                        Toast.makeText(PadidintiNuotrauka.this, "Nuotrauka " + statusas, Toast.LENGTH_LONG).show();
                        //Toast.makeText(PadidintiNuotrauka.this, response, Toast.LENGTH_LONG).show();
                        finish(); //Pradanginamas loadinomo screenas
                        if(!response.contentEquals("none")) {
                            Log.d("Response", response);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                    }

                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("email", Pagrindinis.email);
                params.put("api_token", Pagrindinis.tokenas);

                params.put("id", nuotId);
                params.put("statusas", statusas);
                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    public ImageLoader loadImage()
    {
        mRequestQueue = Volley.newRequestQueue(this);
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
