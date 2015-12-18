package br.com.mobiwork.SCAPrevenda.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import br.com.mobiwork.SCAPrevenda.R;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;

/**
 * Created by LuisGustavo on 07/08/14.
 */
public class ReceberInfoConfigAtuTab extends Activity implements  Runnable {
    private ProgressDialog dialog;
    private Handler handler = new Handler();
    private SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sincconfig);

        DaoCreateDBP dao = new DaoCreateDBP(this);
        db =  dao.getWritableDatabase();

        dialog = ProgressDialog.show(this, "Sincronizar Dados", "Sincronizando Dados, por favor aguarde...", false, true);

        new Thread(this).start();
    }

    @Override
    public void run() {

        final TextView notificacao = (TextView) findViewById(R.id.notificacao);
        try {
            DataXmlExporter expo = new DataXmlExporter(db, ConfigVendedor.getExternalStorageDirectory());
            expo.importData(this, "config", "config", "");
            ReceberInfoConfigAtuTab.this.finish();
            handler.post(new Runnable() {
                public void run() {
                }
            });
        } finally {
            dialog.dismiss();
        }

    }
}