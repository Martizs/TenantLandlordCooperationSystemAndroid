package com.example.potato.privatisistema;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
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

public class AtsiliepimaiList extends AppCompatActivity {

    public static String atsTipas;
    public static String rev_id;

    String emailz[];
    String vard[];
    double ivert[];
    String ats[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atsiliepimai_list);

        getAtsiliepimai();
    }

    public void getAtsiliepimai()
    {
        startActivity(new Intent(this, LoadingScreen.class)); //Loadinimo screenas aktivuojamas

        String url = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/getAtsiliepimai";

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        //String lel = response;
                        //Toast.makeText(ObjektPerz.this, response, Toast.LENGTH_LONG).show();
                        responseApdoroti(response);
                        LoadingScreen.load.finish(); //Pradanginamas loadinomo screenas

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

                params.put("statusas", atsTipas);
                params.put("rev_id", rev_id);

                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    private void responseApdoroti(String response)
    {
        if(response.contentEquals("none"))
        {
            Toast.makeText(this, "Atsiliepimų nėra", Toast.LENGTH_LONG).show(); //Sveikinimo zinute priklausomai ar registracija ivyko ar prisijungimas
            finish();
        }else
        {
            ParseJSON(response);
            CustomAdapterAtsiliep adapter = new CustomAdapterAtsiliep(this, emailz, vard, ivert, ats);
            ListView list = (ListView) findViewById(R.id.listAtsiliep);
            list.setAdapter(adapter);
        }

    }

    public void ParseJSON(String json) {
        if (json != null) {
            try {

                JSONArray atsiliepimai = new JSONArray(json);


                emailz = new String[atsiliepimai.length()];
                vard = new String[atsiliepimai.length()];
                ivert = new double[atsiliepimai.length()];
                ats = new String[atsiliepimai.length()];

                for (int i = 0; i < atsiliepimai.length(); i++) {
                    JSONObject c = atsiliepimai.getJSONObject(i);

                    emailz[i] = c.getString("email");
                    vard[i] = c.getString("name");
                    ivert[i] = Double.parseDouble(c.getString("ivertinimas"));
                    ats[i] = c.getString("atsiliepimas");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP request");
        }
    }
}

