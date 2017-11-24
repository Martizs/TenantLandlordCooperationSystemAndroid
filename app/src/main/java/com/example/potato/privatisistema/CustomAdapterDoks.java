package com.example.potato.privatisistema;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Potato on 5/11/2017.
 */

public class CustomAdapterDoks extends ArrayAdapter<String> {

    Activity context;
    ArrayList<HashMap<String, String>> failai;


    public CustomAdapterDoks(Activity context, ArrayList<HashMap<String, String>> files, String lel[]) {
        super(context, R.layout.dokumentas_item, lel);
        // TODO Auto-generated constructor stub
        failai = files;
        this.context = context;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();

        int pos = position;
        View rowView=inflater.inflate(R.layout.dokumentas_item, null,true);

        if(FailuList.fileType.contentEquals("dokumentai")) {
            TextView text = (TextView) rowView.findViewById(R.id.textPav);
            text.setText(failai.get(position).get("name"));
        }else if (FailuList.fileType.contentEquals("saskaitos"))
        {
            TextView text = (TextView) rowView.findViewById(R.id.textPav);
            text.setText(failai.get(position).get(SkelbimuPaieska.TAG_metai) + "-" + failai.get(position).get(SkaitliukuDuom.TAG_MEN));
            text = (TextView) rowView.findViewById(R.id.textSuma);
            text.setVisibility(View.VISIBLE);
            text.setText(failai.get(position).get("bendraSum") + "Eur");
        }


        TextView text = (TextView) rowView.findViewById(R.id.textStatusas);
        text.setText(failai.get(position).get("statusas"));



        return rowView;

    };

}
