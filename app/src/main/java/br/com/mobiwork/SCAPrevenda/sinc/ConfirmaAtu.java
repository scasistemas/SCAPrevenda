package br.com.mobiwork.SCAPrevenda.sinc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import br.com.mobiwork.SCAPrevenda.MenuInicial;
import br.com.mobiwork.SCAPrevenda.R;
import br.com.mobiwork.SCAPrevenda.dao.DaoConfig;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDB;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;
import br.com.mobiwork.SCAPrevenda.dao.DaoPonto;
import br.com.mobiwork.SCAPrevenda.inf.AsyncResponse;
import br.com.mobiwork.SCAPrevenda.inf.Informacoes;
import br.com.mobiwork.SCAPrevenda.model.Config;
import br.com.mobiwork.SCAPrevenda.model.Ponto;
import br.com.mobiwork.SCAPrevenda.model.PreVenda;
import br.com.mobiwork.SCAPrevenda.util.Alertas;
import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;
import br.com.mobiwork.SCAPrevenda.util.DataXmlExporter;

/**
 * Created by LuisGustavo on 22/09/14.
 */
public class ConfirmaAtu extends Activity implements  AsyncResponse  {

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
    public AsyncResponse delegate=null;
    public int selecionarsincMenuPricipalID;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.confatu);
        this.setFinishOnTouchOutside(false);
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
        SincDownAtu.id=0;


        config= new DaoConfig(this).consultar(this.dbP);
        if(config.getLogin()==null){
            new DataXmlExporter(this.dbP, ConfigVendedor.getExternalStorageDirectory()).importData(this, "config", "config", "");
            config= new DaoConfig(this).consultar(this.dbP);
        }



    }

    public void sincMenuPricipal(View v)
    {
                final int[] arrayOfInt = { 0 };
                    String[] arrayOfString = { "Wi-Fi", "Off-Line" };
                    AlertDialog.Builder localBuilder = new AlertDialog.Builder(ConfirmaAtu.this);
                    localBuilder.setTitle("Sincronizar").setSingleChoiceItems(arrayOfString, 0, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                        {
                            arrayOfInt[0] = paramAnonymous2Int;
                        }
                    }).setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                        {
                            paramAnonymous2DialogInterface.cancel();
                            ConfirmaAtu.this.selecionarsincMenuPricipal(ConfirmaAtu.this.selecionarsincMenuPricipalID, arrayOfInt[0]);
                        }
                    });
                    localBuilder.create().show();
                    return;


    }

    protected void selecionarsincMenuPricipal(int paramInt1, int paramInt2)
    {

        switch (paramInt1)
        {

            case 0:
                if (paramInt2 == 0) {

                    sinc = new SincDown(ConfirmaAtu.this,config,"3g2gWifi","");
                    sinc.delegate = ConfirmaAtu.this;
                    sinc.execute(new String[0]);


                    return;
                } else  {
                    sinc = new SincDown(ConfirmaAtu.this,config,"OffLine","");
                    sinc.delegate = ConfirmaAtu.this;
                    sinc.execute(new String[0]);

                    return;
                }

        }

    }

    public void voltar(View v){
        ConfirmaAtu.this.finish();
    }
    @Override
    public void processFinish(String output) {
        ConfirmaAtu.this.finish();
    }
}
