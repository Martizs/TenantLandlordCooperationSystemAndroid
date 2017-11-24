package com.example.potato.privatisistema;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

public class SkelbimuPaieska extends AppCompatActivity {

    //Response'o tagai
    //------------------Skelbimo --------------------------------------
    public static final String TAG_patalpuTip = "patalpuTipas";
    public static final String TAG_savivaldybe = "savivaldybe";
    public static final String TAG_mikroRaj = "mikroRaj";
    public static final String TAG_gyvenviete = "gyvenviete";
    public static final String TAG_gatve = "gatve";
    public static final String TAG_plotas = "plotas";
    public static final String TAG_kaina = "kaina";
    public static final String TAG_ivertinimas = "ivertinimas";
    public static final String TAG_komentaras = "komentaras";
    public static final String TAG_UserId = "user_id";
    public static final String TAG_nuotPath ="nuot_path";
    //-------Skelbimo END--------------------------------------------
    //------------------Buto --------------------------------------
    public static final String TAG_aukstuSk = "aukstuSk";
    public static final String TAG_aukstas = "aukstas";
    public static final String TAG_pastatoTip = "pastatoTip";
    public static final String TAG_irengimoTip = "irengimoTip";
    public static final String TAG_sildymoTip = "sildymoTip";
    public static final String TAG_kambSk = "kambSk";
    public static final String TAG_metai = "metai";
    public static final String TAG_namoNr = "namoNr";
    public static final String TAG_butoNr = "butoNr";
    public static final String TAG_post_id = "post_id";
    //-------Buto END--------------------------------------------
    //------------------Namo --------------------------------------
    public static final String TAG_namoTip = "namoTip";
    //-------Namo END--------------------------------------------
    //Response'o tagai END



    SkelbSpec fragment;
    int patalpTip = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skelbimu_paieska);

        Spinner spinner = patalpSpinner();


        //Sukuriamas listeneris laukiantis tipo pasirinkimo ir pagal tai uzkraunantis specifinius duomenis
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                    patalpTip = (int)id;
                    //Pridedamas layout'as priklausomai nuo tipo pasirinkimo
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                     if(fragment != null)
                     {
                         fragmentTransaction.remove(fragment);
                     }
                    fragment = new SkelbSpec();
                    fragment.setAttr((int)id);
                    fragment.setType("paieska"); //Nustatoma kad grazintu paieskos layouta
                    fragmentTransaction.add(R.id.insertHere, fragment);
                    fragmentTransaction.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

    }


    //-----------------------HttpRequest-------------------------------------------------------------------
    public void paieska(View view)
    {
        startActivity(new Intent(this, LoadingScreen.class)); //Loadinimo screenas aktivuojamas

        String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/paieska";

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
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
                        Toast.makeText(SkelbimuPaieska.this, error.getMessage(), Toast.LENGTH_LONG).show();
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

                //-------------Traukiami duomenys is inputo------------------------------------------------------
                final EditText saviv = (EditText) findViewById(R.id.stringSavivald);
                final EditText gyv = (EditText) findViewById(R.id.stringGyven);
                final EditText mikroR = (EditText) findViewById(R.id.stringMikRaj);
                final EditText gatve = (EditText) findViewById(R.id.stringGatve);
                final EditText plotN = (EditText) findViewById(R.id.intPlotasNuo);
                final EditText plotI = (EditText) findViewById(R.id.intPlotasIki);
                final EditText kainN = (EditText) findViewById(R.id.intKainNuo);
                final EditText kainI = (EditText) findViewById(R.id.intKainIki);
                final Spinner patalpT = (Spinner) findViewById(R.id.spinnerTipas);

                params.put(TAG_patalpuTip, patalpT.getSelectedItem().toString());
                params.put(TAG_savivaldybe, saviv.getText().toString());
                params.put(TAG_gyvenviete, gyv.getText().toString());
                params.put(TAG_mikroRaj, mikroR.getText().toString());
                params.put(TAG_gatve, gatve.getText().toString());
                params.put(TAG_plotas+"Nuo", plotN.getText().toString());
                params.put(TAG_plotas+"Iki", plotI.getText().toString());
                params.put(TAG_kaina+"Nuo", kainN.getText().toString());
                params.put(TAG_kaina+"Iki", kainI.getText().toString());

                switch (patalpTip)
                {
                    case 0:
                        View frag = fragment.getView();
                        final EditText aukstuSkN = (EditText) frag.findViewById(R.id.aukstSkNuo);
                        final EditText aukstuSkI = (EditText) frag.findViewById(R.id.aukstSkIki);
                        final EditText kambSkN = (EditText) frag.findViewById(R.id.intKambNuo);
                        final EditText kambSkI = (EditText) frag.findViewById(R.id.intKambIki);
                        final EditText metaiN = (EditText) frag.findViewById(R.id.metaiNuo);
                        final EditText metaiI = (EditText) frag.findViewById(R.id.metaiIki);
                        final Spinner aukstas = (Spinner) frag.findViewById(R.id.spinnerAukstPas);
                        final Spinner pastatTip = (Spinner) frag.findViewById(R.id.spinnerPastatas);
                        final Spinner irengTip = (Spinner) frag.findViewById(R.id.spinnerIrengimas);
                        final Spinner sildymTip = (Spinner) frag.findViewById(R.id.spinnerSildymas);

                        params.put(TAG_aukstuSk+"Nuo", aukstuSkN.getText().toString());
                        params.put(TAG_aukstuSk+"Iki", aukstuSkI.getText().toString());
                        params.put(TAG_aukstas, aukstas.getSelectedItem().toString());
                        params.put(TAG_pastatoTip, pastatTip.getSelectedItem().toString());
                        params.put(TAG_irengimoTip, irengTip.getSelectedItem().toString());
                        params.put(TAG_sildymoTip, sildymTip.getSelectedItem().toString());
                        params.put(TAG_kambSk+"Nuo", kambSkN.getText().toString());
                        params.put(TAG_kambSk+"Iki", kambSkI.getText().toString());
                        params.put(TAG_metai+"Nuo", metaiN.getText().toString());
                        params.put(TAG_metai+"Iki", metaiI.getText().toString());
                        break;
                    case 1:
                        View frag1 = fragment.getView();
                        final EditText metaiN1 = (EditText) frag1.findViewById(R.id.metaiNuo);
                        final EditText metaiI1 = (EditText) frag1.findViewById(R.id.metaiIki);
                        final Spinner namoTip = (Spinner) frag1.findViewById(R.id.spinnerNamas);
                        final Spinner pastatTip1 = (Spinner) frag1.findViewById(R.id.spinnerPastatas);
                        final Spinner irengTip1 = (Spinner) frag1.findViewById(R.id.spinnerIrengimas);
                        final Spinner sildymTip1 = (Spinner) frag1.findViewById(R.id.spinnerSildymas);

                        params.put(TAG_pastatoTip, pastatTip1.getSelectedItem().toString());
                        params.put(TAG_irengimoTip, irengTip1.getSelectedItem().toString());
                        params.put(TAG_sildymoTip, sildymTip1.getSelectedItem().toString());
                        String lel = namoTip.getSelectedItem().toString();
                        params.put(TAG_namoTip, namoTip.getSelectedItem().toString());
                        params.put(TAG_metai+"Nuo", metaiN1.getText().toString());
                        params.put(TAG_metai+"Iki", metaiI1.getText().toString());
                        break;
                }

                //-------------Traukiami duomenys is inputo END--------------------------------------------------



                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }
    //-----------------------HttpRequest END-------------------------------------------------------------------



    private Spinner patalpSpinner()
    {
        //Sukuriamas spinneris, su patalpu tipais
        Spinner spinner = (Spinner) findViewById(R.id.spinnerTipas);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.patalpuTipai, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        return spinner;
    }

    private void responseApdoroti(String response)
    {
        if(!response.contentEquals("none")) {
            SkelbimuSarasas.sarasTip = "paprastas";
            SkelbimuSarasas.skelbList = ParseJSON(response);
            startActivity(new Intent(this, SkelbimuSarasas.class));

        }else
        {
            Toast.makeText(SkelbimuPaieska.this, "Skelbimų nerasta", Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<HashMap<String, String>> ParseJSON(String json) {
        if (json != null) {
            try {
// Hashmap for ListView
                ArrayList<HashMap<String, String>> skelbListas = new ArrayList<HashMap<String, String>>();
                //JSONObject jsonObj = new JSONObject(json);

// Getting JSON Array node
                JSONArray skelbimai = new JSONArray(json);

// looping through All posts
                for (int i = 0; i < skelbimai.length(); i++) {
                    JSONObject c = skelbimai.getJSONObject(i);

                    String patalpuTip = c.getString(TAG_patalpuTip);
                    String savivaldybe = c.getString(TAG_savivaldybe);
                    String mikroRaj = c.getString(TAG_mikroRaj);
                    String gyvenviete = c.getString(TAG_gyvenviete);
                    String gatve = c.getString(TAG_gatve);
                    String plotas = c.getString(TAG_plotas);
                    String kaina = c.getString(TAG_kaina);
                    String ivertinimas = c.getString(TAG_ivertinimas);
                    String komentaras = c.getString(TAG_komentaras);
                    String userId = c.getString(TAG_UserId);
                    String nuotPath = c.getString(TAG_nuotPath);
                    // tmp hashmap for single post
                    HashMap<String, String> skelbimas = new HashMap<String, String>();

// adding every child node to HashMap key => value
                    skelbimas.put(TAG_patalpuTip, patalpuTip);
                    skelbimas.put(TAG_savivaldybe, savivaldybe);
                    skelbimas.put(TAG_mikroRaj, mikroRaj);
                    skelbimas.put(TAG_gyvenviete, gyvenviete);
                    skelbimas.put(TAG_gatve, gatve);
                    skelbimas.put(TAG_plotas, plotas);
                    skelbimas.put(TAG_kaina, kaina);
                    skelbimas.put(TAG_ivertinimas, ivertinimas);
                    skelbimas.put(TAG_komentaras, komentaras);
                    skelbimas.put(TAG_UserId, userId);
                    skelbimas.put(TAG_nuotPath, nuotPath);

                    switch (patalpuTip) {

                        case "Butas":
                            String aukstuSk = c.getString(TAG_aukstuSk);
                            String aukstas = c.getString(TAG_aukstas);
                            String pastatoTip = c.getString(TAG_pastatoTip);
                            String irengimoTip = c.getString(TAG_irengimoTip);
                            String sildymoTip = c.getString(TAG_sildymoTip);
                            String kambSk = c.getString(TAG_kambSk);
                            String metai = c.getString(TAG_metai);
                            String namoNr = c.getString(TAG_namoNr);
                            String butoNr = c.getString(TAG_butoNr);
                            String post_id = c.getString(TAG_post_id);

                            skelbimas.put(TAG_aukstuSk, aukstuSk);
                            skelbimas.put(TAG_aukstas, aukstas);
                            skelbimas.put(TAG_pastatoTip, pastatoTip);
                            skelbimas.put(TAG_irengimoTip, irengimoTip);
                            skelbimas.put(TAG_sildymoTip, sildymoTip);
                            skelbimas.put(TAG_kambSk, kambSk);
                            skelbimas.put(TAG_metai, metai);
                            skelbimas.put(TAG_namoNr, namoNr);
                            skelbimas.put(TAG_butoNr, butoNr);
                            skelbimas.put(TAG_post_id, post_id);

                            break;
                        case "Namas":
                            String namoTip = c.getString(TAG_namoTip);
                            String pastatoTipN = c.getString(TAG_pastatoTip);
                            String irengimoTipN = c.getString(TAG_irengimoTip);
                            String sildymoTipN = c.getString(TAG_sildymoTip);
                            String metaiN = c.getString(TAG_metai);
                            String namoNrN = c.getString(TAG_namoNr);
                            String post_idN = c.getString(TAG_post_id);

                            skelbimas.put(TAG_namoTip, namoTip);
                            skelbimas.put(TAG_pastatoTip, pastatoTipN);
                            skelbimas.put(TAG_irengimoTip, irengimoTipN);
                            skelbimas.put(TAG_sildymoTip, sildymoTipN);
                            skelbimas.put(TAG_metai, metaiN);
                            skelbimas.put(TAG_namoNr, namoNrN);
                            skelbimas.put(TAG_post_id, post_idN);

                            break;
                    }

// adding student to students list
                    skelbListas.add(skelbimas);
                }
                return skelbListas;
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
