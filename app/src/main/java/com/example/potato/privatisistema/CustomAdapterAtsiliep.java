package com.example.potato.privatisistema;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Potato on 5/17/2017.
 */

public class CustomAdapterAtsiliep extends ArrayAdapter<String> {

    Activity context;
    String emails[];
    String vardai[];
    double ivertinimai[];
    String atsiliepimai[];


    public CustomAdapterAtsiliep(Activity context, String email[], String vard[], double ivert[], String ats[]) {
        super(context, R.layout.atsiliepimas_item, email);
        // TODO Auto-generated constructor stub

        this.context = context;
        emails = email;
        vardai = vard;
        ivertinimai = ivert;
        atsiliepimai = ats;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();

        int pos = position;
        View rowView=inflater.inflate(R.layout.atsiliepimas_item, null,true);


        TextView text = (TextView) rowView.findViewById(R.id.textVard);
        text.setText(vardai[position]);
        text = (TextView) rowView.findViewById(R.id.textEmail);
        text.setText(emails[position]);
        text = (TextView) rowView.findViewById(R.id.textAtsiliep);
        text.setText(atsiliepimai[position]);
        RatingBar rat = (RatingBar) rowView.findViewById(R.id.ratingVart);
        rat.setRating((float)ivertinimai[position]);

        return rowView;

    };

}
