package com.example.potato.privatisistema;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class Prisijungimas extends AppCompatActivity {

    public static Activity pris; //Skirtia nuzudyt sitam activity, jei buvo prisijungta per registracija

    public static final String PRISIJUNGIMAS = "prisijungimas";
    public static final String TOKENAS = "tokenas";
    public static final String EMAIL = "email";
    public static final String VARDAS = "vardas";
    //String autologinEmail = "nuomininkas@nuomininkas.com";
    //String autologinEmail = "nuomotojas@nuomotojas.com";
    //String autologinPass = "slaptazodis";
    String autologinEmail = "android@android.com";
    String autologinPass = "android";
    //String autologinEmail = "petras@petras.com";
    //String autologinPass = "petras";
    boolean autologin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prisijungimas);

        pris = this;

        if(autologin)
        {
            autoPrisijungt();
        }
    }

    //-----------------------------------------------------Jungimasis Prie Servo--------------------------------

    public void butPrisijungti(View view) {

        String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/login";

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressPrisijungt);
        progressBar.setVisibility(View.VISIBLE);
        final EditText slapv =  (EditText) findViewById(R.id.stringSlapyvardis);
        final EditText slapp =  (EditText) findViewById(R.id.stringSlaptazodis);

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        //SITA VIETA KEICIAS
                        progressBar.setVisibility(View.GONE); //Pasalinamas progress
                        responseApdorojimas(response, slapv, slapp); //Atidaromas pagrindinis langas
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
                params.put("email", slapv.getText().toString());
                params.put("password", slapp.getText().toString());
                return params;

            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    public void autoPrisijungt()
    {
        String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/login";

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        //SITA VIETA KEICIAS
                        responseApdorojimas(response, null, null); //Atidaromas pagrindinis langas
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
                params.put("email", autologinEmail);
                params.put("password", autologinPass);

                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    public void responseApdorojimas(String response, final EditText slapv, final EditText slapp)
    {
        //Jei prisijunge
        if(response.contains("success")) {
            String tokenas = response.substring(8,response.indexOf(' '));
            String vardas = response.substring(response.indexOf(' '), response.indexOf("ID"));
            String id = response.substring(response.indexOf("ID")+2, response.indexOf("TEL"));
            String num = response.substring(response.indexOf("TEL")+3);
            Pagrindinis.telefonas = num;
            Pagrindinis.id = id;
            Intent intent = new Intent(this, Pagrindinis.class); //Atidarom pagrindini langa
            intent.putExtra(PRISIJUNGIMAS, "Sveikiname jūs sėkmingai prisijungėte");
            intent.putExtra(TOKENAS, tokenas);
            intent.putExtra(VARDAS, vardas);
            if(autologin) {
                intent.putExtra(EMAIL, autologinEmail);
            }else
            {
                intent.putExtra(EMAIL, slapv.getText().toString());
            }
            startActivity(intent);
            finish();
        }else if(response.contentEquals("email") || response.contentEquals("pass"))
        {
            Toast.makeText(this, "Jūsų vartotojo vardas arba slaptazodis neteisingi", Toast.LENGTH_LONG).show();
            slapp.setText("");
            slapv.setText("");
        }else
        {
            Toast.makeText(this, "Įvyko serverio klaida", Toast.LENGTH_LONG).show();
        }
    }

    public void butRegistracija(View view)
    {
        startActivity(new Intent(this, Registracija.class));
    }


}
