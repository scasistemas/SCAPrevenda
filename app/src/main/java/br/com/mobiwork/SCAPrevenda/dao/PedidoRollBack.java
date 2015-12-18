package br.com.mobiwork.SCAPrevenda.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import br.com.mobiwork.SCAPrevenda.model.Config;
import br.com.mobiwork.SCAPrevenda.model.PreVenda;
import br.com.mobiwork.SCAPrevenda.model.PreVendaItem;
import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;
import br.com.mobiwork.SCAPrevenda.util.DataHoraPedido;

/**
 * Created by LuisGustavo on 27/05/2015.
 */
public final class PedidoRollBack {
    private static SQLiteDatabase dbPedRollBack,dbP;
    private static PreVenda pedidoEdit;
    private static ArrayList<PreVendaItem> listaItensAnt, listaItensAtual;
    private static String numpre;
    private static Config config;


    public static void  criarPedidoRollBack(SQLiteDatabase db, Cursor c, String id){
        dbPedRollBack = db;
        numpre = id;
        listaItensAnt = new ArrayList<PreVendaItem>();
        if (c.moveToFirst()) {
            pedidoEdit = new PreVenda();
            pedidoEdit.setPreVenda(c);
        }
    }

    public static void rollBackpedido(Context ctx, DataHoraPedido dataHoraMS){
        DaoPreVendaItem dpvi = new DaoPreVendaItem(ctx);
        DaoCreateDB daoDB = new DaoCreateDB(ctx);
        SQLiteDatabase   db;
        db =  daoDB.getWritableDatabase();

        DaoCreateDBP daoP = new DaoCreateDBP(ctx);
        dbP =  daoP.getWritableDatabase();
        config = ConfigVendedor.getConfig(dbP);


        new DaoPreVenda(ctx).atualizar(dbPedRollBack, pedidoEdit, dataHoraMS);
        Cursor c=dpvi.listarPreVendas(db, numpre);

        if (c.moveToFirst()) {
            do {
                PreVendaItem item = new PreVendaItem();
                item.setPreVendaItem(c);
                boolean found = false;
                for (PreVendaItem itemAnt : listaItensAnt){
                    if (itemAnt.getCodprod().equals(item.getCodprod())){
                        new DaoPreVendaItem(ctx).atualizar(dbPedRollBack, itemAnt);
                        listaItensAnt.remove(itemAnt);
                        found = true;
                        break;
                    }
                }
                if (!found){
                    dpvi.excluir(dbPedRollBack, item);
                }
            } while (c.moveToNext());
        }
        for (PreVendaItem item : listaItensAnt){
            new DaoPreVendaItem(ctx).Inserir(dbP,dbPedRollBack,item,dataHoraMS);
        }
    }
    public static void setItemPedido(Cursor c) {
        if (c.moveToFirst()) {
            do {
                PreVendaItem item = new PreVendaItem();
                item.setPreVendaItem(c);
                listaItensAnt.add(item);
            } while (c.moveToNext());
        }
    }

}


