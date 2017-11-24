package com.example.potato.privatisistema;

import android.app.Activity;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Potato on 5/14/2017.
 */

public class CustomAdapterLaikai extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] laikai;
    private SparseArray<String> type;

    public CustomAdapterLaikai(Activity context, String[] laik, SparseArray<String> typ) {
        super(context, R.layout.item_laikas, laik);
        // TODO Auto-generated constructor stub

        laikai = laik;
        this.context = context;

        type = typ;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.item_laikas, null,true);



        TextView txtTitle = (TextView) rowView.findViewById(R.id.textLaik);

        if(type.get(position).contentEquals("green"))
        {
            txtTitle.setBackgroundColor(Color.GREEN);
        }else if(type.get(position).contentEquals("yellow"))
        {
            txtTitle.setBackgroundColor(Color.YELLOW);
        }else if(type.get(position).contentEquals("red"))
        {
            txtTitle.setBackgroundColor(Color.RED);
        }

        txtTitle.setText(laikai[position]);

        return rowView;
    };


}
