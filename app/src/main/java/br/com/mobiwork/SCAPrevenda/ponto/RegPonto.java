package br.com.mobiwork.SCAPrevenda.ponto;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import br.com.mobiwork.SCAPrevenda.R;
import br.com.mobiwork.SCAPrevenda.dao.DaoConfig;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDB;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;
import br.com.mobiwork.SCAPrevenda.dao.DaoPonto;
import br.com.mobiwork.SCAPrevenda.inf.AsyncResponse;
import br.com.mobiwork.SCAPrevenda.model.Config;
import br.com.mobiwork.SCAPrevenda.model.Ponto;
import br.com.mobiwork.SCAPrevenda.model.PreVenda;
import br.com.mobiwork.SCAPrevenda.sinc.SincDown;
import br.com.mobiwork.SCAPrevenda.sinc.SincUp;
import br.com.mobiwork.SCAPrevenda.util.Alertas;
import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;
import br.com.mobiwork.SCAPrevenda.util.DataXmlExporter;

/**
 * Created by LuisGustavo on 22/09/14.
 */
public class RegPonto extends Activity  {

    private Button altOrc, altEnv;
    private EditText editOrc,editEnv,editVend,editAnd,editEmp,result;
    private Config config;
    private DaoCreateDB daoDB;
    private DaoCreateDBP daoDBP;
    private SQLiteDatabase db,dbP;
    private Alertas a;
    private SincDown sinc;
    private SincUp sincup;
    boolean testcamin;
    private DaoConfig dc;
    EditText hora;
    TextView data,txtit;
    private PreVenda pv;
    String tipo;
    private DaoPonto dp;
    private Ponto p;
    GregorianCalendar calendario = new GregorianCalendar();
    SimpleDateFormat formatador,formatador2 ;
    Date data2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.registrarponto);
        if(Integer.valueOf(android.os.Build.VERSION.SDK)>11) {
            this.setFinishOnTouchOutside(false);
        }
        dp = new DaoPonto(this);
        p = new Ponto();
        daoDB = new DaoCreateDB(this);
        db=daoDB.getWritableDatabase();
        daoDBP = new DaoCreateDBP(this);
        dbP=daoDBP.getWritableDatabase();
        a=new Alertas(this);
        testcamin=false;
        dc= new DaoConfig(this);
        pv= new PreVenda();
        hora=(EditText) findViewById(R.id.editHora);
        data=(TextView) findViewById(R.id.txdata);
        txtit=(TextView) findViewById(R.id.txtit);
        tipo  = getIntent().getStringExtra("Tipo");
        selecionar();
        data2 = new Date();
        formatador = new SimpleDateFormat("dd/MM/yyyy");
        formatador2 = new SimpleDateFormat("yyyy-MM-dd");
        data.setText(formatador.format(data2));
        hora.setEnabled(false);
        data.setEnabled(false);

        final Handler atualizador = new Handler();
        atualizador.post(new Runnable() {
            @Override
            public void run() {
                atualizaHora(hora);
                atualizador.postDelayed(this, 100);
            }
        });

        config= new DaoConfig(this).consultar(this.dbP);
        if(config.getLogin()==null){
            new DataXmlExporter(this.dbP, ConfigVendedor.getExternalStorageDirectory()).importData(this, "config", "config", "");
            config= new DaoConfig(this).consultar(this.dbP);
        }
        pv.setNumero("testedovendedor " + config.getVendid());


    }


    public void atualizaHora(EditText relogio) {

        calendario = new GregorianCalendar();
        int h = calendario.get(GregorianCalendar.HOUR_OF_DAY);
        int m = calendario.get(GregorianCalendar.MINUTE);
        int s = calendario.get(GregorianCalendar.SECOND);

        relogio.setText(h+":"+m+":"+s);

    }

    public void selecionar(){
        if(tipo.equalsIgnoreCase("entra")){
            txtit.setText("Entrada");
        }else  if(tipo.equalsIgnoreCase("saia")){
            txtit.setText("Saida Almoco");
        }else  if(tipo.equalsIgnoreCase("voltaa")){
            txtit.setText("Volta do Almoco");
        } if(tipo.equalsIgnoreCase("fim")){
            txtit.setText("Fim de expediente");
        }
    }

    public void confirmar(View v){


        new AlertDialog.Builder(this)
                .setTitle("Deseja confirmar o horario?")
                .setItems(R.array.op_alerta_sim_nao, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        if (i == 0) {

                            p.setDatainicio(formatador2.format(data2).toString());
                            p.setHorainicio(hora.getText().toString());
                            p.setVendedor(config.getVendid());
                            Cursor c = dp.consultar(db, p.getDatainicio(),dbP);
                            c.moveToFirst();
                            boolean reg = false;
                            if (tipo.equalsIgnoreCase("entra")) {
                                    dp.Inserir(db, p);
                                    RegPonto.this.finish();

                            } else if (tipo.equalsIgnoreCase("saia")) {

                                p.setHorasaialm(hora.getText().toString());
                                dp.alterarhorasaiAlm(db, p);
                                RegPonto.this.finish();
                            } else if (tipo.equalsIgnoreCase("voltaa")) {
                                p.setHoravoltalm(hora.getText().toString());
                                dp.alterarvoltAlm(db, p);
                                RegPonto.this.finish();
                            }
                            if (tipo.equalsIgnoreCase("fim")) {
                                p.setHorafim(hora.getText().toString());
                                dp.alterarhorafinal(db, p);
                                RegPonto.this.finish();
                            }

                        }
                    }
                }).show();



    }





    public void voltar(View v){
        RegPonto.this.finish();
    }




}
