package br.com.mobiwork.SCAPrevenda.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by LuisGustavo on 09/08/14.
 */
public class DaoTabCondi extends DaoCreateDBP {

    private Cursor cursor;

    public DaoTabCondi(Context context) {
        super(context);
    }

    public Cursor listarCondi(SQLiteDatabase db,Double valor){

        String where="";

            where ="where tconrvenc<="+valor;
        try{
       // cursor = db.rawQuery("SELECT * FROM tabcondi "+where+" order by tabcondpag desc " ,  null);
            cursor = db.rawQuery("SELECT * FROM tabcondi  " ,  null);
        }catch (Exception e){
           String erro= e.getMessage();
        }
        return cursor;
    }



}
