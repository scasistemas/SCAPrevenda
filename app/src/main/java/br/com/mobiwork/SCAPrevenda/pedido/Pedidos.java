package br.com.mobiwork.SCAPrevenda.pedido;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import br.com.mobiwork.SCAPrevenda.R;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDB;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;
import br.com.mobiwork.SCAPrevenda.dao.DaoPreVenda;
import br.com.mobiwork.SCAPrevenda.dao.DaoPreVendaItem;
import br.com.mobiwork.SCAPrevenda.dao.DaoProduto;
import br.com.mobiwork.SCAPrevenda.dao.DaoTabCondi;
import br.com.mobiwork.SCAPrevenda.model.Config;
import br.com.mobiwork.SCAPrevenda.model.PreVenda;
import br.com.mobiwork.SCAPrevenda.model.PreVendaItem;
import br.com.mobiwork.SCAPrevenda.produto.PesqProd;
import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;
import br.com.mobiwork.SCAPrevenda.util.DataHoraPedido;

/**
 * Created by LuisGustavo on 08/08/14.
 */



public class Pedidos extends Activity {



    private DaoProduto dp;
    private Cursor c,ccond;
    private SQLiteDatabase db,dbP;
    private DaoCreateDBP daoDBP;
    private DaoCreateDB daoDB;
    private DaoTabCondi dtc;
    private SimpleCursorAdapter adapter;
    private Spinner cond;
    private PreVenda pv;
    private DaoPreVenda dpv;
    private Config config;
    private Boolean incluirPedido ;
    private DataHoraPedido dataHoraMS;
    private String prevendaId;
    private DaoPreVendaItem dpi;
    private PreVendaItem pvi;
    private ListView produtosList;
    private Boolean isEditPedido;
    private TextView txvvrTotalPedido;
    private boolean isSelection;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.pedidovenda);
        dtc = new DaoTabCondi(this);
        daoDBP = new DaoCreateDBP(this);
        dbP =  daoDBP.getWritableDatabase();
        daoDB = new DaoCreateDB(this);
        db=  daoDB.getWritableDatabase();
        pv = new PreVenda();
        dpv = new DaoPreVenda(this);
        dpi = new DaoPreVendaItem(this);

        isSelection=false;
        pvi= new PreVendaItem();
        this.config = ConfigVendedor.getConfig(dbP);
        cond = (Spinner) findViewById(R.id.condicao);
        incluirPedido =true;
        prevendaId  = getIntent().getStringExtra("PEDIDO_ID");
        if(prevendaId==null){
            prevendaId="";
        }
        txvvrTotalPedido=(TextView)findViewById(R.id.vrTotalPedido);

        produtosList = (ListView) findViewById (R.id.list);

        verificarEdit();
        popularComboCondicao();

        produtosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                //     if (!isEditItem) {
                editarExcluirItem();
                //    isEditItem = true;
                //   }
            }
        });

        cond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                if(isSelection){
                    if(pv.getNumero()!=null){
                        dpi.alterarvalores(db,dbP,pv,ccond.getString(ccond.getColumnIndex("tabcondpag")));
                    }
                    pv.setCondicao( ccond.getString(ccond.getColumnIndex("tabcondpag")));
                    popularLista(pv.getNumero());
                    somaValores(pv.getNumero());

                }
                isSelection = true;
                pv.setCondicao(ccond.getString(ccond.getColumnIndex("tabcondpag")));
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


    }

    public void verificarEdit(){

        if (!prevendaId.equals("")){
            c=dpv.buscarPreVenda(db, prevendaId);
            if (c.moveToFirst()) {
                isEditPedido = true;
                pv.setNumero( c.getString(c.getColumnIndex("numero")));
                pv.setNumped(c.getInt(c.getColumnIndex("numped")));
                pv.setHora(c.getString(c.getColumnIndex("hora")));
                pv.setData(c.getString(c.getColumnIndex("data")));
                pv.setCondicao(c.getString(c.getColumnIndex("condicao")));
                pv.setLoteenvio(c.getString(c.getColumnIndex("loteenvio")));
                pv.setValortotal(c.getDouble(c.getColumnIndex("valorTotal")));
            }
        }
        popularLista(pv.getNumero());
    }



    public void passarValores(Double total){
        txvvrTotalPedido.setText(String.valueOf(total));
    }


    public void editarExcluirItem() {
        new AlertDialog.Builder(this)
                .setItems(R.array.op_alerta_editar_excluir_item, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        if (i == 0) {
                            //     selecionarAlertaEditarItem();
                        } else {
                            //   selecionarAlertaExcluirItem();
                        }
                    }
                }).show();
    }

    public void salvarItem() {
        new AlertDialog.Builder(this).setTitle("Deseja finalizar o pedido?")
                .setItems(R.array.op_alerta_sim_nao, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        if (i == 0) {
                            GravarPedido(0);
                            Pedidos.this.finish();

                        } else {
                            //   selecionarAlertaExcluirItem();
                        }
                    }
                }).show();
    }



    public void popularComboCondicao(){

        //    ccond=dtc.listarCondi(dbP);
        if(ccond.moveToFirst()){
            this.adapter = new SimpleCursorAdapter(this,R.layout.spinner_item,ccond,new String[] {"tabdescri"},
                    new int[] {R.id.comboName});
            cond.setAdapter(adapter);
        }


    }
    public void somaValores(String prevendaId){
        double t =dpv.somarPreVendas(db, prevendaId);
        txvvrTotalPedido.setText(String.valueOf(dpv.somarPreVendas(db,prevendaId)));
    }

    public void popularLista(String pedidoid) {
        Cursor c;
        c = dpi.listarPreVendas(db,pedidoid);

        if (c.moveToFirst()) {
            do {
                //   vrSubTotal = vrSubTotal + (c.getDouble(c.getColumnIndex("valor")) * c.getDouble(c.getColumnIndex("quantidade")) );
            } while (c.moveToNext());
        }
        adapter = new SimpleCursorAdapter(this,R.layout.itempedido,c,new String[] {"descricao", "quantidade",  "valor"},
                new int[] { R.id.descricao, R.id.quantidade, R.id.preco});
        produtosList.setAdapter(adapter);
        //

    }

    public void opcoesDoMenu(View v) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.op_menu_principal)
                .setItems(R.array.op_menu_pedido, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        selecionarMenuCliente(i);
                    }
                }).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode==RESULT_OK) {
            pvi.setNumpre(data.getExtras().getString("PEDIDO_ID"));
            pv.setNumero(data.getExtras().getString("PEDIDO_ID"));
            popularLista(pv.getNumero());
            somaValores(pv.getNumero());

        }
    }
    protected void selecionarMenuCliente(int i) {
        switch (i) {

            case 0:
                salvarItem();
                break;
            case 1:
                if (verificaPedidoNaoEnviado()){
                    incluirItemPedido();
                    break;
                }
                break;
            case 2:
                Pedidos.this.finish();
                break;
            case 3:
                alertaSair();
                break;


        }

    }

    public void incluirItemPedido() {


        GravarPedido(1);

        Intent ix = new Intent(Pedidos.this,PesqProd.class);
        ix.putExtra("preid",pv.get_id());
        ix.putExtra("numped",pv.getNumped());
        ix.putExtra("numero",pv.getNumero());
        ix.putExtra("condicao",pv.getCondicao());
        ix.putExtra("valorTotal",pv.getValortotal());
        ix.putExtra("idFiltroProduto","");
        Pedidos.this.startActivityForResult(ix,1);
    }
    public void GravarPedido(int confirmado) {
        dataHoraMS = DataHoraPedido.criarDataHoraPedido();
        if(txvvrTotalPedido.getText().toString().equals("")){
            pv.setValortotal(0);
        }else{
            pv.setValortotal(Double.parseDouble(txvvrTotalPedido.getText().toString()));
        }
        if (pvi.getNumpre()==null&&prevendaId.equals("")){
            //insere pedido
            pv.setCondicao(String.valueOf(cond.getSelectedItemId()));
            pv.setNumped(dpv.proxnumped(db));
            pv.setNumero(String.format("%04d", config.getVendid()) + String.format("%06d", dpv.proxnumped(dbP))) ;
            this.config = ConfigVendedor.getConfig(dbP);
            pv = dpv.Inserir(dbP,db,pv,dataHoraMS,this);
            prevendaId=pv.get_id();
            incluirPedido = false;
        } else{
            dpv.atualizar(db,pv,dataHoraMS);
            prevendaId=pv.get_id();
        }
    }




    public void alertaSair() {
        new AlertDialog.Builder(this)
                .setTitle("Deseja Sair do Pedido sem Salvar?")
                .setItems(R.array.op_alerta_sim_nao, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        if (i == 0) {

                            Pedidos.this.finish();
                            //                 cancelarPedido();
                        }
                    }
                }).show();
    }

    private boolean verificaPedidoNaoEnviado() {
    /*    if (.getLoteEnvio() != null){
            new AlertDialog.Builder(this)
                    .setTitle("Pedido já Foi enviado")
                    .setNegativeButton("Ok", null)
                    .show();
            return false;
        } else {

        }*/
        return true;
    }
    @Override
    public void onBackPressed() {
        return;
    }
}
