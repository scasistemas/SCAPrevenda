package br.com.mobiwork.SCAPrevenda.sinc;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import br.com.mobiwork.SCAPrevenda.R;
import br.com.mobiwork.SCAPrevenda.dao.DaoConfig;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDB;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;
import br.com.mobiwork.SCAPrevenda.dao.DaoPreVenda;
import br.com.mobiwork.SCAPrevenda.inf.AsyncResponse;
import br.com.mobiwork.SCAPrevenda.inf.Informacoes;
import br.com.mobiwork.SCAPrevenda.model.Config;
import br.com.mobiwork.SCAPrevenda.model.PreVenda;
import br.com.mobiwork.SCAPrevenda.pedido.EnviarPedidos;
import br.com.mobiwork.SCAPrevenda.util.Alertas;
import br.com.mobiwork.SCAPrevenda.util.Arquivo;
import br.com.mobiwork.SCAPrevenda.util.ArquivoTxt;
import br.com.mobiwork.SCAPrevenda.util.Conexao;
import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;
import br.com.mobiwork.SCAPrevenda.util.DataXmlExporter;
import br.com.mobiwork.SCAPrevenda.util.SharedPreferencesUtil;
import br.com.mobiwork.SCAPrevenda.util.SincDadosSmb;
import br.com.mobiwork.SCAPrevenda.util.ToastManager;

/**
 * Created by LuisGustavo on 22/09/14.
 */
public class SincDownAtu  implements Runnable {

    private SQLiteDatabase db, dbP;
    private String erro;
    private DaoCreateDB daoDB;
    private DaoCreateDBP daoDBP;
    private DaoPreVenda dpv;
    private DaoConfig dc;
    SincDadosSmb smb;
    Context ctx;
    Config config;
    ArquivoTxt tx;
    Conexao con;
    public static int id=0;




    public SincDownAtu(Context context) {
        ctx=context;
        daoDB = new DaoCreateDB(ctx);
        daoDBP = new DaoCreateDBP(ctx);
        db = daoDB.getWritableDatabase();
        dbP = daoDBP.getWritableDatabase();
        config=ConfigVendedor.getConfig(dbP);
        if (config.getVendid() == null) {
            new DataXmlExporter(this.dbP, ConfigVendedor.getExternalStorageDirectory()).importData(ctx, "config", "config", "");
            config = new DaoConfig(ctx).consultar(this.dbP);
        }
        dpv = new DaoPreVenda(ctx);
        dc = new DaoConfig(ctx);
        erro = "";
        con= new Conexao();
        tx = new ArquivoTxt(context);



    }


    @Override
    public void run() {
        SharedPreferencesUtil sdu = new SharedPreferencesUtil(ctx);
        Log.d("prevendaatu","verificando....");
        if (!sdu.getAtuAut()) {
            AtuService.pool.shutdownNow();
        }else{
            boolean versao = false;
            this.erro = "";
            String result = "";
            try {
                if (this.con.Conectado(ctx)) {

                    try {
                        config = ConfigVendedor.getConfig(dbP);
                        smb = new SincDadosSmb(config, 1, ctx);
                        try {
                            result = smb.downloadFilePc("Versao", ".txt", 0);
                            if (!result.equalsIgnoreCase("")) {
                                erro = result;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            versao = tx.lertxt("ver");
                            Log.d("prevendaatu","nova versão"+ String.valueOf(versao));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(!erro.equalsIgnoreCase("")){
                           versao=true;
                        }
                        if (versao &&sdu.getAtuAut()) {
                            String titulo = "";
                            String not = "";
                            String texto = "";
                            if (erro.equalsIgnoreCase("")) {
                                titulo = "Pre Venda - Novos dados disponiveis!";
                                not = "Pre Venda - Novos dados disponiveis";
                                texto = "Clique aqui para atualizar";
                                id++;
                            } else {
                                titulo = "Falha a o verificar atualizacao!";
                                not = "Erro verificar atualizacao";
                                texto = "Clique aqui para verificar !";
                                if(id>-1){
                                    id = 0;
                                }

                            }
                            final NotificationManager mgr =
                                    (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
                            Notification note = new Notification(R.drawable.ic_launcher,
                                    titulo,
                                    1);

                            note.flags = Notification.FLAG_AUTO_CANCEL;
                            PendingIntent i;

                            // This pending intent will open after notification click
                            if (id > 0) {
                                if (id == 1) {
                                        note.defaults |= Notification.DEFAULT_VIBRATE;
                                        note.defaults |= Notification.DEFAULT_LIGHTS;
                                        note.defaults |= Notification.DEFAULT_SOUND;
                                        i = PendingIntent.getActivity(ctx, 0,
                                                new Intent(ctx, ConfirmaAtu.class),
                                                PendingIntent.FLAG_ONE_SHOT);

                                      /*  note.setLatestEventInfo(ctx, not,
                                                texto, i);*/

                                        //After uncomment this line you will see number of notification arrived

                                        mgr.notify(0, note);
                                    } else {
                                     /*   i = PendingIntent.getActivity(ctx, 0,
                                                new Intent(ctx, ConfirmaAtu.class),
                                                PendingIntent.FLAG_ONE_SHOT);
                                        note.setLatestEventInfo(ctx, not,
                                                texto, i);
                                        //After uncomment this line you will see number of notification arrived
                                        mgr.notify(0, note);*/

                                    }
                            } else {
                                    if(id>-1) {
                                        i = PendingIntent.getActivity(ctx, 0,
                                                new Intent(ctx, Informacoes.class),
                                                PendingIntent.FLAG_ONE_SHOT);
                                      /*  note.setLatestEventInfo(ctx, not,
                                                texto, i);*/

                                        //After uncomment this line you will see number of notification arrived
                                        mgr.notify(1, note);
                                        id--;
                                    }
                                }
                            }
                    } catch (Exception e) {
                        erro = e.getMessage();
                    }
                } else {
                    try {
                        erro = tx.testelertxt();
                        if (erro.equalsIgnoreCase("")) {
                            versao = tx.lertxt("ver");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        erro = "Erro no arquivo de atualização";
                    }
                }


            } catch (Exception e) {

            }

        }
        Log.d("prevendaatu","notification"+ String.valueOf(id));
    }


    private boolean isNotificationVisible(Context ctx,int MY_ID) {
        Intent notificationIntent = new Intent(ctx, Informacoes.class);
        PendingIntent test = PendingIntent.getActivity(ctx, MY_ID, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        boolean teste=test!=null;
        Log.d("prevendaatu","notification"+ String.valueOf(teste));
        return test != null;
    }
}