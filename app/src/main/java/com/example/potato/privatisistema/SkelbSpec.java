package com.example.potato.privatisistema;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by Potato on 4/17/2017.
 */

//Pagal pasirinkta tipa uzkraunama dalis  reikiamos vartotojo sasajos
public class SkelbSpec extends Fragment {

    private int id = -1;
    private View v; //Uzkraunamos dalies vaizdas
    private String skelb;
    private String tipas = "";

    public void setAttr(int id)
    {
        this.id = id;
    }
    public void setType(String tipas) {skelb = tipas;}
    public void setPatalpos(String patalp){tipas = patalp;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Priklausomai ar skelbimo paieska, ar redagavimas ar kurimas ar duomenys uzkraunami skirtingi UI
        switch(skelb) {

            case "paieska":
            //Pagal pasirinkta tipo id grazinamas nauja UI dalis
            switch (id) {
                case 0:
                    v = inflater.inflate(R.layout.butas, container, false);
                    sildSpinner();
                    irengSpinner();
                    pastatSpinner();
                    aukstuSpinner();
                    break;
                case 1:
                    v = inflater.inflate(R.layout.namas, container, false);
                    sildSpinner();
                    irengSpinner();
                    pastatSpinner();
                    namasSpinner();
                    break;
            }
            break;

            case "duomenys":
            switch (tipas) {
                case "Butas":
                    v = inflater.inflate(R.layout.butas_patalp, container, false);
                    break;
                case "Namas":
                    v = inflater.inflate(R.layout.namas_patalp, container, false);
                    break;
            }
            break;

            case "redagavimas":
                switch (tipas) {
                    case "Butas":
                        v = inflater.inflate(R.layout.butas_red, container, false);
                        sildSpinner();
                        irengSpinner();
                        pastatSpinner();
                        break;
                    case "Namas":
                        v = inflater.inflate(R.layout.namas_red, container, false);
                        sildSpinner();
                        irengSpinner();
                        pastatSpinner();
                        namasSpinner();
                        break;
                }
                break;
        }

        return v;
    }

    public View getView()
    {
        return v;
    }



    //Spineriu uzkrovimas--------------------------------------------------------


    private void aukstuSpinner()
    {
        //Sukuriamas spinneris, su aukstu tipais
        Spinner spinner = (Spinner) v.findViewById(R.id.spinnerAukstPas);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.aukstuTipai, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void sildSpinner()
    {
        //Sukuriamas spinneris, su sildymu tipais
        Spinner spinner = (Spinner) v.findViewById(R.id.spinnerSildymas);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.sildymTipai, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void pastatSpinner()
    {
        //Sukuriamas spinneris, su pastatu tipais
        Spinner spinner = (Spinner) v.findViewById(R.id.spinnerPastatas);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.pastatTipai, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void irengSpinner()
    {
        //Sukuriamas spinneris, su irengimo tipais
        Spinner spinner = (Spinner) v.findViewById(R.id.spinnerIrengimas);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.irengTipai, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void namasSpinner()
    {
        //Sukuriamas spinneris, su namu tipais
        Spinner spinner = (Spinner) v.findViewById(R.id.spinnerNamas);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.namoTipai, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    //----------------------------Spineriu uzkrovimas END---------------------------------------

}
