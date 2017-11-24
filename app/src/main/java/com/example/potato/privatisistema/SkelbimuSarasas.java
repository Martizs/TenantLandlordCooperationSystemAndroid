package com.example.potato.privatisistema;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SkelbimuSarasas extends AppCompatActivity {

    public static ArrayList<HashMap<String, String>> skelbList;
    public static String sarasTip = ""; //Priklausomai nuo saraso tipo, uzkraunami siek tiek kitokie sarasai ir patalpu funkcionalumas
    String[] adresai;
    String[] nuotraukos;
    String[] inf;
    String[] tim;
    int result = 1;
    CustomListAdapter adapter;
    ListView list;

    public static HashMap<Integer, ImageLoader> skelbImages = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skelbimu_sarasas);

            grazintiAdrNuot();

            adapter = new CustomListAdapter(this, adresai, nuotraukos, inf, tim);

            list = (ListView) findViewById(R.id.listSkelbSar);

            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub

                    Patalpos.patalpos = skelbList.get(position);
                    Intent intent = new Intent(SkelbimuSarasas.this, Patalpos.class);
                    startActivityForResult(intent, result);
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {

        NuotraukosDaug.existNuot = new ArrayList<>();
        NuotraukosDaug.pasalNuot = new ArrayList<>();
        NuotraukosDaug.naujNuot = new ArrayList<>();

            if(skelbList.isEmpty()) {
                finish();
            }else {
                if(SkelbimuSarasas.sarasTip.contentEquals("redagavimas")) {
                    grazintSkelb("http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/getManSkelb");
                }else {
                    grazintiAdrNuot();
                    adapter = new CustomListAdapter(this, adresai, nuotraukos, inf, tim);
                    list.setAdapter(adapter);
                }
            }


            super.onActivityResult(requestCode, resultCode, intent);
    }

    //Is hashmapo gaunami adresai ir nuotraukos
    public void grazintiAdrNuot()
    {
        adresai = new String[skelbList.size()];
        nuotraukos = new String[skelbList.size()];
        inf = new String[skelbList.size()];
        tim = new String[skelbList.size()];
        HashMap<String, String> skelbimas;

        for(int i = 0; i < skelbList.size(); i++)
        {
            skelbimas = skelbList.get(i);
            adresai[i] = skelbimas.get(SkelbimuPaieska.TAG_patalpuTip).toUpperCase() + ": " +
                    skelbimas.get("savivaldybe") + ", " + skelbimas.get("gyvenviete") + ", " + skelbimas.get("mikroRaj")
                    + ", " + skelbimas.get("gatve") + ". KAINA: " + skelbimas.get(SkelbimuPaieska.TAG_kaina) + " eur/mėn";

            if(sarasTip.contentEquals("rezervavimas"))
            {
                inf[i] = skelbimas.get("statusas");
                String diena = Patalpos.dienuPav[Integer.parseInt(skelbimas.get(Patalpos.TAG_diena))-1]; //Paverciam skaiciu i pavadinima
                String savz = Pagrindinis.getWeekName(Integer.parseInt(skelbimas.get(Patalpos.TAG_savaitesNr)));
                tim[i] = savz + " " + diena + " " + skelbimas.get(Patalpos.TAG_laikas);
            }

            //PASKIAU REIKES IR NUOTRAUKAS TAIP PASIIMT.
            nuotraukos[i] = skelbimas.get(SkelbimuPaieska.TAG_nuotPath);
        }

    }

    //Daromas api call kad grazinti vartotojo skelbimus, arba patalpas
    public void grazintSkelb(String url)
    {
        startActivity(new Intent(this, LoadingScreen.class)); //Loadinimo screenas aktivuojamas

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

                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    private void responseApdoroti(String response)
    {
        if(!response.contentEquals("none")) {

            skelbList = ObjektPerz.ParseJSONObj(response);
            grazintiAdrNuot();
            adapter = new CustomListAdapter(this, adresai, nuotraukos, inf, tim);
            list.setAdapter(adapter);
        }else
        {
            Toast.makeText(this, "Nerasta", Toast.LENGTH_LONG).show();
        }
    }
}
