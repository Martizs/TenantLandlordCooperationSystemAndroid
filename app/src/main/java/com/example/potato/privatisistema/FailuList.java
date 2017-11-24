package com.example.potato.privatisistema;

import android.app.DownloadManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.potato.privatisistema.BuildConfig.DEBUG;


public class FailuList extends AppCompatActivity {

    String menesis;
    int monthPos;
    int year;
    String bendraSuma;
    public static String fileType;
    final String dokLink = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/getDok";
    final String sasLink = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/getSas";

    final String dokDelLink = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/postDeleteDok";
    final String sasDelLink = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/postDeleteSas";

    final String dokStatLink = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/postStatDok";
    final String sasStatLink = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/postStatSas";

    final String dokUploadLink = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/postIkeltDok";
    final String sasUploadLink = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/postIkeltSas";

    String sasYear = "", sasMen = "";

    ArrayList<HashMap<String, String>> failai;
    ListView list;
    CustomAdapterDoks adapter;

    //Failo uploadui kintamieji
    private final Context context = this;
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String mimeType = "multipart/form-data;boundary=" + boundary;
    private byte[] multipartBody;
    String lel[];
    String urlas;
    String pavadinimas;

    private final int REQUEST_CODE=1;

    private File chosenFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failu_list);

        //Jei istorija, tai nauju failu negalima ikelti
        if(SkelbimuSarasas.sarasTip.contentEquals("istorija"))
        {
            Button but = (Button) findViewById(R.id.butIkelt);
            but.setVisibility(View.GONE);
        }

        if(fileType.contentEquals("dokumentai")) {
            getFiles(dokLink);
        }else if (fileType.contentEquals("saskaitos"))
        {
            if(!Patalpos.patalpos.get(SkelbimuPaieska.TAG_UserId).contentEquals(Pagrindinis.id))
            {
                Button but = (Button) findViewById(R.id.butIkelt);
                but.setVisibility(View.GONE);
            }

            //Idedami dabartiniai metai
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            monthPos = calendar.get(Calendar.MONTH);
            Resources res = getResources();
            String[] menes = res.getStringArray(R.array.menesiai);
            menesis = menes[monthPos];

            getFiles(sasLink);
        }

        list = (ListView) findViewById(R.id.listDoks);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                final int itemPos = position;
                urlas = failai.get(itemPos).get("path");
                if(fileType.contentEquals("dokumentai")) {
                    pavadinimas = failai.get(itemPos).get("name");
                }else if (fileType.contentEquals("saskaitos"))
                {
                    pavadinimas = failai.get(itemPos).get(SkelbimuPaieska.TAG_metai)+ "-" + failai.get(itemPos).get(SkaitliukuDuom.TAG_MEN);
                }

                //Vartotojui paspaudus ant dokumento atidaromas dialog langas
                AlertDialog.Builder builder = new AlertDialog.Builder(FailuList.this);
                builder.setTitle(pavadinimas);

                //Dokumento parsisiuntimas
                builder.setNeutralButton("Parsisiųsti", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Cia blet nx jei API 25 t.y. Android version 6.0. Tai krv reikia kaip kazkokios ispindejusios paneles
                        //Paklaust ar blet galima butu pasinaudoti storage'u. Krv.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(FailuList.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
                            } else {
                                parsisiust(urlas, pavadinimas);
                            }
                        }else
                        {
                            parsisiust(urlas, pavadinimas);
                        }
                    }
                });

                //Taip pat jei istorija negalima nieko su failais daryt, tik juos parsisiust
                if(!SkelbimuSarasas.sarasTip.contentEquals("istorija")) {
                    if (fileType.contentEquals("dokumentai")) {
                        //Jei vartotojas kuris ikele perziuri dokumenta tai jis gali ji ir istrinti
                        if (Pagrindinis.id.contentEquals(failai.get(position).get("user_id"))) {
                            builder.setNegativeButton("Ištrinti", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    istrint(failai.get(itemPos).get("id"), itemPos, dokDelLink);
                                }
                            });
                        } else { //Jei tai nera, dokumento ikelejas tai sis vartotojas gali patvirtinti arba atmesti dokumenta
                            if (failai.get(position).get("statusas").contentEquals("nepatvirtinta")) {
                                builder.setPositiveButton("Patvirtinti", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        statDok("patvirtinta", itemPos, dokStatLink);
                                    }
                                });

                                builder.setNegativeButton("Atmesti", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        statDok("atmesta", itemPos, dokStatLink);
                                    }
                                });
                            }
                        }
                    } else if (fileType.contentEquals("saskaitos")) {
                        //Jei vartotojas kuris ikele perziuri saskaita tai jis gali ji ir istrinti
                        if (Pagrindinis.id.contentEquals(Patalpos.patalpos.get(SkelbimuPaieska.TAG_UserId))) {
                            builder.setNegativeButton("Ištrinti", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    istrint(failai.get(itemPos).get("id"), itemPos, sasDelLink);
                                }
                            });
                        } else { //Jei tai nera, saskaitos ikelejas tai sis vartotojas gali apmoketi Saskaita
                            if (failai.get(position).get("statusas").contentEquals("neapmokėta")) {
                                builder.setPositiveButton("Apmokėti", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        statDok("apmokėta", itemPos, sasStatLink);
                                    }
                                });
                            }
                        }
                    }
                }



                builder.show();

            }
        });

    }

    //--------------------------------Mygtuku funkcionalumai---------------------------------------------------
    //Istrinti dokumenta
    public void istrint(final String id, final int position, String linkas)
    {


        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        if(fileType.contentEquals("dokumentai")) {
                            getFiles(dokLink);
                        }else if (fileType.contentEquals("saskaitos"))
                        {
                            getFiles(sasLink);
                        }else
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

                params.put("id", id);
                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    //patvirtinti arba atmesti dokumenta
    public void statDok(final String statusas, final int position, String linkas)
    {

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        HashMap<String, String> failas = new HashMap<>();
                        failas = failai.get(position);
                        failai.remove(failai.get(position));
                        failas.put("statusas", statusas);
                        failai.add(failas);

                        adapter = new CustomAdapterDoks(FailuList.this, failai, lel);
                        list.setAdapter(adapter);
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
                params.put("id", failai.get(position).get("id"));

                params.put("statusas", statusas);
                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    //Cia krv del ispindejusios princeses viskas nx, krv oj duok man permission nx
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            //you have the permission now.
            parsisiust(urlas, pavadinimas);
        }
    }

    public void parsisiust(String url, String name)
    {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(name);
// in order for this if to run, you must use the android 3.2 to compile your app

            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String filename = URLUtil.guessFileName(url, null, MimeTypeMap.getFileExtensionFromUrl(url));

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

// get download service and enqueue file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    //---------------------------Failo ikelimas start--------------------------------------------------------------------------------
    public void ikelti(View view)
    {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //Cia sita darom kad atidarytu external storage
        File file = new File(Environment.getExternalStorageDirectory(), "lel");
        intent.setDataAndType(Uri.fromFile(file), "*/*");

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"), 1);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();

                try {
                    String path = getPath(this, filePath);
                    chosenFile = new File(path);
                }catch (Exception e)
                {
                    Log.e("error", "lel");
                }

            //Vartotojui paspaudus ant dokumento atidaromas dialog langas
            AlertDialog.Builder builder = new AlertDialog.Builder(FailuList.this);

            if(fileType.contentEquals("dokumentai")) {
                builder.setTitle("Suteikite failui pavadinimą");

                final EditText input = new EditText(this);

                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Įkelti", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = input.getText().toString();

                        if (name.length() <= 0)
                            name = "be pavadinimo";

                        uploadFile(name);

                    }
                });
            }else if(fileType.contentEquals("saskaitos"))
            {
                builder.setTitle("Įveskite sąskaitos bendrą sumą");

                final Spinner spinMen = new Spinner(this);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(FailuList.this,
                        R.array.menesiai, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinMen.setAdapter(adapter);
                final Spinner spinMet = new Spinner(this);
                ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(FailuList.this,
                        R.array.metai, android.R.layout.simple_spinner_item);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinMet.setAdapter(adapter2);
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);

                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(input);
                layout.addView(spinMen);
                layout.addView(spinMet);

                builder.setView(layout);

                builder.setPositiveButton("Įkelti", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bendraSuma = input.getText().toString();
                        sasYear = spinMet.getSelectedItem().toString();
                        sasMen = spinMen.getSelectedItem().toString();
                        uploadFile(spinMet.getSelectedItem().toString() + "-" + spinMen.getSelectedItem().toString());
                    }
                });
            }


            builder.show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getPath(Context context, Uri uri) throws URISyntaxException {


            String lel = "";
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    lel = Environment.getExternalStorageDirectory().getAbsolutePath();
                    lel = Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
        else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                lel = getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                lel = getDataColumn(context, contentUri, selection, selectionArgs);
            } else if ("content".equalsIgnoreCase(uri.getScheme())) { // MediaStore (and general)

            // Return the remote address
            if (isGooglePhotosUri(uri))
                lel = uri.getLastPathSegment();

            lel = getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            lel = uri.getPath();
        }

        return lel;

    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG)
                    DatabaseUtils.dumpCursor(cursor);

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public void uploadFile(String name)
    {
        byte[] fileData1 = {};
        try {
            fileData1 = fullyReadFileToBytes();
            int lel = fileData1.length;
        }catch (Exception e)
        {
            Log.e("Error: ", "Git Gud");
        }


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            // the first file
            buildPart(dos, fileData1, "pic1.txt");
            buildTextPart(dos, SkelbimuPaieska.TAG_post_id, Patalpos.patalpos.get(SkelbimuPaieska.TAG_post_id));
            buildTextPart(dos, "api_token", Pagrindinis.tokenas);
            buildTextPart(dos, "email", Pagrindinis.email);

            if (fileType.contentEquals("dokumentai")){
                buildTextPart(dos, "name", name);
            }else if(fileType.contentEquals("saskaitos"))
            {
                buildTextPart(dos, SkelbimuPaieska.TAG_metai, sasYear);
                buildTextPart(dos, SkaitliukuDuom.TAG_MEN, sasMen);
                buildTextPart(dos, "bendraSum", bendraSuma);

            }
            // send multipart form data necesssary after file data
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            // pass to multipart body
            multipartBody = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String url = "";
        if(fileType.contentEquals("dokumentai")) {
            url = dokUploadLink;
        }else if (fileType.contentEquals("saskaitos"))
        {
            url = sasUploadLink;
        }

        MultiPartRequest multipartRequest = new MultiPartRequest(url, null, mimeType, multipartBody, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);

                if(!resultResponse.contentEquals("exists")) {
                    //Uzkraunami visi failai is naujo
                    if (fileType.contentEquals("dokumentai")) {
                        getFiles(dokLink);
                        Toast.makeText(context, "Dokumentas įkeltas", Toast.LENGTH_SHORT).show();
                    } else if (fileType.contentEquals("saskaitos")) {
                        getFiles(sasLink);
                        Toast.makeText(context, "Sąskaita įkelta", Toast.LENGTH_SHORT).show();
                    }

                }else
                {
                    Toast.makeText(context, "Šiem metam ir mėnesiui sąskaita jau įkelta", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(context, resultResponse, Toast.LENGTH_SHORT).show();

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

    byte[] fullyReadFileToBytes() throws IOException {
        Uri filePath = null;
        File lel = chosenFile;
        int size = (int) chosenFile.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis= new FileInputStream(chosenFile);
        try {

            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        }  catch (IOException e){
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
    }

    private void buildPart(DataOutputStream dataOutputStream, byte[] fileData, String fileName) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"dokumentas\"; filename=\"post_id" //Sita pakeist i multipart/form-data jei neveiks
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
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
        dataOutputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        dataOutputStream.writeBytes(parameterValue + lineEnd);
    }
    //-------------------------------Failo ikelimas END---------------------------------------------------------------------------------------------------------------
    //--------------------------------Mygtuku funkcionalumai END-----------------------------------------------------------------------------------------------------------------------

    public void getFiles(String linkas)
    {
        startActivity(new Intent(this, LoadingScreen.class)); //Loadinimo screenas aktivuojamas

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, linkas,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        LoadingScreen.load.finish();
                        if(!response.contentEquals("none"))
                        {
                            responseApdoroti(response);
                            adapter = new CustomAdapterDoks(FailuList.this, failai, lel);
                            list.setAdapter(adapter);
                        }else if(!Patalpos.patalpos.get(SkelbimuPaieska.TAG_UserId).contentEquals(Pagrindinis.id) && fileType.contentEquals("saskaitos"))
                        {
                            Toast.makeText(FailuList.this, "Nėra įkeltų sąskaitų", Toast.LENGTH_LONG).show();
                            finish();
                        }else
                        {
                            failai = new ArrayList<HashMap<String, String>>();
                            lel = new String[failai.size()];
                            adapter = new CustomAdapterDoks(FailuList.this, failai, lel);
                            list.setAdapter(adapter);
                        }
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        LoadingScreen.load.finish();
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

                params.put(SkelbimuPaieska.TAG_post_id, Patalpos.patalpos.get(SkelbimuPaieska.TAG_post_id));
                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    //Grafiko grazinimo apdorojimas
    private void responseApdoroti(String response)
    {
        failai = ParseJSON(response);
    }

    private ArrayList<HashMap<String, String>> ParseJSON(String json) {
        if (json != null) {
            try {

                failai = new ArrayList<HashMap<String, String>>();



                JSONArray files = new JSONArray(json);
                lel = new String[files.length()];


                for (int i = 0; i < files.length(); i++) {
                    JSONObject c = files.getJSONObject(i);

                    HashMap<String, String> failas = new HashMap<String, String>();

                    if(fileType.contentEquals("dokumentai")) {
                        String path = c.getString("path");
                        String name = c.getString("name");
                        lel[i] = name;
                        String id = c.getString("id");
                        String statusas = c.getString("statusas");
                        String post_id = c.getString(SkelbimuPaieska.TAG_post_id);
                        String user_id = c.getString(SkelbimuPaieska.TAG_UserId);


                        failas.put("path", path);
                        failas.put("name", name);
                        failas.put("id", id);
                        failas.put("statusas", statusas);
                        failas.put(SkelbimuPaieska.TAG_post_id, post_id);
                        failas.put(SkelbimuPaieska.TAG_UserId, user_id);


                    }else if (fileType.contentEquals("saskaitos"))
                    {
                        String path = c.getString("path");
                        String id = c.getString("id");
                        String post_id = c.getString(SkelbimuPaieska.TAG_post_id);
                        String metai = c.getString(SkelbimuPaieska.TAG_metai);
                        String men = c.getString(SkaitliukuDuom.TAG_MEN);
                        String statusas = c.getString("statusas");
                        String suma = c.getString("bendraSum");

                        failas.put("path", path);
                        failas.put("bendraSum", suma);
                        failas.put("id", id);
                        failas.put("statusas", statusas);
                        failas.put(SkelbimuPaieska.TAG_post_id, post_id);
                        failas.put(SkelbimuPaieska.TAG_metai, metai);
                        failas.put(SkaitliukuDuom.TAG_MEN, men);
                    }

                    failai.add(failas);

                }
                return failai;
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
}
