package com.example.potato.privatisistema;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONObject;

public class VartotojoInfo extends AppCompatActivity {

    String response;
    String vartId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vartotojo_info);

        if(SkelbimuSarasas.sarasTip.contentEquals("istorija"))
        {
            Button but = (Button) findViewById(R.id.butIvert);
            but.setVisibility(View.VISIBLE);
            but = (Button) findViewById(R.id.butAtsiliepimai);
            but.setVisibility(View.GONE);
        }

        Intent intent = getIntent();
        response = intent.getStringExtra("response");

        try {
            JSONObject info = new JSONObject(response);
            TextView text = (TextView) findViewById(R.id.textVard);
            text.setText(info.getString("name"));
            text = (TextView) findViewById(R.id.textEmail);
            text.setText(info.getString("email"));
            text = (TextView) findViewById(R.id.textNumeris);
            text.setText(info.getString("telefonas"));
            RatingBar rat = (RatingBar) findViewById(R.id.ratingVart);
            rat.setRating(Float.valueOf(info.getString("ivertinimas")));
            vartId = info.getString("id");
        }catch(Exception e)
        {
            Log.e("ERROR", e.getMessage());
        }

    }

    public void atsiliepimasVart(View view)
    {
        Atsiliepimas.tipas = "vartotojas";
        startActivity(new Intent(this, Atsiliepimas.class));
    }

    public void getVartAtsiliep(View view)
    {
        AtsiliepimaiList.atsTipas = "vartotojas";
        AtsiliepimaiList.rev_id = vartId;
        startActivity(new Intent(this, AtsiliepimaiList.class));
    }


}
