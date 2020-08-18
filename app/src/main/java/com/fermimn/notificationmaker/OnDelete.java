package com.fermimn.notificationmaker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class OnDelete extends Service {
    public OnDelete() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Cronologia cronologia = Cronologia.getInstance(getApplicationContext());
        if(intent!=null)
            cronologia.setNotificaCancellata(intent.getIntExtra("id",0));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
