package br.com.mobiwork.SCAPrevenda.dao;

/**
 * Created by LuisGustavo on 06/08/14.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import br.com.mobiwork.SCAPrevenda.R;
import br.com.mobiwork.SCAPrevenda.util.AtuTabela;


public class DaoCreateDBEnvio extends SQLiteOpenHelper {
    private static final String NOME_BD = "ScaprevendaDBEnvio";
    private static final int VERSAO_BD = 10;
    private static final String LOG_TAG = "ScaprevendaDBEnvio";
    private  Context contexto;
    private AtuTabela atu;

    public DaoCreateDBEnvio(Context context) {
        super(context, NOME_BD, null, VERSAO_BD);
        this.contexto = context;
        atu = new AtuTabela();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.beginTransaction();

        try
        {
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_prevenda).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_prevendaitem).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_loteEnvio).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_ponto).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_vendatual).split("\n"));
            db.setTransactionSuccessful();

        }
        catch (SQLException e)
        {
            Log.e("Err ao criar as tabelas", e.toString());
        }
        finally
        {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        atualizaDBSQL(db,oldVersion,newVersion);
    }

    private void ExecutarComandosSQL(SQLiteDatabase db, String[] sql)
    {
        for( String s : sql )
            if (s.trim().length()>0)
                db.execSQL(s);
    }

    private void atualizaDBSQL(SQLiteDatabase db,int oldVsDB, int newVsDB)
    {
      db.beginTransaction();

        try
        {
            if (oldVsDB<1&&newVsDB>=1) {
                ExecutarComandosSQL(db, contexto.getString(R.string.tabela_ponto).split("\n"));
            }
            if (oldVsDB<2&&newVsDB>=2) {
                ExecutarComandosSQL(db, contexto.getString(R.string.tabela_vendatual).split("\n"));
            }
            if (oldVsDB<5&&newVsDB>=5) {
                if(!atu.verificar(db,"prevenda","prstatus")) {
                    ExecutarComandosSQL(db, contexto.getString(R.string.atualiza_bd_vs3_1).split("\n"));
                }
            }
            if (oldVsDB<6&&newVsDB>=6) {
                if(!atu.verificar(db,"prevendaitem","desconto")) {
                    ExecutarComandosSQL(db, contexto.getString(R.string.atualiza_bd_vs4_3).split("\n"));
                }
            }
            if (oldVsDB<7&&newVsDB>=7) {
                if(!atu.verificar(db,"prevenda","nomeCliente")) {
                    ExecutarComandosSQL(db, contexto.getString(R.string.atualiza_bd_vs7_1).split("\n"));
                }
            }
            if (oldVsDB<8&&newVsDB>=8) {
                if(!atu.verificar(db,"prevenda","idsmart")) {
                    ExecutarComandosSQL(db, contexto.getString(R.string.atualiza_bd_vs8_1).split("\n"));
                }
            }
            if (oldVsDB<9&&newVsDB>=9) {
                if(!atu.verificar(db,"prevendaitem","tipo")) {
                    ExecutarComandosSQL(db, contexto.getString(R.string.atualiza_bd_vs9_1).split("\n"));
                }
            }
            if (oldVsDB<10&&newVsDB>=10) {
                if(!atu.verificar(db,"prevendaitem","unidade")) {
                    ExecutarComandosSQL(db, contexto.getString(R.string.atualiza_bd_vs10_1).split("\n"));
                }
            }
            db.setTransactionSuccessful();
            return;
        }
        catch (SQLException localSQLException)
        {
           throw localSQLException;
        }
        finally
        {
            db.endTransaction();
        }
    }

    public long insert(SQLiteDatabase db,String table, String nullColumnHack, ContentValues values) {
        return db.insert(table, nullColumnHack, values);
    }

    public static int update(SQLiteDatabase db,String table, ContentValues values, String whereClause, String[] whereArgs){
        return db.update(table, values, whereClause, whereArgs);
    }

    public static int delete(SQLiteDatabase db,String table, String whereClause, String[] whereArgs){
        return db.delete(table, whereClause, whereArgs);
    }

}

