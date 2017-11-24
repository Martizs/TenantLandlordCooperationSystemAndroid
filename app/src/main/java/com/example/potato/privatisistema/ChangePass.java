package com.example.potato.privatisistema;

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

public class ChangePass extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
    }

    public void keist(View view)
    {

        TextView pass = (TextView) findViewById(R.id.stringRegSlaptazodis);
        TextView pakPass = (TextView) findViewById(R.id.stringPakartRegSlaptazodis);

        if(pass.getText().toString().contentEquals(pakPass.getText().toString())) {

            String url = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/postKeistPass";

            RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.contentEquals("wrong")) {
                                Toast.makeText(ChangePass.this, "Neteisingas dabartinis slaptažodis", Toast.LENGTH_LONG).show();
                            } else {
                                finish();
                                Toast.makeText(ChangePass.this, "Slaptažodis sėkmingai pakeistas", Toast.LENGTH_LONG).show();
                            }
                            //Toast.makeText(SkelbimuPaieska.this, response, Toast.LENGTH_LONG).show();
                            Log.d("Response", response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", error.getMessage());
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    //I posta paduodami prisijungimo parametrai
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", Pagrindinis.email);
                    params.put("api_token", Pagrindinis.tokenas);

                    TextView passOld = (TextView) findViewById(R.id.stringDabPass);
                    TextView passNew = (TextView) findViewById(R.id.stringRegSlaptazodis);

                    params.put("password", passOld.getText().toString());
                    params.put("password2", passNew.getText().toString());

                    return params;
                }
            };
            queue.add(postRequest); //Issiunciamas post request
        }else
        {
            Toast.makeText(ChangePass.this, "Neteisingas pakartotinis slaptažodis", Toast.LENGTH_LONG).show();
        }
    }
}
