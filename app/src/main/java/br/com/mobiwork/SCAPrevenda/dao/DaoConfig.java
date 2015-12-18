package br.com.mobiwork.SCAPrevenda.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import br.com.mobiwork.SCAPrevenda.model.Config;
import br.com.mobiwork.SCAPrevenda.model.Vendedor;

/**
 * Created by LuisGustavo on 07/08/14.
 */
public class DaoConfig extends DaoCreateDBP {
    private Cursor cursor;

    public DaoConfig(Context context) {
        super(context);
    }


    public Config consultar(SQLiteDatabase db) {
        Config config = new Config();
        cursor = db.rawQuery("SELECT * FROM config tb " +
                " WHERE tb._id = ?",  new String[]{"1"});

        if (cursor.moveToFirst()) {
            config.setConfig(cursor);
        }
        return config;
    }

    public void updateSaldoVerba (SQLiteDatabase db, double saldoverba){
        try{
            String sqlup=("UPDATE config SET saldoverba = "+saldoverba+" WHERE _id='1'");
            db.execSQL(sqlup);
        }catch(Exception e){
        }
    }

    public void update (SQLiteDatabase db, String endereco,String login,String senha){
        try{
            String sqlup=("UPDATE config SET endereco = '"+endereco+"' , login='"+login+"' , senha='"+senha+"'  WHERE _id='1'");
            db.execSQL(sqlup);
        }catch(Exception e){
            String erro= e.getMessage();
        }
    }

    public void updateOrc (SQLiteDatabase db,  String endereco,String login,String senha){
        try{
            String sqlup=("UPDATE config SET enderecoorc = '"+endereco+"' , login='"+login+"' , senha='"+senha+"' WHERE _id='1'");
            db.execSQL(sqlup);
        }catch(Exception e){
        }
    }

    public void updateVersaoTabela (SQLiteDatabase db, String versao){
        try{
            String sqlup=("UPDATE config SET versaotabela = "+versao+" WHERE _id='1'");
            db.execSQL(sqlup);
        }catch(Exception e){
        }
    }
    public boolean login(String login ,String senha,Context context){

        SQLiteDatabase dbP;
        DaoCreateDBP daoDBP;
        daoDBP= new DaoCreateDBP(context);
        dbP =  daoDBP.getWritableDatabase();
        Cursor cursor2 = dbP.rawQuery("SELECT *  FROM config ",
                null);
        if(cursor2.moveToFirst()){
            String login2=cursor2.getString(cursor2.getColumnIndex("loginpre"));
            String senhapre2=cursor2.getString(cursor2.getColumnIndex("senhapre"));
            String f ="";


        }

        Cursor cursor = dbP.rawQuery("SELECT *  FROM config WHERE  loginpre= '"+login+"' and senhapre='"+senha+"'",
                null);

        if(cursor.moveToFirst()){
          dbP.close();
          return true;
        }else{
           dbP.close();
           return  false;
        }



    }
}
