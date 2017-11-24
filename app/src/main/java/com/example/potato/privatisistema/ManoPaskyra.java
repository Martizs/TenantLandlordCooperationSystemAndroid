package com.example.potato.privatisistema;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class ManoPaskyra extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mano_paskyra);

        TextView text = (TextView) findViewById(R.id.stringRegVardas);
        text.setText(Pagrindinis.vardas);
        text = (TextView) findViewById(R.id.stringRegSlapyvardis);
        text.setText(Pagrindinis.email);
        text = (TextView) findViewById(R.id.stringNumeris);
        text.setText(Pagrindinis.telefonas);

    }

    public void issaugoti(View view)
    {

        String url = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/postKeistDuom";

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        if(response.contentEquals("exists"))
                        {
                            Toast.makeText(ManoPaskyra.this, "Vartotojas su tokiu email jau egzistuoja", Toast.LENGTH_LONG).show();
                        }else {
                            TextView name = (TextView) findViewById(R.id.stringRegVardas);
                            TextView email = (TextView) findViewById(R.id.stringRegSlapyvardis);
                            Pagrindinis.vardas = name.getText().toString();
                            Pagrindinis.email = email.getText().toString();
                            Toast.makeText(ManoPaskyra.this, "Duomenys sėkmingai išsaugoti", Toast.LENGTH_LONG).show();
                        }
                        //Toast.makeText(ManoPaskyra.this, response, Toast.LENGTH_LONG).show();
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

                TextView name = (TextView) findViewById(R.id.stringRegVardas);
                TextView email = (TextView) findViewById(R.id.stringRegSlapyvardis);
                TextView tel = (TextView) findViewById(R.id.stringNumeris);

                params.put("email2", email.getText().toString());
                params.put("name", name.getText().toString());
                params.put("telefonas", tel.getText().toString());

                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    public void keistPass(View view)
    {
        startActivity(new Intent(this, ChangePass.class));
    }

    public void tvarkytGrafika(View view)
    {
        startActivity(new Intent(this, TvarkytiGrafika.class));
    }

}
