package br.com.mobiwork.SCAPrevenda.atualizacao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.mobiwork.SCAPrevenda.R;
import br.com.mobiwork.SCAPrevenda.dao.DaoConfig;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;
import br.com.mobiwork.SCAPrevenda.model.Config;
import br.com.mobiwork.SCAPrevenda.util.BaseAdapterFiltro;
import br.com.mobiwork.SCAPrevenda.util.Conexao;
import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;
import br.com.mobiwork.SCAPrevenda.util.DataXmlExporter;
import br.com.mobiwork.SCAPrevenda.util.SincDadosSmb;
import br.com.mobiwork.SCAPrevenda.util.UpdateApp;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;

/**
 * Created by LuisGustavo on 16/04/2015.
 */
public class ListAtu extends Activity {





    private ArrayAdapter mAdapter;
    List<HashMap<String, String>> fillMaps;
    private ListAdapter adapter;
    String mod;
    private static final String Tag = "DataMobile";

    private ProgressDialog dialog,dialog2;
    private BaseAdapterFiltro b;
    ListView list;
    Config config;





    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.listatu);

        this.list = (ListView) findViewById(R.id.list);
        listar();
        fillMaps = new ArrayList<HashMap<String, String>>();
        DaoCreateDBP daoDBP = new DaoCreateDBP(this);
        SQLiteDatabase dbP=daoDBP.getWritableDatabase();
        config =  new DaoConfig(this).consultar(dbP);
        //populateListView();
        dbP.close();
        daoDBP.close();



        AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener()
        {

            public void onItemClick(AdapterView parent, View v, final int position, long id)
            {
                download(position);
            }
        };
        list.setOnItemClickListener(mMessageClickedHandler);




    }
    public void sair(View v){
        ListAtu.this.finish();
    }

    public void download(final int position){
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try{
                    if(Conexao.Conectado(ListAtu.this)){
                        SincDadosSmb smb= new SincDadosSmb(config,2,ListAtu.this);

                        if(smb.downloadFilePc(fillMaps.get(position).get("_id"),"",1).trim().isEmpty()){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(ListAtu.this)
                                                .setTitle("Alerta")
                                                .setMessage("Tem certeza que deseja atualizar o sistema?")
                                                .setPositiveButton("Atualizar", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        UpdateApp atualizaApp = new UpdateApp();
                                                        atualizaApp.setContext(getApplicationContext(), fillMaps.get(position).get("_id"));
                                                        atualizaApp.execute();
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // do nothing
                                                    }
                                                })

                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                    }
                                });
                        }
                    }else{
                        showToast("Sem Conexao com a Internet");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                    dialog2.dismiss();
                }

            }
        });
        dialog2 = ProgressDialog.show(ListAtu.this, "Transferencia", "Fazendo a transferencia do arquivo...", false, true);
        t.start();
    }

    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ListAtu.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void atualizar(View v){
        listar();
    }





    public void ListarPasta(SmbFile vDir, ArrayList<String> vLista) {
        SmbFile[] files = new SmbFile[100];
        try {
            files = vDir.listFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i=0;i<files.length;i++){
            vLista.add(i,files[i].getName().toString());
        }

    }


    public void listar() {

        DaoCreateDBP daoDBP = new DaoCreateDBP(this);
        SQLiteDatabase dbP=daoDBP.getWritableDatabase();
        final SincDadosSmb smb;
        config= new DaoConfig(this).consultar(dbP);
        if(config==null){
            new DataXmlExporter(dbP, ConfigVendedor.getExternalStorageDirectory()).importData(this, "config", "config", "");
            config= new DaoConfig(this).consultar(dbP);
        }
        smb = new SincDadosSmb(config,2,this);
        smb.criarpastaSmb();



        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try{
                    fillMaps = smb.smbfile();
                    populateListView();

                }finally {
                    dialog.dismiss();
                }

            }
        });
        dialog = ProgressDialog.show(ListAtu.this, "Verificando", "Atualizando lista ...", false, true);
        t.start();


    }






    private void populateListView() {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                list.setAdapter(new BaseAdapterFiltro(ListAtu.this, fillMaps, list, "restaure"));


            }
        });


    }


}
