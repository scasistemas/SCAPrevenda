package br.com.mobiwork.SCAPrevenda.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by LuisGustavo on 10/09/2015.
 */
public class AtuTabela {
    public boolean verificar(SQLiteDatabase db,String tabela, String atu) {
        Cursor c = db.rawQuery("SELECT * FROM "+tabela , null);
        boolean verifiq=false;
        if(c!=null){
            for (int i = 0; i < c.getColumnCount(); i++) {
                if (c.getColumnName(i).equalsIgnoreCase(atu)) {
                    verifiq=true;
                }
            }
        }
        return verifiq;
    }
}
