package br.com.mobiwork.SCAPrevenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.com.mobiwork.SCAPrevenda.model.Config;
import br.com.mobiwork.SCAPrevenda.model.PreVenda;
import br.com.mobiwork.SCAPrevenda.model.PreVendaItem;
import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;
import br.com.mobiwork.SCAPrevenda.util.DataHoraPedido;

/**
 * Created by LuisGustavo on 09/08/14.
 */
public class DaoPreVendaItem extends DaoCreateDB {

    private Cursor cursor;
    private DaoProduto dp;

    public DaoPreVendaItem(Context context) {
        super(context);
        dp= new DaoProduto(context);
    }

    public Cursor listarItensPrevenda(SQLiteDatabase db,PreVendaItem pvi){
        try{
        this.cursor= db.rawQuery("SELECT * FROM prevendaitem tb " +
                " WHERE tb.codprod = ? and tb.numpre = ? and rtrim(tb.digprod) = ?", new String[]{"" + pvi.getCodprod(), "" + pvi.getNumpre(), "" + pvi.getDigprod().replaceAll("\\s+$", "")});
        }catch (Exception e){
            String erro = e.getMessage();
        }
        return cursor;
    }

    public void excluir (SQLiteDatabase db,PreVendaItem pvi){
        String codprod=String.format("%05d", Integer.parseInt(pvi.getCodprod()));
        try{
        String sql="delete  FROM prevendaitem   WHERE codprod = '"+ String.format("%05d", Integer.parseInt(pvi.getCodprod()))+"' and rtrim(digprod) = '"+ pvi.getDigprod().replaceAll("\\s+$", "")+"' and numpre = '"+pvi.getNumpre()+"'";
            db.execSQL(sql);
        }catch (Exception e){

        }

    }

    public void excluirTodos (SQLiteDatabase db,PreVendaItem pvi){
        try{
            String sql="delete  FROM prevendaitem   WHERE numpre = '"+pvi.getNumpre()+"'";
            db.execSQL(sql);
        }catch (Exception e){

        }

    }

    public Cursor listarPreVendas(SQLiteDatabase db,String pedidoid){
        try{
            this.cursor= db.rawQuery("SELECT * FROM prevendaitem tb " +
                    " WHERE  tb.numpre = ?", new String[]{"" + pedidoid});
        }catch (Exception e){
            String erro = e.getMessage();
        }
        return cursor;
    }



    public PreVendaItem Inserir(SQLiteDatabase dbP,SQLiteDatabase db,PreVendaItem prevendaitem, DataHoraPedido dataHoraMS){
        ContentValues values = new ContentValues();
        Config config = ConfigVendedor.getConfig(dbP);
        dataHoraMS = DataHoraPedido.criarDataHoraPedido();
        String _id =
                String.valueOf(dataHoraMS.getAno()) +
                String.valueOf(dataHoraMS.getMes()) +
                String.valueOf(dataHoraMS.getDia()) +
                String.valueOf(dataHoraMS.getHora()) +
                String.valueOf(dataHoraMS.getMinuto()) +
                String.valueOf(dataHoraMS.getSegundo()) +
                String.valueOf(dataHoraMS.getMilesimos()) +
                String.valueOf(dataHoraMS.getAm_pm());
        PreVendaItem prei = prevendaitem;
        values.put("_id", _id);
        values.put("numpre", prevendaitem.getNumpre());
        values.put("codprod", prevendaitem.getCodprod());
        values.put("digprod",prevendaitem.getDigprod());
        values.put("quantidade", prevendaitem.getQuantidade());
        values.put("valor",prevendaitem.getValor());
        values.put("precoTb", prevendaitem.getPrecoTb());
        values.put("descricao",prevendaitem.getDescricao());
        values.put("desconto",prevendaitem.getDesconto());
        values.put("tipo",prevendaitem.getTipo());
        values.put("unidade",prevendaitem.getUnidade());

        try{
            long i= insert(db, "prevendaitem", "", values);
            if(i==-1){
                String sqlup= ("INSERT INTO prevendaitem (_id,numpre,codprod,digprod,quantidade,valor,precoTb,descricao,desconto,tipo,unidade) values " +
                        "('"+_id+"','"+prevendaitem.getNumpre()+"','"+prevendaitem.getCodprod()+"','"+prevendaitem.getDigprod()+"',"+prevendaitem.getQuantidade()+"" +
                        ","+prevendaitem.getValor()+","+prevendaitem.getPrecoTb()+",'"+prevendaitem.getDescricao()+"','"+prevendaitem.getDesconto()+"','"+prevendaitem.getTipo()+"', '"+prevendaitem.getUnidade()+"')");
                db.execSQL(sqlup);
            }
        }catch(Exception e){
            String erro=e.getMessage();
        }
        return prei;
    }
    public  boolean atualizar(SQLiteDatabase db,PreVendaItem prevendaitem) {
        boolean ret;
        try{
            String sqlup=("UPDATE prevendaitem SET quantidade = "+prevendaitem.getQuantidade()+", valor="+prevendaitem.getValor()+", tipo='"+prevendaitem.getTipo()+"', desconto="+prevendaitem.getDesconto()+""+
                    " WHERE numpre = '"+prevendaitem.getNumpre()+"' and codprod='"+prevendaitem.getCodprod()+"' and rtrim(digprod)='"+prevendaitem.getDigprod().replaceAll("\\s+$", "")+"'");
            db.execSQL(sqlup);
            ret=true;
        }catch(Exception e){
          String erro =e.getMessage();
          ret=false;
        }
        return ret;
    }
    public  void alterarvalores(SQLiteDatabase db,SQLiteDatabase dbP,PreVenda prevenda,String cond) {
        boolean ret = false;
        double valor=0;
        Cursor c =listarPreVendas(db,prevenda.getNumero());
        Cursor prod;
        if(c.moveToFirst()){
           do{
              prod=dp.proCodigo(dbP,c.getString(c.getColumnIndex("codprod")),c.getString(c.getColumnIndex("digprod")));
               if(prod.moveToFirst()){
                 /*   if(cond.equals("1")){
                        valor =prod.getDouble(prod.getColumnIndex("proprvenda2"));
                    }else{*/
                   valor=prod.getDouble(prod.getColumnIndex("proprvenda1"));

                   String codprod=c.getString(c.getColumnIndex("codprod"));
                    try{
                        String sqlup=("UPDATE prevendaitem SET valor="+valor+" " +
                                " WHERE numpre = '"+prevenda.getNumero()+"' and codprod='"+codprod+"' ");
                        db.execSQL(sqlup);
                        ret=true;
                    }catch(Exception e){
                        String erro =e.getMessage();
                        ret=false;
                    }
                 //   return ret;
               }
           }while(c.moveToNext());
        }

    }
}
