package com.example.potato.privatisistema;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PasirinktiLaika extends AppCompatActivity {

    public static ArrayList<HashMap<String, String>> laikuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasirinkti_laika);

        //Uzkraunamas savaites spinneris
        ArrayList<String> savaites = new ArrayList<String>();

        HashMap<String, String> savaite;

        String savCheck = "";

        int ii = 0;
        for(int i = 0; i < laikuList.size(); i++)
        {
            savaite = laikuList.get(i);

            if(!savCheck.contentEquals(savaite.get(Patalpos.TAG_savaitesNr)))
            {
                savCheck = savaite.get(Patalpos.TAG_savaitesNr);
                String sav = Pagrindinis.getWeekName(Integer.parseInt(savaite.get(Patalpos.TAG_savaitesNr))); //Konvertuojam savaites numeri i savaites pavadinima
                savaites.add(ii, sav);
                ii++;
            }
        }
        //Collections.sort(savaites);
        final Spinner savSpinner = spinnerLoad(R.id.spinnerSav, savaites);

        //Pagal pasirinkta savaite uzkraunamas dienu spinneris
        savSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                //Uzkraunamas dienu spinneris
                ArrayList<String> dienos = new ArrayList<String>();

                HashMap<String, String> diena;

                String dienCheck = "";

                int ii = 0;
                for(int i = 0; i < laikuList.size(); i++)
                {
                    diena = laikuList.get(i);
                    String sav = Pagrindinis.getWeekNumber(parentView.getSelectedItem().toString())+"";

                    if(diena.get(Patalpos.TAG_savaitesNr).contentEquals(sav)) {
                        if (!dienCheck.contentEquals(diena.get(Patalpos.TAG_diena))) {
                            dienCheck = diena.get(Patalpos.TAG_diena);
                            dienos.add(ii, diena.get(Patalpos.TAG_diena));
                            ii++;
                        }
                    }
                }
                //Collections.sort(dienos);
                for(int j = 0; j < dienos.size(); j++)
                {
                    String dienaz = Patalpos.dienuPav[Integer.parseInt(dienos.get(j))-1]; //Paverciam skaiciu i pavadinima
                    dienos.set(j, dienaz);
                }
                Spinner dienSpinner = spinnerLoad(R.id.spinnerDien, dienos);

                //Pagal pasirinkta diena uzkraunamas laiku spinneris
                dienSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                        //Uzkraunamas laiku spinneris
                        ArrayList<String> laikai = new ArrayList<String>();

                        HashMap<String, String> laikas;

                        String laikCheck = "";

                        int ii = 0;
                        for(int i = 0; i < laikuList.size(); i++)
                        {
                            String lel = parentView.getSelectedItem().toString();
                            int diena = 0;
                            for (int j = 0; j < Patalpos.dienuPav.length; j++) {
                                if (lel.contentEquals(Patalpos.dienuPav[j])) {
                                    diena = j + 1;
                                    break;
                                }
                            }

                            laikas = laikuList.get(i);

                            String sav = Pagrindinis.getWeekNumber(savSpinner.getSelectedItem().toString())+"";

                            if(laikas.get(Patalpos.TAG_diena).contentEquals(diena + "")
                                    &&  laikas.get(Patalpos.TAG_savaitesNr).contentEquals(sav)) {

                                if (laikas.get(Patalpos.TAG_diena).contentEquals(diena + "")) {
                                    if (!laikCheck.contentEquals(laikas.get(Patalpos.TAG_laikas))) {
                                        laikCheck = laikas.get(Patalpos.TAG_laikas);
                                        laikai.add(ii, laikas.get(Patalpos.TAG_laikas));
                                        ii++;
                                    }
                                }
                            }
                        }
                        Collections.sort(laikai);
                        spinnerLoad(R.id.spinnerLaik, laikai);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

    }

    private Spinner spinnerLoad(int spinnerId, ArrayList<String> spinnerArray)
    {
        //Sukuriamas spinneris, su aukstu tipais
        Spinner spinner = (Spinner) findViewById(spinnerId);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        return spinner;
    }

    //Laiko rezervavimas
    public void rezervuotiLaika(View view)
    {
        final Spinner savSpinner = (Spinner) findViewById(R.id.spinnerSav);
        final Spinner dienSpinner = (Spinner) findViewById(R.id.spinnerDien);
        final Spinner laikSpinner = (Spinner) findViewById(R.id.spinnerLaik);

        String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/postRezLaikas";

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        //SITA VIETA KEICIAS
                        //Toast.makeText(PasirinktiLaika.this, response, Toast.LENGTH_LONG).show();
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
                String lel = dienSpinner.getSelectedItem().toString();
                int diena = 0;
                for(int j = 0; j < Patalpos.dienuPav.length; j++)
                {
                    if(lel.contentEquals(Patalpos.dienuPav[j]))
                    {
                        diena = j+1;
                        break;
                    }
                }

                String laikoID = "";
                for(int i = 0; i < laikuList.size(); i++)
                {

                    String sav = Pagrindinis.getWeekNumber(savSpinner.getSelectedItem().toString())+"";

                    HashMap<String, String> laikas = laikuList.get(i);
                    if(laikas.get(Patalpos.TAG_diena).contentEquals(diena+"") &&
                            laikas.get(Patalpos.TAG_laikas).contentEquals(laikSpinner.getSelectedItem().toString())
                            && laikas.get(Patalpos.TAG_savaitesNr).contentEquals(sav))
                    {
                        laikoID = laikas.get(Patalpos.TAG_laikID);
                    }
                }
                params.put(Patalpos.TAG_laikID, laikoID);
                // Paduodamas skelbimo ID, kad zinoti kurios patalpos norimos apziureti
                params.put(SkelbimuPaieska.TAG_post_id, Patalpos.patalpos.get(SkelbimuPaieska.TAG_post_id));

                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request

        Toast.makeText(PasirinktiLaika.this, "Laikas rezervuojamas", Toast.LENGTH_LONG).show();
        finish();
    }
}
