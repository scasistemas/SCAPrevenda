package br.com.mobiwork.SCAPrevenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import br.com.mobiwork.SCAPrevenda.R;
import br.com.mobiwork.SCAPrevenda.util.AtuTabela;

/**
 * Created by LuisGustavo on 06/08/14.
 */
public class DaoCreateDBP extends SQLiteOpenHelper {

    private static final String NOME_BD = "scaprevendadbp";
    private static final int VERSAO_BD = 4;
    private static final String LOG_TAG = "scaprevendadbp";
    private Context contexto;
    private AtuTabela atu;

    public DaoCreateDBP(Context context) {
        super(context, NOME_BD, null, VERSAO_BD);
        this.contexto = context;
        atu= new AtuTabela();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.beginTransaction();

        try
        {
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_config).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_produto).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_condi).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_formaDePgto).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_paramcondi).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_vendedor).split("\n"));
            db.setTransactionSuccessful();
        }
        catch (SQLException e)
        {
            Log.e("Erro ao criar as tabelas e testar os dados", e.toString());
        }
        finally
        {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String teste;
        //atualizaDBSQL(db,oldVersion,newVersion);
    }

    private void ExecutarComandosSQL(SQLiteDatabase db, String[] sql)
    {
        for( String s : sql )
            if (s.trim().length()>0)
                db.execSQL(s);
    }

    public void criarTabelaProdutoInfo(SQLiteDatabase db)
    {
      //  ExecutarComandosSQL(db, contexto.getString(R.string.tabela_produtoInfo).split("\n"));
    }


    private void atualizaDBSQL(SQLiteDatabase db,int oldVsDB, int newVsDB)
    {

        Log.w(LOG_TAG, "Atualizando a base de dados da versão " + oldVsDB + " para " + newVsDB + ", que destruirá todos os dados antigos");

        db.beginTransaction();

        try
        {
            if (oldVsDB<2&&newVsDB>=2) {
                ExecutarComandosSQL(db, contexto.getString(R.string.tabela_vendedor).split("\n"));
            }
            if (oldVsDB<4&&newVsDB>=4) {
                if(!atu.verificar(db,"produto","desconto")) {
                    ExecutarComandosSQL(db, contexto.getString(R.string.atualiza_bd_vs4_1).split("\n"));
                }
                if(!atu.verificar(db,"produto","qtdembal")) {
                    ExecutarComandosSQL(db, contexto.getString(R.string.atualiza_bd_vs4_2).split("\n"));
                }
            }

        }
        catch (SQLException e)
        {
            Log.e("Erro ao atualizar as tabelas e testar os dados", e.toString());
            throw e;
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
