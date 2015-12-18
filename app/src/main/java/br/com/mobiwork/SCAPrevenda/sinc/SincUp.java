package br.com.mobiwork.SCAPrevenda.sinc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;


import br.com.mobiwork.SCAPrevenda.R;
import br.com.mobiwork.SCAPrevenda.dao.DaoConfig;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDB;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;
import br.com.mobiwork.SCAPrevenda.dao.DaoPreVenda;
import br.com.mobiwork.SCAPrevenda.inf.AsyncResponse;
import br.com.mobiwork.SCAPrevenda.model.Config;
import br.com.mobiwork.SCAPrevenda.model.PreVenda;
import br.com.mobiwork.SCAPrevenda.pedido.ConfirmaPedido;
import br.com.mobiwork.SCAPrevenda.pedido.EnviarPedidos;
import br.com.mobiwork.SCAPrevenda.util.Alertas;
import br.com.mobiwork.SCAPrevenda.util.Conexao;
import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;
import br.com.mobiwork.SCAPrevenda.util.DataXmlExporter;
import br.com.mobiwork.SCAPrevenda.util.SincDadosSmb;
import br.com.mobiwork.SCAPrevenda.util.ToastManager;


public class SincUp extends AsyncTask<String, Void, Boolean> {

    private SQLiteDatabase db,dbP;
    private ProgressDialog dialog;
    private Handler handler = new Handler();
    private String loteEnvio,mod;
    private boolean pedidosEnviados;
    private String tConn;
    private int vendid;
    private String us;
    private String erro,logerro;
    private Config config;
    private DaoCreateDB daoDB;
    private DaoCreateDBP daoDBP;
    private DaoPreVenda dpv;
    private DaoConfig dc;
    private volatile boolean running = true;
    Intent i ;
    ToastManager tm;
    SincDadosSmb smb;
    Context ctx;
    PreVenda pv;
    Alertas a;
    EnviarPedidos enviarPedidos;
    Activity confirma = (Activity)ctx;
    public AsyncResponse delegate=null;
    String caminteste;


    public  SincUp(Context paramctx,PreVenda parampv,String caminhoteste,Config c) {
        ctx=paramctx;

        Intent it = new Intent(ctx, AtuService.class);
        ctx.stopService(it);
        daoDB= new DaoCreateDB(ctx);
        daoDBP = new DaoCreateDBP(ctx);
        pv=parampv;
        confirma = (Activity)ctx;

        this.dialog = new ProgressDialog(paramctx);
        db = daoDB.getWritableDatabase();
        dbP=daoDBP.getWritableDatabase();
        config= c;
        if(config==null){
           new DataXmlExporter(this.dbP, ConfigVendedor.getExternalStorageDirectory()).importData(ctx, "config", "config", "");
           config= new DaoConfig(ctx).consultar(this.dbP);
        }
        a= new Alertas(paramctx);
        dpv = new DaoPreVenda(ctx);
        dc = new DaoConfig(ctx);
        enviarPedidos = new EnviarPedidos();
        erro="1";
        caminteste=caminhoteste;
    }

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Pedido");
        this.dialog.setMessage("Processando Pedido...");
        dialog.setProgressStyle(dialog.STYLE_HORIZONTAL);
        dialog.setProgress(0);
        dialog.setMax(2);

        dialog.setButton(ProgressDialog.BUTTON_NEUTRAL,
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //button click stuff here
                        try {
                           // SincUp.this.finalize();
                            //SincUp.this.cancel(false);
                            cancel(true);



                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }

                        if(ctx.getClass().getSimpleName().equalsIgnoreCase("ConfirmaPedido")){
                             confirma.finish();
                        }
                     //   dialog.dismiss();
                    }
                });

        this.dialog.show();
    }

    @Override
    protected Boolean doInBackground(String[] paramArrayOfString) {
        if (!Conexao.Conectado(ctx)) {
            erro = "ATENCAO !!! - WIFI DESCONECTADO";
            dialog.setProgress(5);
            return true;
        } else {
            try {
                loteEnvio = enviarPedidos.gerarLoteDePedidos(ctx, db, config.getVendid(), pv);
                dialog.setProgress(1);
            } catch (Exception e) {
                erro = "Erro ao Gerar Pedido";
                logerro = e.getMessage();
            }
            try {
                if (!caminteste.equalsIgnoreCase("")) {
                    pv.setStatus(0);
                    config.setEnderecoorc(caminteste);
                }
                smb = new SincDadosSmb(config, pv.getStatus(),ctx);

                try {

                    erro = smb.sendFileFromPeerToSdcard(this.loteEnvio, ".db");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } finally {
                dialog.setProgress(2);
                if (erro.equals("1")) {
                    return Boolean.valueOf(false);
                } else {
                    return Boolean.valueOf(true);
                }

            }

        }
    }


    @Override
    protected void onCancelled() {
        running = false;
    }


    @Override
    protected void onPostExecute(Boolean paramBoolean)
    {
        dialog.getButton(ProgressDialog.BUTTON_NEUTRAL).setText("OK");
        dialog.getButton(ProgressDialog.BUTTON_NEUTRAL).setVisibility(View.VISIBLE);
        delegate.processFinish(erro);
        if(paramBoolean.booleanValue())
        {
            this.dialog.setMessage(erro);
            return;
        }else{
            this.dialog.setMessage("Enviado com Sucesso");

            return;


        }

    }


}