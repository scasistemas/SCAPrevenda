package br.com.mobiwork.SCAPrevenda.pedido;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.mobiwork.SCAPrevenda.R;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDB;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;
import br.com.mobiwork.SCAPrevenda.dao.DaoFormaDePgto;
import br.com.mobiwork.SCAPrevenda.dao.DaoParamCondi;
import br.com.mobiwork.SCAPrevenda.dao.DaoPreVenda;
import br.com.mobiwork.SCAPrevenda.dao.DaoPreVendaItem;
import br.com.mobiwork.SCAPrevenda.dao.DaoProduto;
import br.com.mobiwork.SCAPrevenda.dao.DaoTabCondi;
import br.com.mobiwork.SCAPrevenda.dao.DaoVendAtual;
import br.com.mobiwork.SCAPrevenda.dao.PedidoRollBack;
import br.com.mobiwork.SCAPrevenda.model.Config;
import br.com.mobiwork.SCAPrevenda.model.PreVenda;
import br.com.mobiwork.SCAPrevenda.model.PreVendaItem;
import br.com.mobiwork.SCAPrevenda.model.TabCondi;
import br.com.mobiwork.SCAPrevenda.model.VendAtual;
import br.com.mobiwork.SCAPrevenda.produto.PesqProd;
import br.com.mobiwork.SCAPrevenda.util.Alertas;
import br.com.mobiwork.SCAPrevenda.util.BaseAdapterHashMap;
import br.com.mobiwork.SCAPrevenda.util.BigDecimalRound;
import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;
import br.com.mobiwork.SCAPrevenda.util.DataHoraPedido;

public class pedidovendateste extends FragmentActivity {

    private DaoProduto dp;
    private  Cursor c,cforma;
    public static Cursor ccond;
    private SQLiteDatabase db,dbP;
    private DaoCreateDBP daoDBP;
    private DaoCreateDB daoDB;
    private DaoFormaDePgto df;
    private DaoTabCondi dtc;
    public static SimpleCursorAdapter adapter;
    public static Spinner cond,forma,cbtipo;
    public static PreVenda pv;
    private DaoPreVenda dpv;
    private Config config;
    private Boolean incluirPedido ;
    private DataHoraPedido dataHoraMS;
    private String prevendaId;
    private DaoPreVendaItem dpi;
    private PreVendaItem pvi;
    private ListView produtosList;
    private Boolean isEditPedido;
    private boolean isSelection;
    Alertas a;
    Intent i;
    private DaoPreVendaItem dprei;
    private DaoProduto daoProd;
    private boolean isEditPedidoItem;
    public static TextView txvvrTotalPedido,txvalorbruto,txdesc,txdivid;
    private TabCondi tc;
    String tipoVenda;
    private DaoParamCondi dpc;
    private LinearLayout listat,litipo;
    private TextView txinf;
    private VendAtual va;
    private DaoVendAtual dva;
    MenuItem item6;
    public static String tipo;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedidovendateste);
        dprei= new DaoPreVendaItem(this);
        daoProd= new DaoProduto(this);
        isEditPedidoItem=true;
        isEditPedido=false;
        dtc = new DaoTabCondi(this);
        daoDBP = new DaoCreateDBP(this);
        dbP =  daoDBP.getWritableDatabase();
        daoDB = new DaoCreateDB(this);
        db=  daoDB.getWritableDatabase();
        pv = new PreVenda();
        pv.setValores();
        dpv = new DaoPreVenda(this);
        dpi = new DaoPreVendaItem(this);
        tc = new TabCondi();
        a=new Alertas(this);
        df = new DaoFormaDePgto(this);
        if(Integer.valueOf(android.os.Build.VERSION.SDK)>11) {
            ActionBar ab = getActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg));
        }
        dpc = new DaoParamCondi(this);
        dataHoraMS = DataHoraPedido.criarDataHoraPedido();

        isSelection=false;
        pvi= new PreVendaItem();this.config = ConfigVendedor.getConfig(dbP);

        cond = (Spinner) findViewById(R.id.condicao);
        cbtipo = (Spinner) findViewById(R.id.cbtipo);
        forma = (Spinner) findViewById(R.id.forma);
        incluirPedido =true;
        prevendaId  = getIntent().getStringExtra("PEDIDO_ID");
        tipoVenda=getIntent().getStringExtra("tipopre");
        if(prevendaId==null){
            prevendaId="";
        }
        txvvrTotalPedido=(TextView)findViewById(R.id.vrTotalPedido);
        txdesc=(TextView)findViewById(R.id.txtdesc);
        txdivid=(TextView)findViewById(R.id.txtdivid);
        txvalorbruto=(TextView)findViewById(R.id.txtvrBruto);
        produtosList = (ListView) findViewById (R.id.list);
        listat=(LinearLayout) findViewById(R.id.listatus);
        litipo=(LinearLayout) findViewById(R.id.litipo);
        txinf=(TextView) findViewById(R.id.txinf);


        if(tipoVenda.equalsIgnoreCase("prevenda")){
            listat.setBackgroundResource(R.drawable.yellow);

        }else if(tipoVenda.equalsIgnoreCase("orcamento")){
            listat.setBackgroundResource(R.drawable.red);

        }else{
            listat.setBackgroundResource(R.drawable.black);

        }

        va = new VendAtual();
        dva= new DaoVendAtual(this);
        va=dva.getVendAtual(this);

        verificarEdit();
        popularComboCondicao();
        popularforma();
        bloq();
        if(config.getEmp()!=null) {
            if (config.getEmp().equalsIgnoreCase("jrc")) {
                litipo.setVisibility(View.VISIBLE);
                populartipo();
            }
        }

        getOverflowMenu();


        forma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                if (isSelection) {

                    pv.setForma(cforma.getInt(cforma.getColumnIndex("_id")));
                }
                isSelection = true;

            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        cond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                if(isSelection){
                    if(config.getEmp().equalsIgnoreCase("vitoria")) {
                        if (pv.getNumero() != null) {
                            dpi.alterarvalores(db, dbP, pv, ccond.getString(ccond.getColumnIndex("tabcondpag")));
                        }
                    }
                    pv.setCondicao( ccond.getString(ccond.getColumnIndex("tabcodigo")));
                    tc.setconrvenc(ccond.getInt(ccond.getColumnIndex("tconrvenc")));
                    popularLista(pv.getNumero());
                    somaValores(pv.getNumero());
                    passarValores(pv);
                    txdivid.setText(tc.getconrvenc() + "X");


                }
                isSelection = true;
                tc.setconrvenc(ccond.getInt(ccond.getColumnIndex("tconrvenc")));
                pv.setCondicao(ccond.getString(ccond.getColumnIndex("tabcodigo")));
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

    public void bloq(){
        if(tipoVenda.equalsIgnoreCase("prevenda")&&pv.getPrstatus()==1){
            cond.setEnabled(false);
            forma.setEnabled(false);
        }else{
            cond.setEnabled(true);
            forma.setEnabled(true);
        }
    }


    public void verificarEdit(){
        if(prevendaId!=null) {
            if (!prevendaId.equals("")) {
                c = dpv.buscarPreVenda(db, prevendaId);
                if (c.moveToFirst()) {
                    isEditPedido = true;
                    pvi.setNumpre(c.getString(c.getColumnIndex("numero")));
                    pv.setNumero(c.getString(c.getColumnIndex("numero")));
                    pv.setNumped(c.getInt(c.getColumnIndex("numped")));
                    pv.setHora(c.getString(c.getColumnIndex("hora")));
                    pv.setData(c.getString(c.getColumnIndex("data")));
                    pv.setCondicao(c.getString(c.getColumnIndex("condicao")));
                    pv.setLoteenvio(c.getString(c.getColumnIndex("loteenvio")));
                    pv.setValortotal(c.getDouble(c.getColumnIndex("valortotal")));
                    pv.setDesc(c.getDouble(c.getColumnIndex("desc")));
                    pv.setParc(c.getInt(c.getColumnIndex("parc")));
                    pv.setStatus(c.getInt(c.getColumnIndex("status")));
                    pv.setValorbruto(c.getDouble(c.getColumnIndex("valorbruto")));
                    pv.setObs(c.getString(c.getColumnIndex("obs")));
                    pv.setNomeCliente(c.getString(c.getColumnIndex("nomeCliente")));
                    pv.setImp(c.getInt(c.getColumnIndex("imp")));
                    pv.setForma(c.getInt(c.getColumnIndex("forma")));
                    pv.setPrstatus(c.getInt(c.getColumnIndex("prstatus")));
                    passarValores(pv);
                    PedidoRollBack.criarPedidoRollBack(db, c, pvi.getNumpre());
                    DaoPreVendaItem dpvi = new DaoPreVendaItem(this);
                    Cursor cursorItens = dpvi.listarPreVendas(db, pvi.getNumpre());
                    PedidoRollBack.setItemPedido(cursorItens);
                }
            }
            popularLista(pv.getNumero());
        }
    }



    public void passarValores(PreVenda pv){
        txvvrTotalPedido.setText(String.valueOf(BigDecimalRound.Round(pv.getValortotal(), 2)));
        double d=pv.getValorbruto();
        txvalorbruto.setText("R$ " + String.valueOf(pv.getValorbruto()));
        txdesc.setText(String.valueOf(pv.getDesc()));
    }

    public void populartipo(){

        List<String> nomes = new ArrayList<String>();
        final String[] nome = new String[1];

        nomes.add("Varejo");
        nomes.add("Atacado");
        //Identifica o Spinner no layout
        //Cria um ArrayAdapter usando um padrao de layout da classe R do android, passando o ArrayList nomes
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.my_spinner_textview, nomes);
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
        cbtipo.setAdapter(spinnerArrayAdapter);

        //Metodo do Spinner para capturar o item selecionado
        cbtipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                //pega nome pela posicao
                tipo = parent.getItemAtPosition(posicao).toString();
                //imprime um Toast na tela com o nome que foi selecionado
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                            GravarPedido(0,null);
                            pedidovendateste.this.finish();

                        } else {
                            //   selecionarAlertaExcluirItem();
                        }
                    }
                }).show();
    }



    public void popularComboCondicao(){

        if(config.getEmp()!=null) {
            if (config.getEmp().equalsIgnoreCase("vitoria")) {
                if (txvvrTotalPedido.getText().toString().equalsIgnoreCase("")) {
                    pv.setValortotal(0.0);
                } else {
                    pv.setValortotal(Double.parseDouble(txvvrTotalPedido.getText().toString()));
                }
                if (pv.getValortotal() == 0 || pv.getValortotal() < dpc.pegarMenorValor(dbP)) {
                    ccond = dtc.listarCondi(dbP, 1.0);
                } else {
                    Cursor temp = dpc.listarCondi(dbP, pv.getValortotal());
                    if (temp.moveToFirst()) {
                        ccond = dtc.listarCondi(dbP, temp.getDouble(temp.getColumnIndex("parc")));
                    }
                }
            } else {
                ccond = dtc.listarCondi(dbP, 1.0);
            }
        }

        if(ccond!=null){
            if(ccond.moveToFirst()){
                this.adapter = new SimpleCursorAdapter(this,R.layout.spinner_item,ccond,new String[] {"tabdescri"},
                        new int[] {R.id.comboName});
                cond.setAdapter(adapter);
            }
        }
        int x=0;
        if(pv.getCondicao()!=null){
            if (ccond.moveToFirst()) {
                try {
                do {
                        if (String.format("%03d", Integer.parseInt(pv.getCondicao())).equals(ccond.getString(ccond.getColumnIndex("tabcodigo")))) {
                            cond.setSelection(x);
                            break;
                        }
                        x = x + 1;
                    } while (ccond.moveToNext()) ;
                }catch (Exception e){

                }
            }
        }


    }

    public void popularforma(){

        cforma=df.listarForma(dbP);
        if(cforma.moveToFirst()){
            this.adapter = new SimpleCursorAdapter(this,R.layout.spinner_item,cforma,new String[] {"descricao"},
                    new int[] {R.id.comboName});
            forma.setAdapter(adapter);
        }
        int x=0;             if (cforma.moveToFirst()) {
            do {
                if (pv.getForma()==cforma.getInt(cforma.getColumnIndex("_id"))) {
                    forma.setSelection(x);
                    break;
                }
                x=x+1;
            } while (cforma.moveToNext());


        }



    }
    public void somaValores(String prevendaId){
        pv.setValorbruto(BigDecimalRound.Round(dpv.somarPreVendas(db, prevendaId), 2));
        if(pv.getDesc()!=0){
            pv.setValortotal(pv.getValorbruto()-pv.getDesc());
            //  txvvrTotalPedido.setTextColor(Color.parseColor("#00F"));
        }else{
            pv.setValortotal(pv.getValorbruto());
        }

    }


    public void popularLista(String pedidoid) {
        Cursor c;
        c = dpi.listarPreVendas(db,pedidoid);

        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        try{
            if(c.moveToFirst()){

                do{

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("_id", "" + c.getString(c.getColumnIndex("_id")));
                    map.put("numpre", "" +  c.getString(c.getColumnIndex("numpre")));
                    map.put("descricao", "" +  c.getString(c.getColumnIndex("descricao")));
                    map.put("loteenvio", "" + c.getString(c.getColumnIndex("loteenvio")));
                    map.put("codprod", "" +  c.getString(c.getColumnIndex("codprod")));
                    map.put("digprod", "" +  c.getString(c.getColumnIndex("digprod")));
                    map.put("quantidade", "" +  c.getString(c.getColumnIndex("quantidade")));
                    map.put("valor", "" +  c.getString(c.getColumnIndex("valor")));
                    fillMaps.add(map);


                }while(c.moveToNext());
            }
        }finally {
            c.close();

        }
        produtosList.setAdapter(new BaseAdapterHashMap(this, fillMaps, produtosList, tipoVenda, pv.getPrstatus()));


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Incluir Item
        if(requestCode == 1 && resultCode==RESULT_OK) {
            pvi.setNumpre(data.getExtras().getString("PEDIDO_ID"));
            pv.setNumero(data.getExtras().getString("PEDIDO_ID"));
            popularLista(pv.getNumero());
        }

        //ConfirmaPedido
        if(requestCode == 102 && resultCode==RESULT_CANCELED) {
            if(!tipoVenda.equalsIgnoreCase("prevenda")){
                String dat=data.getExtras().getString("desc");
                if(data.getExtras().getString("desc").equalsIgnoreCase("")){
                    pv.setDesc(0);
                }else{
                    pv.setDesc(Double.parseDouble(data.getExtras().getString("desc")));

                }
                pv.setObs(data.getExtras().getString("obs"));
                pv.setNomeCliente(data.getExtras().getString("nomeCliente"));
                pv.setImp(Integer.parseInt(data.getExtras().getString("imp")));
                pv.setStatus(Integer.parseInt(data.getExtras().getString("tipo")));
            }
        }else if (requestCode == 102 && resultCode==RESULT_OK){
            pedidovendateste.this.finish();

        }
        //Codigo de Barras
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            i = new Intent(pedidovendateste.this,IncluirPedido.class);
            String qrcode = data.getStringExtra("SCAN_RESULT");
           // String qrcode = "7899627047290";
            Cursor c= daoProd.procCodBarra(dbP,qrcode);
            if(c.moveToFirst()){
                if(c.moveToFirst()){
                    pvi.setCodprod(c.getString(c.getColumnIndex("procodigo")));
                    pvi.setDescricao(c.getString(c.getColumnIndex("prodescri")));
                    pvi.setDigprod(c.getString(c.getColumnIndex("prodigito")));
                }else{
                    pvi.setCodprod("");
                }
                c = dprei.listarItensPrevenda(db,pvi);
                if (c.moveToFirst()) {
                    pvi.setPreVendaItem(c);
                    isEditPedidoItem = true;
                    i.putExtra("edit","edit");
                }else{
                    i.putExtra("edit","novo");
                }
                i.putExtra("DESCRI",pvi.getDescricao());
                i.putExtra("CODPRODUTO", pvi.getCodprod());
                i.putExtra("tbPrecoId",pvi.getPrecoTb());
                i.putExtra("pedidoItemId",pv.getNumero());
                i.putExtra("quantidade",String.valueOf(pvi.getQuantidade()));
                i.putExtra("precoTb",pvi.getPrecoTb());
                i.putExtra("digito",pvi.getDigprod());
                i.putExtra("barra",qrcode);
                i.putExtra("condicao",String.valueOf(cond.getSelectedItemId()));
                i.putExtra("valor",String.valueOf(pvi.getValor()));
                i.putExtra("tipo", cbtipo.getSelectedItem().toString());
                pedidovendateste.this.startActivityForResult(i, 0) ;
            }else{
                a.Alerta("Produto não Cadastrado");
            }
        }else if((requestCode == 0 && resultCode == 0)){
            popularLista(pv.getNumero());
            somaValores(pv.getNumero());

        }
        if(dpv.getprstatus(db,pv)!=1){
            somaValores(pv.getNumero());
            passarValores(pv);
            popularComboCondicao();
        }
    }

    public void incluirItemPedido() {

        Intent ix = new Intent(pedidovendateste.this,PesqProd.class);
        GravarPedido(1,ix);
        ix.putExtra("preid",pv.get_id());
        ix.putExtra("numped",pv.getNumped());
        ix.putExtra("numero",pv.getNumero());
        ix.putExtra("condicao",pv.getCondicao());
        ix.putExtra("valorTotal",pv.getValortotal());
        ix.putExtra("idFiltroProduto","");
        pedidovendateste.this.startActivityForResult(ix,1);
    }




    public void GravarPedido(int confirmado,Intent ix) {
        if(ix!=null) {
            ix.putExtra("tipo", cbtipo.getSelectedItem().toString());
        }
        pv.setForma(cforma.getInt(cforma.getColumnIndex("_id")));
        dataHoraMS = DataHoraPedido.criarDataHoraPedido();
        if(txvvrTotalPedido.getText().toString().equals("")){
            pv.setValorbruto(0);
        }else{
            pv.setValorbruto(Double.parseDouble(txvvrTotalPedido.getText().toString()));
        }
        if (pvi.getNumpre()==null&&prevendaId.equals("")){
            //insere pedido
            pv.setCondicao(String.valueOf(cond.getSelectedItemId()));
            pv.setNumped(dpv.proxnumped(db));
            //Vendedor
            if((config.getEmp().equalsIgnoreCase("vitoria")&&config.getVendid()!=633)){
                pv.setNumero(String.format("%04d", config.getVendid()) + String.format("%06d", dpv.proxnumped(dbP))) ;
                pvi.setNumpre(pv.getNumero());
            }else if(config.getEmp().equalsIgnoreCase("jrc")){
                pv.setNumero(String.format("%04d", va.getCodigovend()) + String.format("%06d", dpv.proxnumped(dbP))) ;
                pvi.setNumpre(pv.getNumero());
            }
            this.config = ConfigVendedor.getConfig(dbP);
            pv = dpv.Inserir(dbP,db,pv,dataHoraMS,this);
            prevendaId=pv.get_id();
            incluirPedido = false;
        } else{
            dpv.atualizar(db, pv, dataHoraMS);
            prevendaId=pv.get_id();
        }
    }

    public void alertaConlcuir() {

        Intent ix = new Intent(pedidovendateste.this,ConfirmaPedido.class);
        GravarPedido(1,ix);
        ix.putExtra("preid",pv.get_id());
        ix.putExtra("numped",pv.getNumped());
        ix.putExtra("numero",pv.getNumero());
        ix.putExtra("condicao",String.valueOf(pv.getCondicao()));
        ix.putExtra("nparc",tc.getconrvenc());
        ix.putExtra("valorbruto", pv.getValorbruto());
        ix.putExtra("valortotal",pv.getValortotal());
        ix.putExtra("obs",pv.getObs());
        ix.putExtra("nomeCliente",pv.getNomeCliente());
        ix.putExtra("desc",pv.getDesc());
        ix.putExtra("edit",0);
        ix.putExtra("tipopre",tipoVenda);
        ix.putExtra("status", pv.getStatus());
        ix.putExtra("imp",pv.getImp());
        ix.putExtra("forma",String.valueOf(pv.getForma()));
        if(isEditPedido){
            ix.putExtra("edit",1);
            ix.putExtra("desc", pv.getDesc());
            ix.putExtra("valortotal",pv.getValortotal());
            ix.putExtra("obs",pv.getObs());
            ix.putExtra("parc",pv.getParc());
            ix.putExtra("nomeCliente",pv.getNomeCliente());


        }

        pedidovendateste.this.startActivityForResult(ix,102);
    }

    public void alertaSair() {
        if(dpv.getprstatus(db,pv)!=1){
            new AlertDialog.Builder(this)
                    .setTitle("Deseja Sair do Pedido sem Salvar?")
                    .setItems(R.array.op_alerta_sim_nao, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            if (i == 0) {
                                if (!isEditPedido) {
                                    dpv.excluir(db, pv);
                                    dpi.excluirTodos(db, pvi);
                                }else{
                                    PedidoRollBack.rollBackpedido(pedidovendateste.this,dataHoraMS);
                                }
                                pedidovendateste.this.finish();
                                //                 cancelarPedido();
                            }
                        }
                    }).show();
        }else{
            pedidovendateste.this.finish();
        }
    }
    public void alertaExcluirPedido() {
        if(pv.getPrstatus()!=1){
            new AlertDialog.Builder(this)
                    .setTitle("Deseja excluir pedido?")
                    .setItems(R.array.op_alerta_sim_nao, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            if (i == 0) {
                                if (!isEditPedido) {
                                    dpv.excluir(db, pv);
                                    dpi.excluirTodos(db, pvi);
                                    pedidovendateste.this.finish();
                                } else {
                                    if(pv.getStatus()==0){
                                        dpv.excluir(db, pv);
                                        dpi.excluirTodos(db, pvi);
                                        pedidovendateste.this.finish();
                                    }else{
                                        a.AlertaSinc("Prevenda  ja fechada!");
                                    }//                 cancelarPedido();
                                }
                            }
                        }
                    }).show();
        }else{
            a.AlertaSinc("Pre Venda Fechada");
        }
    }


    private boolean verificaPedidoNaoEnviado() {
        if(tipoVenda.equalsIgnoreCase("prevenda")&&pv.getPrstatus()==1){
            a.AlertaSinc("Pre Venda Fechada");
            return false;
        }else{

            return true;
        }
    }
    @Override
    public void onBackPressed() {
        return;
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);


        if(pv!=null){
            if(pv.getPrstatus()==0){
                getMenuInflater().inflate(R.menu.menu2, menu);
            }else{
                getMenuInflater().inflate(R.menu.menu5, menu);
            }
        }else {
            getMenuInflater().inflate(R.menu.menu2, menu);
        }
        item6 = (MenuItem) menu.findItem(R.id.item6);
        return(true);
    }

    public void codbar(){
        GravarPedido(1,null);
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "PRODUCT_MODE");

        try {
            startActivityForResult(intent, 0);

        } catch (ActivityNotFoundException e) {
            a.baixarBarcode();
        }

    }

    public void abricarga(){
        dpv.altstatusPrevenda(db, pv, 0);
        pv.setPrstatus(dpv.getprstatus(db,pv));
        verificarEdit();
        popularComboCondicao();
        popularforma();
        bloq();
        getOverflowMenu();
        item6.setEnabled(false);




    }
    @Override
    public boolean onMenuItemSelected(int panel, MenuItem item){


        switch(item.getItemId()){


            case android.R.id.home:
                alertaSair();
                break;
            case R.id.item1:
                if (verificaPedidoNaoEnviado()){
                    Toast.makeText(this, "cod.barras", Toast.LENGTH_SHORT).show();
                    GravarPedido(1,null);
                    codbar();
                }
                break;
            case R.id.item2:
                if (verificaPedidoNaoEnviado()){
                    incluirItemPedido();

                }
                break;
            case R.id.item3:
                alertaConlcuir();

                break;
            case R.id.item4:
                alertaSair();
                break;
            case R.id.item5:
                alertaExcluirPedido();

                break;
            case R.id.item6:
                abricarga();
                item6.setVisible(false);
                break;

        }

        return(true);
    }





}
