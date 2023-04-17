package com.teste.registraponto;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class EnfermeiroAdapter extends BaseAdapter {

    private List<Enfermeiro> enfermeiros;
    private Activity activity;

    public EnfermeiroAdapter(Activity activity, List<Enfermeiro> enfermeiros) {
        this.activity = activity;
        this.enfermeiros = enfermeiros;
    }

    @Override
    public int getCount() {
        return enfermeiros.size();
    }

    @Override
    public Object getItem(int i) { return enfermeiros.get(i); }

    @Override
    public long getItemId(int i) {
        return enfermeiros.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View viewAdapter = activity.getLayoutInflater().inflate(R.layout.item, viewGroup, false);

        TextView nome = viewAdapter.findViewById(R.id.txtEnf);
        TextView status = viewAdapter.findViewById(R.id.SWstatus);
        TextView data = viewAdapter.findViewById(R.id.txtData);
       TextView hora = viewAdapter.findViewById(R.id.txtHora);

        Enfermeiro a = enfermeiros.get(i);
        nome.setText(a.getNome());
        status.setText(a.getStatus());
        data.setText(a.getData());
        hora.setText(a.getHora());

        return viewAdapter;
    }
}
