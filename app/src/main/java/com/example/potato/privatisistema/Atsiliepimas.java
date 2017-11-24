package com.example.potato.privatisistema;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Atsiliepimas extends AppCompatActivity {

    public static String tipas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atsiliepimas);

    }

    //Atsiliepimas ir ivertinimas issaugojami
    public void paliktAtsIvert(View view)
    {
        String linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/paliktAtsIvert";

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        //Toast.makeText(Atsiliepimas.this, response, Toast.LENGTH_LONG).show();
                        if(response.contentEquals("exists"))
                        {
                            if(tipas.contentEquals("skelbimas")) {
                                Toast.makeText(Atsiliepimas.this, "Jūs jau įvertinote šias patalpas", Toast.LENGTH_LONG).show();
                            }else
                            {
                                Toast.makeText(Atsiliepimas.this, "Jūs jau įvertinote šį vartotoją", Toast.LENGTH_LONG).show();
                            }
                        }else
                        {
                            Toast.makeText(Atsiliepimas.this, "Atsiliepimas ir įvertinimas išsaugoti", Toast.LENGTH_LONG).show();
                        }
                        finish();
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Atsiliepimas.this, error.getMessage(), Toast.LENGTH_LONG).show();
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

                params.put("statusas", tipas);

                EditText string = (EditText) findViewById(R.id.stringAtsiliepimas);
                params.put("atsiliepimas", string.getText().toString());
                RatingBar rat = (RatingBar) findViewById(R.id.ratIvert);
                params.put("ivertinimas", rat.getRating()+"");

                if(tipas.contentEquals("skelbimas")) {
                    params.put("rev_id", Patalpos.patalpos.get(SkelbimuPaieska.TAG_post_id));
                }else if(tipas.contentEquals("vartotojas"))
                {
                    //Jei nuomotojas ivertina nuomininka
                    if(Pagrindinis.id.contentEquals(Patalpos.patalpos.get(SkelbimuPaieska.TAG_UserId)))
                    {
                        //Idedamas nuomininko ID
                        params.put("rev_id", Patalpos.patalpos.get(ObjektPerz.TAG_rezerv[4]));
                    }else //Vice Vers
                    {
                        params.put("rev_id", Patalpos.patalpos.get(SkelbimuPaieska.TAG_UserId));
                    }
                }

                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

}
