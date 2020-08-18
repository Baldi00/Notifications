package com.fermimn.notificationmaker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class NotificheDataComparator implements Comparator<Notifica> {
    @Override
    public int compare(Notifica n1, Notifica n2) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            Date d1 = sdf.parse(n1.getData());
            Date d2 = sdf.parse(n2.getData());

            return d1.compareTo(d2);

        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
