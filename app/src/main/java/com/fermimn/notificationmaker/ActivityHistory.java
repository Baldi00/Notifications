package com.fermimn.notificationmaker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class ActivityHistory extends Activity {

    private ListView history;
    private NotificheAdapter adapter;
    private ArrayList<Notifica> list;

    private Cronologia cronologia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        history = findViewById(R.id.history);

        list = new ArrayList<Notifica>();

        adapter = new NotificheAdapter(this, list);

        history.setAdapter(adapter);

        history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Notifica notifica = (Notifica) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("id",notifica.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        history.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int index, long l) {
                showDialogCancellaCronologiaItem(index);
                return true;
            }
        });

        cronologia = Cronologia.getInstance(getApplicationContext());

        TreeMap<Integer,Notifica> lista = cronologia.getCronologia();
        Set<Integer> ids = lista.keySet();
        Iterator<Integer> iterator = ids.iterator();

        while (iterator.hasNext()){
            int id = iterator.next();
            list.add(lista.get(id));
        }

        Collections.sort(list,new NotificheDataComparator());
        Collections.reverse(list);

        if(lista.size()==0){
            findViewById(R.id.nessunaNotificaLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.deleteAllNotifications).setVisibility(View.GONE);
        } else {
            findViewById(R.id.nessunaNotificaLayout).setVisibility(View.GONE);
            findViewById(R.id.deleteAllNotifications).setVisibility(View.VISIBLE);
        }

    }

    public void cancellaCronologia(View v){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(R.string.dialog_sure_delete_history);
        dialog.setPositiveButton(
                R.string.dialog_yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NotificationManager nManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
                        nManager.cancelAll();
                        try {
                            File f = new File(getFilesDir(), "cronologia.xml");
                            File f2 = new File(getFilesDir(), "notificationId.txt");
                            f.delete();
                            f2.delete();
                            Toast.makeText(getApplicationContext(),R.string.histroy_deleted,Toast.LENGTH_SHORT).show();
                            cronologia.getCronologia().clear();
                            finish();
                        } catch (Exception e) {}
                        dialog.cancel();
                    }
                });

        dialog.setNegativeButton(
                R.string.dialog_no,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        dialog.show();
    }

    public void showDialogCancellaCronologiaItem(final int index){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(R.string.dialog_sure_delete_history_element);
        dialog.setPositiveButton(
                R.string.dialog_yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(adapter.getItem(index).isVisibile()){
                            NotificationManager nManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
                            nManager.cancel(adapter.getItem(index).getId());
                            cronologia.setNotificaCancellata(adapter.getItem(index).getId());
                        }

                        cronologia.rimuoviNotifica(adapter.getItem(index).getId());
                        list.remove(adapter.getItem(index));
                        adapter.notifyDataSetChanged();

                        if(list.size()==0){
                            findViewById(R.id.nessunaNotificaLayout).setVisibility(View.VISIBLE);
                            findViewById(R.id.deleteAllNotifications).setVisibility(View.GONE);
                        } else {
                            findViewById(R.id.nessunaNotificaLayout).setVisibility(View.GONE);
                            findViewById(R.id.deleteAllNotifications).setVisibility(View.VISIBLE);
                        }

                        cronologia.commit();
                    }
                });

        dialog.setNegativeButton(
                R.string.dialog_no,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        dialog.show();
    }

}
