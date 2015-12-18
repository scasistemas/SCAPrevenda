package br.com.mobiwork.SCAPrevenda.ponto;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.mobiwork.SCAPrevenda.R;
import br.com.mobiwork.SCAPrevenda.dao.DaoConfig;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDB;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;
import br.com.mobiwork.SCAPrevenda.dao.DaoPonto;
import br.com.mobiwork.SCAPrevenda.inf.AsyncResponse;
import br.com.mobiwork.SCAPrevenda.model.Config;
import br.com.mobiwork.SCAPrevenda.model.PreVenda;
import br.com.mobiwork.SCAPrevenda.sinc.SincDown;
import br.com.mobiwork.SCAPrevenda.sinc.SincUp;
import br.com.mobiwork.SCAPrevenda.util.Alertas;
import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;
import br.com.mobiwork.SCAPrevenda.util.DataXmlExporter;

/**
 * Created by LuisGustavo on 22/09/14.
 */
public class InfoPonto extends Activity implements AsyncResponse {

    private Button altOrc, altEnv;
    private EditText editOrc,editEnv,editVend,editAnd,editEmp,result;
    private Config config;
    private DaoCreateDBP daoDBP;
    private SQLiteDatabase dbP;
    private DaoCreateDB daoDB;
    private SQLiteDatabase db;

    private SincDown sinc;
    private SincUp sincup;
    boolean testcamin;
    private DaoConfig dc;
    EditText editE,editS,editV,editF;
    private PreVenda pv;
    private DaoPonto dp;
    Alertas a ;
    private Button reg1,reg2,reg3,reg4;
    Date data2;
    SimpleDateFormat formatador2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ponto);
        if(Integer.valueOf(android.os.Build.VERSION.SDK)>11) {
            this.setFinishOnTouchOutside(false);
        }


        reg1=(Button)findViewById(R.id.regEnt);
        reg2=(Button)findViewById(R.id.regSaiA);
        reg3=(Button)findViewById(R.id.regVolAl);
        reg4=(Button)findViewById(R.id.regFimExp);

        editE=(EditText)findViewById(R.id.editEnt);
        editS=(EditText)findViewById(R.id.editSaia);
        editV=(EditText)findViewById(R.id.editVolta);
        editF=(EditText)findViewById(R.id.editFim);

        a= new Alertas(this);
       // iniEna();
        dp =new DaoPonto(this);
        daoDBP = new DaoCreateDBP(this);
        dbP=daoDBP.getWritableDatabase();
        daoDB = new DaoCreateDB(this);
        db=daoDB.getWritableDatabase();
        a=new Alertas(this);
        testcamin=false;
        dc= new DaoConfig(this);
        pv= new PreVenda();
         data2 = new Date();
         formatador2 = new SimpleDateFormat("yyyy-MM-dd");

        config= new DaoConfig(this).consultar(this.dbP);
        if(config.getLogin()==null){
            new DataXmlExporter(this.dbP, ConfigVendedor.getExternalStorageDirectory()).importData(this, "config", "config", "");
            config= new DaoConfig(this).consultar(this.dbP);
        }
        pv.setNumero("testedovendedor "+config.getVendid());
        verificar();

    }

    public void iniEna(){
        reg2.setEnabled(false);
        reg3.setEnabled(false);
        reg4.setEnabled(false);
        editE.setEnabled(false);
        editS.setEnabled(false);
        editV.setEnabled(false);
        editF.setEnabled(false);
    }

    public boolean veri(String campo){
        Cursor c = dp.consultar(db,formatador2.format(data2),dbP);
        c.moveToFirst();
        boolean reg = false;
        if (c.moveToFirst()) {
            if (!c.getString(c.getColumnIndex(campo)).equalsIgnoreCase("")) {

                reg = true;
            }
        }
        return reg;
    }

    public void reg1(View v){
        // get prompts.xml view
        if(veri("horainicial")==false){
            Intent i;
            i = new Intent(this,RegPonto.class);
            i.putExtra("Tipo", "entra");
            this.startActivityForResult(i, 0);
        }else{
            a.Alerta("Ja foi cadastrado horario de entrada");
        }

    }

    public void reg2(View v){
        // get prompts.xml view
        if(veri("horasaialm")==false){
            Intent i;
            i = new Intent(this,RegPonto.class);
            i.putExtra("Tipo", "saia");
            this.startActivityForResult(i, 0);
        }else{
            a.Alerta("Ja foi cadastrado horario de saida para almoco");
        }

    }

    public void reg3(View v){
        // get prompts.xml view
        if(veri("horavoltalm")==false){
            Intent i;
            i = new Intent(this,RegPonto.class);
            i.putExtra("Tipo", "voltaa");
            this.startActivityForResult(i, 0);
        }else{
            a.Alerta("Ja foi cadastrado horario de retorno do almoco");
        }

    }

    public void reg4(View v){
        // get prompts.xml view
        if(veri("horafim")==false){
            Intent i;
            i = new Intent(this,RegPonto.class);
            i.putExtra("Tipo", "fim");
            this.startActivityForResult(i, 0);
        }else{
            a.Alerta("Ja foi cadastrado horario de fim de expediente");
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Incluir Item
       verificar();

        }
    public void verificar(){
        Cursor teste;

        teste=dp.consultar(db,formatador2.format(data2),dbP);
    if(teste!=null) {
        if (teste.moveToFirst()) {
            String teste2=teste.getString(teste.getColumnIndex("datainicial"));
            if (!teste.getString(teste.getColumnIndex("horainicial")).equalsIgnoreCase("")) {
                editE.setText(teste.getString(teste.getColumnIndex("horainicial")));
                reg2.setEnabled(true);
            }
            if (!teste.getString(teste.getColumnIndex("horasaialm")).equalsIgnoreCase("")) {
                reg3.setEnabled(true);
                editS.setText(teste.getString(teste.getColumnIndex("horasaialm")));
            }
            if (!teste.getString(teste.getColumnIndex("horavoltalm")).equalsIgnoreCase("")) {
                reg4.setEnabled(true);
                editV.setText(teste.getString(teste.getColumnIndex("horavoltalm")));
            }
            if (!teste.getString(teste.getColumnIndex("horafim")).equalsIgnoreCase("")) {
                editF.setText(teste.getString(teste.getColumnIndex("horafim")));
            }

    }
}
    }

    public void voltar(View v){
        InfoPonto.this.finish();
    }



    @Override
    public void processFinish(String output) {



    }
}
