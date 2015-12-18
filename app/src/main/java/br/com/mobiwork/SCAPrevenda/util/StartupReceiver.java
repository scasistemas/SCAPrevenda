package br.com.mobiwork.SCAPrevenda.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import br.com.mobiwork.SCAPrevenda.sinc.AtuService;

/**
 * Created by LuisGustavo on 14/04/2015.
 */
public class StartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        ConfigServico cs = new ConfigServico();
        cs.inistopservice(context);
    }
}