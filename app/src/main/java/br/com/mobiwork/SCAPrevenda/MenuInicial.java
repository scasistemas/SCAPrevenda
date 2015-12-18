package br.com.mobiwork.SCAPrevenda;

        import android.app.ActionBar;
        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.Notification;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.database.sqlite.SQLiteDatabase;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.view.ViewConfiguration;
        import android.widget.Toast;


        import java.lang.reflect.Field;

        import br.com.mobiwork.SCAPrevenda.atualizacao.ListAtu;
        import br.com.mobiwork.SCAPrevenda.inf.AsyncResponse;
        import br.com.mobiwork.SCAPrevenda.inf.Informacoes;
        import br.com.mobiwork.SCAPrevenda.model.Ponto;
        import br.com.mobiwork.SCAPrevenda.pedido.Orcamentos;
        import br.com.mobiwork.SCAPrevenda.pedido.PedidosEnviados;
        import br.com.mobiwork.SCAPrevenda.pedido.pedidovendateste;
        import br.com.mobiwork.SCAPrevenda.ponto.InfoPonto;
        import br.com.mobiwork.SCAPrevenda.sinc.AtuService;
        import br.com.mobiwork.SCAPrevenda.sinc.ConfirmaAtu;
        import br.com.mobiwork.SCAPrevenda.sinc.SincConfig;
        import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;
        import br.com.mobiwork.SCAPrevenda.login.Login;
        import br.com.mobiwork.SCAPrevenda.model.Config;
        import br.com.mobiwork.SCAPrevenda.sinc.SincDown;
        import br.com.mobiwork.SCAPrevenda.util.Alertas;
        import br.com.mobiwork.SCAPrevenda.util.BkpBancoDeDados;
        import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;

public class MenuInicial extends Activity implements OnClickListener , AsyncResponse
{


    private static SQLiteDatabase dbP;
    private static int vendid;
    private Config config;
    public static Context mainContext;
    public int selecionarsincMenuPricipalID;
    private Alertas a;
    DaoCreateDBP daoDBP;
    private SincDown sinc;
    public static int id=0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        View button1Click = findViewById(R.id.btAtividadesMenuPricipal);
        button1Click.setOnClickListener(this);
        button1Click = findViewById(R.id.btPedidosMenuPricipal);
        button1Click.setOnClickListener(this);
        button1Click = findViewById(R.id.btSincMenuPricipal);
        button1Click.setOnClickListener(this);
        button1Click = findViewById(R.id.btOrcamento);
        button1Click.setOnClickListener(this);
        button1Click.setOnClickListener(this);
        daoDBP= new DaoCreateDBP(this);
        dbP =  daoDBP.getWritableDatabase();
        this.config= new Config();
        this.config = ConfigVendedor.getConfig(this.dbP);


        a=new Alertas(this);
        if (this.config.getLoginpre()!=null)
        {
            Intent ix = new Intent(MenuInicial.this,Login.class);
            MenuInicial.this.startActivityForResult(ix, 130);
        }
        if(Integer.valueOf(android.os.Build.VERSION.SDK)>11){
            ActionBar ab = getActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg));
        }

        getOverflowMenu();
    }


    private void getOverflowMenu() {

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        switch(v.getId()){

            case R.id.btAtividadesMenuPricipal:
             //   Intent it = new Intent(MenuInicial.this, AtuService.class);
               // MenuInicial.this.startService(it);
                irPedidoVenda();

                break;
            case R.id.btPedidosMenuPricipal:
                pedidosMenuPricipal();
                break;
            case R.id.btSincMenuPricipal:
                sincMenuPricipal();
                break;
            case R.id.btOrcamento:
                orcamentosMenuPrincipal();
                break;
            case R.id.btSairMenuPricipal:
                Intent ix;
                ix = new Intent(this, BkpBancoDeDados.class);
                ix.putExtra("tBkp", "Backuprapido");
                this.startActivityForResult(ix, 0);
                MenuInicial.this.finish();
                System.exit(0);
        }
    }

    private void pedidosMenuPricipal() {
        Intent ix;
        ix = new Intent(this,PedidosEnviados.class);
        ix.putExtra("vendid", vendid);
        startActivityForResult(ix, 101);

    }

    private void orcamentosMenuPrincipal(){
        Intent ix;
        ix = new Intent(this,Orcamentos.class);
        ix.putExtra("vendid", vendid);
        startActivityForResult(ix, 101);
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        this.config = ConfigVendedor.getConfig(this.dbP);
        if(requestCode == 100 && resultCode==RESULT_FIRST_USER) {
            a.AlertaSinc("SEM NOVAS ATUALIZAÇÕES!");
        }
        if(requestCode == 130) {
           if(data.getExtras().getString("fechar").equalsIgnoreCase("fechar")){
               MenuInicial.this.finish();
               System.exit(0);
           }
        }

    }

    public void irPedidoVenda(){
        Intent k;
        k = new Intent(MenuInicial.this,pedidovendateste.class);
        k.putExtra("tipopre", "");
        k.putExtra("PEDIDO_ID", "");
        MenuInicial.this.startActivityForResult(k,0);
    }

    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }



    private void sincMenuPricipal()
    {
        new AlertDialog.Builder(this).setTitle(R.string.op_menu_principal).setItems(R.array.op_menu_principalSinc, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
                MenuInicial.this.selecionarsincMenuPricipalID = paramAnonymousInt;
                if (paramAnonymousInt <1)
                {
                    final int[] arrayOfInt = { 0 };
                    String[] arrayOfString = { "Wi-Fi", "Off-Line" };
                    AlertDialog.Builder localBuilder = new AlertDialog.Builder(MenuInicial.this);
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
                            MenuInicial.this.selecionarsincMenuPricipal(MenuInicial.this.selecionarsincMenuPricipalID, arrayOfInt[0]);
                        }
                    });
                    localBuilder.create().show();
                    return;
                }
                MenuInicial.this.selecionarsincMenuPricipal(paramAnonymousInt, 0);
            }
        }).show();
    }


    protected void runBackup() {

        final int[] arrayOfInt = { 0 };
        String[] arrayOfString = { "Restaurar Backup","Criar  Backup" };
        new AlertDialog.Builder(this).setTitle("Backup").setSingleChoiceItems(arrayOfString, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                arrayOfInt[0] = paramInt;
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                escBackup(arrayOfInt[0]);
            }
        }).show();

    }
    public void escBackup(int param){
        Intent localIntent = new Intent(this, BkpBancoDeDados.class);

        if (param== 1){
            localIntent.putExtra("tBkp", "Backup");
            this.startActivity(localIntent);
        }else if(param==0) {
            localIntent= new Intent(this,BkpBancoDeDados.class);
            localIntent.putExtra("vendid", vendid);
            localIntent.putExtra("tConn", "3g2gWifi");
            localIntent.putExtra("mod","backup");
            localIntent.putExtra("tBkp", "Restaure");
            this.startActivityForResult(localIntent,101);

        }
        return;
    }




    protected void selecionarsincMenuPricipal(int paramInt1, int paramInt2)
    {

        switch (paramInt1)
        {

            case 0:
                if (paramInt2 == 0) {

                    sinc = new SincDown(MenuInicial.this,config,"3g2gWifi","");
                    sinc.delegate = MenuInicial.this;
                    sinc.execute(new String[0]);

                    return;
                } else  {
                    sinc = new SincDown(MenuInicial.this,config,"OffLine","");
                    sinc.delegate = MenuInicial.this;
                    sinc.execute(new String[0]);
                    return;
                }
            case 1:
                startActivityForResult(new Intent(this, SincConfig.class), 0);
                break;

            case 2:
                Intent ix;
                ix = new Intent(this, Informacoes.class);
                this.startActivityForResult(ix,0);
        }

    }
    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public boolean onMenuItemSelected(int panel, MenuItem item){
       int k=item.getItemId();
        switch(item.getItemId()){
            case android.R.id.home:
          alertaSair();

                break;
            case R.id.item1:
                irPedidoVenda();
                break;
            case R.id.item2:
                pedidosMenuPricipal();
                break;
            case R.id.item3:
                runBackup();
                break;
            case R.id.item4:
                Intent i;
                i = new Intent(this, InfoPonto.class);
                i.putExtra("tBkp", "Backuprapido");
                this.startActivityForResult(i, 0);
                break;

            case R.id.item5:
                Intent a;
                a = new Intent(this, ListAtu.class);
                this.startActivityForResult(a, 0);

                break;

            case R.id.item6:
                Intent ix;
                ix = new Intent(this, BkpBancoDeDados.class);
                ix.putExtra("tBkp", "Backuprapido");
                this.startActivityForResult(ix, 0);
                System.exit(0);
                MenuInicial.this.finish();
                break;
        }

        return(true);
    }


    public void alertaSair() {
        new AlertDialog.Builder(this)
                .setTitle("Deseja Sair do sistema?")
                .setItems(R.array.op_alerta_sim_nao, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        if (i == 0) {

                            MenuInicial.this.finish();
                            //                 cancelarPedido();
                        }
                    }
                }).show();
    }

    @Override
    public void processFinish(String output) {

    }
}





