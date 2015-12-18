package br.com.mobiwork.SCAPrevenda.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by LuisGustavo on 24/09/14.
 */
public class DaoParamCondi  extends DaoCreateDBP {

    private Cursor cursor;

    public DaoParamCondi(Context context) {
        super(context);
    }

    public Cursor listarCondi(SQLiteDatabase db,Double valor){
        double valorminimo=pegarMenorValor(db);

        try{
            cursor = db.rawQuery("SELECT _id,max(pcnumero) as parc FROM paramcondi where pcvalor<= "+valor ,  null);
        }catch (Exception e){
            String erro= e.getMessage();
        }
        return cursor;
    }

    public double pegarMenorValor(SQLiteDatabase db){

        double result=0;
        try{
            cursor = db.rawQuery("SELECT _id,min(pcvalor) as valor FROM paramcondi",  null);
        }catch (Exception e){
            String erro= e.getMessage();
        }

        if(cursor.moveToFirst()){
            result=cursor.getDouble(cursor.getColumnIndex("valor"));
        }
        return result;
    }

}
