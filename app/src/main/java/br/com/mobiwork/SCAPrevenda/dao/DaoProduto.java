package br.com.mobiwork.SCAPrevenda.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import br.com.mobiwork.SCAPrevenda.model.Config;

/**
 * Created by LuisGustavo on 09/08/14.
 */
public class DaoProduto extends DaoCreateDBP {

    private Cursor cursor;

    public DaoProduto(Context context) {
        super(context);
    }


    public Cursor listarProduto(SQLiteDatabase db,String whereFiltro, String[] selectArgs){

        try{
         cursor = db.rawQuery("SELECT * FROM produto WHERE " + whereFiltro +" prodescri LIKE ? or probarra like ? or procodigo like ? order by prodescri",  selectArgs);

        }catch(Exception e){
            String erro = e.getMessage();
        }
        return cursor;
    }

    public Cursor procCodBarra(SQLiteDatabase db,String codBarra){

        try{
            cursor = db.rawQuery("SELECT * FROM produto WHERE  probarra='" + codBarra +"'",null );

        }catch(Exception e){
            String erro = e.getMessage();
        }
        return cursor;
    }
    public Cursor proCodigo(SQLiteDatabase db,String codigo,String digito){

        try{
            cursor = db.rawQuery("SELECT * FROM produto WHERE  procodigo='" + codigo +"' and rtrim(prodigito)='" + digito.replaceAll("\\s+$", "") +"'",null );

        }catch(Exception e){
            String erro = e.getMessage();
        }
        return cursor;
    }
    public Cursor proCodigo(SQLiteDatabase db,String codigo){

        try{
            cursor = db.rawQuery("SELECT * FROM produto WHERE  procodigo='" + codigo +"'",null );

        }catch(Exception e){
            String erro = e.getMessage();
        }
        return cursor;
    }

}
