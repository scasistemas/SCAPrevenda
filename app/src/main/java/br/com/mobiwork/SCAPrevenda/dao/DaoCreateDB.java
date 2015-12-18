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
public class DaoCreateDB extends SQLiteOpenHelper {
    private static final String NOME_BD = "ScaprevendaDB";
    private static final int VERSAO_BD = 10;
    private static final String LOG_TAG = "ScaprevendaDB";
    private Context contexto;
    private AtuTabela atu;

    public DaoCreateDB(Context context) {
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
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_prevenda).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_prevendaitem).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_loteEnvio).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_ponto).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_vendatual).split("\n"));
            db.setTransactionSuccessful();
        }
        catch (SQLException e)
        {
            Log.e("Erro ao CRIAR TABELAS", e.toString());
        }
        finally
        {
            db.endTransaction();
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        atualizaDBSQL(db, oldVersion, newVersion);

    }

    private void ExecutarComandosSQL(SQLiteDatabase paramSQLiteDatabase, String[] paramArrayOfString)
    {
        int i = paramArrayOfString.length;
        for (int j = 0; j < i; j++)
        {
            String str = paramArrayOfString[j];
            if (str.trim().length() > 0) {
                paramSQLiteDatabase.execSQL(str);
            }
        }
    }

    private void atualizaDBSQL(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
    {
        Log.w("MercadoDB", "Atualizando a base de dados da versão " + paramInt1 + " para " + paramInt2 + ", que destruirá todos os dados antigos");
        paramSQLiteDatabase.beginTransaction();

        try
        {
            if (paramInt1<1&&paramInt2>=1) {
                ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.tabela_ponto).split("\n"));
            }
            if (paramInt1<2&&paramInt2>=2) {
                ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.tabela_vendatual).split("\n"));
            }
            if (paramInt1<3&&paramInt2>=3) {
                if(!atu.verificar(paramSQLiteDatabase,"prevenda","prstatus")) {
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs3_1).split("\n"));
                }
            }
            if (paramInt1<6&&paramInt2>=6) {
                if(!atu.verificar(paramSQLiteDatabase,"prevendaitem","desconto")) {
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs4_3).split("\n"));
                }
            }
            if (paramInt1<7&&paramInt2>=7) {
                if(!atu.verificar(paramSQLiteDatabase,"prevenda","nomeCliente")) {
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs7_1).split("\n"));
                }
            }
            if (paramInt1<8&&paramInt2>=8) {
                if(!atu.verificar(paramSQLiteDatabase,"prevenda","idsmart")) {
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs8_1).split("\n"));
                }
            }
            if (paramInt1<9&&paramInt2>=9) {
                if(!atu.verificar(paramSQLiteDatabase,"prevendaitem","tipo")) {
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs9_1).split("\n"));
                }
            }

            if (paramInt1<10&&paramInt2>=10) {
                if(!atu.verificar(paramSQLiteDatabase,"prevendaitem","unidade")) {
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs10_1).split("\n"));
                }
            }
            paramSQLiteDatabase.setTransactionSuccessful();
            return;
        }
        catch (SQLException localSQLException)
        {
            Log.e("Erro ao atualizar as tabelas e testar os dados", localSQLException.toString());
            throw localSQLException;
        }
        finally
        {
            paramSQLiteDatabase.endTransaction();
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
