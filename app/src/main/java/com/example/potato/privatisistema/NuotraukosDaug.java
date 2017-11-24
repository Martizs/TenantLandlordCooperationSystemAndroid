package com.example.potato.privatisistema;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NuotraukosDaug extends AppCompatActivity {

    public static ArrayList<Bitmap> naujNuot = new ArrayList<>();
    public static ArrayList<String> existNuot = new ArrayList<>();
    public static ArrayList<String> pasalNuot = new ArrayList<>();
    public static int nuotPos;
    public static int pagrNuot = 0;

    public String currentPath;

    public static String nuotTipas;

    GridView gridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuotraukos_daug);

        if(SkelbimuSarasas.sarasTip.contentEquals("redagavimas") || SkelbimuSarasas.sarasTip.contentEquals("naujas")) {
            if (naujNuot.size() == 0) { //Jei nuotraukos dar nebuvo ikeltos
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.android_ikelt);
                naujNuot.add(largeIcon);

                if (nuotTipas.contentEquals("redaguojama")) {

                    grazintNuot();
                } else {
                    pagrNuot = 1;
                    gridview = (GridView) findViewById(R.id.gridview);
                    gridview.setAdapter(new ImageAdapter(this));
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {
                            if (position == 0) {
                                showFileChooser();
                            } else {
                                nuotPos = position;
                                startActivityForResult(new Intent(NuotraukosDaug.this, PadidintiNuotrauka.class), 2);
                            }
                        }
                    });
                }
            } else { //Jei jau buvom ikele nuotraukas
                gridview = (GridView) findViewById(R.id.gridview);
                gridview.setAdapter(new ImageAdapter(this));
                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        if (position == 0) {
                            showFileChooser();
                        } else {
                            nuotPos = position;
                            startActivityForResult(new Intent(NuotraukosDaug.this, PadidintiNuotrauka.class), 2);
                        }
                    }
                });
            }
        }else
        { if (existNuot.size() == 0) {
                grazintNuot();
            }else
            {
                gridview = (GridView) findViewById(R.id.gridview);
                gridview.setAdapter(new ImageAdapter(NuotraukosDaug.this));
                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        if(SkelbimuSarasas.sarasTip.contentEquals("redagavimas") || SkelbimuSarasas.sarasTip.contentEquals("naujas")) {
                            if (position == 0) {
                                showFileChooser();
                            } else {
                                nuotPos = position;
                                startActivityForResult(new Intent(NuotraukosDaug.this, PadidintiNuotrauka.class), 2);
                            }
                        }else
                        {
                            PadidintiNuotrauka.imgUrl = existNuot.get(position);
                            startActivity(new Intent(NuotraukosDaug.this, PadidintiNuotrauka.class));
                        }
                    }
                });
            }
        }
    }

    //Nuotraukos pasirinkimas--------------------------------------------------------------------------------------
    public void showFileChooser() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pasirinkite");

        builder.setPositiveButton("Fotografuoti", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(NuotraukosDaug.this,
                                "com.example.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, 3);
                    }
                }
            }
        });
        builder.setNegativeButton("Galerija", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
        builder.show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri filePath = data.getData();
                try {
                    //Getting the Bitmap from Gallery
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    naujNuot.add(bitmap);

                    gridview = (GridView) findViewById(R.id.gridview);
                    gridview.setAdapter(new ImageAdapter(this));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (requestCode == 2)
            {
                gridview = (GridView) findViewById(R.id.gridview);
                gridview.setAdapter(new ImageAdapter(this));
            }

        if (requestCode == 3)
        {
            // Get the dimensions of the View
            int targetW = 200;
            int targetH = 200;

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(currentPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(currentPath, bmOptions);

            naujNuot.add(bitmap);

            gridview = (GridView) findViewById(R.id.gridview);
            gridview.setAdapter(new ImageAdapter(this));
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public void grazintNuot()
    {
        String url = "http://marbar4.stud.if.ktu.lt/Nuoma2/public/android/getNuotraukos";

        startActivity(new Intent(this, LoadingScreen.class)); //Loadinimo screenas aktivuojamas

        RequestQueue queue = Volley.newRequestQueue(this); //Request queu to hold the http requests

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        responseApdoroti(response);
                        //Toast.makeText(SkelbimuPaieska.this, response, Toast.LENGTH_LONG).show();
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
                params.put(SkelbimuPaieska.TAG_post_id, Patalpos.patalpos.get(SkelbimuPaieska.TAG_post_id));

                return params;
            }
        };
        queue.add(postRequest); //Issiunciamas post request
    }

    private void responseApdoroti(String response)
    {
        LoadingScreen.load.finish(); //Pradanginamas loadinomo screenas
        if(SkelbimuSarasas.sarasTip.contentEquals("redagavimas") || SkelbimuSarasas.sarasTip.contentEquals("naujas")) {
            if (!response.contentEquals("none")) {
                ParseJSON(response);
            } else {
                gridview = (GridView) findViewById(R.id.gridview);
                gridview.setAdapter(new ImageAdapter(NuotraukosDaug.this));
                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        if (position == 0) {
                            showFileChooser();
                        } else {
                            nuotPos = position;
                            startActivityForResult(new Intent(NuotraukosDaug.this, PadidintiNuotrauka.class), 2);
                        }
                    }
                });
            }
        }else
        {
            if (!response.contentEquals("none")) {
                ParseJSON(response);
            } else
            {
                finish();
                Toast.makeText(NuotraukosDaug.this, "Nuotraukų nėra", Toast.LENGTH_LONG).show();
            }
        }
    }

    public ArrayList<HashMap<String, String>> ParseJSON(String json) {
        if (json != null) {
            try {

                ArrayList<HashMap<String, String>> skelbList = new ArrayList<HashMap<String, String>>();


                JSONArray skelbimai = new JSONArray(json);


                for (int i = 0; i < skelbimai.length(); i++) {
                    JSONObject c = skelbimai.getJSONObject(i);

                    existNuot.add(c.getString("path"));
                    if(c.getString("statusas").contentEquals("pagrindine"))
                    {
                        pagrNuot = i+1;
                    }
                }

                new AsyncGettingBitmapFromUrl().execute();

                return skelbList;
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

    private class AsyncGettingBitmapFromUrl extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... params) {

            //Uzkraunam egzistuojancias nuotrauaks i bitmap array lista
            for(int i = 0; i < existNuot.size(); i++) {

                try {
                    URL url = new URL(existNuot.get(i));
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);

                    naujNuot.add(myBitmap);
                } catch (IOException e) {

                    String lel = e.getMessage();
                    // Log exception
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute()
        {
            startActivity(new Intent(NuotraukosDaug.this, LoadingScreen.class)); //Loadinimo screenas aktivuojamas
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            gridview = (GridView) findViewById(R.id.gridview);
            gridview.setAdapter(new ImageAdapter(NuotraukosDaug.this));
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    if(SkelbimuSarasas.sarasTip.contentEquals("redagavimas") || SkelbimuSarasas.sarasTip.contentEquals("naujas")) {
                        if (position == 0) {
                            showFileChooser();
                        } else {
                            nuotPos = position;
                            startActivityForResult(new Intent(NuotraukosDaug.this, PadidintiNuotrauka.class), 2);
                        }
                    }else
                    {
                        PadidintiNuotrauka.imgUrl = existNuot.get(position);
                        startActivity(new Intent(NuotraukosDaug.this, PadidintiNuotrauka.class));
                    }
                }
            });

            LoadingScreen.load.finish(); //Pradanginamas loadinomo screenas
        }
    }

    public void gerai(View view)
    {
        finish();
    }

}
