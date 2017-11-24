package com.example.potato.privatisistema;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RedaguotiSkelbima extends AppCompatActivity {


    private final Context context = this;
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String mimeType = "multipart/form-data;boundary=" + boundary;
    private byte[] multipartBody;

    SkelbSpec fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redaguoti_skelbima);

       final Spinner spin = patalpSpinner();

        if(SkelbimuSarasas.sarasTip.contentEquals("redagavimas")) {
            Button but = (Button) findViewById(R.id.butTvarkNuot);
            but.setVisibility(View.VISIBLE);
            //Pridedamas layout'as priklausomai nuo skelbimo patalpu tipo
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if (fragment != null) {
                fragmentTransaction.remove(fragment);
            }
            fragment = new SkelbSpec();
            fragment.setPatalpos(Patalpos.patalpos.get(SkelbimuPaieska.TAG_patalpuTip));
            fragment.setType("redagavimas"); //Nustatoma kad grazintu perziuros layouta
            fragmentTransaction.add(R.id.insertHere, fragment);
            fragmentTransaction.commit();
        }else if(SkelbimuSarasas.sarasTip.contentEquals("naujas"))
        {
            Button but = (Button) findViewById(R.id.butIkeltNuot);
            but.setVisibility(View.VISIBLE);
            spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    //Pridedamas layout'as priklausomai nuo skelbimo patalpu tipo
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    if (fragment != null) {
                        fragmentTransaction.remove(fragment);
                    }
                    fragment = new SkelbSpec();
                    fragment.setPatalpos(spin.getSelectedItem().toString());
                    fragment.setType("redagavimas"); //Nustatoma kad grazintu perziuros layouta
                    fragmentTransaction.add(R.id.insertHere, fragment);
                    fragmentTransaction.commit();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private Spinner patalpSpinner()
    {
        //Sukuriamas spinneris, su patalpu tipais
        Spinner spinner = (Spinner) findViewById(R.id.spinnerTipas);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.patalpuTipai, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        return spinner;
    }


    @Override
    protected void onStart() {
        super.onStart();

        if(SkelbimuSarasas.sarasTip.contentEquals("redagavimas")) {
            //Pildomi patalpu duomenys
            //Skelbimo duomenys
            final EditText saviv = (EditText) findViewById(R.id.stringSavivald);
            final EditText gyv = (EditText) findViewById(R.id.stringGyven);
            final EditText mikroR = (EditText) findViewById(R.id.stringMikRaj);
            final EditText gatve = (EditText) findViewById(R.id.stringGatve);
            final EditText plot = (EditText) findViewById(R.id.intPlotas);
            final EditText kain = (EditText) findViewById(R.id.stringKaina);
            final EditText kom = (EditText) findViewById(R.id.stringKomentaras);
            final Spinner patTip = (Spinner) findViewById(R.id.spinnerTipas);

            saviv.setText(Patalpos.patalpos.get(SkelbimuPaieska.TAG_savivaldybe));
            gyv.setText(Patalpos.patalpos.get(SkelbimuPaieska.TAG_gyvenviete));
            mikroR.setText(Patalpos.patalpos.get(SkelbimuPaieska.TAG_mikroRaj));
            gatve.setText(Patalpos.patalpos.get(SkelbimuPaieska.TAG_gatve));
            plot.setText(Patalpos.patalpos.get(SkelbimuPaieska.TAG_plotas));
            kain.setText(Patalpos.patalpos.get(SkelbimuPaieska.TAG_kaina));
            kom.setText(Patalpos.patalpos.get(SkelbimuPaieska.TAG_komentaras));
            patTip.setSelection(pasirinktoIndexas(R.array.patalpuTipai, Patalpos.patalpos.get(SkelbimuPaieska.TAG_patalpuTip)));

            View v = fragment.getView();


            //Namo tipo duomenys
            switch (Patalpos.patalpos.get(SkelbimuPaieska.TAG_patalpuTip)) {
                case "Butas":
                    final EditText aukstas = (EditText) v.findViewById(R.id.stringAukstas);
                    final EditText aukstuSk = (EditText) v.findViewById(R.id.stringAukstSk);
                    final EditText kambSk = (EditText) v.findViewById(R.id.stringKambSk);
                    final EditText metai = (EditText) v.findViewById(R.id.stringMetai);
                    final EditText namoNr = (EditText) v.findViewById(R.id.stringNamNr);
                    final EditText butoNr = (EditText) v.findViewById(R.id.stringButNr);
                    final Spinner pastatTip = (Spinner) v.findViewById(R.id.spinnerPastatas);
                    final Spinner irengTip = (Spinner) v.findViewById(R.id.spinnerIrengimas);
                    final Spinner sildymTip = (Spinner) v.findViewById(R.id.spinnerSildymas);

                    aukstas.setText(Patalpos.patalpos.get(SkelbimuPaieska.TAG_aukstas));
                    aukstuSk.setText(Patalpos.patalpos.get(SkelbimuPaieska.TAG_aukstuSk));
                    kambSk.setText(Patalpos.patalpos.get(SkelbimuPaieska.TAG_kambSk));
                    metai.setText(Patalpos.patalpos.get(SkelbimuPaieska.TAG_metai));
                    namoNr.setText(Patalpos.patalpos.get(SkelbimuPaieska.TAG_namoNr));
                    butoNr.setText(Patalpos.patalpos.get(SkelbimuPaieska.TAG_butoNr));
                    pastatTip.setSelection(pasirinktoIndexas(R.array.pastatTipai, Patalpos.patalpos.get(SkelbimuPaieska.TAG_pastatoTip)));
                    irengTip.setSelection(pasirinktoIndexas(R.array.irengTipai, Patalpos.patalpos.get(SkelbimuPaieska.TAG_irengimoTip)));
                    sildymTip.setSelection(pasirinktoIndexas(R.array.sildymTipai, Patalpos.patalpos.get(SkelbimuPaieska.TAG_sildymoTip)));
                    break;
                case "Namas":

                    final EditText metai1 = (EditText) v.findViewById(R.id.stringMetai);
                    final EditText namoNr1 = (EditText) v.findViewById(R.id.stringNamNr);
                    final Spinner namoTip = (Spinner) v.findViewById(R.id.spinnerNamas);
                    final Spinner pastatTip1 = (Spinner) v.findViewById(R.id.spinnerPastatas);
                    final Spinner irengTip1 = (Spinner) v.findViewById(R.id.spinnerIrengimas);
                    final Spinner sildymTip1 = (Spinner) v.findViewById(R.id.spinnerSildymas);

                    metai1.setText(Patalpos.patalpos.get(SkelbimuPaieska.TAG_metai));
                    namoNr1.setText(Patalpos.patalpos.get(SkelbimuPaieska.TAG_namoNr));
                    namoTip.setSelection(pasirinktoIndexas(R.array.namoTipai, Patalpos.patalpos.get(SkelbimuPaieska.TAG_namoTip)));
                    pastatTip1.setSelection(pasirinktoIndexas(R.array.pastatTipai, Patalpos.patalpos.get(SkelbimuPaieska.TAG_pastatoTip)));
                    irengTip1.setSelection(pasirinktoIndexas(R.array.irengTipai, Patalpos.patalpos.get(SkelbimuPaieska.TAG_irengimoTip)));
                    sildymTip1.setSelection(pasirinktoIndexas(R.array.sildymTipai, Patalpos.patalpos.get(SkelbimuPaieska.TAG_sildymoTip)));
                    break;
            }
        }
    }

    //Grazinamas spinner item indeksas
    public int pasirinktoIndexas(int arrayID, String pasirinktas)
    {

        Resources res = getResources();
        String itemArray[] = res.getStringArray(arrayID);

        for(int i = 0; i < itemArray.length; i++)
        {
            if(itemArray[i].contentEquals(pasirinktas))
                return i;
        }

        return -1;
    }

    public void ikeltNuot(View view)
    {
        NuotraukosDaug.nuotTipas = "nauja";
        startActivity(new Intent(this, NuotraukosDaug.class));
    }

    public void tvarkNuot(View view)
    {
        NuotraukosDaug.nuotTipas = "redaguojama";
        startActivity(new Intent(this, NuotraukosDaug.class));
    }

    public byte[] getByteImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, baos); //Pabandyt 0 jei neveiks
        byte[] imageBytes = baos.toByteArray();
        return imageBytes;
    }

    //Issaugojimo mygtukas
    public void issaugot(View view)
    {
        startActivity(new Intent(this, LoadingScreen.class)); //Loadinimo screenas aktivuojamas

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        OutputStreamWriter topKek = new OutputStreamWriter(dos);
        try {
            //Skelbimo nuotraukos ikeliamos
            for(int i = (1+NuotraukosDaug.existNuot.size()); i < NuotraukosDaug.naujNuot.size(); i++)
            {
                byte[] fileData1 = getByteImage(NuotraukosDaug.naujNuot.get(i));
                buildPart(dos, fileData1, "pic"+(i-NuotraukosDaug.existNuot.size()));
            }

            //Skelbimo nuotraukos istrinamos
            for(int i = 0; i < NuotraukosDaug.pasalNuot.size(); i++)
            {
                buildTextPart(dos, "remove"+i, NuotraukosDaug.pasalNuot.get(i));
            }

            buildTextPart(dos, "email", Pagrindinis.email);
            buildTextPart(dos, "api_token", Pagrindinis.tokenas);


            //Idedama kuri nuotrauka bus pagrindine
            buildTextPart(dos, "pagrNuot", NuotraukosDaug.pagrNuot+"");

            if(SkelbimuSarasas.sarasTip.contentEquals("redagavimas")) {
                SkelbimuSarasas.skelbList.remove(Patalpos.patalpos);
                buildTextPart(dos, SkelbimuPaieska.TAG_post_id, Patalpos.patalpos.get(SkelbimuPaieska.TAG_post_id));
            }

            //-------------Traukiami duomenys is inputo------------------------------------------------------
            final EditText saviv = (EditText) findViewById(R.id.stringSavivald);
            final EditText gyv = (EditText) findViewById(R.id.stringGyven);
            final EditText mikroR = (EditText) findViewById(R.id.stringMikRaj);
            final EditText gatve = (EditText) findViewById(R.id.stringGatve);
            final EditText plot = (EditText) findViewById(R.id.intPlotas);
            final EditText kain = (EditText) findViewById(R.id.stringKaina);
            final Spinner patalpT = (Spinner) findViewById(R.id.spinnerTipas);
            final EditText kom = (EditText) findViewById(R.id.stringKomentaras);


            buildTextPart(dos, SkelbimuPaieska.TAG_patalpuTip, patalpT.getSelectedItem().toString());
            buildTextPart(dos, SkelbimuPaieska.TAG_savivaldybe, saviv.getText().toString());
            buildTextPart(dos, SkelbimuPaieska.TAG_gyvenviete, gyv.getText().toString());
            buildTextPart(dos, SkelbimuPaieska.TAG_mikroRaj, mikroR.getText().toString());
            buildTextPart(dos,SkelbimuPaieska.TAG_gatve, gatve.getText().toString());
            buildTextPart(dos,SkelbimuPaieska.TAG_plotas, plot.getText().toString());
            buildTextPart(dos,SkelbimuPaieska.TAG_kaina, kain.getText().toString());
            buildTextPart(dos,SkelbimuPaieska.TAG_komentaras, kom.getText().toString());

            if(SkelbimuSarasas.sarasTip.contentEquals("redagavimas")) {
                Patalpos.patalpos.put(SkelbimuPaieska.TAG_patalpuTip, patalpT.getSelectedItem().toString());
                Patalpos.patalpos.put(SkelbimuPaieska.TAG_savivaldybe, saviv.getText().toString());
                Patalpos.patalpos.put(SkelbimuPaieska.TAG_gyvenviete, gyv.getText().toString());
                Patalpos.patalpos.put(SkelbimuPaieska.TAG_mikroRaj, mikroR.getText().toString());
                Patalpos.patalpos.put(SkelbimuPaieska.TAG_gatve, gatve.getText().toString());
                Patalpos.patalpos.put(SkelbimuPaieska.TAG_plotas, plot.getText().toString());
                Patalpos.patalpos.put(SkelbimuPaieska.TAG_kaina, kain.getText().toString());
                Patalpos.patalpos.put(SkelbimuPaieska.TAG_komentaras, kom.getText().toString());
            }


            switch (patalpT.getSelectedItem().toString())
            {
                case "Butas":
                    View frag = fragment.getView();
                    final EditText aukstuSk = (EditText) frag.findViewById(R.id.stringAukstSk);
                    final EditText kambSk = (EditText) frag.findViewById(R.id.stringKambSk);
                    final EditText metai = (EditText) frag.findViewById(R.id.stringMetai);
                    final EditText aukstas = (EditText) frag.findViewById(R.id.stringAukstas);
                    final Spinner pastatTip = (Spinner) frag.findViewById(R.id.spinnerPastatas);
                    final Spinner irengTip = (Spinner) frag.findViewById(R.id.spinnerIrengimas);
                    final Spinner sildymTip = (Spinner) frag.findViewById(R.id.spinnerSildymas);
                    final EditText namoNr = (EditText) frag.findViewById(R.id.stringNamNr);
                    final EditText butNr = (EditText) frag.findViewById(R.id.stringButNr);

                    buildTextPart(dos, SkelbimuPaieska.TAG_aukstuSk, aukstuSk.getText().toString());
                    buildTextPart(dos, SkelbimuPaieska.TAG_aukstas, aukstas.getText().toString());
                    buildTextPart(dos, SkelbimuPaieska.TAG_pastatoTip, pastatTip.getSelectedItem().toString());
                    buildTextPart(dos, SkelbimuPaieska.TAG_irengimoTip, irengTip.getSelectedItem().toString());
                    buildTextPart(dos, SkelbimuPaieska.TAG_sildymoTip, sildymTip.getSelectedItem().toString());
                    buildTextPart(dos, SkelbimuPaieska.TAG_kambSk, kambSk.getText().toString());
                    buildTextPart(dos, SkelbimuPaieska.TAG_metai, metai.getText().toString());
                    buildTextPart(dos, SkelbimuPaieska.TAG_namoNr, namoNr.getText().toString());
                    buildTextPart(dos, SkelbimuPaieska.TAG_butoNr, butNr.getText().toString());

                    if(SkelbimuSarasas.sarasTip.contentEquals("redagavimas")) {
                        Patalpos.patalpos.put(SkelbimuPaieska.TAG_aukstuSk, aukstuSk.getText().toString());
                        Patalpos.patalpos.put(SkelbimuPaieska.TAG_aukstas, aukstas.getText().toString());
                        Patalpos.patalpos.put(SkelbimuPaieska.TAG_pastatoTip, pastatTip.getSelectedItem().toString());
                        Patalpos.patalpos.put(SkelbimuPaieska.TAG_irengimoTip, irengTip.getSelectedItem().toString());
                        Patalpos.patalpos.put(SkelbimuPaieska.TAG_sildymoTip, sildymTip.getSelectedItem().toString());
                        Patalpos.patalpos.put(SkelbimuPaieska.TAG_kambSk, kambSk.getText().toString());
                        Patalpos.patalpos.put(SkelbimuPaieska.TAG_metai, metai.getText().toString());
                        Patalpos.patalpos.put(SkelbimuPaieska.TAG_namoNr, namoNr.getText().toString());
                        Patalpos.patalpos.put(SkelbimuPaieska.TAG_butoNr, butNr.getText().toString());
                    }
                    break;

                case "Namas":
                    View frag1 = fragment.getView();
                    final EditText metai1 = (EditText) frag1.findViewById(R.id.stringMetai);
                    final Spinner namoTip = (Spinner) frag1.findViewById(R.id.spinnerNamas);
                    final Spinner pastatTip1 = (Spinner) frag1.findViewById(R.id.spinnerPastatas);
                    final Spinner irengTip1 = (Spinner) frag1.findViewById(R.id.spinnerIrengimas);
                    final Spinner sildymTip1 = (Spinner) frag1.findViewById(R.id.spinnerSildymas);
                    final EditText namoNr1 = (EditText) frag1.findViewById(R.id.stringNamNr);

                    buildTextPart(dos, SkelbimuPaieska.TAG_pastatoTip, pastatTip1.getSelectedItem().toString());
                    buildTextPart(dos, SkelbimuPaieska.TAG_irengimoTip, irengTip1.getSelectedItem().toString());
                    buildTextPart(dos, SkelbimuPaieska.TAG_sildymoTip, sildymTip1.getSelectedItem().toString());
                    buildTextPart(dos, SkelbimuPaieska.TAG_namoTip, namoTip.getSelectedItem().toString());
                    buildTextPart(dos, SkelbimuPaieska.TAG_metai, metai1.getText().toString());
                    buildTextPart(dos, SkelbimuPaieska.TAG_namoNr, namoNr1.getText().toString());

                    if(SkelbimuSarasas.sarasTip.contentEquals("redagavimas")) {
                        Patalpos.patalpos.put(SkelbimuPaieska.TAG_pastatoTip, pastatTip1.getSelectedItem().toString());
                        Patalpos.patalpos.put(SkelbimuPaieska.TAG_irengimoTip, irengTip1.getSelectedItem().toString());
                        Patalpos.patalpos.put(SkelbimuPaieska.TAG_sildymoTip, sildymTip1.getSelectedItem().toString());
                        Patalpos.patalpos.put(SkelbimuPaieska.TAG_namoTip, namoTip.getSelectedItem().toString());
                        Patalpos.patalpos.put(SkelbimuPaieska.TAG_metai, metai1.getText().toString());
                        Patalpos.patalpos.put(SkelbimuPaieska.TAG_namoNr, namoNr1.getText().toString());
                    }
                    break;
            }
            if(SkelbimuSarasas.sarasTip.contentEquals("redagavimas")) {
                SkelbimuSarasas.skelbList.add(Patalpos.patalpos);
            }
            String lel = bos.toString();
            lel = dos.toString();
            lel = lel + "top kek";
            //-------------Traukiami duomenys is inputo END--------------------------------------------------

            // send multipart form data necesssary after file data
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            // pass to multipart body
            multipartBody = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String linkas = "";
        if(SkelbimuSarasas.sarasTip.contentEquals("redagavimas")) {
            linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/redaguotSkelb";
        }else if(SkelbimuSarasas.sarasTip.contentEquals("naujas"))
        {
            linkas = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/postSkelb";
        }

        MultiPartRequest multipartRequest = new MultiPartRequest(linkas, null, mimeType, multipartBody, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                /*String resultResponse = new String(response.data);
                Toast.makeText(RedaguotiSkelbima.this, resultResponse, Toast.LENGTH_SHORT).show();*/
                NuotraukosDaug.pagrNuot = 0;
                NuotraukosDaug.existNuot = new ArrayList<>();
                NuotraukosDaug.pasalNuot = new ArrayList<>();
                NuotraukosDaug.naujNuot = new ArrayList<>();

                finish();
                if(SkelbimuSarasas.sarasTip.contentEquals("redagavimas")) {
                    Patalpos.pat.finish();
                }
                Toast.makeText(RedaguotiSkelbima.this, "Skelbimas išsaugotas", Toast.LENGTH_SHORT).show();
                LoadingScreen.load.finish(); //Pradanginamas loadinomo screenas
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Upload failed!\r\n" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(multipartRequest);
    }

    private void buildPart(DataOutputStream dataOutputStream, byte[] fileData, String fileName) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + fileName + "\"; filename=\"email" //Sita pakeist i multipart/form-data jei neveiks
                + fileName + "\"" + lineEnd);                                                             //Taip pat ir gal kitaip irasyt ta name paduodama
        dataOutputStream.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileData);
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        // read file and write it into form...
        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataOutputStream.writeBytes(lineEnd);
    }

    private void buildTextPart(DataOutputStream dataOutputStream, String parameterName, String parameterValue) throws IOException {

        //Bytes manually convertuojam i UTF-8 supported, kad blet lietuviskos raides butu
        String converted = twoHyphens + boundary + lineEnd;
        byte[] b = converted.getBytes("UTF-8");
        dataOutputStream.write(b);

        converted = "Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd;
        b = converted.getBytes("UTF-8");
        dataOutputStream.write(b);

        converted = "Content-Type: text/plain; charset=UTF-8;" + lineEnd;
        b = converted.getBytes("UTF-8");
        dataOutputStream.write(b);

        converted = lineEnd;
        b = converted.getBytes("UTF-8");
        dataOutputStream.write(b);

        converted = parameterValue + lineEnd;
        b = converted.getBytes("UTF-8");
        dataOutputStream.write(b);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        NuotraukosDaug.pagrNuot = 0;
        NuotraukosDaug.existNuot = new ArrayList<>();
        NuotraukosDaug.pasalNuot = new ArrayList<>();
        NuotraukosDaug.naujNuot = new ArrayList<>();
        finish();
    }
}
