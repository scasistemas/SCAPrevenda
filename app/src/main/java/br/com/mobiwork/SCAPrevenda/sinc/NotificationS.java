package br.com.mobiwork.SCAPrevenda.sinc;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import br.com.mobiwork.SCAPrevenda.R;

/**
 * Created by LuisGustavo on 14/04/2015.
 */
public class NotificationS extends Activity {
    public static final String TEXTO = "texto";
    public static final String USUARIO = "usuario";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);
        TextView usuarioTextView =
                (TextView) findViewById(R.id.usuario);
        TextView textoTextView = (TextView) findViewById(R.id.texto);
        String usuario = getIntent().getStringExtra(USUARIO);
        String texto = getIntent().getStringExtra(TEXTO);
        usuarioTextView.setText(usuario);
        textoTextView.setText(texto);
    }



}