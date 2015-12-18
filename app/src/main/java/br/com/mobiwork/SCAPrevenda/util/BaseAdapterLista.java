package br.com.mobiwork.SCAPrevenda.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import br.com.mobiwork.SCAPrevenda.R;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDB;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;
import br.com.mobiwork.SCAPrevenda.dao.DaoPreVendaItem;
import br.com.mobiwork.SCAPrevenda.dao.DaoProduto;
import br.com.mobiwork.SCAPrevenda.model.PreVendaItem;
import br.com.mobiwork.SCAPrevenda.pedido.IncluirPedido;
import br.com.mobiwork.SCAPrevenda.pedido.pedidovendateste;

import static br.com.mobiwork.SCAPrevenda.pedido.pedidovendateste.*;

/**
 * Created by LuisGustavo on 28/08/14.
 */
public class BaseAdapterLista extends BaseAdapter {

    private Context context;
    private Cursor c,c2;
    DateFormat formatter;
    Date d ;
    private int _id;
    ArrayList<Integer> p= new ArrayList<Integer>();
    String sit;
    ListView le ;
    private SQLiteDatabase db,dbP;
    private DaoCreateDBP daoDBP;
    DaoPreVendaItem dpvi;
    PreVendaItem pvi;
    boolean isEditPedidoItem;
    private DaoPreVendaItem dprei;
    String cond;
    private DaoProduto daoProd;
    Cursor ce;
    public BaseAdapterLista(Context context, Cursor rotas,ListView l,String condicao){
        this.context = context;
        this.c = rotas;
        DaoCreateDB dao = new DaoCreateDB(context);
        db =  dao.getWritableDatabase();
        formatter= new SimpleDateFormat("dd/MM/yyyy");
        d = new Date();
        le=l;
        ce=rotas;
        daoDBP = new DaoCreateDBP(context);
        dbP =  daoDBP.getWritableDatabase();
        dpvi= new DaoPreVendaItem(context);
        pvi= new PreVendaItem();
        dprei= new DaoPreVendaItem(context);
        cond=condicao;
        daoProd= new DaoProduto(context);

    }


    @Override
    public int getCount() {
        return c.getCount();
    }

    @Override
    public Object getItem(int position) {
        return this.p.get(position);
    }

    @Override
    public long getItemId(int position) {

        return this.p.get(position);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {


        View layout;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listaproduto, null);
        }
        else{
            convertView = convertView;
        }
        boolean r=false;
        this.c.moveToPosition(position);
        this._id=Integer.parseInt(c.getString(c.getColumnIndex("procodigo")));
    //  this.p.add(position,_id);
        if(p.isEmpty()){
            this.p.add(position,_id);
        }else{
            if(position==p.size()){
                this.p.add(position,_id);
            }
        }


        String nomeCli=c.getString(c.getColumnIndex("prodescri"));
        TextView descricao = (TextView) convertView.findViewById(R.id.produto);
        descricao.setText(c.getString(c.getColumnIndex("prodescri")));



        convertView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Activity aa = (Activity)context;
                Object j= getItem(position);

                Intent i = new Intent(aa,IncluirPedido.class);
                Cursor c2= daoProd.proCodigo(dbP, j.toString());
                if(c2.moveToFirst()){
                    pvi.setCodprod(c2.getString(c2.getColumnIndex("procodigo")));
                    pvi.setDescricao(c2.getString(c2.getColumnIndex("prodescri")));

                }else{
                    pvi.setCodprod("");
                }
                Cursor c = dprei.listarItensPrevenda(db,pvi);

                if (c.moveToFirst()) {
                    pvi.setPreVendaItem(c);
                    i.putExtra("edit","edit");
                }else{
                    i.putExtra("edit","novo");
                }

                i.putExtra("DESCRI", pvi.getDescricao());
                i.putExtra("CODPRODUTO", pvi.getCodprod());
                i.putExtra("tbPrecoId",pvi.getPrecoTb());
                i.putExtra("pedidoItemId",pvi.getNumpre());
                i.putExtra("quantidade",String.valueOf(pvi.getQuantidade()));
                i.putExtra("precoTb",pvi.getPrecoTb());
                i.putExtra("digito",pvi.getDigprod());
                i.putExtra("barra",c2.getString(c2.getColumnIndex("probarra")));
                i.putExtra("condicao","");
                i.putExtra("valor",String.valueOf(pvi.getValor()));
                aa.startActivityForResult(i, 0);

            }
        });



        return convertView;
    }
}
