package com.fermimn.notificationmaker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NotificheAdapter extends ArrayAdapter<Notifica> {

    private TextView titolo, descrizione, data;

    public NotificheAdapter(Context context, ArrayList<Notifica> list) {
        super(context, R.layout.notifica_cronologia, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.notifica_cronologia, null);
        titolo = (TextView)convertView.findViewById(R.id.titolo);
        descrizione = (TextView)convertView.findViewById(R.id.descrizione);
        data = (TextView)convertView.findViewById(R.id.data);

        Notifica notifica = getItem(position);

        titolo.setText(notifica.getTitolo());

        if(notifica.getDescrizione().equals("")) {
            descrizione.setText(R.string.no_description);
        } else {
            descrizione.setText(notifica.getDescrizione());
        }

        if(notifica.getData().equals("")) {
            data.setText(R.string.no_date);
        } else {
            data.setText(R.string.creation_date);
            data.setText(data.getText() + ": " + notifica.getData());
        }

        return convertView;
    }
}
