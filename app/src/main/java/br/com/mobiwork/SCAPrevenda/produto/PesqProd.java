package br.com.mobiwork.SCAPrevenda.produto;

        import android.app.ActionBar;
        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.ActivityNotFoundException;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.os.Bundle;
        import android.support.v4.app.FragmentActivity;
        import android.text.Editable;
        import android.text.TextWatcher;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.Window;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.AdapterView;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ListAdapter;
        import android.widget.ListView;
        import android.widget.SimpleCursorAdapter;
        import android.widget.TextView;
        import android.widget.Toast;

        import br.com.mobiwork.SCAPrevenda.R;
        import br.com.mobiwork.SCAPrevenda.dao.DaoConfig;
        import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDB;
        import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;
        import br.com.mobiwork.SCAPrevenda.dao.DaoPreVenda;
        import br.com.mobiwork.SCAPrevenda.dao.DaoPreVendaItem;
        import br.com.mobiwork.SCAPrevenda.dao.DaoProduto;
        import br.com.mobiwork.SCAPrevenda.model.Config;
        import br.com.mobiwork.SCAPrevenda.model.PreVendaItem;
        import br.com.mobiwork.SCAPrevenda.pedido.IncluirPedido;
        import br.com.mobiwork.SCAPrevenda.pedido.pedidovendateste;
        import br.com.mobiwork.SCAPrevenda.util.Alertas;
        import br.com.mobiwork.SCAPrevenda.util.BaseAdapterLista;

/**
 * Created by LuisGustavo on 08/08/14.
 */
public class PesqProd extends FragmentActivity {

    private ListView produtosList;
    private EditText searchText ;
    private Cursor cListProd;
    private Button selectItem;
    private Config config;
    Intent i;
    private ListAdapter adapter;
    private String idFiltroProduto;
    DaoCreateDBP daoDBP;
    DaoCreateDB daoDB;
    private SQLiteDatabase db,dbP;
    private DaoProduto daoProd;
    private PreVendaItem pvi;
    private Boolean isEditPedidoItem;
    private DaoPreVenda dpre;
    private DaoPreVendaItem dprei;
    String pedidoId,condicao;
    Alertas a;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.pesqprod);
        searchText = (EditText) findViewById (R.id.searchText2);

        daoDBP = new DaoCreateDBP(this);
        dbP =  daoDBP.getWritableDatabase();
        daoDB = new DaoCreateDB(this);
        db =  daoDB.getWritableDatabase();
        config= new DaoConfig(this).consultar(this.dbP);
        daoProd= new DaoProduto(this);
        pvi= new PreVendaItem();
        dprei=new DaoPreVendaItem(this);
        a= new Alertas(this);
        pedidoId  = getIntent().getStringExtra("preid");
        pvi.setNumpre(getIntent().getStringExtra("numero"));
        condicao=getIntent().getStringExtra("condicao");
        idFiltroProduto = getIntent().getStringExtra("idFiltroProduto");
        produtosList = (ListView) findViewById (R.id.list);
        pvi.setId(pedidoId);

        if(Integer.valueOf(android.os.Build.VERSION.SDK)>11){
            ActionBar ab = getActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg));
        }
        produtosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                String aux = cListProd.getString(cListProd.getColumnIndex("prodescri"));
                selecionar("pesq");
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                popularLista();
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        popularLista();

    }

    public void popularLista() {
        String whereFiltro  ;
        String[] selectArgs;
        if (searchText != null){}
        if (!idFiltroProduto.equals("")){
            whereFiltro = " idFiltroProduto  = ? and  ";
            selectArgs = new String[]{idFiltroProduto, searchText.getText().toString() + "%"};
        } else {
            whereFiltro = "";
            selectArgs = new String[]{ searchText.getText().toString() + "%",searchText.getText().toString() + "%","%"+searchText.getText().toString() + "%"};
        }
        cListProd= daoProd.listarProduto(dbP,whereFiltro,selectArgs);
        if(cListProd.moveToFirst()){
            String y="teste";
            double ct=cListProd.getDouble(cListProd.getColumnIndex("proprvenda1"));
            String t=cListProd.getString(cListProd.getColumnIndex("proprvenda2"));
            String s="2;";
        }
        if(config.getEmp().equalsIgnoreCase("jrc")){
            if(pedidovendateste.tipo.equalsIgnoreCase("varejo")) {
                adapter = new SimpleCursorAdapter(this, R.layout.listaproduto, cListProd, new String[]{"procodigo", "prodescri", "probarra", "proprvenda1", "prodigito"},
                        new int[]{R.id.procodigo, R.id.produto, R.id.barra, R.id.valor2, R.id.txtgrade});
            }else{
                adapter = new SimpleCursorAdapter(this, R.layout.listaproduto, cListProd, new String[]{"procodigo", "prodescri", "probarra", "proprvenda2", "prodigito"},
                        new int[]{R.id.procodigo, R.id.produto, R.id.barra, R.id.valor2, R.id.txtgrade});

            }
        }else{
            adapter = new SimpleCursorAdapter(this, R.layout.listaproduto, cListProd, new String[]{"procodigo", "prodescri", "probarra", "proprvenda1", "prodigito"},
                    new int[]{R.id.procodigo, R.id.produto, R.id.barra, R.id.valor2, R.id.txtgrade});

        }
        produtosList.setAdapter(adapter);



    }

    public void limpa(View view) {
        searchText.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu3, menu);
        return(true);
    }


    public void selecionar(String param) {
        if ((cListProd.getCount() > 0) && (cListProd.getString(cListProd.getColumnIndex("procodigo")) != null)) {
            i = new Intent(PesqProd.this,IncluirPedido.class);
            Cursor c2= daoProd.proCodigo(dbP, cListProd.getString(cListProd.getColumnIndex("procodigo")),cListProd.getString(cListProd.getColumnIndex("prodigito")));
            if(c2.moveToFirst()){
                pvi.setCodprod(c2.getString(c2.getColumnIndex("procodigo")));
                pvi.setDescricao(c2.getString(c2.getColumnIndex("prodescri")));
                pvi.setValor(c2.getDouble(c2.getColumnIndex("proprvenda1")));
                pvi.setDigprod(c2.getString(c2.getColumnIndex("prodigito")));

            }else{
                pvi.setCodprod("");
            }
            Cursor c = dprei.listarItensPrevenda(db,pvi);

            if (c.moveToFirst()) {
                pvi.setPreVendaItem(c);
                i.putExtra("edit","edit");
            }else{
                i.putExtra("edit","novo");
                pvi.setTipo("null");
            }

            i.putExtra("DESCRI", pvi.getDescricao());
            i.putExtra("CODPRODUTO", pvi.getCodprod());
            i.putExtra("tbPrecoId",pvi.getPrecoTb());
            i.putExtra("pedidoItemId",pvi.getNumpre());
            i.putExtra("quantidade",String.valueOf(pvi.getQuantidade()));
            i.putExtra("precoTb",pvi.getPrecoTb());
            i.putExtra("digito",pvi.getDigprod());
            i.putExtra("barra",c2.getString(c2.getColumnIndex("probarra")));
            i.putExtra("condicao",condicao);
            i.putExtra("valor",String.valueOf(pvi.getValor()));
            i.putExtra("tipo",String.valueOf(pvi.getTipo()));
            PesqProd.this.startActivityForResult(i,0);

        }
    }

    public void codbar(){
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
        try {
            startActivityForResult(intent, 0);

        } catch (ActivityNotFoundException e) {
            a.baixarBarcode();
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            i = new Intent(PesqProd.this,IncluirPedido.class);
            String qrcode = intent.getStringExtra("SCAN_RESULT");
            //String qrcode = "7899627047290";
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
                i.putExtra("pedidoItemId",pvi.getNumpre());
                i.putExtra("quantidade",String.valueOf(pvi.getQuantidade()));
                i.putExtra("precoTb",pvi.getPrecoTb());
                i.putExtra("digito",pvi.getDigprod());
                i.putExtra("barra",qrcode);
                i.putExtra("condicao",condicao);
                i.putExtra("valor",String.valueOf(pvi.getValor()));
                PesqProd.this.startActivityForResult(i, 0) ;
            }else{
                a.Alerta("Produto não Cadastrado");
            }
        }
    }

    @Override
    public boolean onMenuItemSelected(int panel, MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                voltar();
                break;
            case R.id.item1:
                codbar();
                break;
            case R.id.item2:
                voltar();
                break;

        }

        return(true);
    }

    public void voltar() {
        //GravarPedido();

        Intent result = new Intent(this, PesqProd.class);
        result.putExtra("PEDIDO_ID", pvi.getNumpre());
        setResult(RESULT_OK, result);
        PesqProd.this.finish();

    }



}
