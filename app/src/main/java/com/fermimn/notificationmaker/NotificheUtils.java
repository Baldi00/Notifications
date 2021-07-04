package com.fermimn.notificationmaker;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class NotificheUtils {

    private static NotificheUtils instance;
    private Context context;

    private int notificationId;
    private final String NOTIFICATION_ID = "notificationId.txt";

    public NotificheUtils(Context context){
        this.context = context;
    }

    public static NotificheUtils getInstance(Context context){
        if(instance == null){
            instance = new NotificheUtils(context);
        }
        return instance;
    }

    public void caricaNotificationId() {
        try {
            File f = new File(context.getFilesDir(), NOTIFICATION_ID);
            if (!f.exists()) {
                f.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                bw.write(""+2);
                bw.close();
                notificationId = 2;
            }else {
                BufferedReader bf = new BufferedReader(new FileReader(f));
                notificationId = Integer.parseInt(bf.readLine());
                if(notificationId>=Integer.MAX_VALUE){
                    notificationId=2;
                    aggiornaNotificationId();
                }
                bf.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void aggiornaNotificationId(){
        try {
            File f = new File(context.getFilesDir(), NOTIFICATION_ID);
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write(""+notificationId);
            bw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getNotificationId(){
        caricaNotificationId();
        return notificationId;
    }

    public void incrementaNotificationId(){
        notificationId++;
        aggiornaNotificationId();
    }


}
