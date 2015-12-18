package br.com.mobiwork.SCAPrevenda.sinc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import br.com.mobiwork.SCAPrevenda.R;
import br.com.mobiwork.SCAPrevenda.dao.DaoConfig;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDB;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;
import br.com.mobiwork.SCAPrevenda.dao.DaoPreVenda;
import br.com.mobiwork.SCAPrevenda.inf.AsyncResponse;
import br.com.mobiwork.SCAPrevenda.model.Config;
import br.com.mobiwork.SCAPrevenda.model.PreVenda;
import br.com.mobiwork.SCAPrevenda.pedido.EnviarPedidos;
import br.com.mobiwork.SCAPrevenda.util.Alertas;
import br.com.mobiwork.SCAPrevenda.util.Arquivo;
import br.com.mobiwork.SCAPrevenda.util.ArquivoTxt;
import br.com.mobiwork.SCAPrevenda.util.Conexao;
import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;
import br.com.mobiwork.SCAPrevenda.util.DataXmlExporter;
import br.com.mobiwork.SCAPrevenda.util.SincDadosSmb;
import br.com.mobiwork.SCAPrevenda.util.ToastManager;

/**
 * Created by LuisGustavo on 22/09/14.
 */
public class SincDown  extends AsyncTask<String, Void, Boolean> {

    private SQLiteDatabase db,dbP;
    private ProgressDialog dialog;
    private Handler handler = new Handler();
    private String loteEnvio;
    private String erro,logerro,status,caminhoteste;
    private DaoCreateDB daoDB;
    private DaoCreateDBP daoDBP;
    private DaoPreVenda dpv;
    private DaoConfig dc;
    Intent i ;
    ToastManager tm;
    SincDadosSmb smb;
    Context ctx;
    PreVenda pv;
    Alertas a;
    EnviarPedidos enviarPedidos;
    Activity confirma = (Activity)ctx;
    Config config;
    ArquivoTxt tx;
    Arquivo arq ;
    Activity af;
    public static String vsjaatualizada="Versão mais atual ja atualizada!";


    public AsyncResponse delegate=null;



    public  SincDown(Context paramctx,Config paramconfig,String paramstatus,String paramteste) {
        ctx=paramctx;
        tx = new ArquivoTxt(paramctx);
        arq= new Arquivo(paramctx);
        daoDB= new DaoCreateDB(ctx);
        daoDBP = new DaoCreateDBP(ctx);
         af= (Activity) ctx;
        config=paramconfig;
        confirma = (Activity)ctx;
        this.dialog = new ProgressDialog(paramctx);
        db = daoDB.getWritableDatabase();
        dbP=daoDBP.getWritableDatabase();
        this.status=paramstatus;
        caminhoteste=paramteste;
        if(config.getVendid()==null){
            new DataXmlExporter(this.dbP, ConfigVendedor.getExternalStorageDirectory()).importData(ctx, "config", "config", "");
            config= new DaoConfig(ctx).consultar(this.dbP);
        }
        a= new Alertas(paramctx);
        dpv = new DaoPreVenda(ctx);
        dc = new DaoConfig(ctx);
        enviarPedidos = new EnviarPedidos();
        erro="";

    }

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Sincronização");
        this.dialog.setMessage("Sincronizando Dados...");
        this.dialog.setProgressStyle(dialog.STYLE_HORIZONTAL);
        this.dialog.setProgress(0);
        this.dialog.setMax(5);



        dialog.setButton(ProgressDialog.BUTTON_NEUTRAL,
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //button click stuff here

                        dialog.dismiss();
                        try {
                   //         this.finalize();
                            SincDown.this.cancel(true);
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }


                    }
                });

        this.dialog.show();
      
    }




    @Override
    protected Boolean doInBackground(String[] paramArrayOfString)
    {
        String extensao="";
        if(config.getEmp().equalsIgnoreCase("jrc")) {
            extensao="_g.db";
        }else{
            extensao=config.getVendid() + ".db";
        }
        boolean versao=false;;
        this.erro="";
        String result="";
       if(!Conexao.Conectado(ctx)&&this.status.endsWith("3g2gWifi")){
           erro="ATENCAO !!! - WIFI DESCONECTADO";
           dialog.setProgress(5);
           return  true;
       }else {
           try {

               if (this.status.endsWith("3g2gWifi")) {
                   dialog.setProgress(1);

                   if (!caminhoteste.equals("")) {
                       config.setEndereco(caminhoteste);
                   }
                   try {
                       smb = new SincDadosSmb(config, 1,ctx);
                       //
                       try {
                           //
                           result = smb.downloadFilePc("Versao", ".txt", 0);

                           if (!result.equalsIgnoreCase("")) {
                               erro = result;
                           }
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                       try {
                           dialog.setProgress(2);
                           versao = tx.lertxt("atu");
                           if(config.getVersaotabela()==null){
                               versao=true;
                           }else if(config.getVersaotabela().equalsIgnoreCase("")){
                               versao=true;
                           }
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                       if (versao && !isCancelled()) {
                           smb.smbfile();

                                result = smb.downloadFilePc(String.valueOf("scaprevendadbp"),extensao, 0);

                           //    result=smb.downloadFilePc(String.valueOf(config.getVendid()) , ".zip",0);
                           if (!result.equalsIgnoreCase("")) {
                               erro = result;
                           }
                       }
                   } catch (Exception e) {
                       erro =e.getMessage();
                   }


               } else {
                   dialog.setProgress(2);
                   try {
                       erro = tx.testelertxt();
                       if (erro.equalsIgnoreCase("")) {
                           versao = tx.lertxt("atu");
                           versao=true;
                       }
                   } catch (IOException e) {
                       e.printStackTrace();
                       erro = "Erro no arquivo de atualização";
                   }


               }

               if (versao) {
                   if ((erro.equalsIgnoreCase(""))) {
                       String pathExternalStorage = ConfigVendedor.getExternalStorageDirectory();
                       String thisFile = pathExternalStorage + "/" + config.getVendid() + ".zip";
                       //recebEmail(thisFile);
                       java.io.File file = new java.io.File(thisFile);
                       if (!file.exists()) {
                           thisFile = ConfigVendedor.getExternalStorageDirectory() + "/" + config.getVendid() + ".zip";
                           file = new java.io.File(thisFile);
                       }
                       thisFile = pathExternalStorage + "/";
                       String FilePath = file.getPath();
                       dialog.setProgress(3);
                       // this.arq.unzip(FilePath, thisFile);
                       thisFile = pathExternalStorage + "/" + "scaprevendadbp" + extensao;
                       java.io.File dbFile = new java.io.File(thisFile);
                       config = new DaoConfig(ctx).consultar(this.dbP);
                       if (dbFile.exists()) {
                           dialog.setProgress(4);
                           String dbPackage = "br.com.mobiwork.SCAPrevenda";
                           String dbName = "scaprevendadbp";
                           String dbPath = Environment.getDataDirectory() + "/data/" + dbPackage + "/databases/";
                           java.io.File exportDir = new java.io.File(dbPath);
                           if (exportDir.exists()) {
                               exportDir.delete();
                           }
                           file = new java.io.File(exportDir, dbName);
                           try {
                               file.createNewFile();
                               this.arq.copyFile(dbFile, file);

                           } catch (IOException e) {
                               String t = e.getMessage();
                               erro = "Erro ao criar arquivo baixado";
                           }
                           //break;

                       }
                   }
               }else{
                   if(erro.equalsIgnoreCase("")){
                       erro=vsjaatualizada;
                   }

               }
               handler.post(new Runnable() {
                   public void run() {
                   }
               });
           } finally {
               dialog.setProgress(5);
               if (erro.equals("")) {
                   return Boolean.valueOf(false);
               } else {
                   return Boolean.valueOf(true);
               }

           }

       }
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

            return  ;
        }else{
            this.dialog.setMessage("Atualizado com Sucesso");

            return ;

        }

    }


}
