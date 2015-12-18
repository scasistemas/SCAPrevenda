package br.com.mobiwork.SCAPrevenda.pedido;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import br.com.mobiwork.SCAPrevenda.R;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDB;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;
import br.com.mobiwork.SCAPrevenda.dao.DaoPreVenda;
import br.com.mobiwork.SCAPrevenda.model.Config;
import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;

/**
 * Created by LuisGustavo on 19/08/14.
 */
public class LotePedido  extends Activity{
    private EditText searchText;
    private Cursor cursor;
    private ListAdapter adapter;
    private ListView pedidosList;
    private boolean  isEditPedido;
    private int vendid;
    private Config config;
    private SQLiteDatabase dbP,db;
    private DaoCreateDB daoDB;
    private DaoCreateDBP daoDBP;
    private DaoPreVenda dpv;
    Intent i;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lotepedidos);

        daoDBP = new DaoCreateDBP(this);
        dbP =  daoDBP.getWritableDatabase();
        daoDB = new DaoCreateDB(this);
        db =  daoDB.getWritableDatabase();
        dpv = new DaoPreVenda(this);

        this.config = ConfigVendedor.getConfig(this.dbP);
        i= getIntent();
        vendid  = i.getIntExtra("vendid",0);


        pedidosList = (ListView) findViewById (R.id.list);

        pedidosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                if (!isEditPedido) {
                    Intent i = new Intent(LotePedido.this,PedidosEnviados.class);
                    i.putExtra("loteid",cursor.getString(cursor.getColumnIndex("loteenvio")));
                    LotePedido.this.startActivityForResult(i,100);
                    isEditPedido = true;
                }
            }
        });
        if(Integer.valueOf(android.os.Build.VERSION.SDK)>11) {
            ActionBar ab = getActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg));
        }
        popularLista();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu4, menu);
        return true;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isEditPedido = false;
        if(requestCode == 0) {
            popularLista();
            Intent result = new Intent(this, PedidosEnviados.class);
            setResult(resultCode, result);
            LotePedido.this.finish();
        }
        if(resultCode==1000){
            popularLista();
        }
        if(requestCode==100){
            popularLista();
        }

    }



    public void popularLista() {
        if(config.getEmp()!=null){
            cursor =dpv.pedenvi(db,"");
            adapter = new SimpleCursorAdapter(this,R.layout.list_lote,cursor,new String[] {"loteenvio"},
                    new int[] {R.id.firstName});
            pedidosList.setAdapter(adapter);
        }
    }
    public void sair(View view) {
        Intent result = new Intent(this, PedidosEnviados.class);
        setResult(1000, result);
        LotePedido.this.finish();
    }



    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public boolean onMenuItemSelected(int panel, MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                LotePedido.this.finish();
                break;
            case R.id.item2:
                LotePedido.this.finish();
                break;

        }

        return(true);
    }

}