package br.com.mobiwork.SCAPrevenda.pedido;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import br.com.mobiwork.SCAPrevenda.R;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDB;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;
import br.com.mobiwork.SCAPrevenda.dao.DaoPreVenda;
import br.com.mobiwork.SCAPrevenda.dao.DaoVendAtual;
import br.com.mobiwork.SCAPrevenda.inf.AsyncResponse;
import br.com.mobiwork.SCAPrevenda.model.Config;
import br.com.mobiwork.SCAPrevenda.model.PreVenda;
import br.com.mobiwork.SCAPrevenda.model.VendAtual;
import br.com.mobiwork.SCAPrevenda.sinc.SincUp;
import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;

/**
 * Created by LuisGustavo on 19/08/14.
 */
public class PedidosEnviados extends Activity  implements AsyncResponse {
    private EditText searchText,txtSearch;
    private Cursor cursor;
    private ListAdapter adapter;
    private ListView pedidosList;
    private boolean  isEditPedido;
    private int vendid;
    private Config config;
    private SQLiteDatabase dbP,db;
    private DaoCreateDB daoDB;
    private DaoCreateDBP daoDBP;
    private Button enviar;
    private DaoPreVenda dpv;
    Intent i;
    PreVenda pv;
    private SincUp sincup;
    private boolean todosvend;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedidonenviados);

        daoDBP = new DaoCreateDBP(this);
        dbP =  daoDBP.getWritableDatabase();
        daoDB = new DaoCreateDB(this);
        db =  daoDB.getWritableDatabase();
        dpv = new DaoPreVenda(this);

        this.config = ConfigVendedor.getConfig(this.dbP);
        i= getIntent();
        vendid  = i.getIntExtra("vendid",0);
        enviar=(Button)findViewById(R.id.SairNovoPed);
        pv =new PreVenda();

        pedidosList = (ListView) findViewById (R.id.list);

        pedidosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

                pv.setNumero(cursor.getString(cursor.getColumnIndex("numero")));
                pv.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                visreenviped();


            }
        });
        if(Integer.valueOf(android.os.Build.VERSION.SDK)>11) {
            ActionBar ab = getActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg));
        }
        popularLista(todosvend);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();

        if(Integer.valueOf(android.os.Build.VERSION.SDK)>11) {
            inflater.inflate(R.menu.menuorc, menu);
            View v = (View) menu.findItem(R.id.search).getActionView();
            txtSearch = ( EditText ) v.findViewById(R.id.txt_search);
            txtSearch.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                    popularLista(todosvend);
                }

                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
        }else{
            inflater.inflate(R.menu.menu4, menu);
        }

        /** Setting an action listener */


        return true;
    }

    public void visreenviped() {
        new AlertDialog.Builder(this)
                .setItems(R.array.op_alerta_vis_enviar_ped, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        if (i == 0) {
                            selecionarReenviarPedido();
                        } else {
                            selecionarVisualizarPedido();
                        }
                    }
                }).show();
    }

    public void selecionarVisualizarPedido(){
        if (!isEditPedido) {
            Intent i = new Intent(PedidosEnviados.this,pedidovendateste.class);
            i.putExtra("PEDIDO_ID",cursor.getString(cursor.getColumnIndex("numero")));
            i.putExtra("tipopre","prevenda");
            PedidosEnviados.this.startActivityForResult(i,100);
            isEditPedido = true;
        }
    }

    public void selecionarReenviarPedido(){
        final double[] resp = new double[1];
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.op_alerta_reimp_mens);
        builder.setTitle(R.string.op_alerta_reimp_tit);
        builder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pv.setImp(1);
                        dpv.alterarimp(db,pv);
                        enviar();
                    }
                });
        builder.setNegativeButton("Não",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pv.setImp(0);
                        dpv.alterarimp(db,pv);
                        enviar();
                    }
                });

        AlertDialog d = builder.create();
        d.show();

    }
    public void enviar(){
        if (!isEditPedido) {
            sincup = new SincUp(PedidosEnviados.this,pv,"",config);
            sincup.delegate = PedidosEnviados.this;
            sincup.execute(new String[0]);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isEditPedido = false;
        if(requestCode == 0) {
            popularLista(todosvend);
        }
        if(resultCode==1000){
            popularLista(todosvend);
        }
        if(requestCode==100){
            popularLista(todosvend);
        }
    }



    public void popularLista(boolean paramvend) {
        if(config.getEmp()!=null){
            String param="";
            if(txtSearch!=null) {
                if (!txtSearch.getText().toString().equalsIgnoreCase("")) {
                    param = txtSearch.getText().toString();
                }
            }
            if(config.getEmp().equalsIgnoreCase("jrc")&&paramvend==false) {
                VendAtual va = new VendAtual();
                DaoVendAtual dva = new DaoVendAtual(this);
                va = dva.getVendAtual(this);
                cursor =dpv.pedenvi(db, param, va);
            }else{
                cursor =dpv.pedenvi(db,param);
            }

            adapter = new SimpleCursorAdapter(this,R.layout.list_item_nenvi,cursor,new String[] {"numero", "data",  "valortotal","nomeCliente"},
                    new int[] {R.id.firstName, R.id.lastName, R.id.title,R.id.txtCli});
            pedidosList.setAdapter(adapter);
        }
    }
    public void sair(View view) {
        Intent result = new Intent(this, PedidosEnviados.class);
        setResult(1000, result);
        PedidosEnviados.this.finish();
    }

    public void enviar(View view) {
        /*EnviarPedidos enviarPedidos = new EnviarPedidos(this);
        enviarPedidos.gerarLoteDePedidos(this,db, vendid);*/
        Intent ix = new Intent(this,SincUp.class);
        ix.putExtra("vendid", vendid);
        ix.putExtra("loteId", "");
        ix.putExtra("pedidosEnviados", false);
        ix.putExtra("tConn", "3g2gWifi");
        ix.putExtra("mod","pedido");
        this.startActivityForResult(ix,0);

    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public boolean onMenuItemSelected(int panel, MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                PedidosEnviados.this.finish();
                break;
            case R.id.item2:
                PedidosEnviados.this.finish();
                break;
            case R.id.check_all:
                item.setChecked(!item.isChecked());
                if(item.isChecked()){
                    todosvend=true;
                    popularLista(todosvend);
                }else{
                    todosvend=false;
                    popularLista(todosvend);
                }

                break;
        }
        return(true);
    }
    @Override
    public void processFinish(String output) {


    }

}