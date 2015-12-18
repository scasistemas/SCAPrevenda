package br.com.mobiwork.SCAPrevenda.pedido;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBEnvio;
import br.com.mobiwork.SCAPrevenda.dao.DaoPreVenda;
import br.com.mobiwork.SCAPrevenda.dao.DaoPreVendaItem;
import br.com.mobiwork.SCAPrevenda.model.PreVenda;
import br.com.mobiwork.SCAPrevenda.model.PreVendaItem;
import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;
import br.com.mobiwork.SCAPrevenda.util.DataHoraPedido;
import br.com.mobiwork.SCAPrevenda.util.SharedPreferencesUtil;

public class EnviarPedidos  extends Activity {
    private static final String TAG = EnviarPedidos.class.getSimpleName();
    private Cursor cursor, c,c2,c3;
    private Context context;
    private SharedPreferencesUtil sdu;


    public boolean isExternalStorageAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    public ContentValues ContentValues(Cursor c, String loteEnvio) {
        ContentValues result = new ContentValues();
        for (int i = 0; i < c.getColumnCount(); i++) {
            if (c.getString(i) == null){
                String colum=c.getColumnName(i);
                if (c.getColumnName(i).equals("loteenvio")) {
                    result.put("loteenvio", loteEnvio);
                }else  if(c.getColumnName(i).equalsIgnoreCase("envi")){
                    result.put("envi", "enviado");
                }
                else {
                    result.put(c.getColumnName(i), "");
                }
            } else {
                String colum=c.getColumnName(i);
                if (c.getColumnName(i).equals("loteenvio")) {
                    result.put("loteenvio", loteEnvio);
                }else {

                    result.put(c.getColumnName(i), c.getString(i));

                }
            }
        }
        return result;
    }




    public String gerarLoteDePedidos(Context ctx, SQLiteDatabase db, int vendid,PreVenda pv)  {
        context=ctx;
        DataHoraPedido dataHoraMS = DataHoraPedido.criarDataHoraPedido();
        sdu = new SharedPreferencesUtil(ctx);
        String loteEnvio = sdu.getIdSmart() + pv.getNumero();
        if(sdu.getIdSmart()>0) {
             loteEnvio = sdu.getIdSmart() + pv.getNumero();
        }else{
            loteEnvio =  pv.getNumero();
        }
        c = db.rawQuery("SELECT * FROM prevenda WHERE numero='"+pv.getNumero()+"'"   , null);
        c2 = db.rawQuery("SELECT * FROM prevendaitem WHERE numpre= '"+pv.getNumero()+"'"   , null);
        c3 = db.rawQuery("SELECT * FROM ponto "   , null);

        ArrayList<ContentValues> prevenda = new ArrayList<ContentValues>();
        ArrayList<ContentValues> prevendaitem = new ArrayList<ContentValues>();
        ArrayList<ContentValues> ponto = new ArrayList<ContentValues>();
        DataHoraPedido dataHoraMSPedido = DataHoraPedido.criarDataHoraPedido();;

        if (c.moveToFirst()) {

            do {
                prevenda.add(ContentValues(c,loteEnvio));
                PreVenda opreVenda = new PreVenda();
                opreVenda.setPreVenda(c);
                DaoPreVenda dao = new DaoPreVenda(ctx);
                opreVenda.setLoteenvio(loteEnvio);

                if (dao.atualizar(db,opreVenda,dataHoraMSPedido)){
                    cursor = db.rawQuery("SELECT * FROM prevendaitem tb " +
                            " WHERE tb.numpre = ? " ,  new String[]{ ""+opreVenda.getNumero()});
                    if (cursor.moveToFirst()) {
                        do {
                            PreVendaItem pvitem = new PreVendaItem();
                            pvitem.setPreVendaItem(cursor);
                            pvitem.setLoteenvio(loteEnvio);
                            DaoPreVendaItem daoItem = new DaoPreVendaItem(ctx);
                            daoItem.atualizar(db,pvitem);
                            prevendaitem.add(ContentValues(cursor,loteEnvio));
                            //gerarInsertiValorNaTabela(itensPedido,cursor);
                        } while (cursor.moveToNext());
                    }
                }

            } while (c.moveToNext());
        }
        if (c3.moveToFirst()) {
            do {
                ponto.add(ContentValues(c3,loteEnvio));
            } while (c3.moveToNext());
        }

        DaoCreateDBEnvio dao = new DaoCreateDBEnvio(ctx);
        db =  dao.getWritableDatabase();
        db.execSQL("DELETE FROM prevenda");
        db.execSQL("DELETE FROM prevendaitem");
        db.execSQL("DELETE FROM ponto");


        for (ContentValues value : prevenda){
            dao.insert(db,"prevenda", "", value);
        }
        for (ContentValues value : prevendaitem){
            dao.insert(db,"prevendaitem", "", value);
        }
        for (ContentValues value : ponto){
            dao.insert(db,"ponto", "", value);
        }


        enviarLoteDePedidos(loteEnvio);
        return loteEnvio;

    }
    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void enviarLoteDePedidos( String loteEnvio)  { //SQLiteDatabase db,
        String dbPackage = "br.com.mobiwork.SCAPrevenda";
        String dbName = "ScaprevendaDBEnvio";
        String dbPath = Environment.getDataDirectory() + "/data/" + dbPackage + "/databases/";
        File exportDir = new File(dbPath);
        if (exportDir.exists()){
            File file = new File(exportDir, dbName);
            try {
                file.createNewFile();
                String thisFile = ConfigVendedor.getExternalStorageDirectory() + "/" + loteEnvio  + ".db";
                File dbFile = new File(thisFile);
                this.copyFile(file,dbFile);

            } catch (IOException e) {
                showToast("Erro na exportação");
                Log.e(TAG, e.getMessage(), e);
            }
        }


    }

    void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }


}

