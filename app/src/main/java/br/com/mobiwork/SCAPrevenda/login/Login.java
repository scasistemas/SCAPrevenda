
package br.com.mobiwork.SCAPrevenda.login;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.database.sqlite.SQLiteDatabase;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.view.Window;
        import android.view.WindowManager;
        import android.widget.EditText;
        import android.widget.TextView;

        import java.text.SimpleDateFormat;
        import java.util.Date;

        import br.com.mobiwork.SCAPrevenda.MenuInicial;
        import br.com.mobiwork.SCAPrevenda.R;
        import br.com.mobiwork.SCAPrevenda.dao.DaoConfig;
        import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDB;
        import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;
        import br.com.mobiwork.SCAPrevenda.dao.DaoPonto;
        import br.com.mobiwork.SCAPrevenda.dao.DaoVendAtual;
        import br.com.mobiwork.SCAPrevenda.dao.DaoVendedor;
        import br.com.mobiwork.SCAPrevenda.model.Config;
        import br.com.mobiwork.SCAPrevenda.model.VendAtual;
        import br.com.mobiwork.SCAPrevenda.model.Vendedor;
        import br.com.mobiwork.SCAPrevenda.sinc.AtuService;
        import br.com.mobiwork.SCAPrevenda.util.Alertas;
        import br.com.mobiwork.SCAPrevenda.util.Conexao;
        import br.com.mobiwork.SCAPrevenda.util.ConfigServico;
        import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;

/**
 * Created by LuisGustavo on 08/08/14.
 */
public class Login extends Activity {

    private EditText login,senha;
    private Alertas a;
    private VendAtual va;
    private Vendedor v;
    private DaoVendAtual dva;
    private DaoVendedor dv;
    private SQLiteDatabase db,dbP;
    private DaoCreateDB daoDB ;
    private DaoCreateDBP daoDBP ;
    private DaoPonto dp;
    private TextView txtvendedor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        if(Integer.valueOf(android.os.Build.VERSION.SDK)>=11) {
            this.setFinishOnTouchOutside(false);
        }
        login=(EditText)findViewById(R.id.login);
        senha =(EditText)findViewById(R.id.senha);
        dva = new DaoVendAtual(this);
        dv= new DaoVendedor(this);
        daoDB = new DaoCreateDB(this);
        db=daoDB.getWritableDatabase();
        daoDBP = new DaoCreateDBP(this);
        dbP=daoDBP.getWritableDatabase();
        dp = new DaoPonto(this);
        txtvendedor=(TextView)findViewById(R.id.txtvendedor);
        Config config= new DaoConfig(this).consultar(this.dbP);
        if(config.getEmp().equalsIgnoreCase("vitoria")) {
            if (config.getVendid() == 633) {
                txtvendedor.setText("Vendedor(a) : Geral");
            } else {
                txtvendedor.setText("Vendedor(a) : " + config.getNome());

            }
        }else{
           // login.setText("CLEIDE");
          //  senha.setText("123");
            txtvendedor.setText("Vendedor(a) ");
        }
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        a=new Alertas(this);
    }
    @Override
    public void onBackPressed() {
        return;
    }

    public void alterarPonto(){
        Intent result=new Intent(Login.this,MenuInicial.class);
        result.putExtra("fechar","nao");
        setResult(3, result);
        SimpleDateFormat formatador2 = new SimpleDateFormat("yyyy-MM-dd");
        Date data2 = new Date();
        if(!dp.veri("horainicial", db, formatador2, data2,dbP)||(dp.veri("horasaialm", db, formatador2, data2,dbP)==true && dp.veri("horavoltalm", db, formatador2, data2,dbP)==false)){

            new AlertDialog.Builder(this)
                    .setTitle(" Atualizar  hora da sua entrada?")
                    .setItems(R.array.op_alerta_sim_nao, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            if (i == 0) {
                                dp.inserirAtu(db,dbP);
                                Login.this.finish();
                                //                 cancelarPedido();
                            }else{
                                Login.this.finish();
                            }
                        }
                    }).show();

        }else{
            Login.this.finish();
        }
    }

    public void acessar(View v){
        Config config= new DaoConfig(this).consultar(this.dbP);
        if((config.getEmp().equalsIgnoreCase("vitoria")&&config.getVendid()!=633)){
            DaoConfig dc = new DaoConfig(this);
            if(dc.login(login.getText().toString(),senha.getText().toString(),this)){
                alterarPonto();
                ConfigServico cs = new ConfigServico();
                cs.inistopservice(this);
            }else{
                a.Alerta("Login ou Senha incorretos!!!");
            }
        }else if(config.getEmp().equalsIgnoreCase("jrc")) {
            ConfigServico cs = new ConfigServico();
            cs.inistopservice(this);
            Vendedor vend = new Vendedor();
            vend.setVennome(login.getText().toString());
            vend.setVensenha(senha.getText().toString());
            vend = dv.login(vend, this);
            final DaoPonto dp = new DaoPonto(this);
            if (vend.getVencodigo() != -1) {
                va = new VendAtual();
                va.setCodigovend(vend.getVencodigo());
                va.setNomevend(vend.getVennome());
                dva.logar(va, this);
                alterarPonto();

            } else {
                a.Alerta("Login ou Senha incorretos!!!");
            }
        }


    }
    public void fecharprevenda(View v){
        Intent result=new Intent(Login.this,MenuInicial.class);
        result.putExtra("fechar", "fechar");
        setResult(3, result);



        Login.this.finish();
    }
}
