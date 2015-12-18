package br.com.mobiwork.SCAPrevenda.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import br.com.mobiwork.SCAPrevenda.model.Vendedor;

/**
 * Created by LuisGustavo on 16/04/2015.
 */
public class DaoVendedor extends DaoCreateDBP {


    public DaoVendedor(Context context) {
        super(context);

    }

    public Vendedor login(Vendedor v,Context context){

        SQLiteDatabase dbP;
        DaoCreateDBP daoDBP;
        daoDBP= new DaoCreateDBP(context);
        dbP =  daoDBP.getWritableDatabase();
       Cursor cursor = dbP.rawQuery("SELECT *  FROM vendedor WHERE  rtrim(vennome)= '"+v.getVennome()+"' and vensenha='"+v.getVensenha()+"'",
                null);

       if(cursor.moveToFirst()){
           v.setVencodigo(cursor.getInt(cursor.getColumnIndex("vencodigo")));
       }else{
           v.setVencodigo(-1);
       }
        dbP.close();

        return v;
    }


}
