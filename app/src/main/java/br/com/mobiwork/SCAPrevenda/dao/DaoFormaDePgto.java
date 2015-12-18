package br.com.mobiwork.SCAPrevenda.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by LuisGustavo on 03/09/14.
 */
public class DaoFormaDePgto extends DaoCreateDBP {

    private Cursor cursor;

    public DaoFormaDePgto(Context context) {
        super(context);
    }

    public Cursor listarForma (SQLiteDatabase db){
        try{
            cursor = db.rawQuery("SELECT * FROM formaDePgto" ,  null);
        }catch (Exception e){
            String erro= e.getMessage();
        }
        return cursor;
    }

}
