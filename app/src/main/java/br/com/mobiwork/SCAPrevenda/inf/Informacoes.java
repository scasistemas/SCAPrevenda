package br.com.mobiwork.SCAPrevenda.inf;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import br.com.mobiwork.SCAPrevenda.R;
import br.com.mobiwork.SCAPrevenda.dao.DaoConfig;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;
import br.com.mobiwork.SCAPrevenda.dao.DaoVendAtual;
import br.com.mobiwork.SCAPrevenda.model.Config;
import br.com.mobiwork.SCAPrevenda.model.PreVenda;
import br.com.mobiwork.SCAPrevenda.model.VendAtual;
import br.com.mobiwork.SCAPrevenda.sinc.AtuService;
import br.com.mobiwork.SCAPrevenda.sinc.SincDown;
import br.com.mobiwork.SCAPrevenda.sinc.SincDownAtu;
import br.com.mobiwork.SCAPrevenda.sinc.SincUp;
import br.com.mobiwork.SCAPrevenda.util.Alertas;
import br.com.mobiwork.SCAPrevenda.util.ConfigServico;
import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;
import br.com.mobiwork.SCAPrevenda.util.DataXmlExporter;
import br.com.mobiwork.SCAPrevenda.util.SharedPreferencesUtil;

/**
 * Created by LuisGustavo on 22/09/14.
 */
public class Informacoes extends Activity implements AsyncResponse {

    private Button altOrc, altEnv;
    private EditText editOrc,editEnv,editVend,editAnd,editEmp,result,versao,edtxsmart;
    private Config config;
    private DaoCreateDBP daoDBP;
    private SQLiteDatabase dbP;
    private Alertas a;
    private SincDown sinc;
    private SincUp sincup;
    boolean testcamin;
    private DaoConfig dc;
    EditText userInput,login,senha,loginorc,senhaorc;
    private PreVenda pv;
    private CheckBox ckstatus,ckatuaaut;
    private boolean bckatuaaut=false;
    SharedPreferencesUtil sdu ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.informacoes);
        inibot();
        iniEdit();
        iniperm();
        altatuaut();
        daoDBP = new DaoCreateDBP(this);
        dbP=daoDBP.getWritableDatabase();
        a=new Alertas(this);
        testcamin=false;
        dc= new DaoConfig(this);
        pv= new PreVenda();
        sdu = new SharedPreferencesUtil(getBaseContext());

        config= new DaoConfig(this).consultar(this.dbP);
        if(config.getLogin()==null){
            new DataXmlExporter(this.dbP, ConfigVendedor.getExternalStorageDirectory()).importData(this, "config", "config", "");
            config= new DaoConfig(this).consultar(this.dbP);
        }
        pv.setNumero("testedovendedor "+config.getVendid());
        preencherCampos(config);
        SincDownAtu.id=0;
    }

    public void inibot(){
        altOrc=(Button) findViewById(R.id.altOrc);
        altEnv=(Button) findViewById(R.id.altEnv);
    }

    public void altatuaut(){
        ckatuaaut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                SharedPreferencesUtil sdu = new SharedPreferencesUtil(getBaseContext());
                sdu.setAtuAut(ckatuaaut.isChecked());
                    if (!ckatuaaut.isChecked()) {
                        Intent it = new Intent(Informacoes.this, AtuService.class);
                        Informacoes.this.stopService(it);
                    } else {
                        ConfigServico cs = new ConfigServico();
                        cs.inistopservice(Informacoes.this);
                    }
                }

        });
    }

    public void iniEdit(){
        editOrc=(EditText) findViewById(R.id.editOrc);
        editEnv=(EditText) findViewById(R.id.editEnv);
        editVend=(EditText) findViewById(R.id.editVend);
        editAnd=(EditText) findViewById(R.id.editAnd);
        editEmp=(EditText) findViewById(R.id.editEmp);
        edtxsmart=(EditText) findViewById(R.id.edtxsmartinfo);
        result = (EditText) findViewById(R.id.editTextDialogUserInput);
        ckatuaaut=(CheckBox) findViewById(R.id.ckatuaut);


    }

    public void iniperm(){
        editOrc.setEnabled(false);
        editEnv.setEnabled(false);
        editVend.setEnabled(false);
        editAnd.setEnabled(false);
        editEmp.setEnabled(false);
        edtxsmart.setEnabled(false);

    }

    public void preencherCampos(Config config){
        SharedPreferencesUtil sdu = new SharedPreferencesUtil(this);
        VendAtual va;
        DaoVendAtual dva= new DaoVendAtual(this);
        va=dva.getVendAtual(this);
        editOrc.setText(config.getEnderecoorc());
        editEnv.setText(config.getEndereco());
        //    UM VENDEDOR
        if(config.getEmp()!=null) {
            if ( (config.getEmp().equalsIgnoreCase("vitoria") && config.getVendid() != 633)) {
                editVend.setText(ConfigVendedor.getConfig(dbP).getVendid() + "-" + ConfigVendedor.getConfig(dbP).getNome());
            } else if(config.getEmp().equalsIgnoreCase("jrc")) {
                editVend.setText(va.getCodigovend() + "-" + va.getNomevend());
            }
            editAnd.setText(ConfigVendedor.getExternalStorageDirectory());
            editEmp.setText(config.getEmp() + " - 1.20");
            edtxsmart.setText(String.valueOf(sdu.getIdSmart()));
        }

        ckatuaaut.setChecked(sdu.getAtuAut());

    }

    public void habserv(View v){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(Informacoes.this);
        View promptsView = li.inflate(R.layout.alertinput, null);
        SharedPreferencesUtil sdu = new SharedPreferencesUtil(getBaseContext());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Informacoes.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        login = (EditText) promptsView
                .findViewById(R.id.txtlogin);
        senha = (EditText) promptsView
                .findViewById(R.id.txtsenha);
        ckstatus=(CheckBox)promptsView
                .findViewById(R.id.ckstatus);
        ckstatus.setChecked(sdu.getGuestUser());
        userInput.setText(config.getEndereco());
        login.setText(config.getLogin());
        senha.setText(config.getSenha());

       //userInput.setText("smb://LuisGustavo-pc/Users/LuisGustavo/prevendajrc");
       //login.setText("LuisGustavo");
       //senha.setText("lg982110");

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                try {
                                    SharedPreferencesUtil sdu = new SharedPreferencesUtil(getBaseContext());
                                    sdu.setGuestUser(ckstatus.isChecked());
                                    config.setLogin(login.getText().toString());
                                    config.setSenha(senha.getText().toString());
                                    sinc = new SincDown(Informacoes.this, config, "3g2gWifi", userInput.getText().toString());
                                    sinc.delegate = Informacoes.this;
                                    sinc.execute(new String[0]);
                                } catch (Exception e) {
                                    a.AlertaSinc("Caminho invalido");
                                }
                            }

                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void habservorc(View v){

        LayoutInflater li = LayoutInflater.from(Informacoes.this);
        View promptsView = li.inflate(R.layout.alertinput, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Informacoes.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        SharedPreferencesUtil sdu = new SharedPreferencesUtil(getBaseContext());

        userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        loginorc = (EditText) promptsView
                .findViewById(R.id.txtlogin);
        senhaorc = (EditText) promptsView
                .findViewById(R.id.txtsenha);
        ckstatus=(CheckBox)promptsView
                .findViewById(R.id.ckstatus);
        ckstatus.setChecked(sdu.getGuestUser());
        userInput.setText(config.getEnderecoorc());
        loginorc.setText(config.getLogin());
        senhaorc.setText(config.getSenha());



        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                try {
                                    SharedPreferencesUtil sdu = new SharedPreferencesUtil(getBaseContext());
                                    sdu.setGuestUser(ckstatus.isChecked());
                                    config.setLogin(loginorc.getText().toString());
                                    config.setSenha(senhaorc.getText().toString());
                                    sincup = new SincUp(Informacoes.this,pv,userInput.getText().toString(),config);
                                    sincup.delegate = Informacoes.this;
                                    sincup.execute(new String[0]);

                                } catch (Exception e) {
                                    a.AlertaSinc("Caminho invalido");
                                }
                            }

                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


    }

    public void showaltsmart(View v){
        a.CadSmart(this);
    }


    public void voltar(View v){
        Informacoes.this.finish();
    }



    @Override
    public void processFinish(String output) {

        if(output.equalsIgnoreCase("")){
            dc.update(dbP, userInput.getText().toString(),login.getText().toString(),senha.getText().toString());
            config = new DaoConfig(Informacoes.this).consultar(dbP);

        }
        if(output.equalsIgnoreCase("1")){
            String t= userInput.getText().toString();
            dc.updateOrc(dbP, userInput.getText().toString(),loginorc.getText().toString(),senhaorc.getText().toString());

        }
        if(output.equalsIgnoreCase(SincDown.vsjaatualizada)){
            String t= userInput.getText().toString();
            dc.update(dbP, userInput.getText().toString(), login.getText().toString(), senha.getText().toString());
        }

        SincDownAtu.id=0;
        config = new DaoConfig(Informacoes.this).consultar(dbP);
        preencherCampos(config);


    }
}
