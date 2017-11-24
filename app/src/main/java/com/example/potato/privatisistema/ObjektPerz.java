package com.example.potato.privatisistema;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ObjektPerz extends AppCompatActivity {

    //Grafiko duomenu tagai
    public static final String[] TAG_rezerv = {"diena", "laikas", "savaitesNr", "statusas", "nuomin_id", "komentaras", "id", "user_id", "post_id"};
    public static String TAG_apsilankymas = "apsilankymas";
    public static String TAG_post_hist_id = "post_history_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objekt_perz);
    }

    //----------------------Pagrindiniai skelbimu grazinimai----------------------------------------------------
    //Grazinami vartotojo ideti skelbimai
    public void manSkelb(View view)
    {
        SkelbimuSarasas.sarasTip = "redagavimas";
        grazintSkelb("http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/getManSkelb");
    }

    //Grazinamos vartotojo isnuomotos patalpos
    public void manNuomot(View view)
    {
        SkelbimuSarasas.sarasTip = "nuomot";
        grazintSkelb("http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/getManNuomot");
    }

    //Grazinamos vartotojo issinuomotos patalpos
    public void manNuomin(View view)
    {
        SkelbimuSarasas.sarasTip = "nuomin";
        grazintSkelb("http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/getManNuomin");
    }

    //Grazinami skelbimai susija su vartotojo patalpu peržiūrėjimo laikų rezervacijom
    public void perzLaikai(View view)
    {
        SkelbimuSarasas.sarasTip = "rezervavimas";
        grazintSkelb("http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/getRezervSkelb");
    }

    //Grazinami skelbimai susija su vartotojo nuomos istorija
    public void getIstorija(View view)
    {
        SkelbimuSarasas.sarasTip = "istorija";
        grazintSkelb("http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/getIstorija");
    }

    //---------------------Pagrindiniai skelbimu grazinimai END-------------------------------------------------

    //Daromas api call kad grazinti vartotojo skelbimus, arba patalpas
    public void grazintSkelb(String url)
    {
        startActivity(new Intent(this, LoadingScreen.class)); //Loadinimo screenas aktivuojamas



        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        LoadingScreen.load.finish(); //Pradanginamas loadinomo screenas

                        //String lel = response;
                        //Toast.makeText(ObjektPerz.this, response, Toast.LENGTH_LONG).show();
                        responseApdoroti(response);

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
                //I posta paduodami prisijungimo parametrai
                Map<String, String>  params = new HashMap<String, String>();
                params.put("email", Pagrindinis.email);
                params.put("api_token", Pagrindinis.tokenas);

                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    private void responseApdoroti(String response)
    {
        if(!response.contentEquals("none")) {

            SkelbimuSarasas.skelbList = ParseJSONObj(response);
            startActivity(new Intent(this, SkelbimuSarasas.class));

        }else
        {
            Toast.makeText(this, "Nerasta", Toast.LENGTH_LONG).show();
        }
    }

    public static ArrayList<HashMap<String, String>> ParseJSONObj(String json) {
        if (json != null) {
            try {
// Hashmap for ListView
                ArrayList<HashMap<String, String>> skelbList = new ArrayList<HashMap<String, String>>();
                //JSONObject jsonObj = new JSONObject(json);

// Getting JSON Array node
                JSONArray skelbimai = new JSONArray(json);

// looping through All posts
                for (int i = 0; i < skelbimai.length(); i++) {
                    JSONObject c = skelbimai.getJSONObject(i);

                    String patalpuTip = c.getString(SkelbimuPaieska.TAG_patalpuTip);
                    String savivaldybe = c.getString(SkelbimuPaieska.TAG_savivaldybe);
                    String mikroRaj = c.getString(SkelbimuPaieska.TAG_mikroRaj);
                    String gyvenviete = c.getString(SkelbimuPaieska.TAG_gyvenviete);
                    String gatve = c.getString(SkelbimuPaieska.TAG_gatve);
                    String plotas = c.getString(SkelbimuPaieska.TAG_plotas);
                    String kaina = c.getString(SkelbimuPaieska.TAG_kaina);
                    String ivertinimas = c.getString(SkelbimuPaieska.TAG_ivertinimas);
                    String komentaras = c.getString(SkelbimuPaieska.TAG_komentaras);
                    String userId = c.getString(SkelbimuPaieska.TAG_UserId);
                    String nuominId = c.getString(TAG_rezerv[4]);
                    String apsilankymas = c.getString(TAG_apsilankymas);
                    String postHistId = c.getString(TAG_post_hist_id);
                    String nuotPath = c.getString(SkelbimuPaieska.TAG_nuotPath);
                    // tmp hashmap for single post
                    HashMap<String, String> skelbimas = new HashMap<String, String>();

// adding every child node to HashMap key => value
                    skelbimas.put(SkelbimuPaieska.TAG_patalpuTip, patalpuTip);
                    skelbimas.put(SkelbimuPaieska.TAG_savivaldybe, savivaldybe);
                    skelbimas.put(SkelbimuPaieska.TAG_mikroRaj, mikroRaj);
                    skelbimas.put(SkelbimuPaieska.TAG_gyvenviete, gyvenviete);
                    skelbimas.put(SkelbimuPaieska.TAG_gatve, gatve);
                    skelbimas.put(SkelbimuPaieska.TAG_plotas, plotas);
                    skelbimas.put(SkelbimuPaieska.TAG_kaina, kaina);
                    skelbimas.put(SkelbimuPaieska.TAG_ivertinimas, ivertinimas);
                    skelbimas.put(SkelbimuPaieska.TAG_komentaras, komentaras);
                    skelbimas.put(SkelbimuPaieska.TAG_UserId, userId);
                    skelbimas.put(TAG_rezerv[4], nuominId);
                    skelbimas.put(TAG_apsilankymas, apsilankymas);
                    skelbimas.put(TAG_post_hist_id, postHistId);
                    skelbimas.put(SkelbimuPaieska.TAG_nuotPath, nuotPath);

                    switch (patalpuTip) {

                        case "Butas":
                            String aukstuSk = c.getString(SkelbimuPaieska.TAG_aukstuSk);
                            String aukstas = c.getString(SkelbimuPaieska.TAG_aukstas);
                            String pastatoTip = c.getString(SkelbimuPaieska.TAG_pastatoTip);
                            String irengimoTip = c.getString(SkelbimuPaieska.TAG_irengimoTip);
                            String sildymoTip = c.getString(SkelbimuPaieska.TAG_sildymoTip);
                            String kambSk = c.getString(SkelbimuPaieska.TAG_kambSk);
                            String metai = c.getString(SkelbimuPaieska.TAG_metai);
                            String namoNr = c.getString(SkelbimuPaieska.TAG_namoNr);
                            String butoNr = c.getString(SkelbimuPaieska.TAG_butoNr);
                            String post_id = c.getString(SkelbimuPaieska.TAG_post_id);

                            skelbimas.put(SkelbimuPaieska.TAG_aukstuSk, aukstuSk);
                            skelbimas.put(SkelbimuPaieska.TAG_aukstas, aukstas);
                            skelbimas.put(SkelbimuPaieska.TAG_pastatoTip, pastatoTip);
                            skelbimas.put(SkelbimuPaieska.TAG_irengimoTip, irengimoTip);
                            skelbimas.put(SkelbimuPaieska.TAG_sildymoTip, sildymoTip);
                            skelbimas.put(SkelbimuPaieska.TAG_kambSk, kambSk);
                            skelbimas.put(SkelbimuPaieska.TAG_metai, metai);
                            skelbimas.put(SkelbimuPaieska.TAG_namoNr, namoNr);
                            skelbimas.put(SkelbimuPaieska.TAG_butoNr, butoNr);
                            skelbimas.put(SkelbimuPaieska.TAG_post_id, post_id);

                            break;
                        case "Namas":
                            String namoTip = c.getString(SkelbimuPaieska.TAG_namoTip);
                            String pastatoTipN = c.getString(SkelbimuPaieska.TAG_pastatoTip);
                            String irengimoTipN = c.getString(SkelbimuPaieska.TAG_irengimoTip);
                            String sildymoTipN = c.getString(SkelbimuPaieska.TAG_sildymoTip);
                            String metaiN = c.getString(SkelbimuPaieska.TAG_metai);
                            String namoNrN = c.getString(SkelbimuPaieska.TAG_namoNr);
                            String post_idN = c.getString(SkelbimuPaieska.TAG_post_id);

                            skelbimas.put(SkelbimuPaieska.TAG_namoTip, namoTip);
                            skelbimas.put(SkelbimuPaieska.TAG_pastatoTip, pastatoTipN);
                            skelbimas.put(SkelbimuPaieska.TAG_irengimoTip, irengimoTipN);
                            skelbimas.put(SkelbimuPaieska.TAG_sildymoTip, sildymoTipN);
                            skelbimas.put(SkelbimuPaieska.TAG_metai, metaiN);
                            skelbimas.put(SkelbimuPaieska.TAG_namoNr, namoNrN);
                            skelbimas.put(SkelbimuPaieska.TAG_post_id, post_idN);

                            break;
                    }


                    if(SkelbimuSarasas.sarasTip.contentEquals("rezervavimas"))
                    {
                        //Budas uzkrauti duomenis is Json objekto be hardkodinimo
                        String value;
                        for(String tagas : TAG_rezerv)
                        {
                            value = c.getString(tagas);
                            skelbimas.put(tagas, value);
                        }
                    }

// adding student to students list
                    skelbList.add(skelbimas);
                }
                return skelbList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } /*catch (JSONException e) {
                e.printStackTrace();
            }*/
        } else {
            Log.e("ServiceHandler", "No data received from HTTP request");
            return null;
        }
    }
}
