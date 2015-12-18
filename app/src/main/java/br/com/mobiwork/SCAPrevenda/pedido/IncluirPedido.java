package br.com.mobiwork.SCAPrevenda.pedido;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.mobiwork.SCAPrevenda.R;
import br.com.mobiwork.SCAPrevenda.dao.DaoConfig;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDB;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;
import br.com.mobiwork.SCAPrevenda.dao.DaoPreVendaItem;
import br.com.mobiwork.SCAPrevenda.dao.DaoProduto;
import br.com.mobiwork.SCAPrevenda.model.Config;
import br.com.mobiwork.SCAPrevenda.model.PreVendaItem;
import br.com.mobiwork.SCAPrevenda.model.Produto;
import br.com.mobiwork.SCAPrevenda.sinc.SincDown;
import br.com.mobiwork.SCAPrevenda.util.Alertas;
import br.com.mobiwork.SCAPrevenda.util.BigDecimalRound;
import br.com.mobiwork.SCAPrevenda.util.DataHoraPedido;
import br.com.mobiwork.SCAPrevenda.util.SharedPreferencesUtil;

/**
 * Created by LuisGustavo on 14/08/14.
 */
public class IncluirPedido extends Activity implements  View.OnClickListener{

    private SQLiteDatabase db,dbp ;
    private DaoCreateDBP daop;
    private DaoCreateDB daob;
    private Cursor c;
    private DaoProduto dp;
    private Produto p;
    private Spinner un,cbtipod;
    private TextView produto,txtgradecusto,txtnomgradecusto;
    private SimpleCursorAdapter  adapter;
    private EditText edTxQtd, edTxSelecionado,edTxVrunit,edTxDesc;
    private DaoPreVendaItem dpi;
    private PreVendaItem pvi;
    private DataHoraPedido dataHoraMS;
    private Alertas a ;
    private String isedit,condicao,tipo;
    private TextView txtdesconto;
    private Button btcalc;
    private ImageButton btdetalhes;
    private ListView descqtde;
    private  Config config;
    private LinearLayout litxtdesc;
    double precogeral;
    private LinearLayout prilaycb;
    private Spinner cbtipo;
    boolean entroutipo =false;
    private double preco1,preco2,preco3;
    private String descri1,descri2,descri3;



    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.incluirpedido);
        if(Integer.valueOf(android.os.Build.VERSION.SDK)>11){
            this.setFinishOnTouchOutside(false);
        }

        a= new Alertas(this);
        daop = new DaoCreateDBP(this);
        dbp =  daop.getWritableDatabase();
        daob = new DaoCreateDB(this);
        db =  daob.getWritableDatabase();
        dp=new DaoProduto(this);
        dpi= new DaoPreVendaItem(this);
        config= new DaoConfig(this).consultar(this.dbp);

        un=(Spinner)findViewById(R.id.un);
        cbtipod=(Spinner)findViewById(R.id.cbdesc);
        edTxQtd=(EditText)findViewById(R.id.qtd);
        edTxVrunit=(EditText)findViewById(R.id.vrunit);
        edTxDesc=(EditText)findViewById(R.id.edtxdesconto);
        txtdesconto=(TextView)findViewById(R.id.txtdesconto);
        txtgradecusto=(TextView)findViewById(R.id.txtgradecusto);
        txtnomgradecusto=(TextView)findViewById(R.id.txtnomgradecusto);
        btcalc=(Button)findViewById(R.id.btcalc);
        btdetalhes=(ImageButton)findViewById(R.id.btdetalhes);
        descqtde=(ListView)findViewById(R.id.descqtde);
        litxtdesc=(LinearLayout)findViewById(R.id.litextdesc);
        prilaycb=(LinearLayout)findViewById(R.id.prilaycb);
        edTxVrunit.setEnabled(false);
        produto=(TextView)findViewById(R.id.produto);
        cbtipo=(Spinner)findViewById(R.id.cbtipo);
        edTxSelecionado = edTxQtd;
        preco1=0;preco2=0;preco3=0;


        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        edTxQtd.setInputType(InputType.TYPE_NULL);
        edTxQtd.setOnTouchListener(new View.OnTouchListener() {
                                       public boolean onTouch(View v, MotionEvent event) {
                                           edTxQtd.setInputType(InputType.TYPE_CLASS_TEXT);
                                           edTxQtd.onTouchEvent(event); // call native handler
                                           return true; // consume touch even
                                       }
                                   }
        );

        edTxDesc.setInputType(InputType.TYPE_NULL);
        edTxDesc.setOnTouchListener(new View.OnTouchListener() {
                                        public boolean onTouch(View v, MotionEvent event) {
                                            edTxDesc.setInputType(InputType.TYPE_CLASS_TEXT);
                                            edTxDesc.onTouchEvent(event); // call native handler
                                            return true; // consume touch even
                                        }
                                    }
        );

        pvi= new PreVendaItem();
        p = new Produto();
        p.setDescricao(getIntent().getStringExtra("DESCRI"));
        pvi.setDescricao(getIntent().getStringExtra("DESCRI"));
        pvi.setCodprod(getIntent().getStringExtra("CODPRODUTO"));
        pvi.setNumpre(getIntent().getStringExtra("pedidoItemId"));
        String edit=getIntent().getStringExtra("edit");
        pvi.setDigprod(getIntent().getStringExtra("digito"));
        pvi.setTipo(getIntent().getStringExtra("tipo"));
        p.setProbarra(getIntent().getStringExtra("barra"));
        condicao=getIntent().getStringExtra("condicao");
        tipo=getIntent().getStringExtra("tipo");
        isedit=getIntent().getStringExtra("edit");
        if(!p.getProbarra().trim().equalsIgnoreCase("")) {
            c = dp.procCodBarra(dbp, p.getProbarra());
            if (c.moveToFirst()) {
                p.setProduto(c,config);

            }
        }else{
            c = dp.proCodigo(dbp, pvi.getCodprod(),pvi.getDigprod());
            if (c.moveToFirst()) {
                p.setProduto(c,config);
            }
        }
        if (config.getEmp().equalsIgnoreCase("jrc")){
            btdetalhes.setVisibility(View.VISIBLE);
            popularregrajrc();
            populartipo();
        }else{
            txtgradecusto.setText(p.getProdigito());
        }

        popularUnid(c);
        popularvalores();

        configBt();
        disableInputEdTx();

        btcalc.setVisibility(View.GONE);
        txtdesconto.setVisibility(View.GONE);
        edTxDesc.setVisibility(View.GONE);
        cbtipod.setVisibility(View.GONE);

    }

    public void popularvalores(){
        if(isedit.equalsIgnoreCase("edit")){
            pvi.setQuantidade(Double.parseDouble(getIntent().getStringExtra("quantidade")));
            pvi.setValor(Double.parseDouble(getIntent().getStringExtra("valor")));
            setaValores(pvi, p,"edit");
        }else{

            if(config.getEmp().equalsIgnoreCase("jrc")) {
                pvi.setPrecoTb(precogeral);
            }else{
                pvi.setPrecoTb(p.getProprvenda1());
            }
            pvi.setDigprod(p.getProdigito());
            setaValores(pvi, p,"novo");
        }
    }

    public void popularregrajrc(){
        //regraDesconto();
        // popularComboDesc();
        if(config.getEmp().equalsIgnoreCase("jrc")){
            if(pvi.getTipo()!=null ) {
                if(pvi.getTipo().equalsIgnoreCase("null")) {
                    if(p.getProprvenda2()>0){
                        pvi.setTipo(pedidovendateste.tipo);
                    }else{
                        pvi.setTipo("Varejo");
                    }
                }
            }
            if(pvi.getTipo().equalsIgnoreCase("varejo")){
                precogeral=p.getProprvenda1();
            }else{
                precogeral=p.getProprvenda2();
            }

        }
        populaListDesc();
        txtgradecusto.setText(String.valueOf(p.getProprcusto()));
        txtnomgradecusto.setText("C:");
        prilaycb.setVisibility(View.VISIBLE);
    }


    public void populartipo(){

        List<String> nomes = new ArrayList<String>();
        final String[] nome = new String[1];

        nomes.add("Varejo");
        if(p.getProprvenda2()>0) {
            nomes.add("Atacado");
        }
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
                if (entroutipo) {
                    pvi.setTipo(cbtipo.getSelectedItem().toString());
                    tipo = parent.getItemAtPosition(posicao).toString();
                    popularregrajrc();
                    popularvalores();
                }
                entroutipo = true;
                //imprime um Toast na tela com o nome que foi selecionado
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if(pvi!=null) {
            if (pvi.getTipo().equalsIgnoreCase("varejo")) {
                cbtipo.setSelection(0);
            } else {
                if(p.getProprvenda2()>0) {
                    cbtipo.setSelection(1);
                }
            }
        }else{
            if (pedidovendateste.tipo.equalsIgnoreCase("varejo")) {
                cbtipo.setSelection(0);
            } else {
                if(p.getProprvenda2()>0){
                    cbtipo.setSelection(1);
                }

            }
        }
    }
    private void disableInputEdTx(){
        View.OnTouchListener otlQtd = new View.OnTouchListener() {
            public boolean onTouch (View v, MotionEvent event) {
                int inType = edTxQtd.getInputType();
                edTxQtd.setInputType(InputType.TYPE_NULL);
                edTxQtd.onTouchEvent(event);
                edTxQtd.setInputType(inType);
                edTxSelecionado = edTxQtd;
                return true;
            }
        };
        edTxQtd.setOnTouchListener(otlQtd);

        View.OnTouchListener vdesc = new View.OnTouchListener() {
            public boolean onTouch (View v, MotionEvent event) {
                int inType = edTxDesc.getInputType();
                edTxDesc.setInputType(InputType.TYPE_NULL);
                edTxDesc.onTouchEvent(event);
                edTxDesc.setInputType(inType);
                edTxSelecionado = edTxDesc;
                return true;
            }
        };
        edTxDesc.setOnTouchListener(vdesc);

        View.OnTouchListener otlInformaValor = new View.OnTouchListener() {
            public boolean onTouch (View v, MotionEvent event) {
                int inType = edTxVrunit.getInputType();
                edTxVrunit.setInputType(InputType.TYPE_NULL);
                edTxVrunit.onTouchEvent(event);
                edTxVrunit.setInputType(inType);
                edTxSelecionado = edTxVrunit;
                return true;
            }
        };
        edTxVrunit.setOnTouchListener(otlInformaValor);
    }

    public void regraJrc(){
        descqtde.setVisibility(View.VISIBLE);
        edTxVrunit.setEnabled(true);

    }
    public void configBt(){
        Button bt = (Button) findViewById(R.id.x1);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x2);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x3);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x4);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x5);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x6);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x7);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x8);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x9);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x0);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.xDone);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.xCancel);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.xPonto);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.xLimpa);
        bt.setOnClickListener(this);
    }

    public void popularUnid(Cursor c){
        String codun=c.getString(c.getColumnIndex("codun"));
        adapter = new SimpleCursorAdapter(this,R.layout.list_item2,c,new String[] {"codun"},
                new int[] {R.id.comboName});
        un.setAdapter(adapter);
    }


    public void popularComboDesc() {
        List<String> nomes = new ArrayList<String>();
        final String[] nome = new String[1];

        nomes.add("Valor");
        nomes.add("Porcento");

        //Identifica o Spinner no layout
        //Cria um ArrayAdapter usando um padrao de layout da classe R do android, passando o ArrayList nomes
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, nomes);
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        cbtipod.setAdapter(spinnerArrayAdapter);

        //Metodo do Spinner para capturar o item selecionado
        cbtipod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                //pega nome pela posicao
                nome[0] = parent.getItemAtPosition(posicao).toString();
                //imprime um Toast na tela com o nome que foi selecionado
                calcular();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void populaListDesc(){
        int _id=0;
        final ArrayList<HashMap<String, String>> precoQtdeList=new ArrayList<HashMap<String, String>>();
        regraJrc();
        if (config.getEmp().equalsIgnoreCase("jrc")) {
            _id=0;
            if(p.getProprvenda1F()>0){
                HashMap<String, String> map;
                map = new HashMap<String, String>();
                map.put("_id", String.valueOf(_id));
                if(p.getCodun().equalsIgnoreCase("KG.")){
                    map.put("precoQtdMin", "R$ " + BigDecimalRound.Round(p.getProprvenda1F(), 2));
                    map.put("precoQtdeMin","(KG: " + String.valueOf(1.0) + ")");
                    map.put("preco", String.valueOf(BigDecimalRound.Round(p.getProprvenda1F(), 4)));
                    descri1= "(KG: " + String.valueOf(1.0) + ")";
                    map.put("quantidade", "1");
                    map.put("unitario", "KG:");
                }else{
                    map.put("precoQtdMin","R$ " + BigDecimalRound.Round(p.getProprvenda1F(), 2));
                    map.put("precoQtdeMin"," (UN:" + String.valueOf(1.0) + ")");
                    descri1="(UN: " + String.valueOf(1.0) + ")";
                    map.put("preco", String.valueOf(BigDecimalRound.Round(p.getProprvenda1F(), 4)));
                    map.put("unitario", "Unit:");
                }
                map.put("qtdcalc",String.valueOf(1.0));
                preco1=BigDecimalRound.Round(p.getProprvenda1F(), 4);
                precoQtdeList.add(map);
                _id++;
            }
            if(p.getQtdembal()>0 || p.getProQuantEmbal()>1 ){
                HashMap<String, String> map;
                map = new HashMap<String, String>();
                map.put("_id", String.valueOf(_id));
                if(p.getCodun().equalsIgnoreCase("KG.")){
                    map.put("precoQtdMin","R$ " + BigDecimalRound.Round(precogeral, 4));
                    map.put("precoQtdeMin","(KG: " + p.getQtdembal()+ ")");
                    map.put("quantidade", "1");
                    map.put("preco", String.valueOf(BigDecimalRound.Round(precogeral, 4)));
                    preco2=BigDecimalRound.Round(BigDecimalRound.Round(precogeral, 4));
                    descri2= "(KG: " + p.getQtdembal()+ ")";
                    map.put("unitario", "KG:");
                    map.put("qtdcalc",String.valueOf(p.getQtdembal()));
                }else{
                    if(p.getProQuantEmbal()>1){
                        map.put("precoQtdMin","R$ " + BigDecimalRound.Round(precogeral, 2));
                        map.put("precoQtdeMin","(EB: " + String.valueOf(p.getProQuantEmbal()) +")");
                        map.put("quantidade", String.valueOf(p.getProQuantEmbal()));
                        map.put("preco", String.valueOf(BigDecimalRound.Round(precogeral, 4)));
                        preco2=BigDecimalRound.Round(precogeral/p.getConversor(), 4);
                        descri2="(EB: " + String.valueOf(p.getProQuantEmbal()) + ")";
                        map.put("unitario", "EB:");
                        map.put("qtdcalc",String.valueOf(p.getProQuantEmbal()));
                    }else {
                        if(p.getProprvenda1F()>0) {
                            map.put("precoQtdMin", "R$ " + BigDecimalRound.Round(precogeral, 2));
                            map.put("precoQtdeMin","(EB:" + String.valueOf(p.getQtdembal()) +" ");
                            map.put("quantidade", String.valueOf(p.getQtdembal()));
                            map.put("preco", String.valueOf(BigDecimalRound.Round(precogeral, 4)));
                            preco2=BigDecimalRound.Round(precogeral/p.getConversor(), 4);
                            descri2="(EB: " + String.valueOf(p.getQtdembal()) + " )";
                            map.put("unitario", "EB:");
                            map.put("qtdcalc",String.valueOf(p.getQtdembal()));
                        }else{
                            map.put("precoQtdMin","R$ " + BigDecimalRound.Round(precogeral, 2));
                            map.put("precoQtdeMin","(EB: " + String.valueOf(p.getProQuantEmbal()) + ")");
                            map.put("quantidade", String.valueOf(p.getProQuantEmbal()));
                            map.put("preco", String.valueOf(BigDecimalRound.Round(precogeral, 4)));
                            preco2=BigDecimalRound.Round(precogeral/p.getConversor(), 4);
                            descri2="(EB: " + String.valueOf(p.getProQuantEmbal()) + ")";
                            map.put("unitario", "EB:");
                            map.put("qtdcalc",String.valueOf(p.getProQuantEmbal()));
                        }
                    }
                }
                precoQtdeList.add(map);
                _id++;
            }

            if(p.getProQuantEmbal2()>0 && p.getDesconto()>0){
                HashMap<String, String> map;
                map = new HashMap<String, String>();
                map.put("_id", String.valueOf(_id));
                if(p.getCodun().equalsIgnoreCase("KG.")) {
                    map.put("precoQtdMin", "R$ " + BigDecimalRound.Round(descprod(precogeral, p.getDesconto()), 3));
                    map.put("precoQtdeMin","(KG:" + String.valueOf(p.getQtdembal() * p.getProQuantEmbal2()) +")");
                    map.put("preco", String.valueOf(BigDecimalRound.Round(descprod(precogeral, p.getDesconto()), 4)));
                    map.put("quantidade", String.valueOf(p.getProQuantEmbal()));
                    preco3=BigDecimalRound.Round(descprod(precogeral, p.getDesconto())/p.getConversor(),4);
                    descri3="(KG: " + String.valueOf(p.getQtdembal() * p.getProQuantEmbal2()) + ")";
                    map.put("unitario", "KG:");
                    map.put("qtdcalc",String.valueOf(p.getQtdembal() * p.getProQuantEmbal2()));
                }else{
                    map.put("precoQtdMin", "R$ " + BigDecimalRound.Round(descprod(precogeral, p.getDesconto()), 3));
                    map.put("precoQtdeMin","(cx: " + String.valueOf(p.getQtdembal() * p.getProQuantEmbal2()) +" )");
                    map.put("preco", String.valueOf(BigDecimalRound.Round(descprod(precogeral, p.getDesconto()), 4)));
                    map.put("quantidade", String.valueOf(p.getProQuantEmbal()));
                    preco3=BigDecimalRound.Round(descprod(precogeral, p.getDesconto())/p.getConversor(),4);
                    descri3="(cx: " + String.valueOf(p.getQtdembal() * p.getProQuantEmbal2()) + " )";
                    map.put("unitario", "Caixa:");
                    map.put("qtdcalc",String.valueOf(p.getQtdembal() * p.getProQuantEmbal2()));
                }
                precoQtdeList.add(map);
                _id++;

            }
            if(_id>0){
                litxtdesc.setVisibility(View.VISIBLE);
            }else{
                litxtdesc.setVisibility(View.GONE);
            }
            SimpleAdapter precoQtdeAdapter=new SimpleAdapter(this, precoQtdeList, R.layout.precoqtdelist, new String[]{"unitario", "precoQtdeMin", "precoQtdMin"}, new int[]{R.id.unitario, R.id.qtdminfatura, R.id.precoQtdMin});
            descqtde.setAdapter(precoQtdeAdapter);
        }

        descqtde.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (HashMap<String, String> map : precoQtdeList) {
                    if (id == Integer.parseInt(map.get("_id"))) {
                        if (config.getEmp().equalsIgnoreCase("jrc")) {
                            edTxQtd.setText(String.valueOf(BigDecimalRound.Round(Double.parseDouble(map.get("qtdcalc")), 4)));
                            if (map.get("unitario").equalsIgnoreCase("unit:")) {
                                edTxVrunit.setText(map.get("preco"));
                            } else {
                                try {
                                    if (p.getProQuantEmbal() == 1) {
                                        double p = Double.parseDouble(map.get("preco"));
                                        edTxVrunit.setText(String.valueOf(BigDecimalRound.Round(Double.parseDouble(map.get("preco")), 4)));
                                    } else {
                                        //  edTxVrunit.setText(String.valueOf(BigDecimalRound.Round(Double.parseDouble(map.get("preco")) / Double.parseDouble(map.get("quantidade")), 4)));
                                        edTxVrunit.setText(String.valueOf(BigDecimalRound.Round(Double.parseDouble(map.get("preco")) / Double.parseDouble(String.valueOf(p.getConversor())), 4)));

                                    }

                                } catch (Exception e) {
                                    edTxVrunit.setText(map.get("preco"));
                                }
                            }
                        }
                        break;
                    }
                }
            }
        });
    }

    public double descprod(double vrPreco,double desc){
        double descpreco=0;
        descpreco=vrPreco-(vrPreco*desc/100);
        return descpreco;
    }

    public void setaValores(PreVendaItem pv,Produto pr,String status){
        produto.setText(pr.getProcodigo()+" - "+pr.getDescricao());
        if(status.equalsIgnoreCase("novo")){
           /* if(condicao.equals("1")){
                edTxVrunit.setText(String.valueOf(p.getProprvenda2()));
            }else{*/
            if(config.getEmp().equalsIgnoreCase("jrc")) {
                if(p.getProprvenda1F()>0){
                    edTxVrunit.setText(String.valueOf(p.getProprvenda1F()));
                }else{
                    if(p.getConversor()>0){
                        edTxVrunit.setText(String.valueOf(BigDecimalRound.Round(precogeral/p.getConversor(),4)));
                    }else{
                        edTxVrunit.setText(String.valueOf(precogeral));
                    }
                }
            }else{
                edTxVrunit.setText(String.valueOf(p.getProprvenda1()));
            }
        //    }

        }else{
            edTxVrunit.setText(String.valueOf(pv.getValor()));
            edTxQtd.setText(String.valueOf(pv.getQuantidade()));
        }

    }

    private void isBack() {
        CharSequence cc = edTxSelecionado.getText();
        if (cc != null && cc.length() > 0)
        {
            edTxSelecionado.setText("");
            edTxSelecionado.append(cc.subSequence(0, cc.length() - 1));
        }
    }
    public void inserirEditar(PreVendaItem pvi){
        dataHoraMS = DataHoraPedido.criarDataHoraPedido();
        pvi.setQuantidade(Double.parseDouble(edTxQtd.getText().toString()));
        pvi.setValor(Double.parseDouble(edTxVrunit.getText().toString()));
        pvi.setTipo(cbtipo.getSelectedItem().toString());
        Cursor ctemp = (Cursor)un.getSelectedItem();
        pvi.setUnidade(ctemp.getString(ctemp.getColumnIndex("codun")));
        try {
            if (!isedit.equalsIgnoreCase("edit")) {
                dpi.Inserir(dbp, db, pvi, dataHoraMS);
            } else {
                dpi.atualizar(db, pvi);
            }
        } catch (Exception e) {
            a.Alerta(e.getMessage());
        }
        IncluirPedido.this.finish();
    }

    @Override
    public void onClick(View v) {
        String keyInfo = (String)v.getTag();
        if ( keyInfo.equals("done")){
            if(config.getEmp().equalsIgnoreCase("vitoria")) {
                if (edTxQtd.getText().toString().equals("")) {
                    a.Alerta("Preencha a Quantidade");
                } else{
                    inserirEditar(pvi);
                }
            }else if(config.getEmp().equalsIgnoreCase("jrc")){
                if (edTxQtd.getText().toString().equals("")) {
                    a.Alerta("Preencha a Quantidade !");
                }else if (edTxVrunit.getText().toString().equals("")) {
                    a.Alerta("Preencha o Valor !");
                }else if(Double.parseDouble(edTxVrunit.getText().toString())<p.getProprcusto()){
                    if(p.getProQuantEmbal()>0){
                        if(Double.parseDouble(edTxVrunit.getText().toString())<p.getProprcusto()){
                            a.Alerta("Valor Abaixo do Permitido !");
                        }else{
                            inserirEditar(pvi);
                        }
                    }else {
                        a.Alerta("Valor Abaixo do Permitido !");
                    }
                }else{
                    inserirEditar(pvi);
                }
            }
        }
        else if (keyInfo.equals("back"))
            isBack();
        else if (keyInfo.equals("limpa"))
            edTxSelecionado.setText("");
        else if (keyInfo.equals("cancel")){
            Intent result = new Intent(this, IncluirPedido.class);
            setResult(RESULT_CANCELED, result);
            IncluirPedido.this.finish();
        } else {
            if (!keyInfo.equals("")){
                String valorSelecionado = edTxSelecionado.getText().toString();
                if (!((keyInfo.equals(".")) && (valorSelecionado.equals("")))){
                    if (keyInfo.equals(".")){
                     if(config.getEmp().equalsIgnoreCase("jrc")){
                             if (edTxSelecionado.equals(edTxQtd)){
                                 if(p.getProqtddecimal()!=2) {
                                     edTxSelecionado.append(keyInfo);
                                 }else{
                                     a.AlertaSinc("NÃO PERMITE FRACIONADO !");
                                 }
                             }else if(edTxSelecionado.equals(edTxDesc)){
                                 edTxSelecionado.append(keyInfo);

                             } else {
                                 if (edTxSelecionado.getText().toString().indexOf(".") < 0)
                                     edTxSelecionado.append(keyInfo);
                             }
                     }else{
                         if (edTxSelecionado.equals(edTxQtd)){
                             edTxSelecionado.append(keyInfo);
                         }else if(edTxSelecionado.equals(edTxDesc)){
                             edTxSelecionado.append(keyInfo);

                         } else {
                             if (edTxSelecionado.getText().toString().indexOf(".") < 0)
                                 edTxSelecionado.append(keyInfo);
                         }
                     }

                    } else {
                        edTxSelecionado.append(keyInfo);
                    }
                }else {
                    if (keyInfo.equals(".")) {
                        if(config.getEmp().equalsIgnoreCase("jrc")){
                            if (edTxSelecionado.equals(edTxQtd)){
                                if(p.getProqtddecimal()!=2) {
                                    if (edTxSelecionado.getText().toString().equalsIgnoreCase("")) {
                                        edTxSelecionado.setText("0" + keyInfo);
                                    }
                                }else{
                                    a.AlertaSinc("NÃO PERMITE FRACIONADO !");
                                }
                            }
                        }else{
                            if (edTxSelecionado.getText().toString().equalsIgnoreCase("")) {
                                edTxSelecionado.setText("0" + keyInfo);
                            }
                        }
                    }
                }

            }
        }

    }
    public void btcalcular(View v){
        calcular();
    }
    public void calcular(){
       Alertas a = new Alertas(this);
       try {
           if(!edTxDesc.getText().toString().equalsIgnoreCase("")) {
               double desconto = Double.parseDouble(edTxDesc.getText().toString());
               if (cbtipod.getSelectedItem().toString().equalsIgnoreCase("porcento")) {
                   edTxVrunit.setText(String.valueOf(BigDecimalRound.Round(precogeral - (precogeral * desconto / 100))));
                   a.Alerta("Desconto de R$:" +BigDecimalRound.Round(precogeral * desconto / 100));
               } else {
                   edTxVrunit.setText(String.valueOf(BigDecimalRound.Round(precogeral - desconto)));
                   a.Alerta("Desconto de :" + BigDecimalRound.Round(desconto * 100 / precogeral,2) + "%");
               }
           }
       }catch (Exception e){

       }

    }

    public void detalhes(View v){
        // get prompts.xml view
        LinearLayout lidesc1,lidesc2,lidesc3;
        final EditText edtxqtd1 ,edtxqtd2 ,edtxqtd3 ,edtxpreco1 ,edtxpreco2 ,edtxpreco3 ,edtxtotal1,edtxtotal2,edtxtotal3;
        TextView txtdesc1,txtdesc2,txtdesc3;
        Button btcalc1,btcalc2,btcalc3;
        final RadioButton rb1, rb2, rb3;
        LayoutInflater li = LayoutInflater.from(IncluirPedido.this);
        View promptsView = li.inflate(R.layout.detalhes, null);
        rb1 = (RadioButton) promptsView.findViewById(R.id.rb1);
        rb2 = (RadioButton) promptsView.findViewById(R.id.rb2);
        rb3 = (RadioButton) promptsView.findViewById(R.id.rb3);
        edtxqtd1=(EditText) promptsView.findViewById(R.id.edtxqtd1);
        edtxpreco1=(EditText) promptsView.findViewById(R.id.edtxpreco1);
        edtxqtd2=(EditText) promptsView.findViewById(R.id.edtxqtd2);
        edtxpreco2=(EditText) promptsView.findViewById(R.id.edtxpreco2);
        edtxqtd3=(EditText) promptsView.findViewById(R.id.edtxqtd3);
        edtxpreco3=(EditText) promptsView.findViewById(R.id.edtxpreco3);
        edtxtotal1=(EditText) promptsView.findViewById(R.id.edtxtotal1);

        if(preco1>0){
            lidesc1=(LinearLayout) promptsView.findViewById(R.id.lidesc1);
            lidesc1.setVisibility(View.VISIBLE);
            if(p.getProqtddecimal()==2){
                edtxqtd1.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            edtxtotal1.setEnabled(false);
            btcalc1=(Button)promptsView.findViewById(R.id.btcalc1);
            txtdesc1=(TextView)promptsView.findViewById(R.id.txtdesc1);
            edtxqtd1.setText(edTxQtd.getText().toString());
            edtxpreco1.setText(String.valueOf(preco1));
            if(!edTxQtd.getText().toString().equalsIgnoreCase("")){
                edtxtotal1.setText(String.valueOf(calc(Double.parseDouble(edTxQtd.getText().toString()),preco1)));
            }
            txtdesc1.setText("Preço de :" + descri1);
            btcalc1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    if (edtxqtd1.getText().toString().equalsIgnoreCase("")) {
                        a.AlertaSinc("Preencha a quantidade");
                    } else if (edtxpreco1.getText().toString().equalsIgnoreCase("")) {
                        a.AlertaSinc("Preencha o campo valor");
                    } else {
                        edtxtotal1.setText(String.valueOf(calc(Double.parseDouble(edtxqtd1.getText().toString()), Double.parseDouble(edtxpreco1.getText().toString()))));
                    }
                }
            });
            rb1.setChecked(true);
            rb1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    rb1.setChecked(true);
                    rb2.setChecked(false);
                    rb3.setChecked(false);
                }
            });
            edtxqtd1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    rb1.setChecked(true);
                    rb2.setChecked(false);
                    rb3.setChecked(false);
                }
            });
            edtxpreco1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    rb1.setChecked(true);
                    rb2.setChecked(false);
                    rb3.setChecked(false);
                }
            });
        }

        if(preco1==00&&preco1==0&&preco3==0){
            lidesc1=(LinearLayout) promptsView.findViewById(R.id.lidesc1);
            lidesc1.setVisibility(View.VISIBLE);
            if(p.getProqtddecimal()==2){
                edtxqtd1.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            edtxtotal1.setEnabled(false);
            btcalc1=(Button)promptsView.findViewById(R.id.btcalc1);
            txtdesc1=(TextView)promptsView.findViewById(R.id.txtdesc1);
            edtxqtd1.setText(edTxQtd.getText().toString());
            edtxpreco1.setText(edTxVrunit.getText().toString());
            if(!edTxQtd.getText().toString().equalsIgnoreCase("")&&!edTxVrunit.getText().toString().equalsIgnoreCase("")){
                edtxtotal1.setText(String.valueOf(calc(Double.parseDouble(edTxQtd.getText().toString()),Double.parseDouble(edTxVrunit.getText().toString()))));
            }
            txtdesc1.setText("Preço de Unico");
            btcalc1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    if (edtxqtd1.getText().toString().equalsIgnoreCase("")) {
                        a.AlertaSinc("Preencha a quantidade");
                    } else if (edtxpreco1.getText().toString().equalsIgnoreCase("")) {
                        a.AlertaSinc("Preencha o campo valor");
                    } else {
                        edtxtotal1.setText(String.valueOf(calc(Double.parseDouble(edtxqtd1.getText().toString()), Double.parseDouble(edtxpreco1.getText().toString()))));
                    }
                }
            });
            rb1.setChecked(true);
            rb1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    rb1.setChecked(true);
                    rb2.setChecked(false);
                    rb3.setChecked(false);
                }
            });

        }

        if(preco2>0){
            lidesc2=(LinearLayout) promptsView.findViewById(R.id.lidesc2);
            lidesc2.setVisibility(View.VISIBLE);

            if(p.getProqtddecimal()==2){
                edtxqtd2.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            edtxtotal2=(EditText) promptsView.findViewById(R.id.edtxtotal2);
            edtxtotal2.setEnabled(false);
            btcalc2=(Button)promptsView.findViewById(R.id.btcalc2);
            txtdesc2=(TextView)promptsView.findViewById(R.id.txtdesc2);
            edtxqtd2.setText(edTxQtd.getText().toString());
            edtxpreco2.setText(String.valueOf(BigDecimalRound.Round(preco2,4)));
            if(!edTxQtd.getText().toString().equalsIgnoreCase("")){
                edtxtotal2.setText(String.valueOf(calc(Double.parseDouble(edTxQtd.getText().toString()),preco2)));
            }
            txtdesc2.setText("Preço de :"+descri2);
            btcalc2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    if (edtxqtd2.getText().toString().equalsIgnoreCase("")) {
                        a.AlertaSinc("Preencha a quantidade");
                    } else if (edtxpreco2.getText().toString().equalsIgnoreCase("")) {
                        a.AlertaSinc("Preencha o campo valor");
                    } else {
                        edtxtotal2.setText(String.valueOf(calc(Double.parseDouble(edtxqtd2.getText().toString()), Double.parseDouble(edtxpreco2.getText().toString()))));
                    }
                }
            });
            rb2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    rb1.setChecked(false);
                    rb2.setChecked(true);
                    rb3.setChecked(false);
                }
            });

            edtxqtd2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    rb1.setChecked(false);
                    rb2.setChecked(true);
                    rb3.setChecked(false);
                }
            });

            edtxpreco2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    rb1.setChecked(false);
                    rb2.setChecked(true);
                    rb3.setChecked(false);
                }
            });


        }
        if(preco3>0){
            lidesc3=(LinearLayout) promptsView.findViewById(R.id.lidesc3);
            lidesc3.setVisibility(View.VISIBLE);
            if(p.getProqtddecimal()==2){
                edtxqtd3.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            edtxtotal3=(EditText) promptsView.findViewById(R.id.edtxtotal3);
            edtxtotal3.setEnabled(false);
            txtdesc3=(TextView)promptsView.findViewById(R.id.txtdesc3);
            btcalc3=(Button)promptsView.findViewById(R.id.btcalc3);
            edtxqtd3.setText(edTxQtd.getText().toString());
            edtxpreco3.setText(String.valueOf(BigDecimalRound.Round(preco3,4)));
            if(!edTxQtd.getText().toString().equalsIgnoreCase("")){
                edtxtotal3.setText(String.valueOf(calc(Double.parseDouble(edTxQtd.getText().toString()),preco3)));
            }
            txtdesc3.setText("Preço de :"+descri3);
            btcalc3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    if (edtxqtd3.getText().toString().equalsIgnoreCase("")) {
                        a.AlertaSinc("Preencha a quantidade");
                    } else if (edtxpreco3.getText().toString().equalsIgnoreCase("")) {
                        a.AlertaSinc("Preencha o campo valor");
                    } else {
                        edtxtotal3.setText(String.valueOf(calc(Double.parseDouble(edtxqtd3.getText().toString()), Double.parseDouble(edtxpreco3.getText().toString()))));
                    }
                }
            });
            rb3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    rb1.setChecked(false);
                    rb2.setChecked(false);
                    rb3.setChecked(true);
                }
            });
            edtxqtd3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    rb1.setChecked(false);
                    rb2.setChecked(false);
                    rb3.setChecked(true);
                }
            });
            edtxpreco3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    rb1.setChecked(false);
                    rb2.setChecked(false);
                    rb3.setChecked(true);
                }
            });

        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                IncluirPedido.this);


        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (rb1.isChecked()) {
                                    edTxQtd.setText(edtxqtd1.getText().toString());
                                    edTxVrunit.setText(edtxpreco1.getText().toString());
                                } else if (rb2.isChecked()) {
                                    edTxQtd.setText(edtxqtd2.getText().toString());
                                    edTxVrunit.setText(edtxpreco2.getText().toString());
                                } else if(rb3.isChecked()) {
                                    edTxQtd.setText(edtxqtd3.getText().toString());
                                    edTxVrunit.setText(edtxpreco3.getText().toString());
                                }else{
                                    a.AlertaSinc("POR FAVOR SELECIONE ALGUMA DAS OPÇÕES!");
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

    public  double calc(double quanti,double valor ){
        return BigDecimalRound.Round(quanti*valor,2);
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
