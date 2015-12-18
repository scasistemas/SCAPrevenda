package br.com.mobiwork.SCAPrevenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import br.com.mobiwork.SCAPrevenda.model.Config;
import br.com.mobiwork.SCAPrevenda.model.Ponto;
import br.com.mobiwork.SCAPrevenda.model.PreVenda;
import br.com.mobiwork.SCAPrevenda.model.VendAtual;
import br.com.mobiwork.SCAPrevenda.ponto.InfoPonto;
import br.com.mobiwork.SCAPrevenda.util.ConfigVendedor;
import br.com.mobiwork.SCAPrevenda.util.DataHoraPedido;

/**
 * Created by LuisGustavo on 20/03/2015.
 */
public class DaoPonto  extends DaoCreateDBP {


    Context ctx;
    public DaoPonto(Context context) {

        super(context);
        ctx =context;
    }

    //cursor = db.rawQuery("SELECT _id, cliente, nomeCliente, vrTotal,data, (dia || '/' || mes || '/' || ano) as data  FROM pedidos where data between '"+dataInGlobal+"'   and '"+datafimGlobal+"'",
    //null);

    public Cursor consultar(SQLiteDatabase db,String data,SQLiteDatabase dbP){
        Cursor cursor=null;
        VendAtual va;
        DaoVendAtual dva= new DaoVendAtual(ctx);
        va=dva.getVendAtual(ctx);
        String t;


        try{
          /*  cursor= db.rawQuery("SELECT * from ponto " +
                    " WHERE  datainicial = '"+data+"'", null);*/
       /*     um vendedor
       cursor= db.rawQuery("SELECT * from ponto " +
                    " WHERE  datainicial = '"+data+"' and vendedor='"+ConfigVendedor.getConfig(dbP).getVendid()+"'", null);
          */
            cursor= db.rawQuery("SELECT * from ponto " +
                    " WHERE  datainicial = '"+data+"' and vendedor='"+va.getCodigovend()+"'", null);
        }catch (Exception e){
            String erro = e.getMessage();
        }


        return cursor;
    }

    public Cursor inserirAtu(SQLiteDatabase db,SQLiteDatabase dbP){
        VendAtual va;
        DaoVendAtual dva= new DaoVendAtual(ctx);
        va=dva.getVendAtual(ctx);
        Ponto p = new Ponto();
        p.setVendedor(va.getCodigovend());
        //UM VENDEDOR
      //  p.setVendedor(ConfigVendedor.getConfig(dbP).getVendid());
        SimpleDateFormat formatador2 = new SimpleDateFormat("yyyy-MM-dd");
        Date data2 = new Date();
        String data=formatador2.format(data2).toString();
        p.setDatainicio(data);
        GregorianCalendar calendario = new GregorianCalendar();
        String hora = calendario.get(GregorianCalendar.HOUR_OF_DAY)+":"+calendario.get(GregorianCalendar.MINUTE)+":"+calendario.get(GregorianCalendar.SECOND);
        Cursor c =consultar(db,data,dbP);

        if(!veri("horainicial",db,formatador2,data2,dbP)){
            p.setHorainicio(hora);
            Inserir(db,p);
        }else{
            if(veri("horasaialm",db,formatador2,data2,dbP)==true && veri("horavoltalm",db,formatador2,data2,dbP)==false) {
               p.setHoravoltalm(hora);
               alterarvoltAlm(db,p);
            }
        }


        return null;
    }

    public boolean veri(String campo,SQLiteDatabase db, SimpleDateFormat formatador,Date data2,SQLiteDatabase dbP){
        Cursor c = consultar(db, formatador.format(data2),dbP);
        c.moveToFirst();
        boolean reg = false;
        if (c.moveToFirst()) {
            if (!c.getString(c.getColumnIndex(campo)).equalsIgnoreCase("")) {

                reg = true;
            }
        }
        return reg;
    }


    public void alterarhorafinal(SQLiteDatabase db,Ponto p){

        double valor=0;
        VendAtual va;
        DaoVendAtual dva= new DaoVendAtual(ctx);
        va=dva.getVendAtual(ctx);
        try{
          //  String sqlup=("UPDATE ponto SET  horafim ='"+p.getHorafim()+"' WHERE datainicial = '"+p.getDatainicio()+"'");
            String sqlup=("UPDATE ponto SET  horafim ='"+p.getHorafim()+"' WHERE datainicial = '"+p.getDatainicio()+"' and vendedor='"+va.getCodigovend()+"'");

            db.execSQL(sqlup);
        }catch(Exception e){
            String erro = e.getMessage();
        }
    }

    public void alterarhorasaiAlm(SQLiteDatabase db,Ponto p){
        VendAtual va;
        DaoVendAtual dva= new DaoVendAtual(ctx);
        va=dva.getVendAtual(ctx);
        double valor=0;

        try{
       //     String sqlup=("UPDATE ponto SET  horasaialm ='"+p.getHorasaialm()+"' WHERE datainicial = '"+p.getDatainicio()+"'");
            String sqlup=("UPDATE ponto SET  horasaialm ='"+p.getHorasaialm()+"' WHERE datainicial = '"+p.getDatainicio()+"'and  vendedor='"+va.getCodigovend()+"'");

            db.execSQL(sqlup);
        }catch(Exception e){
            String erro = e.getMessage();
        }
    }

    public void alterarvoltAlm(SQLiteDatabase db,Ponto p){
        VendAtual va;
        DaoVendAtual dva= new DaoVendAtual(ctx);
        va=dva.getVendAtual(ctx);
        double valor=0;
        try{
         //   String sqlup=("UPDATE ponto SET  horavoltalm ='"+p.getHoravoltalm()+"' WHERE datainicial = '"+p.getDatainicio()+"'");
            String sqlup=("UPDATE ponto SET  horavoltalm ='"+p.getHoravoltalm()+"' WHERE datainicial = '"+p.getDatainicio()+"' and vendedor='"+va.getCodigovend()+"'");

            db.execSQL(sqlup);
        }catch(Exception e){
            String erro = e.getMessage();
        }
    }
    public Ponto Inserir(SQLiteDatabase db,Ponto p){

        try{
        String sqlup=("insert into ponto (vendedor,datainicial,horainicial,horasaialm,horavoltalm,enviado,horafim) values ('"+p.getVendedor()+"','"+p.getDatainicio()+"','"+p.getHorainicio()+"'" +
                ",'"+p.getHorasaialm()+"','"+p.getHoravoltalm()+"','"+p.getEnviado()+"','"+p.getHorafim()+"')");
            db.execSQL(sqlup);
        }catch(Exception e){
            String erro = e.getMessage();
        }
      /*  long i=0;
        Ponto ponto=p;
        values.put("_id",1);
        values.put("vendedor", p.getVendedor());
        values.put("datainicial",p.getDatainicio());
        values.put("horainicial", p.getHorainicio());
        values.put("horasaialm","2");
        values.put("horavoltalm","2");
        values.put("enviado","2");
        values.put("horafim","2");
        try{
          i=  insert(db, "ponto", "", values);
        }catch(Exception e){
            String erro=e.getMessage();
        }*/
        return p;
    }
}
