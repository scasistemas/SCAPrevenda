package br.com.mobiwork.SCAPrevenda.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.mobiwork.SCAPrevenda.R;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDB;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;
import br.com.mobiwork.SCAPrevenda.dao.DaoPreVenda;
import br.com.mobiwork.SCAPrevenda.dao.DaoPreVendaItem;
import br.com.mobiwork.SCAPrevenda.dao.DaoTabCondi;
import br.com.mobiwork.SCAPrevenda.model.Config;
import br.com.mobiwork.SCAPrevenda.model.PreVendaItem;
import br.com.mobiwork.SCAPrevenda.pedido.IncluirPedido;
import br.com.mobiwork.SCAPrevenda.pedido.pedidovendateste;
import br.com.mobiwork.SCAPrevenda.util.BigDecimalRound;

/**
 * Created by LuisGustavo on 29/08/14.
 */
public class BaseAdapterHashMap extends BaseAdapter   {

    private Context context;
    private Cursor c,c2;
    DateFormat formatter;
    Date d ;
    private int _id;
    private String _id2;
    ArrayList<Integer> p= new ArrayList<Integer>();
    ArrayList<String> p2= new ArrayList<String>();
    String sit,filtro;
    ListView le ;
    List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
    private SQLiteDatabase db,dbP;
    private DaoCreateDBP daoDBP;
    DaoPreVendaItem dpvi;
    PreVendaItem pvi;
    boolean isEditPedidoItem;
    private DaoPreVendaItem dprei;
    private DaoPreVenda dpv;
    private Alertas a;
    private DaoTabCondi dtc;
    private String edit;
    private int prstatus;
    private Config config;

    public BaseAdapterHashMap(Context context,  List<HashMap<String, String>> fillMaps2,ListView l,String ed,int prstatus){
        this.context = context;
        //this.c = rotas;
        this.fillMaps=fillMaps2;
        DaoCreateDB dao = new DaoCreateDB(context);
        dao.close();
        db =  dao.getWritableDatabase();
        d = new Date();
        le=l;
        daoDBP = new DaoCreateDBP(context);
        dbP =  daoDBP.getWritableDatabase();
        dpvi= new DaoPreVendaItem(context);
        pvi= new PreVendaItem();
        dprei= new DaoPreVendaItem(context);
        dpv= new DaoPreVenda(context);
        a= new Alertas(context);
        dtc = new DaoTabCondi(context);
        le =l;
        edit=ed;
        this.prstatus=prstatus;
        config=ConfigVendedor.getConfig(dbP);


    }




    @Override
    public int getCount() {
        return fillMaps.size();
    }

    @Override
    public Object getItem(int position) {

        return this.p.get(position);


    }


    @Override
    public long getItemId(int position) {

        return this.p.get(position);


    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub

        int t = position;
    }


    public View getView(final int position,  View convertView, ViewGroup parent) {


        View convertView2 = null;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.itemvenda, null);
        }


        boolean r=false;

       // this.p.add(position,_id);
        if(p.isEmpty()){
            this.p.add(position,_id);
        }else{
            if(position==p.size()){
                this.p.add(position,_id);
            }
        }
        this._id=Integer.parseInt(fillMaps.get(position).get("codprod"));
        this.p.add(position, Integer.parseInt(fillMaps.get(position).get("codprod")));

        boolean er=false;
        TextView descricao = (TextView) convertView.findViewById(R.id.descricao);
        if (config.getEmp().equalsIgnoreCase("jrc")) {
            descricao.setText(fillMaps.get(position).get("codprod") + "- " + fillMaps.get(position).get("descricao").replaceAll("\\s+$", "") );
        }else{
            descricao.setText(fillMaps.get(position).get("codprod") + "- " + fillMaps.get(position).get("descricao").replaceAll("\\s+$", "") + "/ G: " + (fillMaps.get(position).get("digprod")).replaceAll("\\s+$", ""));
        }
        TextView qtd = (TextView) convertView.findViewById(R.id.quantidade);
        qtd.setText(String.valueOf(BigDecimalRound.Round(Double.parseDouble(fillMaps.get(position).get("quantidade")), 3)));

        TextView preco = (TextView) convertView.findViewById(R.id.preco);
        preco.setText(String.valueOf(BigDecimalRound.Round(Double.parseDouble(fillMaps.get(position).get("valor")),3)));

        TextView precorotal = (TextView) convertView.findViewById(R.id.precoTotal);
        precorotal.setText(String.valueOf(BigDecimalRound.Round(Double.parseDouble(fillMaps.get(position).get("valor")) * Double.parseDouble(fillMaps.get(position).get("quantidade")), 2)));


        Button op= (Button) convertView.findViewById(R.id.op);
        if(edit.equalsIgnoreCase("prevenda")&&prstatus==1){
            op.setVisibility(View.GONE);
        }else{
            op.setVisibility(View.VISIBLE);
        }


        op.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                new AlertDialog.Builder(context)
                        .setItems(R.array.op_alerta_editar_excluir_item, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int k) {
                                if (k == 0) {
                                    Object j = getItem(position);
                                    Activity aa = (Activity) context;
                                    Intent i = new Intent(aa, IncluirPedido.class);
                                    pvi.setCodprod(String.format("%05d", Integer.parseInt(fillMaps.get(position).get("codprod"))));
                                    pvi.setNumpre(fillMaps.get(position).get("numpre"));
                                    pvi.setDigprod(fillMaps.get(position).get("digprod"));

                                    c = dprei.listarItensPrevenda(db, pvi);
                                    if (c.moveToFirst()) {
                                        pvi.setPreVendaItem(c);
                                        isEditPedidoItem = true;
                                        i.putExtra("edit", "edit");
                                    } else {
                                        i.putExtra("edit", "novo");
                                    }
                                    i.putExtra("DESCRI", pvi.getDescricao());
                                    i.putExtra("CODPRODUTO", pvi.getCodprod());
                                    i.putExtra("tbPrecoId", pvi.getPrecoTb());
                                    i.putExtra("pedidoItemId", pvi.getNumpre());
                                    i.putExtra("quantidade", String.valueOf(pvi.getQuantidade()));
                                    i.putExtra("precoTb", pvi.getPrecoTb());
                                    i.putExtra("digito", pvi.getDigprod());
                                    i.putExtra("tipo", pvi.getTipo());
                                    i.putExtra("barra", "");
                                    i.putExtra("condicao", String.valueOf("1"));
                                    i.putExtra("valor", String.valueOf(pvi.getValor()));
                                    aa.startActivityForResult(i, 0);
                                } else {
                                    final int[] resp = {0};
                                    new AlertDialog.Builder(context)
                                            .setTitle("Deseja excluir o item do pedido?")
                                            .setItems(R.array.op_alerta_sim_nao, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialoginterface, int i) {
                                                    if (i == 0) {
                                                        Object j = getItem(position);
                                                        pvi.setCodprod(fillMaps.get(position).get("codprod"));
                                                        pvi.setNumpre(fillMaps.get(position).get("numpre"));
                                                        pvi.setDigprod(fillMaps.get(position).get("digprod"));
                                                        dprei.excluir(db, pvi);
                                                        DaoCreateDB dao = new DaoCreateDB(context);
                                                        db = dao.getWritableDatabase();
                                                        Cursor temp = dpv.buscarPreVenda(db, fillMaps.get(position).get("numpre"));
                                                        double totbruto = 0;
                                                        double total = 0;
                                                        if (temp.moveToFirst()) {
                                                            totbruto = BigDecimalRound.Round(dpv.somarPreVendas(db, fillMaps.get(position).get("numpre")));
                                                            double desc = temp.getDouble(temp.getColumnIndex("desc"));
                                                            double totdesc = totbruto * temp.getDouble(temp.getColumnIndex("desc")) / 100;
                                                            ;
                                                            //    total=totbruto-totbruto*temp.getDouble(temp.getColumnIndex("desc"))/100;
                                                            total = totbruto - temp.getDouble(temp.getColumnIndex("desc"));
                                                        }
                                                        // popularCombo(total);
                                                        pedidovendateste.txvvrTotalPedido.setText(String.valueOf(BigDecimalRound.Round(total, 2)));
                                                        pedidovendateste.txvalorbruto.setText(String.valueOf(BigDecimalRound.Round(dpv.somarPreVendas(db, fillMaps.get(position).get("numpre")), 2)));
                                                        fillMaps.remove(position);
                                                        notifyDataSetChanged();
                                                    }
                                                }
                                            }).show();
                                }
                            }
                        }).show();


            }
        });
        double resto=position%2;
        int d=_id;
        if(position%2>0){
            convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg2));
        }else{
            convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg3));
        }

        return convertView;
    }


    public void popularCombo(double total){
        pedidovendateste.ccond=dtc.listarCondi(dbP,total);
        if(pedidovendateste.ccond.moveToFirst()){
            pedidovendateste.adapter = new SimpleCursorAdapter(context,R.layout.spinner_item,pedidovendateste.ccond,new String[] {"tabdescri"},
                    new int[] {R.id.comboName});
            pedidovendateste.cond.setAdapter(pedidovendateste.adapter);
        }

        int x=0;
        if(pedidovendateste.pv.getCondicao()!=null){
            if (pedidovendateste.ccond.moveToFirst()) {
                String _id=pedidovendateste.ccond.getString(pedidovendateste.ccond.getColumnIndex("_id"));
                do {
                    if (pedidovendateste.pv.getCondicao().equals(pedidovendateste.ccond.getString(pedidovendateste.ccond.getColumnIndex("_id")))) {
                        pedidovendateste.cond.setSelection(x)
                        ;
                        break;
                    }
                    x=x+1;

                } while (pedidovendateste.ccond.moveToNext());
            }
        }


    }





}
