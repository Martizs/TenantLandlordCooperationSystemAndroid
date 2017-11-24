package com.example.potato.privatisistema;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Patalpos extends AppCompatActivity {

    public static Activity pat;

    public static final String TAG_laikID = "id";
    public static final String TAG_diena = "diena";
    public static final String TAG_laikas = "laikas";
    public static final String TAG_savaitesNr = "savaitesNr";
    public static final String []dienuPav = {"Pirmadienis", "Antradienis", "Trečiadienis", "Ketvirtadienis", "Penktadienis", "Šeštadienis", "Sekmadienis"};

    SkelbSpec fragment;
    public static HashMap<String, String> patalpos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patalpos);

        pat = this;

        if(SkelbimuSarasas.sarasTip.contentEquals("istorija"))
        {
            Button but = (Button) findViewById(R.id.butRezervuoti);
            but.setVisibility(View.GONE);
            but = (Button) findViewById(R.id.butAtsiliepimai);
            but.setVisibility(View.GONE);
            but = (Button) findViewById(R.id.butDok);
            but.setVisibility(View.VISIBLE);
            but = (Button) findViewById(R.id.butSas);
            but.setVisibility(View.VISIBLE);
            but = (Button) findViewById(R.id.butNuot);
            but.setVisibility(View.VISIBLE);
            but = (Button) findViewById(R.id.butIstrintIst);
            but.setVisibility(View.VISIBLE);

            //Jei patalpu nuomotojas
            if (Pagrindinis.id.contentEquals(patalpos.get(SkelbimuPaieska.TAG_UserId))) {
                but = (Button) findViewById(R.id.butPerzNuomot);
                but.setVisibility(View.GONE);
                but = (Button) findViewById(R.id.butPerzNuomin);
                but.setVisibility(View.VISIBLE);
            }else
            {
                but = (Button) findViewById(R.id.butSkelbAtsNuomin);
                but.setVisibility(View.VISIBLE);
            }
        }

        if(SkelbimuSarasas.sarasTip.contentEquals("nuomin"))
        {
            Button but = (Button) findViewById(R.id.butRezervuoti);
            but.setVisibility(View.GONE);
            but = (Button) findViewById(R.id.butDok);
            but.setVisibility(View.VISIBLE);
            but = (Button) findViewById(R.id.butSas);
            but.setVisibility(View.VISIBLE);
            but = (Button) findViewById(R.id.butNuot);
            but.setVisibility(View.VISIBLE);
            but = (Button) findViewById(R.id.butSkaitl);
            but.setVisibility(View.VISIBLE);
            TextView text = (TextView) findViewById(R.id.labelApsilank);
            text.setVisibility(View.VISIBLE);
            text = (TextView) findViewById(R.id.textApsilank);
            text.setVisibility(View.VISIBLE);
        }

        if(SkelbimuSarasas.sarasTip.contentEquals("nuomot"))
        {
            Button but = (Button) findViewById(R.id.butRezervuoti);
            but.setVisibility(View.GONE);
            but = (Button) findViewById(R.id.butDok);
            but.setVisibility(View.VISIBLE);
            but = (Button) findViewById(R.id.butSas);
            but.setVisibility(View.VISIBLE);
            but = (Button) findViewById(R.id.butNuot);
            but.setVisibility(View.VISIBLE);
            but = (Button) findViewById(R.id.butSkaitl);
            but.setVisibility(View.VISIBLE);
            but = (Button) findViewById(R.id.butAtvyk);
            but.setVisibility(View.VISIBLE);
            but = (Button) findViewById(R.id.butPerzNuomin);
            but.setVisibility(View.VISIBLE);
            but = (Button) findViewById(R.id.butNutr);
            but.setVisibility(View.VISIBLE);
        }

        //Pertvarkomas patalpu susijusiu su redagavimu UI
        if(SkelbimuSarasas.sarasTip.contentEquals("redagavimas")) {
            Button but = (Button) findViewById(R.id.butRezervuoti);
            but.setVisibility(View.GONE);
            but = (Button) findViewById(R.id.butAtsiliepimai);
            but.setVisibility(View.GONE);
            but = (Button) findViewById(R.id.butPerzNuot);
            but.setVisibility(View.GONE);
            but = (Button) findViewById(R.id.butPerzNuomot);
            but.setVisibility(View.GONE);
            but = (Button) findViewById(R.id.butRedaguoti);
            but.setVisibility(View.VISIBLE);
            but = (Button) findViewById(R.id.butIstrint);
            but.setVisibility(View.VISIBLE);
            but = (Button) findViewById(R.id.butIsnuomoti);
            but.setVisibility(View.VISIBLE);
        }

        //Pertvarkomas patalpu susijusiu su rezervavimo laikais UI
        if(SkelbimuSarasas.sarasTip.contentEquals("rezervavimas")) {
            Button but = (Button) findViewById(R.id.butRezervuoti);
            but.setVisibility(View.GONE);

            TextView text = (TextView) findViewById(R.id.labelDien);
            text.setVisibility(View.VISIBLE);
            text = (TextView) findViewById(R.id.textDien);
            text.setVisibility(View.VISIBLE);
            String diena = dienuPav[Integer.parseInt(patalpos.get(ObjektPerz.TAG_rezerv[0]))-1]; //Paverciam skaiciu i pavadinima
            text.setText(diena);

            text = (TextView) findViewById(R.id.labelLaik);
            text.setVisibility(View.VISIBLE);
            text = (TextView) findViewById(R.id.textLaik);
            text.setVisibility(View.VISIBLE);
            text.setText(patalpos.get(ObjektPerz.TAG_rezerv[1]));

            text = (TextView) findViewById(R.id.labelSav);
            text.setVisibility(View.VISIBLE);
            text = (TextView) findViewById(R.id.textSavNr);
            text.setVisibility(View.VISIBLE);
            String sav = Pagrindinis.getWeekName(Integer.parseInt(patalpos.get(ObjektPerz.TAG_rezerv[2])));
            text.setText(sav);

            text = (TextView) findViewById(R.id.labelStatusas);
            text.setVisibility(View.VISIBLE);
            text = (TextView) findViewById(R.id.textStatusas);
            text.setVisibility(View.VISIBLE);
            text.setText(patalpos.get(ObjektPerz.TAG_rezerv[3]));

            //Dar spalvos suteikiam tekstui
            if(patalpos.get(ObjektPerz.TAG_rezerv[3]).contentEquals("rezervuojama") || patalpos.get(ObjektPerz.TAG_rezerv[3]).contentEquals("patvirtinta"))
                text.setTextColor(Color.GREEN);
            else
                text.setTextColor(Color.RED);

            text = (TextView) findViewById(R.id.labelRezLaik);
            text.setVisibility(View.VISIBLE);

                if (Pagrindinis.id.contentEquals(patalpos.get(SkelbimuPaieska.TAG_UserId))) {
                    //Diaktivuojamas mygtukas perziureti nuomotoja, nes cia prisijunge nuomotojas
                    but = (Button) findViewById(R.id.butPerzNuomot);
                    but.setVisibility(View.GONE);
                    but = (Button) findViewById(R.id.butPerzNuomin);
                    but.setVisibility(View.VISIBLE);
                    //Jei nuomininkas atsauke patvirtinta laika, tai nuomotojui bus parodoma tai
                    if (patalpos.get(ObjektPerz.TAG_rezerv[3]).contentEquals("atsaukta")) {
                        but = (Button) findViewById(R.id.butSupra);
                        but.setVisibility(View.VISIBLE);
                        text = (TextView) findViewById(R.id.labelKoment);
                        text.setVisibility(View.VISIBLE);
                        text = (TextView) findViewById(R.id.textRezKoment);
                        text.setVisibility(View.VISIBLE);
                        text.setText(patalpos.get(ObjektPerz.TAG_rezerv[5]));
                    }else {
                        //Jei patalpu nuomotojas perziuri tai jam uzkraunami patvirtinimo arba atmetimo mygtukai
                        but = (Button) findViewById(R.id.butAtm);
                        but.setVisibility(View.VISIBLE);
                        if (!patalpos.get(ObjektPerz.TAG_rezerv[3]).contentEquals("patvirtinta")) {
                            but = (Button) findViewById(R.id.butPatv);
                            but.setVisibility(View.VISIBLE);
                        }else
                        {
                            but = (Button) findViewById(R.id.butIsnuomoti);
                            but.setVisibility(View.VISIBLE);
                        }
                    }

                } else //Jei potencialus nuomininkas peržiūri
                {
                    //Ir nuomininko perziurejimo laikas buvo atmestas jam uzkraunamas atskiras UI
                    if (patalpos.get(ObjektPerz.TAG_rezerv[3]).contentEquals("atmesta")) {
                        but = (Button) findViewById(R.id.butSupra);
                        but.setVisibility(View.VISIBLE);
                        text = (TextView) findViewById(R.id.labelKoment);
                        text.setVisibility(View.VISIBLE);
                        text = (TextView) findViewById(R.id.textRezKoment);
                        text.setVisibility(View.VISIBLE);
                        text.setText(patalpos.get(ObjektPerz.TAG_rezerv[5]));
                    }else //Jei patalpu potencialus nuomininkas noretu atsaukti rezervavimo laika
                    {
                        but = (Button) findViewById(R.id.butAts);
                        but.setVisibility(View.VISIBLE);
                    }
                }

        }

        //Pridedamas layout'as priklausomai nuo skelbimo patalpu tipo
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(fragment != null)
        {
            fragmentTransaction.remove(fragment);
        }
        fragment = new SkelbSpec();
        fragment.setPatalpos(patalpos.get(SkelbimuPaieska.TAG_patalpuTip));
        fragment.setType("duomenys"); //Nustatoma kad grazintu perziuros layouta
        fragmentTransaction.add(R.id.insertHere, fragment);
        fragmentTransaction.commit();

    }

    @Override
    protected void onStart() {
        super.onStart();

        //Pildomi patalpu duomenys
        //Skelbimo duomenys
        final TextView saviv = (TextView) findViewById(R.id.textSavivald);
        final TextView gyv = (TextView) findViewById(R.id.textGyven);
        final TextView mikroR = (TextView) findViewById(R.id.textMikRaj);
        final TextView gatve = (TextView) findViewById(R.id.textGatve);
        final TextView plot = (TextView) findViewById(R.id.textPlotas);
        final TextView kain = (TextView) findViewById(R.id.textKaina);
        final TextView kom = (TextView) findViewById(R.id.textKomentaras);
        final TextView patTip = (TextView) findViewById(R.id.textPatTip);
        final RatingBar rate = (RatingBar) findViewById(R.id.ratingBar);
        final  TextView apsilank = (TextView) findViewById(R.id.textApsilank);

        saviv.setText(patalpos.get(SkelbimuPaieska.TAG_savivaldybe));
        gyv.setText(patalpos.get(SkelbimuPaieska.TAG_gyvenviete));
        mikroR.setText(patalpos.get(SkelbimuPaieska.TAG_mikroRaj));
        gatve.setText(patalpos.get(SkelbimuPaieska.TAG_gatve));
        plot.setText(patalpos.get(SkelbimuPaieska.TAG_plotas));
        kain.setText(patalpos.get(SkelbimuPaieska.TAG_kaina));
        kom.setText(patalpos.get(SkelbimuPaieska.TAG_komentaras));
        patTip.setText(patalpos.get(SkelbimuPaieska.TAG_patalpuTip));
        rate.setRating(Float.parseFloat(patalpos.get(SkelbimuPaieska.TAG_ivertinimas)));
        apsilank.setText(patalpos.get(ObjektPerz.TAG_apsilankymas));

        View v = fragment.getView();

        switch (patalpos.get(SkelbimuPaieska.TAG_patalpuTip))
        {
            case "Butas":
                final TextView aukstas = (TextView) v.findViewById(R.id.textAukstas);
                final TextView aukstuSk = (TextView) v.findViewById(R.id.textAukstSk);
                final TextView kambSk = (TextView) v.findViewById(R.id.textKambSk);
                final TextView metai = (TextView) v.findViewById(R.id.textMetai);
                final TextView namoNr = (TextView) v.findViewById(R.id.textNamNr);
                final TextView butoNr = (TextView) v.findViewById(R.id.textButNr);
                final TextView pastatTip = (TextView) v.findViewById(R.id.textPastTip);
                final TextView irengTip = (TextView) v.findViewById(R.id.textIreng);
                final TextView sildymTip = (TextView) v.findViewById(R.id.textSildymas);

                aukstas.setText(patalpos.get(SkelbimuPaieska.TAG_aukstas));
                aukstuSk.setText(patalpos.get(SkelbimuPaieska.TAG_aukstuSk));
                kambSk.setText(patalpos.get(SkelbimuPaieska.TAG_kambSk));
                metai.setText(patalpos.get(SkelbimuPaieska.TAG_metai));
                namoNr.setText(patalpos.get(SkelbimuPaieska.TAG_namoNr));
                butoNr.setText(patalpos.get(SkelbimuPaieska.TAG_butoNr));
                pastatTip.setText(patalpos.get(SkelbimuPaieska.TAG_pastatoTip));
                irengTip.setText(patalpos.get(SkelbimuPaieska.TAG_irengimoTip));
                sildymTip.setText(patalpos.get(SkelbimuPaieska.TAG_sildymoTip));
                break;
            case "Namas":

                final TextView metai1 = (TextView) v.findViewById(R.id.textMetai);
                final TextView namoNr1 = (TextView) v.findViewById(R.id.textNamNr);
                final TextView namoTip = (TextView) v.findViewById(R.id.textNamTip);
                final TextView pastatTip1 = (TextView) v.findViewById(R.id.textPastTip);
                final TextView irengTip1 = (TextView) v.findViewById(R.id.textIreng);
                final TextView sildymTip1 = (TextView) v.findViewById(R.id.textSildymas);

                metai1.setText(patalpos.get(SkelbimuPaieska.TAG_metai));
                namoNr1.setText(patalpos.get(SkelbimuPaieska.TAG_namoNr));
                namoTip.setText(patalpos.get(SkelbimuPaieska.TAG_namoTip));
                pastatTip1.setText(patalpos.get(SkelbimuPaieska.TAG_pastatoTip));
                irengTip1.setText(patalpos.get(SkelbimuPaieska.TAG_irengimoTip));
                sildymTip1.setText(patalpos.get(SkelbimuPaieska.TAG_sildymoTip));

                break;
        }
    }

    //-------------------------Mygtuku funkcionalumas-----------------------------------------------------------------

    //Grazinamas nuomotojo laisvas grafikas
    public void rezervuoti(View view)
    {
        startActivity(new Intent(this, LoadingScreen.class)); //Loadinimo screenas aktivuojamas

        String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/getLaikai";

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        //SITA VIETA KEICIAS
                        LoadingScreen.load.finish(); //Pradanginamas loadinomo screenas
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
                params.put("user_id", patalpos.get(SkelbimuPaieska.TAG_UserId)); //Nusiunciamas patalpu savininko id kad grazint jo grafika
                params.put(SkelbimuPaieska.TAG_post_id, patalpos.get(SkelbimuPaieska.TAG_post_id));
                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    //Nuomotojas patvirtina rezervavimo laika
    public void patv(View view)
    {
        startActivity(new Intent(this, LoadingScreen.class)); //Loadinimo screenas aktivuojamas

        String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/patvirtintiLaika";

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        //SITA VIETA KEICIAS
                        SkelbimuSarasas.skelbList.remove(patalpos);
                        patalpos.put("statusas", "patvirtinta");
                        SkelbimuSarasas.skelbList.add(patalpos);
                        finish();
                        LoadingScreen.load.finish(); //Pradanginamas loadinomo screenas
                        Toast.makeText(Patalpos.this, "Laikas patvirtintas", Toast.LENGTH_LONG).show();
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

                //I posta siunciami rezervavimo parametrai
                params.put(Patalpos.TAG_laikID, patalpos.get(Patalpos.TAG_laikID));

                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    //Nuomotojas atmeta rezervavimo laika
    public void atm(View view)
    {
        //Ismetamas dialogas, kad nuomotojas paliktu komentara del ko rezervavimo laikas buvo atmestas
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Palikite komentarą");


        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String komentaras = input.getText().toString();

                //CIA JAU PRASIDEDA API CALL

                String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/atmestiLaika";

                RequestQueue queue = Volley.newRequestQueue(Patalpos.this); //Request queu to hold the http requests

                StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                //SITA VIETA KEICIAS
                                SkelbimuSarasas.skelbList.remove(patalpos);
                                finish();
                                Toast.makeText(Patalpos.this, "Laikas atmestas", Toast.LENGTH_LONG).show();
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

                        //I posta siunciami rezervavimo parametrai
                        params.put(Patalpos.TAG_laikID, patalpos.get(Patalpos.TAG_laikID));
                        params.put(ObjektPerz.TAG_rezerv[5], komentaras);

                        return params;
                    }
                };
                queue.add(postRequest); //Issiunciamas post request
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    //Nuomininkas atsaukia rezervavimo patvirtinta ar nepatvirtinta laika
    public void ats(View view)
    {
        //Ismetamas dialogas, kad nuomotojas paliktu komentara del ko rezervavimo laikas buvo atmestas
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Palikite komentarą");


        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String komentaras = input.getText().toString();

                //CIA JAU PRASIDEDA API CALL

                String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/atsauktiLaika";

                RequestQueue queue = Volley.newRequestQueue(Patalpos.this); //Request queu to hold the http requests

                StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                //SITA VIETA KEICIAS
                                SkelbimuSarasas.skelbList.remove(patalpos);
                                finish();
                                Toast.makeText(Patalpos.this, "Laikas atšauktas", Toast.LENGTH_LONG).show();
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

                        //I posta siunciami rezervavimo parametrai
                        params.put(Patalpos.TAG_laikID, patalpos.get(Patalpos.TAG_laikID));
                        params.put(ObjektPerz.TAG_rezerv[5], komentaras);

                        return params;
                    }
                };
                queue.add(postRequest); //Issiunciamas post request
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    //Vartotojas peržiūri kitą vartotoją susijusį su, patalpomis
    public void perzVart(View view)
    {
        final String user_id;

        //Priklausomai nuo to kuris mygtukas paspaustas, issiunciamas user_id arba nuomininko arba nuomotojo
        switch (view.getId()) {
            case R.id.butPerzNuomin:
                user_id = patalpos.get(ObjektPerz.TAG_rezerv[4]);
                break;
            case R.id.butPerzNuomot:
                user_id = patalpos.get(SkelbimuPaieska.TAG_UserId);
                break;
            default:
                user_id = "what";
                break;
        }


        startActivity(new Intent(this, LoadingScreen.class)); //Loadinimo screenas aktivuojamas

        String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/perzVart";

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        //SITA VIETA KEICIAS
                        LoadingScreen.load.finish(); //Pradanginamas loadinomo screenas
                        //Toast.makeText(Patalpos.this, response, Toast.LENGTH_LONG).show();
                        Log.d("Response", response);
                        Intent intent = new Intent(Patalpos.this, VartotojoInfo.class);
                        intent.putExtra("response", response);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        LoadingScreen.load.finish(); //Pradanginamas loadinomo screenas
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

                //I posta siunciami nuomininko ID
                params.put(SkelbimuPaieska.TAG_UserId, user_id);

                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    //Nuomininkui patvirtinus kad supranta kodel jo laiko rezervavimas buvo atmestas, laikas pasalinamas is duombazes
    public void supra(View view)
    {
        startActivity(new Intent(this, LoadingScreen.class)); //Loadinimo screenas aktivuojamas

        String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/pasalintiLaika";

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        //SITA VIETA KEICIAS
                        SkelbimuSarasas.skelbList.remove(patalpos);
                        finish();
                        LoadingScreen.load.finish(); //Pradanginamas loadinomo screenas
                        Toast.makeText(Patalpos.this, ":c", Toast.LENGTH_LONG).show();
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

                //I posta siunciamas grafiko id
                params.put(ObjektPerz.TAG_rezerv[6], patalpos.get(ObjektPerz.TAG_rezerv[6]));

                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    //Skelbimo redagavimas
    public void redSkelb(View view) {
        startActivity(new Intent(this, RedaguotiSkelbima.class));
    }

    //Istrinti skelbima
    public void istrintSkelb(View view)
    {
        String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/istrintSkelb";

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        //SITA VIETA KEICIAS
                        if(!response.contentEquals("exists")) {
                            SkelbimuSarasas.skelbList.remove(patalpos);
                            finish();
                            Toast.makeText(Patalpos.this, "Skelbimas ištrintas", Toast.LENGTH_LONG).show();
                        }else
                        {
                            Toast.makeText(Patalpos.this, "Šitom patalpom yra rezervuojamų ir/arba patvirtintų laikų, peržvelkite Rezervavimo Laikai skiltyje", Toast.LENGTH_LONG).show();
                        }
                        //Toast.makeText(Patalpos.this, response, Toast.LENGTH_LONG).show();
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

                //Siunciamas post id pagal kuri bus istrintas skelbimas
                params.put(SkelbimuPaieska.TAG_post_id, patalpos.get(SkelbimuPaieska.TAG_post_id));

                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    //Istrinamas istorijos irasas
    public void istrintIstorija(View view)
    {
        String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/istrintIstorija";

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        //SITA VIETA KEICIAS
                        SkelbimuSarasas.skelbList.remove(Patalpos.patalpos);
                        finish();
                        Toast.makeText(Patalpos.this, "Istorijos įrašas pašalintas", Toast.LENGTH_LONG).show();
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(Patalpos.this, error.getMessage(), Toast.LENGTH_LONG).show();
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

                //Siunciamas post id pagal kuri bus istrintas skelbimas
                params.put(SkelbimuPaieska.TAG_post_id, patalpos.get(SkelbimuPaieska.TAG_post_id));

                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    //Nuomotojas isnuomoja patalpas
    public void isnuomoti(View view)
    {

        if(SkelbimuSarasas.sarasTip.contentEquals("rezervavimas"))
        {
            issiustiIsnuom(ObjektPerz.TAG_rezerv[4], patalpos.get(ObjektPerz.TAG_rezerv[4]));
        }else {
            //Ismetamas dialogas, kad nuomotojas paliktu komentara del ko rezervavimo laikas buvo atmestas
            AlertDialog.Builder builder = new AlertDialog.Builder(Patalpos.this);
            builder.setTitle("Įveskite vartotojo email");

            final EditText input = new EditText(Patalpos.this);

            input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            builder.setView(input);


            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String email = input.getText().toString();
                    issiustiIsnuom("email2", email);
                }
            });

            builder.show();
        }
    }

    //Cia vyksta pats siuntimas isnuomojimo
    public void issiustiIsnuom(final String tagas, final String value)
    {
        startActivity(new Intent(this, LoadingScreen.class)); //Loadinimo screenas aktivuojamas

        String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/isnuomoti";

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        //SITA VIETA KEICIAS
                        /*Toast.makeText(Patalpos.this, response, Toast.LENGTH_LONG).show();
                        LoadingScreen.load.finish(); //Pradanginamas loadinomo screenas*/
                        if(response.contentEquals("none"))
                        {
                            LoadingScreen.load.finish(); //Pradanginamas loadinomo screenas
                            Toast.makeText(Patalpos.this, "Vartotojas su tokiu email neegzistuoja", Toast.LENGTH_LONG).show();
                        }else if (response.contentEquals("nuomot"))
                        {
                            LoadingScreen.load.finish(); //Pradanginamas loadinomo screenas
                            Toast.makeText(Patalpos.this, "Jūs negalite išnuomoti patalpų sau", Toast.LENGTH_LONG).show();
                        }
                        else {
                            //Pasalinamos isnnuomotos patalpos is saraso
                            for(int i = 0; i < SkelbimuSarasas.skelbList.size(); i++)
                            {
                                HashMap<String, String> skelbimas = SkelbimuSarasas.skelbList.get(i);
                                if(skelbimas.get(SkelbimuPaieska.TAG_post_id)
                                        .contentEquals(patalpos.get(SkelbimuPaieska.TAG_post_id)))
                                {
                                    SkelbimuSarasas.skelbList.remove(skelbimas);
                                }
                            }
                            LoadingScreen.load.finish(); //Pradanginamas loadinomo screenas
                            finish();
                            Toast.makeText(Patalpos.this, "Patalpos išnuomotos ir visi kiti susidomėja vartotojai informuoti apie tai", Toast.LENGTH_LONG).show();
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

                params.put(SkelbimuPaieska.TAG_post_id, patalpos.get(SkelbimuPaieska.TAG_post_id));
                params.put(tagas, value);
                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    public void skaitliukai(View view)
    {
        startActivity(new Intent(this, SkaitliukuDuom.class));
    }

    public void nuotraukosPries(View view)
    {
        NuotraukosDaug.nuotTipas = "url";
        startActivity(new Intent(Patalpos.this, Nuotraukos.class));
    }

    public void getDok(View view)
    {
        FailuList.fileType = "dokumentai";
        startActivity(new Intent(Patalpos.this, FailuList.class));
    }

    public void saskaitos(View view)
    {
        FailuList.fileType = "saskaitos";
        startActivity(new Intent(Patalpos.this, FailuList.class));
    }

    public void atvykimas(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Įveskite apsilankymo datą ir laiką");


        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);


        builder.setPositiveButton("Informuoti", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String apsilankymas = input.getText().toString();

                String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/postApsilank";

                RequestQueue queue = Volley.newRequestQueue(Patalpos.this); //Request queu to hold the http requests

                StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                //SITA VIETA KEICIAS

                                    Toast.makeText(Patalpos.this, "Nuomininkas informuotas", Toast.LENGTH_LONG).show();

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

                        params.put(SkelbimuPaieska.TAG_post_id, patalpos.get(SkelbimuPaieska.TAG_post_id));
                        params.put(ObjektPerz.TAG_apsilankymas, apsilankymas);
                        return params;
                    }
                };
                queue.add(postRequest); //Issiunciamas post request
            }
        });
        builder.setNegativeButton("Atšaukti", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    //Patalpu nuoma baigiama
    public void nutraukimas(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ar tikrai norite nutraukti nuomą?");

        builder.setPositiveButton("Taip", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/postNutraukt";

                RequestQueue queue = Volley.newRequestQueue(Patalpos.this); //Request queu to hold the http requests

                StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                //SITA VIETA KEICIAS
                                Toast.makeText(Patalpos.this, "Nuoma nutraukta", Toast.LENGTH_LONG).show();
                                SkelbimuSarasas.skelbList.remove(Patalpos.patalpos);
                                finish();
                                //Toast.makeText(Patalpos.this, response, Toast.LENGTH_LONG).show();
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

                        params.put(SkelbimuPaieska.TAG_post_id, patalpos.get(SkelbimuPaieska.TAG_post_id));
                        return params;
                    }
                };
                queue.add(postRequest); //Issiunciamas post request
            }
        });
        builder.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void atsiliepimasSkelb(View view)
    {
        Atsiliepimas.tipas = "skelbimas";
        startActivity(new Intent(this, Atsiliepimas.class));
    }

    public void getSkelbAts(View view)
    {
        AtsiliepimaiList.atsTipas = "skelbimas";
        AtsiliepimaiList.rev_id = Patalpos.patalpos.get(SkelbimuPaieska.TAG_post_id);
        startActivity(new Intent(this, AtsiliepimaiList.class));
    }

    public void perzNuot(View view)
    {
        NuotraukosDaug.nuotTipas = "lel";
        startActivity(new Intent(this, NuotraukosDaug.class));
    }
    //-------------------------Mygtuku funkcionalumas END-----------------------------------------------------------------

    //Grafiko grazinimo apdorojimas
    private void responseApdoroti(String response)
    {
        if(response.contentEquals("none")) {

            Toast.makeText(Patalpos.this, "Skelbimo savininkas nesusidaręs grafiko", Toast.LENGTH_LONG).show();
        }
        else if(response.contentEquals("exists"))
        {
            Toast.makeText(Patalpos.this, "Jūs jau esate užsirezervavęs laiką šitom patalpom, jas galite pamatyti Rezervavimo Laikai skiltyje", Toast.LENGTH_LONG).show();
        }else if(response.contentEquals("nuomot"))
        {
        Toast.makeText(Patalpos.this, "Jūs šio skelbimo savininkas, todėl negalite rezervuoti laiko", Toast.LENGTH_LONG).show();
         }
        else
        {
            PasirinktiLaika.laikuList = ParseJSON(response);
            startActivity(new Intent(this, PasirinktiLaika.class));
        }
    }

    private ArrayList<HashMap<String, String>> ParseJSON(String json) {
        if (json != null) {
            try {
// Hashmap for ListView
                ArrayList<HashMap<String, String>> laikList = new ArrayList<HashMap<String, String>>();
                //JSONObject jsonObj = new JSONObject(json);

// Getting JSON Array node
                JSONArray skelbimai = new JSONArray(json);

// looping through All posts
                for (int i = 0; i < skelbimai.length(); i++) {
                    JSONObject c = skelbimai.getJSONObject(i);

                    String laikId = c.getString(TAG_laikID);
                    String diena = c.getString(TAG_diena);
                    String laikas = c.getString(TAG_laikas);
                    String savaitesNr = c.getString(TAG_savaitesNr);

                    // tmp hashmap for single post
                    HashMap<String, String> skelbimas = new HashMap<String, String>();

// adding every child node to HashMap key => value
                    skelbimas.put(TAG_laikID, laikId);
                    skelbimas.put(TAG_diena, diena);
                    skelbimas.put(TAG_laikas, laikas);
                    skelbimas.put(TAG_savaitesNr, savaitesNr);

// adding student to students list
                    laikList.add(skelbimas);
                }
                return laikList;
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
