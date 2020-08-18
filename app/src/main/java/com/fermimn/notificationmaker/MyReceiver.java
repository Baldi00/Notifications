package com.fermimn.notificationmaker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class MyReceiver extends BroadcastReceiver {

    private Cronologia cronologia;
    private NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            cronologia = Cronologia.getInstance(context);
            creaNotifiche(context, cronologia.getNotificheVisibili());
        }
    }

    public void creaNotifiche(Context context, ArrayList<Notifica> notifiche){
        for(int i=0;i<notifiche.size();i++) {
            Notifica current = notifiche.get(i);

            Notification.Builder notification = new Notification.Builder(context)
                    .setSmallIcon(R.mipmap.icona_notifica)
                    .setContentTitle(current.getTitolo())
                    .setContentText(current.getDescrizione());

            if (current.isPermanente())
                notification.setOngoing(true);

            Intent intentOnClick = new Intent(context, MainActivity.class);
            intentOnClick.putExtra("id", current.getId());
            intentOnClick.putExtra("titolo", current.getTitolo());
            intentOnClick.putExtra("descrizione", current.getDescrizione());
            intentOnClick.putExtra("permanente", current.isPermanente());

            Intent intentOnDelete = new Intent(context, OnDelete.class);
            intentOnDelete.putExtra("id",current.getId());

            TaskStackBuilder stackBuilderOnClick = TaskStackBuilder.create(context);
            stackBuilderOnClick.addNextIntentWithParentStack(intentOnClick);

            PendingIntent pendingIntentOnClick = stackBuilderOnClick.getPendingIntent(current.getId(), PendingIntent.FLAG_UPDATE_CURRENT);

            PendingIntent pendingIntentOnDelete = PendingIntent.getService(context, current.getId(), intentOnDelete, 0);

            notification.setContentIntent(pendingIntentOnClick);
            notification.setDeleteIntent(pendingIntentOnDelete);

            notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            notificationManager.notify(current.getId(), notification.build());
        }
    }
}
