package com.fermimn.notificationmaker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {
    private EditText titolo, descrizione;
    private CheckBox permanente;
    private Button buttonCreaNotifica;
    private ImageButton buttonCancella;

    private Cronologia cronologia;
    private NotificheUtils notificheUtils;

    //Caso vengo da intent
    private boolean vengoDaIntent = false;
    private int notificationIdIntent;

    //CALLBACKS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titolo = findViewById(R.id.titolo);
        descrizione = findViewById(R.id.descrizione);
        permanente = findViewById(R.id.permanente);
        buttonCreaNotifica = findViewById(R.id.buttonCreaNotifica);
        buttonCancella = findViewById(R.id.buttonCancella);

        buttonCreaNotifica.setEnabled(false);
        buttonCreaNotifica.setBackgroundColor(0xFFFFD2AF);

        titolo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    buttonCreaNotifica.setEnabled(false);
                    buttonCreaNotifica.setBackgroundColor(0xFFFFD2AF);
                }else{
                    buttonCreaNotifica.setEnabled(true);
                    buttonCreaNotifica.setBackgroundColor(0xFFFF9800);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        cronologia = Cronologia.getInstance(getApplicationContext());
        notificheUtils = NotificheUtils.getInstance(getApplicationContext());

        notificheUtils.caricaNotificationId();

        if(getIntent()!=null && getIntent().getIntExtra("id",-1)!=-1){
            Notifica notifica = cronologia.getNotificaById(getIntent().getIntExtra("id",-1));
            titolo.setText(notifica.getTitolo());
            descrizione.setText(notifica.getDescrizione());
            permanente.setChecked(notifica.isPermanente());
            notificationIdIntent = notifica.getId();
            vengoDaIntent = true;
            buttonCreaNotifica.setText(R.string.update_notification);

            if(notifica.isVisibile()) {
                buttonCancella.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        notificheUtils.caricaNotificationId();

        if(vengoDaIntent){
            Notifica n = cronologia.getNotificaById(notificationIdIntent);
            if(n==null){
                ripristinaCondizioneIniziale();
                vengoDaIntent = false;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        notificheUtils.aggiornaNotificationId();
    }



    //ALTRO

    public void creaNotifica(View v){

        int notificationId;

        if(vengoDaIntent) {
            notificationId = notificationIdIntent;
        } else {
            notificationId = notificheUtils.getNotificationId();
        }

        Notification.Builder notification  = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.icona_notifica)
                .setContentTitle(titolo.getText())
                .setContentText(descrizione.getText())
                .setGroup("MyNotifications");

        if(permanente.isChecked())
            notification.setOngoing(true);

        Intent intentOnClick = new Intent(this,MainActivity.class);
        intentOnClick.putExtra("id",notificationId);
        intentOnClick.putExtra("titolo",titolo.getText().toString());
        intentOnClick.putExtra("descrizione",descrizione.getText().toString());
        intentOnClick.putExtra("permanente",permanente.isChecked());

        intentOnClick.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Intent intentOnDelete = new Intent(this, OnDelete.class);
        intentOnDelete.putExtra("id",notificationId);

        TaskStackBuilder stackBuilderOnClick = TaskStackBuilder.create(getApplicationContext());
        stackBuilderOnClick.addNextIntentWithParentStack(intentOnClick);

        PendingIntent pendingIntentOnClick = stackBuilderOnClick.getPendingIntent(notificationId, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent pendingIntentOnDelete = PendingIntent.getService(this, notificationId, intentOnDelete, 0);

        notification.setContentIntent(pendingIntentOnClick);
        notification.setDeleteIntent(pendingIntentOnDelete);

        NotificationManager notificationManager;
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId,notification.build());

        Notification summaryNotification =
                new Notification.Builder(MainActivity.this)
                        .setSmallIcon(R.mipmap.icona_notifica)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setGroup("MyNotifications")
                        .setGroupSummary(true)
                        .build();
        notificationManager.notify(1,summaryNotification);


        buttonCreaNotifica.setBackgroundColor(0xFFFFD2AF);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String data = simpleDateFormat.format(new Date());

        int aggiungiResult = cronologia.aggiungiNotifica(notificationId,new Notifica(titolo.getText().toString(),descrizione.getText().toString(),data,permanente.isChecked(),true, notificationId));

        if(aggiungiResult != -1 && !vengoDaIntent){
            NotificationManager nManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
            nManager.cancel(aggiungiResult);
        }

        if(vengoDaIntent) {
            vengoDaIntent = false;
            buttonCreaNotifica.setText(R.string.create_notification);
        } else {
            notificheUtils.incrementaNotificationId();
        }

        titolo.setText("");
        descrizione.setText("");
        permanente.setChecked(false);
    }

    public void cancellaNotifica(View v) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(getString(R.string.dialog_sure_delete_notification,cronologia.getNotificaById(notificationIdIntent).getTitolo()));
        dialog.setPositiveButton(
                R.string.dialog_yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NotificationManager nManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
                        nManager.cancel(notificationIdIntent);
                        cronologia.setNotificaCancellata(notificationIdIntent);
                        ripristinaCondizioneIniziale();
                        vengoDaIntent = false;
                        dialog.cancel();

                        if(cronologia.getNotificheVisibili().size()==0){
                            nManager.cancel(1);
                        }
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

    public void mostraCronologia(View v) {
        Intent intent = new Intent(this, ActivityHistory.class);
        startActivity(intent);
    }

    public void ripristinaCondizioneIniziale(){
        titolo.setText("");
        descrizione.setText("");
        permanente.setChecked(false);
        buttonCreaNotifica.setText(R.string.create_notification);
        buttonCancella.setVisibility(View.GONE);
    }

    //NO LONGER USED
//    public void cancellaNotifiche(View v) {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//        dialog.setMessage("Sei sicuro di voler cancellare tutte le notifiche visibili?");
//        dialog.setPositiveButton(
//                "Si",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        NotificationManager nManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
//                        nManager.cancelAll();
//                        cronologia.setNotificheTutteCancellate();
//                        titolo.setText("");
//                        descrizione.setText("");
//                        permanente.setChecked(false);
//                        dialog.cancel();
//                    }
//                });
//
//        dialog.setNegativeButton(
//                "No",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//        dialog.show();
//    }
}
