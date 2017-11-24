package com.example.potato.privatisistema;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TvarkytiGrafika extends AppCompatActivity {

    ArrayList<HashMap<String, String>> grafikas;

    SparseArray<String>  pasirinktiLaikai;
    SparseArray<String>   laikoStatusas;
    SparseIntArray grafId;
    SparseArray<String>   spalva;
    Spinner sav;
    Spinner dien;

    String spinDiena, spinSavaite;

    final String []laikai = {"8:00-9:00", "9:00-10:00", "10:00-11:00", "11:00-12:00", "12:00-13:00",
    "13:00-14:00", "14:00-15:00", "15:00-16:00", "16:00-17:00", "17:00-18:00", "18:00-19:00", "19:00-20:00",
    "20:00-21:00", "21:00-22:00", "22:00-23:00"};
    String []dienos;
    String ats;

    CustomAdapterLaikai adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvarkyti_grafika);

        Resources res = getResources();
        dienos = res.getStringArray(R.array.dienos);

        getGrafikas();
    }

    //Loadinimui pasibaigus pildomas visas grafikas
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        pasirinktiLaikai = new SparseArray<>();
        laikoStatusas = new SparseArray<>();
        grafId = new SparseIntArray();
        spalva = new SparseArray<>();

        for(int i = 0; i < laikai.length; i++)
        {
            spalva.put(i, "red");
        }

        listView = (ListView) findViewById(R.id.listLaikas);
        //Uzkraunam list view
        adapter = new CustomAdapterLaikai(this, laikai, spalva);

        //Uzkraunamas savaites START--------------------------------------------------------
        Resources res = getResources();
        String []savaites = res.getStringArray(R.array.savaites);
        ArrayList<String>savPav = new ArrayList<>();

        for (int i = 0; i < savaites.length; i++)
        {
            String savz = Pagrindinis.getWeekName(Integer.parseInt(savaites[i]));
            savPav.add(savz);
        }

        sav = (Spinner) findViewById(R.id.spinnerSav);
        ArrayAdapter<String> adapterz = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, savPav);
        adapterz.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sav.setAdapter(adapterz);
        //Uzkraunam savaites END------------------------------------------------------------

        dien = spinnerLoad(R.id.spinnerDien, R.array.dienos);

        //Savaiciu spinnerio listeneris
        sav.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(grafikas != null)
                {
                    pasirinktiLaikai = new SparseArray<>();
                    laikoStatusas = new SparseArray<>();
                    grafId = new SparseIntArray();
                    spalva = new SparseArray<>();

                    for(int ii = 0; ii < laikai.length; ii++)
                    {
                        spalva.put(ii, "red");
                    }

                    for(int i = 0; i < grafikas.size(); i++)
                    {
                        String lel = grafikas.get(i).get(ObjektPerz.TAG_rezerv[2]);
                        String savz = Pagrindinis.getWeekName(Integer.parseInt(grafikas.get(i).get(ObjektPerz.TAG_rezerv[2])));
                        if(savz.contentEquals(sav.getSelectedItem().toString()) &&
                                grafikas.get(i).get(ObjektPerz.TAG_rezerv[0]).contentEquals(getIndex(dien.getSelectedItem().toString(), dienos)+""))
                        {
                            for (int j = 0; j < laikai.length; j++) {

                                String laikas = laikai[j];

                                if (grafikas.get(i).get(ObjektPerz.TAG_rezerv[1]).contentEquals(laikas)) {

                                    grafId.put(j, i);
                                    pasirinktiLaikai.put(j, laikas); //Idedam egzistuojancius laikus i lista
                                    laikoStatusas.put(j, grafikas.get(i).get(ObjektPerz.TAG_rezerv[3]));

                                    if(grafikas.get(i).get(ObjektPerz.TAG_rezerv[3]).contentEquals("laisva"))
                                    {
                                        spalva.put(j, "green");

                                    }else
                                    {
                                        spalva.put(j, "yellow");
                                    }

                                }

                            }

                        }
                    }

                    adapter = new CustomAdapterLaikai(TvarkytiGrafika.this, laikai, spalva);
                    listView.setAdapter(adapter);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        //Dienu spinner listeneris
        dien.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if(grafikas != null)
                {
                    pasirinktiLaikai = new SparseArray<>();
                    laikoStatusas = new SparseArray<>();
                    grafId = new SparseIntArray();
                    spalva = new SparseArray<>();

                    for(int ii = 0; ii < laikai.length; ii++)
                    {
                        spalva.put(ii, "red");
                    }

                    for(int i = 0; i < grafikas.size(); i++)
                    {
                        int size = grafikas.size();
                        String lelz = grafikas.get(i).get(ObjektPerz.TAG_rezerv[2]);
                        lelz = sav.getSelectedItem().toString();
                        lelz = grafikas.get(i).get(ObjektPerz.TAG_rezerv[0]);
                        lelz = getIndex(dien.getSelectedItem().toString(), dienos)+"";
                        String laiks = grafikas.get(i).get(ObjektPerz.TAG_rezerv[1]);

                        String savz = Pagrindinis.getWeekName(Integer.parseInt(grafikas.get(i).get(ObjektPerz.TAG_rezerv[2])));
                        if(savz.contentEquals(sav.getSelectedItem().toString()) &&
                                grafikas.get(i).get(ObjektPerz.TAG_rezerv[0]).contentEquals(getIndex(dien.getSelectedItem().toString(), dienos)+""))
                        {
                            for (int j = 0; j < laikai.length; j++) {

                                String laikas = laikai[j];

                                if (grafikas.get(i).get(ObjektPerz.TAG_rezerv[1]).contentEquals(laikas)) {

                                    //---------------------
                                    HashMap<String, String> test = grafikas.get(i);
                                    String lel = test.get("diena");
                                    lel = test.get("laikas");
                                    lel = test.get("savaitesNr");
                                    //--------------------------------

                                    grafId.put(j, i);
                                    pasirinktiLaikai.put(j, laikas); //Idedam egzistuojancius laikus i lista
                                    laikoStatusas.put(j, grafikas.get(i).get(ObjektPerz.TAG_rezerv[3]));

                                    if(grafikas.get(i).get(ObjektPerz.TAG_rezerv[3]).contentEquals("laisva"))
                                    {
                                        spalva.put(j, "green");

                                    }else
                                    {
                                        spalva.put(j, "yellow");
                                    }

                                }

                            }

                        }
                    }

                    SparseArray<String> lel = spalva;
                    adapter = new CustomAdapterLaikai(TvarkytiGrafika.this, laikai, spalva);
                    listView.setAdapter(adapter);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });


        listView.setAdapter(adapter);

        //Laiku paspaudimo listeneris
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                try {
                    if(!laikoStatusas.get(position).contentEquals("laisva"))
                    {
                        Toast.makeText(TvarkytiGrafika.this, "Šis laikas patvirtintas arba rezervuojamas, negalite jo išmesti iš grafiko", Toast.LENGTH_LONG).show();

                    }else
                    {
                        pasirinktiLaikai.remove(position);
                        laikoStatusas.remove(position);
                        grafikas.remove(grafId.get(position));
                        int pozId = grafId.get(position);
                        grafId.delete(position);

                        for(int i = 0; i < laikai.length; i++)
                        {
                            try
                            {
                                if(grafId.get(i) > pozId)
                                {
                                    grafId.put(i, (grafId.get(i)-1));
                                }
                            }catch (Exception e)
                            {
                                Log.e("Lel", e.getMessage());
                            }
                        }



                        spalva.put(position, "red");

                        adapter = new CustomAdapterLaikai(TvarkytiGrafika.this, laikai, spalva);
                        listView.setAdapter(adapter);
                    }


                } catch ( Exception e ) {
                    //Idedam nauja hashmapa i laika.
                    if(grafikas == null) {
                        grafikas = new ArrayList<HashMap<String, String>>();
                    }
                    String savz = Pagrindinis.getWeekNumber(sav.getSelectedItem().toString()) + "";

                    HashMap<String, String> laikas = new HashMap<String, String>();
                    laikas.put(ObjektPerz.TAG_rezerv[0], getIndex(dien.getSelectedItem().toString(), dienos)+"");
                    laikas.put(ObjektPerz.TAG_rezerv[1], laikai[position]);
                    laikas.put(ObjektPerz.TAG_rezerv[2], savz);
                    laikas.put(ObjektPerz.TAG_rezerv[3], "laisva");
                    laikas.put(ObjektPerz.TAG_rezerv[4], "0");
                    laikas.put(ObjektPerz.TAG_rezerv[5], "nėra");
                    //Grafiko ID praskipinamas
                    laikas.put(ObjektPerz.TAG_rezerv[7], Pagrindinis.id+"");
                    laikas.put(ObjektPerz.TAG_rezerv[8], "0");


                    grafikas.add(laikas);


                    pasirinktiLaikai.put(position, laikai[position]); //Is nebuvusio atsiranda laisvas

                    laikoStatusas.put(position, "laisva");
                    grafId.put(position, grafikas.size()-1);
                    //Ir pakeiciama spalva i zalia
                    spalva.put(position, "green");

                    adapter = new CustomAdapterLaikai(TvarkytiGrafika.this, laikai, spalva);
                    listView.setAdapter(adapter);
                }

                listView.setSelection(position);
            }
        });


    }

    private Spinner spinnerLoad(int spinnerId, int spinnerArray)
    {

        Spinner spinner = (Spinner) findViewById(spinnerId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                spinnerArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        return spinner;
    }

    public void makeDien(View view)
    {
        //Tikrinama ar dabartiniam grafike nera rezervuojamu arba patvirtintu laiku
        for(int i = 0; i < grafikas.size(); i++)
        {
            if(!grafikas.get(i).get(ObjektPerz.TAG_rezerv[3]).contentEquals("laisva"))
            {
                Toast.makeText(TvarkytiGrafika.this, "Vienas arba daugiau laikų kuriuos norite keisti yra rezervuojami", Toast.LENGTH_LONG).show();
                return;
            }
        }

        new PildytGrafika().execute("makeDay");
    }

    public void makeSav(View view)
    {
        //Tikrinama ar dabartiniam grafike nera rezervuojamu arba patvirtintu laiku
        for(int i = 0; i < grafikas.size(); i++)
        {
            if(!grafikas.get(i).get(ObjektPerz.TAG_rezerv[3]).contentEquals("laisva"))
            {
                Toast.makeText(TvarkytiGrafika.this, "Vienas arba daugiau laikų kuriuos norite keisti yra rezervuojami", Toast.LENGTH_LONG).show();
                return;
            }
        }


        spinSavaite = sav.getSelectedItem().toString();
        new PildytGrafika().execute("makeSav");
    }

    public void trys(View view)
    {
        //Tikrinama ar dabartiniam grafike nera rezervuojamu arba patvirtintu laiku
        for(int i = 0; i < grafikas.size(); i++)
        {
            if(!grafikas.get(i).get(ObjektPerz.TAG_rezerv[3]).contentEquals("laisva"))
            {
                Toast.makeText(TvarkytiGrafika.this, "Vienas arba daugiau laikų kuriuos norite keisti yra rezervuojami", Toast.LENGTH_LONG).show();
                return;
            }
        }

        Toast.makeText(TvarkytiGrafika.this, "Grafikas išsaugotas, gali reikėti palaukta keleta minučių kol bus įkeltas", Toast.LENGTH_LONG).show();
        siusti();
    }

    public void siusti()
    {

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        String url = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/postGrafikas";

        JSONArray array = new JSONArray(grafikas);

        //Idedam dar email ir api prie viso JSON ARRAY.
        JSONObject json = new JSONObject();
        JSONObject jsonTemp = new JSONObject();
        try {
            json.put("email", Pagrindinis.email);
            json.put("api_token", Pagrindinis.tokenas);
            jsonTemp = array.getJSONObject(0);
            array.put(0, json);
            array.put(jsonTemp);
        }catch (Exception e)
        {
            Log.e("lel", e.getMessage());
        }

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.POST, url, array,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String lel = response.toString();
                        //Toast.makeText(TvarkytiGrafika.this, response.toString(), Toast.LENGTH_LONG).show();
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TvarkytiGrafika.this, error.toString(), Toast.LENGTH_LONG).show();
                        Log.d("Response", error.toString());
                    }
                });

        //Sito blet reikia kad time out error nemestu
        request.setRetryPolicy(new DefaultRetryPolicy(
                600000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request); //Issiunciamas post request
    }

    public void getGrafikas()
    {
        startActivityForResult(new Intent(this, LoadingScreen.class), 1); //Loadinimo screenas aktivuojamas

        String url = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/getGrafikas";

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        LoadingScreen.load.finish();
                        //Toast.makeText(TvarkytiGrafika.this, response, Toast.LENGTH_LONG).show();
                        responseApdoroti(response);
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TvarkytiGrafika.this, error.toString(), Toast.LENGTH_LONG).show();
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

            ParseJSON(response);
        }
    }

    private ArrayList<HashMap<String, String>> ParseJSON(String json) {
        if (json != null) {
            try {

                grafikas = new ArrayList<HashMap<String, String>>();


                JSONArray grafikai = new JSONArray(json);


                for (int i = 0; i < grafikai.length(); i++) {
                    JSONObject c = grafikai.getJSONObject(i);
                    HashMap<String,String> graf = new HashMap<>();

                  //Budas uzkrauti duomenis is Json objekto be hardkodinimo
                        String value;
                        for(String tagas : ObjektPerz.TAG_rezerv)
                        {
                            value = c.getString(tagas);
                            graf.put(tagas, value);
                        }

                    grafikas.add(graf);
                }
                return grafikas;
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

    private class PildytGrafika extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... tipas) {

            if(tipas[0].contentEquals("makeDay")) {
                ArrayList<String> pasLaik = new ArrayList<>();
                grafikas = new ArrayList<>();

                for(int i = 0; i < laikai.length; i++) {
                    if(pasirinktiLaikai.get(i) != null) {
                        pasLaik.add(pasirinktiLaikai.get(i));
                    }
                }

                for(int savaite = 0; savaite < 52; savaite++) {
                    for(int diena = 0; diena < 7; diena++) {
                        for(int laiks = 0; laiks < pasLaik.size(); laiks++) {
                            HashMap<String, String> laikas = new HashMap<String, String>();
                            laikas.put(ObjektPerz.TAG_rezerv[0], (diena+1)+"");
                            laikas.put(ObjektPerz.TAG_rezerv[1], pasLaik.get(laiks));
                            laikas.put(ObjektPerz.TAG_rezerv[2], (savaite+1)+"");
                            laikas.put(ObjektPerz.TAG_rezerv[3], "laisva");
                            laikas.put(ObjektPerz.TAG_rezerv[4], "0");
                            laikas.put(ObjektPerz.TAG_rezerv[5], "nėra");
                            //Grafiko ID praskipinamas
                            laikas.put(ObjektPerz.TAG_rezerv[7], Pagrindinis.id+"");
                            laikas.put(ObjektPerz.TAG_rezerv[8], "0");

                            grafikas.add(laikas);
                        }
                    }
                }

            }else if(tipas[0].contentEquals("makeSav")) {
                ArrayList<HashMap<String, String>> savaitesGraf = new ArrayList<>();

                for(int i = 0; i < grafikas.size(); i++)
                {
                    String savz = Pagrindinis.getWeekName(Integer.parseInt(grafikas.get(i).get(ObjektPerz.TAG_rezerv[2])));
                   if(savz.contentEquals(spinSavaite))
                    {
                        savaitesGraf.add(grafikas.get(i));
                    }
                }

                grafikas = new ArrayList<>();

                for(int savaite = 0; savaite < 52; savaite++) {

                    for(int i = 0; i < savaitesGraf.size(); i++)
                    {
                        HashMap<String, String> laikas = new HashMap<String, String>();
                        laikas.put(ObjektPerz.TAG_rezerv[0], savaitesGraf.get(i).get(ObjektPerz.TAG_rezerv[0]));
                        laikas.put(ObjektPerz.TAG_rezerv[1], savaitesGraf.get(i).get(ObjektPerz.TAG_rezerv[1]));
                        laikas.put(ObjektPerz.TAG_rezerv[2], (savaite+1)+"");
                        laikas.put(ObjektPerz.TAG_rezerv[3], "laisva");
                        laikas.put(ObjektPerz.TAG_rezerv[4], "0");
                        laikas.put(ObjektPerz.TAG_rezerv[5], "nėra");
                        //Grafiko ID praskipinamas
                        laikas.put(ObjektPerz.TAG_rezerv[7], Pagrindinis.id+"");
                        laikas.put(ObjektPerz.TAG_rezerv[8], "0");

                        grafikas.add(laikas);
                    }
                }

            }

            return "lel";
        }

        @Override
        protected void onPreExecute() {

            startActivity(new Intent(TvarkytiGrafika.this, LoadingScreen.class)); //Loadinimo screenas aktivuojamas

        }

        @Override
        protected void onPostExecute(String result) {
            LoadingScreen.load.finish();

            Toast.makeText(TvarkytiGrafika.this, "Grafikas užkrautas", Toast.LENGTH_LONG).show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public int getIndex(String dien, String []diens)
    {
        for(int i = 0; i < diens.length; i++)
        {
            if(dien.contentEquals(diens[i]))
            {
                return i+1;
            }
        }

        return 0;
    }
}
