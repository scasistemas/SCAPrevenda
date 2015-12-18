package br.com.mobiwork.SCAPrevenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import br.com.mobiwork.SCAPrevenda.model.Config;
import br.com.mobiwork.SCAPrevenda.model.PreVenda;
import br.com.mobiwork.SCAPrevenda.model.PreVendaItem;
import br.com.mobiwork.SCAPrevenda.model.VendAtual;
import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;
import br.com.mobiwork.SCAPrevenda.util.DataHoraPedido;
import br.com.mobiwork.SCAPrevenda.util.SharedPreferencesUtil;

/**
 * Created by LuisGustavo on 09/08/14.
 */
public class DaoPreVenda extends DaoCreateDB {

    private Cursor cursor;

    public DaoPreVenda(Context context) {
        super(context);
    }

    public int proxnumped(SQLiteDatabase db){
        int proxnumped=0;

        try{
            cursor = db.rawQuery("SELECT max(numped)as maximo FROM prevenda" ,  null);
        }catch (Exception e){
            String erro= e.getMessage();
        }
        if(cursor.moveToFirst()){
            proxnumped=cursor.getInt(cursor.getColumnIndex("maximo"));
        }else{
            proxnumped=0;
        }
        return proxnumped+1;
    }

    public void excluir (SQLiteDatabase db,PreVenda pv){
        try{
            String sql="delete  FROM prevenda   WHERE  numero = '"+pv.getNumero()+"'";
            db.execSQL(sql);
        }catch (Exception e){

        }

    }

    public PreVenda Inserir(SQLiteDatabase dbP,SQLiteDatabase db,PreVenda prevenda, DataHoraPedido dataHoraMS,Context context){
        ContentValues values = new ContentValues();
        Config config= new DaoConfig(context).consultar(dbP);
        SharedPreferencesUtil sdu = new SharedPreferencesUtil(context);
       int vendid=0;
        if((config.getEmp().equalsIgnoreCase("vitoria")&&config.getVendid()!=633)){
            //UM VENDEDOR
           vendid= ConfigVendedor.getConfig(dbP).getVendid();
        }else if(config.getEmp().equalsIgnoreCase("jrc")){
            VendAtual va;
            DaoVendAtual dva= new DaoVendAtual(context);
            va=dva.getVendAtual(context);
            vendid=va.getCodigovend();
        }

        String _id =vendid +
                String.valueOf(dataHoraMS.getAno()) +
                String.valueOf(dataHoraMS.getMes()) +
                String.valueOf(dataHoraMS.getDia()) +
                String.valueOf(dataHoraMS.getHora()) +
                String.valueOf(dataHoraMS.getMinuto()) +
                String.valueOf(dataHoraMS.getSegundo()) +
                String.valueOf(dataHoraMS.getMilesimos()) +
                String.valueOf(dataHoraMS.getAm_pm());
        PreVenda pre = prevenda;
        pre.set_id(_id);
        values.put("_id", _id);
        values.put("numped", pre.getNumped());
        values.put("numero", pre.getNumero());
        values.put("condicao",pre.getCondicao());
        values.put("valorbruto", pre.getValorbruto());
        values.put("valortotal",pre.getValortotal());
        values.put("forma",pre.getForma());
        values.put("nomeCliente",pre.getNomeCliente());
        values.put("desc",0);
        values.put("data", dataHoraMS.getAno()+"-"+String.format("%02d", dataHoraMS.getMes())+"-"+String.format("%02d", dataHoraMS.getDia()));
        values.put("hora", dataHoraMS.getHora()+":"+dataHoraMS.getMinuto()+":"+dataHoraMS.getSegundo());
        values.put("status", 0);
        values.put("imp", 0);
        values.put("obs","");
        values.put("parc",0);
        values.put("nparc",0);
        values.put("idsmart",sdu.getIdSmart());

        try{
         long i=   insert(db, "prevenda", "", values);
            int total=  prevenda(db);
            String e="";

        }catch(Exception e){
            String erro=e.getMessage();
        }
        return pre;
    }

    public double somarPreVendas(SQLiteDatabase db,String pedidoid){
       double total =0;
        cursor=null;

        try{
            this.cursor= db.rawQuery("SELECT codprod,valor,quantidade FROM prevendaitem tb " +
                    " WHERE  tb.numpre = ?", new String[]{"" + pedidoid});
        }catch (Exception e){
            String erro = e.getMessage();
        }


        if(cursor.moveToFirst()){
            do{
                total=total+(cursor.getDouble(cursor.getColumnIndex("valor"))*cursor.getDouble(cursor.getColumnIndex("quantidade")));
            }while(cursor.moveToNext());
        }
       return total;
    }


    public static boolean atualizar(SQLiteDatabase db,PreVenda pre, DataHoraPedido dataHoraMS) {

        ContentValues values = new ContentValues();
        values.put("_id", pre.get_id());
        values.put("numped", pre.getNumped());
        values.put("numero", pre.getNumero());
        values.put("condicao",pre.getCondicao());
        values.put("valorbruto", pre.getValorbruto());
        values.put("forma",pre.getForma());
        values.put("valortotal",pre.getValortotal());
        values.put("data",dataHoraMS.getAno()+"-"+String.format("%02d", dataHoraMS.getMes())+"-"+String.format("%02d", dataHoraMS.getDia()));
        values.put("hora", dataHoraMS.getHora()+":"+dataHoraMS.getMinuto()+":"+dataHoraMS.getSegundo());
        values.put("loteenvio",pre.getLoteenvio());
        try{
            String sqlup=("update prevenda set hora='"+dataHoraMS.getHora()+":"+dataHoraMS.getMinuto()+":"+dataHoraMS.getSegundo()+"', _id='"+pre.get_id()+"', valortotal="+pre.getValortotal()+", numped="+pre.getNumped()+", " +
                    "data='"+dataHoraMS.getAno()+"-"+String.format("%02d", dataHoraMS.getMes())+"-"+String.format("%02d", dataHoraMS.getDia())+"'," +
                    " valorbruto=+"+pre.getValorbruto()+", condicao="+pre.getCondicao()+", loteenvio='"+pre.getLoteenvio()+"',nomeCliente='"+pre.getNomeCliente()+"', forma="+pre.getForma()+", numero='"+pre.getNumero()+"' where _id='"+pre.get_id()+"'");

           db.execSQL(sqlup);
           return true;

        }catch(Exception e){
          String erro=e.getMessage();
            return false;
        }

    }

    public Cursor pedenvi(SQLiteDatabase db,String pesq){
        String where="";
        if(!pesq.equals("")) {
            where = "and  (nomeCliente LIKE '"+ pesq+"%' or numero like '%"+pesq+"%')";
        }
        try {
            cursor = db.rawQuery("SELECT _id,numped,status, numero,hora,condicao,valorTotal, strftime('%d/%m/%Y',data) as data, nomeCliente  FROM prevenda WHERE  status =1 " + where + " order by numero desc ",
                    null);
        }catch(Exception e){
                String erro =e.getMessage();
            }

        return cursor;
    }

    public Cursor pedenvi(SQLiteDatabase db,String pesq,VendAtual va){
        String where="";
        if(!pesq.equals("")) {
            where = "and  (nomeCliente LIKE '"+ pesq+"%' or numero like '%"+pesq+"%')";
        }
        try {
            cursor = db.rawQuery("SELECT _id,numped,status, numero,hora,condicao,valorTotal, strftime('%d/%m/%Y',data) as data, nomeCliente  FROM prevenda WHERE  status =1  and numero like '"+String.format("%04d", va.getCodigovend())+"%' " + where + " order by numero desc ",
                    null);
        }catch(Exception e){
            String erro =e.getMessage();
        }

        return cursor;
    }

    public Cursor orcenvi(SQLiteDatabase db,String pesq){
        String where="";

        if(!pesq.equals("")) {
            where = "and  (nomeCliente LIKE '"+ pesq+"%' or numero like '%"+pesq+"%')";
        }
        try {
            cursor = db.rawQuery("SELECT _id,numped,status, numero,hora,condicao,valorTotal, strftime('%d/%m/%Y',data) as data,nomeCliente  FROM prevenda WHERE    status=0 " + where + " order by numero desc ",
                    null);
        }catch (Exception e){

        }
        return cursor;
    }

    public Cursor orcenvi(SQLiteDatabase db,String pesq,VendAtual va){
        String where="";
        va.getCodigovend();
        if(!pesq.equals("")) {
            where = "and  (nomeCliente LIKE '"+ pesq+"%' or numero like '%"+pesq+"%')";
        }
        try {
            cursor = db.rawQuery("SELECT _id,numped,status, numero,hora,condicao,valorTotal, strftime('%d/%m/%Y',data) as data,nomeCliente  FROM prevenda WHERE    status=0 and numero like '"+String.format("%04d", va.getCodigovend())+"%' " + where + " order by numero desc ",
                    null);
        }catch (Exception e){
            String r=e.getMessage();
        }
        return cursor;
    }




    public Cursor pedidosporlote(SQLiteDatabase db,String id){
        cursor = db.rawQuery("select * from prevenda WHERE loteenvio <>'null' ",
               null);

        return cursor;
    }

    public Cursor buscarPreVenda(SQLiteDatabase db,String pedidoid){
        try{
            this.cursor= db.rawQuery("SELECT * FROM prevenda tb " +
                    " WHERE  tb.numero = ?", new String[]{"" + pedidoid});
        }catch (Exception e){
            String erro = e.getMessage();
        }
        return cursor;
    }
    public void terminaCad(SQLiteDatabase db,PreVenda pre){
        double valor=0;
        try{
            String sqlup=(
                    "UPDATE prevenda SET obs = '"+pre.getObs()+"', condicao='"+pre.getCondicao()+"', forma='"+pre.getForma()+"', nparc='"+pre.getNparc()+"'," +
                    " imp ="+pre.getImp()+", status='"+pre.getStatus()+"',parc='"+pre.getParc()+"',desc='"+pre.getDesc()+"', nomeCliente='"+pre.getNomeCliente()+"', idsmart="+pre.getIdsmart()+", valortotal ='"+pre.getValortotal()+"'" +
                    "WHERE numero = '"+pre.getNumero()+"'");
            db.execSQL(sqlup);
        }catch(Exception e){
            String erro = e.getMessage();
        }
    }
    public void terminaCadBruto(SQLiteDatabase db,PreVenda pre){
        double valor=0;
        try{
            String sqlup=("UPDATE prevenda SET valorbruto = '"+pre.getValorbruto()+"' WHERE numero = '"+pre.getNumero()+"'");
            db.execSQL(sqlup);
        }catch(Exception e){
            String erro = e.getMessage();
        }
    }
    public void altstatusPrevenda(SQLiteDatabase db,PreVenda pre,int status){
        double valor=0;
        try{
            String sqlup=("UPDATE prevenda SET prstatus = "+status+" WHERE numero = '"+pre.getNumero()+"'");
            db.execSQL(sqlup);
        }catch(Exception e){
            String erro = e.getMessage();
        }
    }

    public int prevenda(SQLiteDatabase db){
        double valor=0;
        try{
            this.cursor= db.rawQuery("select count(*) as num from prevenda WHERE loteenvio <>'null' ",null);
            if(cursor.moveToFirst()){
                return cursor.getInt(cursor.getColumnIndex("num"));
            }
        }catch(Exception e){
            String erro = e.getMessage();
        }
        return 0;
    }


    public void alterarimp(SQLiteDatabase db,PreVenda pre){
        double valor=0;
        try{
            String sqlup=("UPDATE prevenda SET  imp ="+pre.getImp()+" WHERE numero = '"+pre.getNumero()+"'");
            db.execSQL(sqlup);
        }catch(Exception e){
            String erro = e.getMessage();
        }
    }
    public int getprstatus(SQLiteDatabase db,PreVenda pre){

        try{
            cursor = db.rawQuery("select prstatus from prevenda WHERE numero = "+pre.getNumero()+"",
                    null);
            if(cursor.moveToFirst()){
                return cursor.getInt(cursor.getColumnIndex("prstatus"));
            }
        }catch(Exception e){
            String erro = e.getMessage();
        }
        return 0;
    }

}
