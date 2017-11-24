package com.example.potato.privatisistema;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Pagrindinis extends AppCompatActivity {

    public final String[] KEY_WORDS = {"prisijungimas", "registracija"};
    static public String id = "";
    static public String vardas = "";
    static public String tokenas = "";
    static public String email = "";
    static public String telefonas = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagrindinis);


        //---------------Cia gaunamas tokenas, emailas atsijungimui, ir sveikinimo zinute-----------
        Intent intent = getIntent();
        int i = 0;

        while(intent.getStringExtra(KEY_WORDS[i]) == null) {
            i++;
        }
        String zinute = intent.getStringExtra(KEY_WORDS[i]);
        email = intent.getStringExtra(Prisijungimas.EMAIL); //emailas gaunamas kad atsijungti, t.y. istrinti API tokena is duombazes

        Toast.makeText(this, zinute, Toast.LENGTH_LONG).show(); //Sveikinimo zinute priklausomai ar registracija ivyko ar prisijungimas

        tokenas = intent.getStringExtra(Prisijungimas.TOKENAS); //Issaugojamas tokenas
        vardas = intent.getStringExtra(Prisijungimas.VARDAS);
        TextView text = (TextView) findViewById(R.id.textView3);
        text.setText(vardas);
    }

    //-------------------Pagrindinis funkcionalumas---------------------------------
    public void paieska(View view)
    {
        startActivity(new Intent(this, SkelbimuPaieska.class));
    }

    //Grazinami populiariausi skelbimai
    public void pop(View view)
    {
        SkelbimuSarasas.sarasTip = "paprastas";
        grazintSkelb("http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/postPop");
    }

    //Grazinami naujausi skelbimai
    public void nauj(View view)
    {
        SkelbimuSarasas.sarasTip = "paprastas";
        grazintSkelb("http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/postNauj");
    }

    public void objektPerz(View view)
    {
        startActivity(new Intent(this, ObjektPerz.class));
    }
    //-------------------Pagrindinis funkcionalumas END-----------------------------

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Uždaryti");
        builder.setMessage("Ar tikrai norite uždaryti programėlę?");
        builder.setPositiveButton("Taip",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        atsijungti();
                        finish();
                    }
                });
        builder.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void atsijungti() {

        String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/logoff";

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests
        StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        //SITA VIETA KEICIAS
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
                params.put("email", email);
                params.put("api_token", tokenas);
                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    //Daromas api call kad grazint populiariausius arba naujausius skelbimus
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

                        responseApdoroti(response);
                        //Toast.makeText(SkelbimuPaieska.this, response, Toast.LENGTH_LONG).show();
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

    public void naujasSkelb(View view)
    {
        SkelbimuSarasas.sarasTip = "naujas";
        startActivity(new Intent(this, RedaguotiSkelbima.class));
    }

    public void paskyra(View view)
    {
        startActivity(new Intent(this, ManoPaskyra.class));
    }

    private void responseApdoroti(String response)
    {
            if (!response.contentEquals("none")) {


                SkelbimuSarasas.skelbList = ParseJSON(response);
                startActivity(new Intent(this, SkelbimuSarasas.class));

            } else {
                Toast.makeText(this, "Skelbimų nerasta", Toast.LENGTH_LONG).show();
            }

    }

    public static ArrayList<HashMap<String, String>> ParseJSON(String json) {
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

    //Savaiciu konvertavimai
    public static String getWeekName(int weekNumb)
    {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.set(Calendar.WEEK_OF_YEAR, weekNumb);
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        String day = sdf.format(cal.getTime());
        sdf = new SimpleDateFormat("MMM", Locale.getDefault());
        String men = sdf.format(cal.getTime());
        String weekStart = men + " " + day;

        if(cal.get(Calendar.DAY_OF_MONTH)+6 > cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        {
            int skirtums = cal.get(Calendar.DAY_OF_MONTH)+6-cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, skirtums);

        }else
        {
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)+6);
        }

        sdf = new SimpleDateFormat("dd");
        day = sdf.format(cal.getTime());
        sdf = new SimpleDateFormat("MMM", Locale.getDefault());
        men = sdf.format(cal.getTime());
        String weekEnd = men + " " + day;

        String week = weekStart + " - " + weekEnd;

        return  week;
    }

    public static int getWeekNumber(String week)
    {
        try {

            String mon = week.substring(0, week.indexOf(" "));
            String da = week.substring(week.indexOf(" ")+1, week.indexOf(" ", week.indexOf(" ")+1));
            int day = Integer.parseInt(da);

            Date date = new SimpleDateFormat("MMM", Locale.getDefault()).parse(mon);
            Calendar cal = Calendar.getInstance(Locale.getDefault());
            cal.setTime(date);
            int month = cal.get(Calendar.MONTH);

            cal = Calendar.getInstance(Locale.getDefault());
            cal.set(cal.get(Calendar.YEAR), month, day);
            int weekNumb = cal.get(Calendar.WEEK_OF_YEAR);

            return weekNumb;

        }catch (Exception e)
        {
            Log.e("LELELEL", e.getMessage());
            return 0;
        }
    }
}
