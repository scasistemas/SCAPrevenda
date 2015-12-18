package br.com.mobiwork.SCAPrevenda.pedido;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.os.Bundle;
        import android.view.View;
        import android.view.Window;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.CheckBox;
        import android.widget.EditText;
        import android.widget.LinearLayout;
        import android.widget.RadioButton;
        import android.widget.RadioGroup;
        import android.widget.Spinner;
        import android.widget.TextView;

        import java.util.ArrayList;
        import java.util.List;

        import br.com.mobiwork.SCAPrevenda.R;
        import br.com.mobiwork.SCAPrevenda.dao.DaoConfig;
        import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDB;
        import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;
        import br.com.mobiwork.SCAPrevenda.dao.DaoPreVenda;
        import br.com.mobiwork.SCAPrevenda.inf.AsyncResponse;
        import br.com.mobiwork.SCAPrevenda.model.Config;
        import br.com.mobiwork.SCAPrevenda.model.PreVenda;
        import br.com.mobiwork.SCAPrevenda.model.PreVendaItem;
        import br.com.mobiwork.SCAPrevenda.model.TabCondi;
        import br.com.mobiwork.SCAPrevenda.sinc.SincUp;
        import br.com.mobiwork.SCAPrevenda.util.Alertas;
        import br.com.mobiwork.SCAPrevenda.util.BigDecimalRound;
        import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;
        import br.com.mobiwork.SCAPrevenda.util.DataXmlExporter;
        import br.com.mobiwork.SCAPrevenda.util.SharedPreferencesUtil;


/**
 * Created by LuisGustavo on 04/09/14.
 */

public class ConfirmaPedido extends Activity  implements AsyncResponse {

    PreVendaItem pvi;
    String pedidoId,prevendaId;
    String condicao,tipopre;
    PreVenda pv;
    private TabCondi tc;
    private TextView vrtotal,divid,parc,desconto,txtperc,txtimp,txtprevenda;
    private EditText obs,edtxCli;
    Alertas a ;
    private DaoPreVenda dpv;
    private SQLiteDatabase db,dbP;
    DaoCreateDB daoDB;
    DaoCreateDBP daoDBP;
    private RadioGroup rgp;
    private CheckBox checimp,ckstatus;
    private RadioButton orc,pre;
    private SincUp sincup;
    private Button confirma,calc;
    private Config config;
    private Spinner percval,cbimp;
    private LinearLayout listatus;
    private int prstatus;


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.confirmprevenda);
        if(Integer.valueOf(android.os.Build.VERSION.SDK)>11) {
            this.setFinishOnTouchOutside(false);
        }
        tipopre="";

        pvi= new PreVendaItem();
        pv = new PreVenda();
        tc = new TabCondi();
        a= new Alertas(this);
        dpv= new DaoPreVenda(this);

        daoDBP = new DaoCreateDBP(this);
        dbP =  daoDBP.getWritableDatabase();
        daoDB = new DaoCreateDB(this);
        db=  daoDB.getWritableDatabase();
        a=new Alertas(this);

        txtimp=(TextView)findViewById(R.id.txtimp);
        percval=(Spinner)findViewById(R.id.percval);
        cbimp=(Spinner)findViewById(R.id.cbimp);
        vrtotal= (TextView)findViewById(R.id.txvrtot);
        divid= (TextView)findViewById(R.id.txdivid);
        parc= (TextView)findViewById(R.id.txparc);
        txtperc= (TextView)findViewById(R.id.txtperc);
        desconto=(TextView) findViewById(R.id.desconto);
        txtprevenda=(TextView) findViewById(R.id.txtprevenda);
        obs=(EditText) findViewById(R.id.obs);
        rgp=(RadioGroup)findViewById(R.id.rgOpinion);
        checimp=(CheckBox)findViewById(R.id.checimp);
        ckstatus=(CheckBox)findViewById(R.id.ckstatus);
        orc=(RadioButton)findViewById(R.id.rborc);
        pre=(RadioButton) findViewById(R.id.rbpre);
        confirma=(Button) findViewById(R.id.xCancelBt);
        calc=(Button) findViewById(R.id.btcalc);
        edtxCli=(EditText) findViewById(R.id.edtxCli);
        listatus=(LinearLayout)findViewById(R.id.listatus);

        pedidoId  = getIntent().getStringExtra("preid");
        prevendaId= getIntent().getStringExtra("numero");
        config= new DaoConfig(this).consultar(this.dbP);

        if(config==null){
            new DataXmlExporter(this.dbP, ConfigVendedor.getExternalStorageDirectory()).importData(this, "config", "config", "");
            config= new DaoConfig(this).consultar(this.dbP);
        }

        if(getIntent().getDoubleExtra("valorbruto",0)>=0){
            tc.setTabcodigo(getIntent().getStringExtra("condicao"));
            tc.setconrvenc(getIntent().getIntExtra("nparc", 0));
            condicao=getIntent().getStringExtra("condicao");
            pv.setCondicao(getIntent().getStringExtra("condicao"));
            pv.setNumero(getIntent().getStringExtra("numero"));
            pv.setObs(getIntent().getStringExtra("obs"));
            pv.setValorbruto(BigDecimalRound.Round(getIntent().getDoubleExtra("valorbruto", 2)));
            pv.setNparc(tc.getconrvenc());
            String g=getIntent().getStringExtra("forma");
            if(getIntent().getStringExtra("forma")!=null){
                pv.setForma(Integer.parseInt(getIntent().getStringExtra("forma")));
            }
            pv.setObs(getIntent().getStringExtra("obs"));
            pv.setNomeCliente(getIntent().getStringExtra("nomeCliente"));
            pv.setDesc(getIntent().getDoubleExtra("desc", 0));
            pv.setValortotal(BigDecimalRound.Round(getIntent().getDoubleExtra("valortotal", 2)));
            if(tc.getconrvenc()==0){
                tc.setconrvenc(1);
            }
            pv.setParc(BigDecimalRound.Round(pv.getValorbruto()/tc.getconrvenc(),2));
            tipopre=getIntent().getStringExtra("tipopre");
            pv.setStatus(getIntent().getIntExtra("status", 0));
            pv.setImp(getIntent().getIntExtra("imp", 0));
            if(getIntent().getIntExtra("edit",0)==1){
                pv.setValortotal(getIntent().getDoubleExtra("valortotal",0));
                pv.setParc(getIntent().getDoubleExtra("parc", 0));
                pv.setObs(getIntent().getStringExtra("obs"));
                pv.setDesc(getIntent().getDoubleExtra("desc", 0));
                pv.setNparc(getIntent().getIntExtra("nparc", 0));
                pv.setNomeCliente(getIntent().getStringExtra("nomeCliente"));


            }
            prstatus=dpv.getprstatus(db,pv);
            preencherCampos(pv);
            popularpercval();
            popularcbimp();
            calc(0);
            impcom();
            bloq();
            statusPrevenda();

        }else {
            vrtotal.setText("R$ " + 0.0);
            divid.setText(0 + "X");
            parc.setText("R$ "+0.0);
        }


        rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch(checkedId)
                {
                    case R.id.rbpre:
                        ckstatus.setEnabled(true);
                        ckstatus.setChecked(true);
                        ckstatus.setVisibility(View.VISIBLE);
                        txtprevenda.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rborc:
                        ckstatus.setEnabled(false);
                        ckstatus.setChecked(false);
                        ckstatus.setVisibility(View.GONE);
                        txtprevenda.setVisibility(View.GONE);
                        break;

                }
            }
        });



    }
    public void bloq(){
        if(tipopre.equalsIgnoreCase("prevenda")&&prstatus==1){
            confirma.setVisibility(View.GONE);
            obs.setEnabled(false);
            orc.setEnabled(false);
            pre.setEnabled(false);
            checimp.setEnabled(false);
            desconto.setEnabled(false);
            calc.setEnabled(false);



        }
    }

    public void statusPrevenda(){
       if(pv.getStatus()==1){
           ckstatus.setEnabled(true);
           ckstatus.setChecked(true);
           ckstatus.setVisibility(View.VISIBLE);
           txtprevenda.setVisibility(View.VISIBLE);

       }else{
            ckstatus.setEnabled(false);
           ckstatus.setChecked(false);
           ckstatus.setVisibility(View.GONE);
           txtprevenda.setVisibility(View.GONE);

        }
    }

    public void impcom(){
        if(config.getEmp().equalsIgnoreCase("jrc")){
            txtimp.setVisibility(View.GONE);
            cbimp.setVisibility(View.VISIBLE);
        }
    }

    public void preencherCampos(PreVenda p){
        vrtotal.setText("R$ " +String.valueOf(BigDecimalRound.Round(p.getValortotal(),2)));
        divid.setText(tc.getconrvenc()+ "X");
        parc.setText("R$ " +String.valueOf(BigDecimalRound.Round(p.getParc(),2)));
        desconto.setText(String.valueOf(p.getDesc()));
        obs.setText(p.getObs());
        edtxCli.setText(p.getNomeCliente());
        if(p.getImp()==1){
            checimp.setChecked(true);
        }else{
            checimp.setChecked(false);
        }
        if(p.getStatus()==1){
            pre.setChecked(true);
        }else{
            orc.setChecked(true);
        }
    }

    public void voltar(View v){
        Intent result = new Intent(this, ConfirmaPedido.class);
        if(!tipopre.equalsIgnoreCase("prevenda")&&prstatus!=1){
            calc(0);
            pegarDados();
            dpv.terminaCad(db, pv);
            result.putExtra("desc", String.valueOf(pv.getDesc()));
            result.putExtra("obs",obs.getText().toString());
            result.putExtra("nomeCliente",edtxCli.getText().toString());
            result.putExtra("imp",String.valueOf(pv.getImp()));
            result.putExtra("tipo",String.valueOf(pv.getStatus()));
            setResult(RESULT_CANCELED, result);

        }else{
            result.putExtra("desc", String.valueOf(pv.getDesc()));
            result.putExtra("obs", pv.getObs());
            setResult(RESULT_CANCELED, result);
        }
        ConfirmaPedido.this.finish();

    }
    public void calcular(View v){
        if(prstatus!=1){
            calc(1);
        }


    }
    public void pegarDados(){
        pv.setObs(obs.getText().toString());
        int it=rgp.getCheckedRadioButtonId();
        View radioButton = rgp.findViewById(it);
        pv.setStatus(rgp.indexOfChild(radioButton));
        if(checimp.isChecked()){
            if(config.getEmp().equalsIgnoreCase("jrc")) {
                if (cbimp.getSelectedItem().toString().equalsIgnoreCase("imp.")) {
                    pv.setImp(1);
                } else {
                    pv.setImp(2);
                }
            }else{
                pv.setImp(1);
            }

        }else{
            pv.setImp(0);
        }
        pv.setNomeCliente(edtxCli.getText().toString());
    }


    public void calc(int i){
        if(tipopre.equalsIgnoreCase("prevenda")&&prstatus==1){
            a.AlertaSinc("PreVenda Fechada");
        }else{
            if(!desconto.getText().toString().equalsIgnoreCase("")){

                if (percval.getSelectedItem().toString().equalsIgnoreCase("%")&&Double.parseDouble(desconto.getText().toString())>100) {
                    a.Alerta("Desconto acima do valor da venda ");
                }else if(percval.getSelectedItem().toString().equalsIgnoreCase("R$")&&Double.parseDouble(desconto.getText().toString())>dpv.somarPreVendas(db, prevendaId)){
                    a.Alerta("Desconto acima do valor da venda ");
                }else {
                    // pv.setValortotal(dpv.somarPreVendas(db, prevendaId) - (dpv.somarPreVendas(db, prevendaId) * Double.parseDouble(desconto.getText().toString()) / 100));

                    if(percval.getSelectedItem().toString().equalsIgnoreCase("%")){
                        pv.setValortotal(dpv.somarPreVendas(db, prevendaId) -dpv.somarPreVendas(db, prevendaId)*Double.parseDouble(desconto.getText().toString())/100);
                        txtperc.setText("R$" + String.valueOf(BigDecimalRound.Round(dpv.somarPreVendas(db, prevendaId) * Double.parseDouble(desconto.getText().toString())/100)));
                        pv.setParc(pv.getValortotal() / tc.getconrvenc());
                        pv.setDesc(BigDecimalRound.Round(dpv.somarPreVendas(db, prevendaId)*Double.parseDouble(desconto.getText().toString())/100));
                        vrtotal.setText("R$ " + BigDecimalRound.Round(pv.getValortotal()));
                        parc.setText("R$" + BigDecimalRound.Round(pv.getParc()));

                    }else{
                        pv.setValortotal(dpv.somarPreVendas(db, prevendaId) - Double.parseDouble(desconto.getText().toString()));
                        txtperc.setText((String.valueOf(BigDecimalRound.Round(Double.parseDouble(desconto.getText().toString()) / dpv.somarPreVendas(db, prevendaId) * 100))) + "%");
                        pv.setParc(pv.getValortotal() / tc.getconrvenc());
                        pv.setDesc(BigDecimalRound.Round(Double.parseDouble(desconto.getText().toString())));
                        vrtotal.setText("R$ " + BigDecimalRound.Round(pv.getValortotal()));
                        parc.setText("R$" + BigDecimalRound.Round(pv.getParc()));
                    }
                }
            }else{
                if(i==1){
                    a.Alerta("Preencha um valor no campo Desc.");
                }
            }
        }
    }
    public void confirmar(View v){

        SharedPreferencesUtil sdu = new SharedPreferencesUtil(this);
        pv.setValorbruto(BigDecimalRound.Round(dpv.somarPreVendas(db, prevendaId), 2));
        pv.setIdsmart(sdu.getIdSmart());
        dpv.terminaCadBruto(db, pv);
        if(tipopre.equalsIgnoreCase("prevenda")&&pv.getPrstatus()==1){
            Intent result = new Intent(ConfirmaPedido.this, ConfirmaPedido.class);
            setResult(RESULT_OK, result);
            new SincUp(ConfirmaPedido.this,pv,"",config).execute(new String[0]);
        }else{
            calc(0);
            pegarDados();

            dpv.terminaCad(db, pv);


            new AlertDialog.Builder(this)
                    .setTitle("Deseja concluir o pedido?")
                    .setItems(R.array.op_alerta_sim_nao, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            if (i == 0) {
                                if (ckstatus.isChecked()) {
                                    dpv.altstatusPrevenda(db, pv, 1);
                                } else {
                                    dpv.altstatusPrevenda(db, pv,0);
                                }

                                Intent result = new Intent(ConfirmaPedido.this, ConfirmaPedido.class);
                                setResult(RESULT_OK, result);
                                sincup = new SincUp(ConfirmaPedido.this, pv, "", config);
                                sincup.delegate = ConfirmaPedido.this;
                                sincup.execute(new String[0]);


                            }
                        }
                    }).show();

        }

    }
    public void popularpercval(){
        List<String> nomes = new ArrayList<String>();
        final String[] nome = new String[1];

        nomes.add("R$");
        nomes.add("%");
        //Identifica o Spinner no layout
        //Cria um ArrayAdapter usando um padrao de layout da classe R do android, passando o ArrayList nomes
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nomes);
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        percval.setAdapter(spinnerArrayAdapter);

        //Metodo do Spinner para capturar o item selecionado
        percval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                //pega nome pela posicao
                nome[0] = parent.getItemAtPosition(posicao).toString();
                //imprime um Toast na tela com o nome que foi selecionado
                calc(0);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void popularcbimp(){
        List<String> nomes = new ArrayList<String>();
        final String[] nome = new String[1];

        nomes.add("imp.");
        nomes.add("imp.completa");


        //Identifica o Spinner no layout
        //Cria um ArrayAdapter usando um padrao de layout da classe R do android, passando o ArrayList nomes
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nomes);
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        cbimp.setAdapter(spinnerArrayAdapter);

        //Metodo do Spinner para capturar o item selecionado
        cbimp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                //pega nome pela posicao
                nome[0] = parent.getItemAtPosition(posicao).toString();
                //imprime um Toast na tela com o nome que foi selecionado
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }
    @Override
    public void processFinish(String output) {

    }
}
