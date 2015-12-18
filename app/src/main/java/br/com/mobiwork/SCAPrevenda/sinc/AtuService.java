package br.com.mobiwork.SCAPrevenda.sinc;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import br.com.mobiwork.SCAPrevenda.R;

/**
 * Created by LuisGustavo on 14/04/2015.
 */
public class AtuService extends Service {

    private Intent inte;
    public static ScheduledThreadPoolExecutor pool ;
    @Override
    public int  onStartCommand(Intent intent,int flags,int id) {
         pool =
                new ScheduledThreadPoolExecutor(1);
        long delayInicial = 1;
        long periodo =1;
        Context context=getApplicationContext();
        TimeUnit unit = TimeUnit.MINUTES;
        Log.d("prevendaatu", String.valueOf("SERVICO"));
        pool.scheduleAtFixedRate(new SincDownAtu(context),
                delayInicial, periodo, unit);

        return START_STICKY;


    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}

