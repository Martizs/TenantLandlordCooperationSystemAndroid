package com.example.potato.privatisistema;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Nuotraukos extends AppCompatActivity {

    private final String Tag_stat = "statusas";
    private final String Tag_path = "path";

    public String nuotId[];
    public  String response;
    private String status[];
    private String nuotUrl[];
    public static HashMap<Integer, ImageLoader> images = new HashMap<>();

    //Listo kurimas
    CustomListNuotPries adapter;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuotraukos);

        //Jei istorija tai jokiu daugiau nuotrauku negalima ikelti
        if(SkelbimuSarasas.sarasTip.contentEquals("istorija"))
        {
            Button but = (Button) findViewById(R.id.butGal);
            but.setVisibility(View.GONE);
            but = (Button) findViewById(R.id.butFot);
            but.setVisibility(View.GONE);
        }

        grazintNuotraukas();

        list = (ListView) findViewById(R.id.listPriesNuot);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                PadidintiNuotrauka.imgUrl = nuotUrl[position];
                PadidintiNuotrauka.nuotId = nuotId[position];
                Intent intent = new Intent(Nuotraukos.this, PadidintiNuotrauka.class);
                if(SkelbimuSarasas.sarasTip.contentEquals("nuomot"))
                {
                    startActivityForResult(intent, 1);
                }else if(SkelbimuSarasas.sarasTip.contentEquals("nuomin"))
                {
                    startActivity(intent);
                }else if(SkelbimuSarasas.sarasTip.contentEquals("istorija"))
                {
                    startActivity(intent);
                }
            }
        });

        //Jei nuomotojas atsidare nuotraukas
        if(SkelbimuSarasas.sarasTip.contentEquals("nuomot"))
        {
            Button but = (Button) findViewById(R.id.butGal);
            but.setVisibility(View.GONE);
            but = (Button) findViewById(R.id.butFot);
            but.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            grazintNuotraukas();

    }

    public void fotografuoti(View view)
    {
        UploadPhoto.tipas = "fotografuoti";
        startActivityForResult(new Intent(this, UploadPhoto.class), 1);

    }

    public void galerija(View view)
    {
        UploadPhoto.tipas = "galerija";
        startActivityForResult(new Intent(this, UploadPhoto.class), 1);
    }

    private void grazintNuotraukas()
    {
        startActivity(new Intent(this, LoadingScreen.class)); //Loadinimo screenas aktivuojamas

        String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/getNuotPries";

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        //SITA VIETA KEICIAS
                        /*Toast.makeText(Patalpos.this, response, Toast.LENGTH_LONG).show();
                        LoadingScreen.load.finish(); //Pradanginamas loadinomo screenas*/
                        if(!response.contentEquals("none"))
                        {
                            //Uzkraunamas nuotrauku pries listas jei jis egzistuoja
                                ParseJSON(response); //Parsinamas json objektas

                                adapter = new CustomListNuotPries(Nuotraukos.this, status, nuotUrl);

                                list = (ListView) findViewById(R.id.listPriesNuot);

                                list.setAdapter(adapter);


                            LoadingScreen.load.finish(); //Pradanginamas loadinomo screenas

                        }else if(Pagrindinis.id.contentEquals(Patalpos.patalpos.get(SkelbimuPaieska.TAG_UserId)))
                        {
                            LoadingScreen.load.finish(); //Pradanginamas loadinomo screenas
                            Toast.makeText(Nuotraukos.this, "Nuomininkas neįkėlęs jokių nuotraukų", Toast.LENGTH_LONG).show();
                            finish();
                        }else {
                            LoadingScreen.load.finish(); //Pradanginamas loadinomo screenas
                        }
                        Log.d("Response", response);
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

                params.put(SkelbimuPaieska.TAG_post_id, Patalpos.patalpos.get(SkelbimuPaieska.TAG_post_id));
                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    //Parsinamas json objektas
    private void ParseJSON(String json) {
        if (json != null) {
            try {

                JSONArray skelbimai = new JSONArray(json);
                status = new String[skelbimai.length()];
                nuotUrl = new String[skelbimai.length()];
                nuotId = new String[skelbimai.length()];

                for (int i = 0; i < skelbimai.length(); i++) {
                    JSONObject c = skelbimai.getJSONObject(i);

                    status[i] = c.getString(Tag_stat);
                    nuotUrl[i] = c.getString(Tag_path);
                    nuotId[i] = c.getString("id");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } /*catch (JSONException e) {
                e.printStackTrace();
            }*/
        } else {
            Log.e("ServiceHandler", "No data received from HTTP request");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
       images = new HashMap<>();
       finish();
    }
}
