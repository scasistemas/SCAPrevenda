package br.com.mobiwork.SCAPrevenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import br.com.mobiwork.SCAPrevenda.model.Config;
import br.com.mobiwork.SCAPrevenda.model.PreVenda;
import br.com.mobiwork.SCAPrevenda.model.VendAtual;
import br.com.mobiwork.SCAPrevenda.model.Vendedor;
import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;
import br.com.mobiwork.SCAPrevenda.util.DataHoraPedido;

/**
 * Created by LuisGustavo on 16/04/2015.
 */
public class DaoVendAtual extends DaoCreateDB {

    public DaoVendAtual(Context context) {
        super(context);


    }

    public int veriinsercao(SQLiteDatabase db){
        Cursor cursor=null;
        int total =0;
        try{
            cursor= db.rawQuery("SELECT count(*) as total from vendatual " , null);
        }catch (Exception e){
            String erro = e.getMessage();
        }
        if(cursor.moveToFirst()){
            do{
                total=(cursor.getInt(cursor.getColumnIndex("total")));
            }while(cursor.moveToNext());
        }
        return total;
    }

    public VendAtual getVendAtual(Context context){
        SQLiteDatabase db;
        DaoCreateDB daoDB;
        daoDB= new DaoCreateDB(context);
        db =  daoDB.getWritableDatabase();
        VendAtual va = new VendAtual();
        Cursor cursor = null ;
        int total =0;
        try{
            cursor= db.rawQuery("SELECT *  from vendatual " , null);
        }catch (Exception e){
            String erro = e.getMessage();
        }
        if(cursor.moveToFirst()){
            va.VendAtual(cursor);
        }
        daoDB.close();
        db.close();
        return va;
    }

    public long inserir(SQLiteDatabase db,VendAtual v){
        long i=2;
        ContentValues values = new ContentValues();
        values.put("_id", 1);
        values.put("codigovend", v.getCodigovend());
        values.put("nomevend", v.getNomevend());

        try{
            i=insert(db, "vendatual", "", values);
        }catch(Exception e){
            String erro=e.getMessage();
        }
        return i;
    }

    public void alteraVendAtual(SQLiteDatabase db,VendAtual v){
        try{
            String sqlup=(
                    "UPDATE vendatual SET codigovend = "+v.getCodigovend()+", nomevend='"+v.getNomevend()+"' WHERE _id = 1");
            db.execSQL(sqlup);
        }catch(Exception e){
            String erro = e.getMessage();
        }
    }

    public void logar (VendAtual v,Context context){
        SQLiteDatabase db;
        DaoCreateDB daoDB;
        daoDB= new DaoCreateDB(context);
        db =  daoDB.getWritableDatabase();
        if(veriinsercao(db)>0){
            alteraVendAtual(db,v);
        }else{
            inserir(db, v);
        }
        db.close();
        daoDB.close();
    }



}
