package br.com.mobiwork.SCAPrevenda.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import br.com.mobiwork.SCAPrevenda.sinc.AtuService;

/**
 * Created by LuisGustavo on 16/11/2015.
 */
public class ConfigServico extends Activity {

    public static  Intent it;
    private Context ctx;
    private static String ATU_SERVICE="br.com.mobiwork.SCAPrevenda.sinc.AtuService";
    public void inistopservice(Context context){
        SharedPreferencesUtil sdu = new SharedPreferencesUtil(context);
        Conexao con= new Conexao();
        con.Conectado(context);
        ctx=context;
        if (con != null) {
            if (con.Conectado(context)&&sdu.getAtuAut()) {
                //start servic
                if (!isServiceRunning(ConfigServico.ATU_SERVICE)) {
                    Log.d("prevendaatu", String.valueOf("iniservice"));
                    Intent it = new Intent(context, AtuService.class);
                    context.startService(it);

                }
            }
            else {
                //stop service
                Log.d("prevendaatu", String.valueOf("notservice"));
                Intent it = new Intent(context, AtuService.class);
                context.stopService(it);

            }
        }

    }
    public boolean isServiceRunning(String serviceClassName){
        final ActivityManager activityManager = (ActivityManager)this.ctx.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                return true;
            }
        }
        return false;
    }
}
