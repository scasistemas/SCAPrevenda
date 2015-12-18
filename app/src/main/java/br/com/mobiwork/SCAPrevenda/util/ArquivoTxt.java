package br.com.mobiwork.SCAPrevenda.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.api.services.drive.model.File;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import br.com.mobiwork.SCAPrevenda.dao.DaoConfig;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;
import br.com.mobiwork.SCAPrevenda.model.Config;

/**
 * Created by LuisGustavo on 07/08/14.
 */
public class ArquivoTxt {

    private String mDLValltx;
    private Config localConfig;
    private SQLiteDatabase db;
    String linha="";


    DaoConfig dc;
    private Context ctx;

    public  ArquivoTxt(Context ctx){

        this.db = new DaoCreateDBP(ctx).getWritableDatabase();
        this.ctx=ctx;
        dc= new DaoConfig(ctx);

    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    public boolean  lertxt(String param) throws IOException {
        boolean testar=false;

        localConfig = new DaoConfig(ctx).consultar(this.db);
        SharedPreferencesUtil sdu = new SharedPreferencesUtil(ctx);
        double result,result2;
        mDLValltx = "Versao.txt";
        BufferedReader leitor = null;
        FileReader reader = null;
        final java.io.File file = new java.io.File(ConfigVendedor.getExternalStorageDirectory(),
                mDLValltx);
        if(file.exists()){
            String t = "entrou";
            File file2 = new File();
            try {
                reader = new FileReader(String.valueOf(file));
                leitor = new BufferedReader(reader);
                    this.linha = null;

                    this.linha = leitor.readLine();
                    String data2 = localConfig.getVersaotabela();
                    Double anoatu = null, dataatu = null, horaatu = null;
                    Double ano, data, hora;
                    try{
                anoatu = Double.parseDouble(this.linha.substring(0, 4));
                dataatu = Double.parseDouble(this.linha.substring(4, 8));
                horaatu = Double.parseDouble(this.linha.substring(8, 12));
               }catch(Exception e){
                    testar=false;
                }
                ano=0.0;
                data=0.0;
                hora=0.0;
                if (localConfig.getVersaotabela() == null || localConfig.getVersaotabela().equalsIgnoreCase("")) {

                    return false;
                }
                if(localConfig.getVersaotabela()!=null&&!localConfig.getVersaotabela().equalsIgnoreCase("")) {
                    if(sdu.getversaodados().equalsIgnoreCase("")){
                        sdu.setversaodados(localConfig.getVersaotabela());
                    }
                    ano = Double.parseDouble(sdu.getversaodados().substring(0, 4));
                    data = Double.parseDouble(sdu.getversaodados().substring(4, 8));
                    hora = Double.parseDouble(sdu.getversaodados().substring(8, 12));

                    Log.d("prevendaatu", "arquivo:"+linha);
                    Log.d("prevendaatu","vs atual:"+ sdu.getversaodados());


                    if (anoatu > ano) {
                        testar = true;
                    } else if (anoatu >= ano) {
                        if (dataatu > data) {
                            testar = true;
                        } else if (dataatu >= data && horaatu > hora) {
                            testar = true;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            leitor.close();
            reader.close();
        }else{
            testar=true;
        }
        if(testar&&param.equalsIgnoreCase("atu")){
            sdu.setversaodados(linha);
        }
        Log.d("true",String.valueOf(testar));
        return testar;
    }
    public String  testelertxt() throws IOException {
        String testar="";
        localConfig = new DaoConfig(ctx).consultar(this.db);
        double result,result2;
        mDLValltx = "ver_"+this.localConfig.getVendid()+".txt";
        BufferedReader leitor = null;
        FileReader reader = null;
        final java.io.File file = new java.io.File(ConfigVendedor.getExternalStorageDirectory(),
                mDLValltx);
        if(file.exists()){

            File file2 = new File();
            try {
                reader = new FileReader(String.valueOf(file));
                leitor = new BufferedReader(reader);
                this.linha = null;

                this.linha = leitor.readLine();
                String data2 = localConfig.getVersaotabela();
                Double anoatu = null, dataatu = null, horaatu = null;
                Double ano, data, hora;
                try{
                    anoatu = Double.parseDouble(this.linha.substring(0, 4));
                    dataatu = Double.parseDouble(this.linha.substring(4, 8));
                    horaatu = Double.parseDouble(this.linha.substring(8, 12));
                }catch(Exception e){
                    testar="ERRO NO ARQUIVO DE VERSAO";
                    return testar;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            leitor.close();
            reader.close();
        }

        return testar;
    }



}