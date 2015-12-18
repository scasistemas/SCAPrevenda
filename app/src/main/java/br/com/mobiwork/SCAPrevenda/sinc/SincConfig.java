package br.com.mobiwork.SCAPrevenda.sinc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.com.mobiwork.SCAPrevenda.R;
import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;

/**
 * Created by LuisGustavo on 07/08/14.
 */
public class SincConfig extends Activity implements View.OnClickListener, Runnable {

    private ProgressDialog dialog;
    private Handler handler = new Handler();
    private static final int DOWNLOAD_FILES_REQUEST = 1;
    private Button btVoltar, btAtualizar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sincconfig);
        btVoltar = (Button) findViewById(R.id.voltar);
        btAtualizar = (Button) findViewById(R.id.enviar);
        btAtualizar.setText("Criar");
        btVoltar.setOnClickListener(this);
        btAtualizar.setOnClickListener(this);
        dialog = ProgressDialog.show(this, "Sincronizar Dados", "Sincronizando Dados, por favor aguarde...", false, true);

        new Thread(this).start();
    }

    @Override
    public void run() {

        final TextView notificacao = (TextView) findViewById(R.id.notificacao);
        try {
            notificacao.setText(ConfigVendedor.getExternalStorageDirectory());
            notificacao.setVisibility(View.VISIBLE);

            handler.post(new Runnable() {
                public void run() {
                }
            });
        } finally {
            dialog.dismiss();
        }

    }

    @Override
    public void onClick(View v) {
        if (v == btVoltar) {
            SincConfig.this.finish();
        } else if (v == btAtualizar){
           run();
        }
    }

}
