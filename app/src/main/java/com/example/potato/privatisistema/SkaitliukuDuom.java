package com.example.potato.privatisistema;

import android.content.Intent;
import android.content.res.Resources;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SkaitliukuDuom extends AppCompatActivity {

    public static final String TAG_MEN = "menesis";
    public static final String [] TAGS_sasDuomKeys = {"elektra", "dujos", "karstas", "saltas"};
    public static final int [] TAGS_sasDuomStrings = {R.id.stringElektra, R.id.stringDujos, R.id.stringKarstas, R.id.stringSaltas};
    public ArrayList<HashMap<String, String>> duomList;

    int monthPos;
    String menesis;

    Spinner spinMen, spinMet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skaitliuku_duom);

        spinMen = spinnerLoad(R.id.spinnerMen, R.array.menesiai);
        spinMet = spinnerLoad(R.id.spinnerMet, R.array.metai);

        //Jei nuomotojas atsidare skaitliuku duomenis
        if(SkelbimuSarasas.sarasTip.contentEquals("nuomot"))
        {
            Button but = (Button) findViewById(R.id.butSave);
            but.setVisibility(View.GONE);

            //Disablinam inputa duomenim
            for (int j = 0; j < TAGS_sasDuomStrings.length; j++) {
                final EditText text = (EditText) findViewById(TAGS_sasDuomStrings[j]);
                text.setEnabled(false);
            }

            grazintSkaitlDuomenis();
        }

        //Metu spinner listener
        spinMet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //Disablinam inputa duomenim
                for (int j = 0; j < TAGS_sasDuomStrings.length; j++) {
                    final EditText text = (EditText) findViewById(TAGS_sasDuomStrings[j]);
                    text.setText("");
                }
                if(duomList != null)
                {
                    for(int i = 0; i < duomList.size(); i++) {

                        HashMap<String, String> duomenys = duomList.get(i);

                        if(duomenys.get(SkelbimuPaieska.TAG_metai).contentEquals(spinMet.getSelectedItem().toString())
                                && duomenys.get(TAG_MEN).contentEquals(spinMen.getSelectedItem().toString()))
                        {
                            for (int j = 0; j < TAGS_sasDuomStrings.length; j++) {

                                final EditText text = (EditText) findViewById(TAGS_sasDuomStrings[j]);
                                text.setText(duomenys.get(TAGS_sasDuomKeys[j]));
                            }
                        }

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        //Menesiu spinner listener
        spinMen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //Disablinam inputa duomenim
                for (int j = 0; j < TAGS_sasDuomStrings.length; j++) {
                    final EditText text = (EditText) findViewById(TAGS_sasDuomStrings[j]);
                    text.setText("");
                }
                if(duomList != null)
                {
                    for(int i = 0; i < duomList.size(); i++) {

                        HashMap<String, String> duomenys = duomList.get(i);

                        if(duomenys.get(SkelbimuPaieska.TAG_metai).contentEquals(spinMet.getSelectedItem().toString())
                                && duomenys.get(TAG_MEN).contentEquals(spinMen.getSelectedItem().toString()))
                        {
                            for (int j = 0; j < TAGS_sasDuomStrings.length; j++) {

                                final EditText text = (EditText) findViewById(TAGS_sasDuomStrings[j]);
                                text.setText(duomenys.get(TAGS_sasDuomKeys[j]));
                            }
                        }

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }


    private Spinner spinnerLoad(int spinnerId, int spinnerArray)
    {
        //Sukuriamas spinneris, su aukstu tipais
        Spinner spinner = (Spinner) findViewById(spinnerId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                spinnerArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        return spinner;
    }

    public void issaugotSkaitl(View view)
    {
        String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/saveSkaitlRodm";

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        finish();
                        Toast.makeText(SkaitliukuDuom.this, "Skaitliukų duomenys išsaugoti", Toast.LENGTH_LONG).show();
                        //Toast.makeText(SkaitliukuDuom.this, response, Toast.LENGTH_LONG).show();
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(SkaitliukuDuom.this, error.getMessage(), Toast.LENGTH_LONG).show();
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

                params.put(SkelbimuPaieska.TAG_post_id, Patalpos.patalpos.get(SkelbimuPaieska.TAG_post_id));




                //-------------Traukiami duomenys is inputo------------------------------------------------------
                Spinner spin = (Spinner) findViewById(R.id.spinnerMet);
                params.put(SkelbimuPaieska.TAG_metai, spin.getSelectedItem().toString());
                spin = (Spinner) findViewById(R.id.spinnerMen);
                params.put(TAG_MEN, spin.getSelectedItem().toString());

                for(int i = 0; i < TAGS_sasDuomStrings.length; i++)
                {
                    final EditText text = (EditText) findViewById(TAGS_sasDuomStrings[i]);
                    params.put(TAGS_sasDuomKeys[i], text.getText().toString());
                }

                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    //NUomotojui tik grazinami skaitliuku duomenys
    public void grazintSkaitlDuomenis()
    {
        startActivity(new Intent(this, LoadingScreen.class)); //Loadinimo screenas aktivuojamas

        String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/grazintSkaitlDuom";

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        if(response.contentEquals("none"))
                        {
                            LoadingScreen.load.finish(); //Pradanginamas loadinomo screenas
                            Toast.makeText(SkaitliukuDuom.this, "Skaitliukų duomenys šiem metam ir šiam mėnesiui neįkelti", Toast.LENGTH_LONG).show();
                            finish();
                        }else
                        {
                            responseApdoroti(response);
                            LoadingScreen.load.finish(); //Pradanginamas loadinomo screenas
                        }
                        //Toast.makeText(SkaitliukuDuom.this, response, Toast.LENGTH_LONG).show();
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(SkaitliukuDuom.this, error.getMessage(), Toast.LENGTH_LONG).show();
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

                params.put(SkelbimuPaieska.TAG_post_id, Patalpos.patalpos.get(SkelbimuPaieska.TAG_post_id));

                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    private void responseApdoroti(String response)
    {
        ParseJSON(response);
    }

    private ArrayList<HashMap<String, String>> ParseJSON(String json) {
        if (json != null) {
            try {


                duomList = new ArrayList<HashMap<String, String>>();

                JSONArray duom = new JSONArray(json);

                for (int i = 0; i < duom.length(); i++) {
                    JSONObject c = duom.getJSONObject(i);

                    HashMap<String, String> duomenys = new HashMap<>();

                    duomenys.put(SkelbimuPaieska.TAG_metai, c.getString(SkelbimuPaieska.TAG_metai));
                    duomenys.put(TAG_MEN, c.getString(TAG_MEN));
                    for (int j = 0; j < TAGS_sasDuomStrings.length; j++) {
                        duomenys.put(TAGS_sasDuomKeys[j], c.getString(TAGS_sasDuomKeys[j]));
                    }
                    duomList.add(duomenys);

                    if(duomenys.get(SkelbimuPaieska.TAG_metai).contentEquals("2016") && duomenys.get(TAG_MEN).contentEquals("Sausis"))
                    {
                        for (int j = 0; j < TAGS_sasDuomStrings.length; j++) {
                            final EditText text = (EditText) findViewById(TAGS_sasDuomStrings[j]);
                            text.setText(duomenys.get(TAGS_sasDuomKeys[j]));
                        }
                    }
                }

                spinMet.setSelection(0); //Kad aktivuotusi listeneris

                return null;

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
