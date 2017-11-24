package com.example.potato.privatisistema;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class Registracija extends AppCompatActivity {

    public static final String REGISTRACIJA = "registracija";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registracija);
    }

    public void registracija(View view) {

        final EditText slapv =  (EditText) findViewById(R.id.stringRegSlapyvardis);
        final EditText slapp =  (EditText) findViewById(R.id.stringRegSlaptazodis);
        final EditText kartSlap = (EditText) findViewById(R.id.stringPakartRegSlaptazodis);
        final EditText vard =  (EditText) findViewById(R.id.stringRegVardas);
        final EditText num =  (EditText) findViewById(R.id.stringNumeris);


        if(slapp.getText().toString().contentEquals(kartSlap.getText().toString())) {//Tikrinama ar teisingai ivestas pakartotinis slaptazodis


            String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/register";

            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressRegistracija);
            progressBar.setVisibility(View.VISIBLE); //Loading komponentas pasidaro matomas

            RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

            StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //SITA VIETA KEICIAS
                            progressBar.setVisibility(View.GONE); //Pasalinamas progress
                            responseApdorojimas(response, slapv, slapp, vard); //Atidaromas pagrindinis langas
                            Pagrindinis.telefonas = num.getText().toString();
                                    //Toast.makeText(Registracija.this, response, Toast.LENGTH_LONG).show();
                            Log.d("Response", response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            progressBar.setVisibility(View.GONE); //Pasalinamas progress
                            Log.d("Error.Response", error.getMessage());
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    //I posta paduodami prisijungimo parametrai
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", slapv.getText().toString());
                    params.put("name", vard.getText().toString());
                    params.put("password", slapp.getText().toString());
                    params.put("numeris", num.getText().toString());


                    return params;
                }
            };
            queue.add(postRequest); //Issiunciamas post request
        }else
        {
            Toast.makeText(this, "Neteisingai įvestas pakartotinis slaptažodis", Toast.LENGTH_LONG).show();
            slapp.setText("");
            kartSlap.setText("");
        }
    }

    public void responseApdorojimas(String response, final EditText slapv, final EditText slapp, final EditText vard)
    {

        //Jei prisijunge
        if(response.contains("success")) {
            String tokenas = response.substring(8, response.indexOf(" ID"));
            Pagrindinis.id = response.substring(response.indexOf(" ID")+2);
            Intent intent = new Intent(this, Pagrindinis.class); //Atidarom pagrindini langa
            intent.putExtra(REGISTRACIJA, "Sveikiname jūs sėkmingai užsiregistravote");
            intent.putExtra(Prisijungimas.TOKENAS, tokenas);
            intent.putExtra(Prisijungimas.EMAIL, slapv.getText().toString());
            intent.putExtra(Prisijungimas.VARDAS, vard.getText().toString());
            startActivity(intent);
            finish();
            Prisijungimas.pris.finish();
        }else if(response.contentEquals("exists"))
        {
            Toast.makeText(this, "Vartotojas su tokiu vardu jau egzistuoja, bandykite dar karta", Toast.LENGTH_LONG).show();
            slapp.setText("");
            slapv.setText("");
        }else
        {
            Toast.makeText(this, "Įvyko serverio klaida", Toast.LENGTH_LONG).show();
        }
    }
}
