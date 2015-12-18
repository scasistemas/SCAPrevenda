package br.com.mobiwork.SCAPrevenda.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDB;
import br.com.mobiwork.SCAPrevenda.dao.DaoCreateDBP;
import br.com.mobiwork.SCAPrevenda.model.Config;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class DataXmlExporter {

    private static final String OPEN_XML_STANZA = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
    private static final String CLOSE_WITH_TICK = "'>";
    private static final String DB_OPEN = "<database name='";
    private static final String DB_CLOSE = "</database>";
    private static final String TABLE_OPEN = "<table name='";
    private static final String TABLE_CLOSE = "</table>";
    private static final String ROW_OPEN = "<row>";
    private static final String ROW_CLOSE = "</row>" + '\n';
    private static final String COL_OPEN = "<col name='";
    private static final String COL_CLOSE = "</col>";

    private static final String COL_OPENImport = "<col name=";
    private static final String CLOSE_WITH_TICKImport = ">";
    private static String dirImportExport;

    private SQLiteDatabase db;
    private XmlBuilder xmlBuilder;
    public DataXmlExporter(SQLiteDatabase db, String dirImportExport) {
        this.dirImportExport = dirImportExport;
        this.db = db;
    }
    public void export(String dbName, String exportFileNamePrefix, String loteEnvio, String tableName) throws IOException {
        Log.i("", "exporting database - " + dbName + " exportFileNamePrefix=" + exportFileNamePrefix);
        this.xmlBuilder = new XmlBuilder();
        this.xmlBuilder.start(dbName);
        String sql = "select * from sqlite_master where name = ?";
        Cursor c = this.db.rawQuery(sql, new String[]{ ""+tableName});
        if (c.moveToFirst()) {
            do {
                if ( tableName.equals(tableName)) {
                    this.exportTable(tableName, loteEnvio);
                }
            }
            while (c.moveToNext());
        }
        String xmlString = this.xmlBuilder.end();
        this.writeToFile(xmlString, exportFileNamePrefix + ".xml");
        Log.i("", "exporting database complete");
    }
    private void exportTable(final String tableName, String loteEnvio) throws IOException {
        Log.d("", "exporting table - " + tableName);
        this.xmlBuilder.openTable(tableName);
        Cursor c;
        if (tableName.equals("pedidos") || tableName.equals("itensPedido")){
            c = this.db.rawQuery("SELECT * FROM  " + tableName + " tb " +
                    " WHERE tb.loteEnvio = ? " ,  new String[]{ ""+loteEnvio});
        } else {
            String sql = "select * from " + tableName;
            c = this.db.rawQuery(sql, new String[0]);
        }

        if (c.moveToFirst()) {
            int cols = c.getColumnCount();
            do {
                this.xmlBuilder.openRow();
                for (int i = 0; i < cols; i++) {
                    this.xmlBuilder.addColumn(c.getColumnName(i), c.getString(i));
                }
                this.xmlBuilder.closeRow();
            } while (c.moveToNext());
        }
        c.close();
        this.xmlBuilder.closeTable();
    }
    private void writeToFile(String xmlString, String exportFileName) throws IOException {
        File file = new File(dirImportExport + "/" + exportFileName);
        file.createNewFile();

        ByteBuffer buff = ByteBuffer.wrap(xmlString.getBytes());
        FileChannel channel = new FileOutputStream(file).getChannel();
        try {
            channel.write(buff);
        } finally {
            if (channel != null)
                channel.close();
        }
        //}
    }
    class XmlBuilder {
        private final StringBuilder sb;
        public XmlBuilder() throws IOException {
            this.sb = new StringBuilder();
        }
        void start(String dbName) {
            this.sb.append(OPEN_XML_STANZA);
            this.sb.append(DB_OPEN + dbName + CLOSE_WITH_TICK);
        }
        String end() throws IOException {
            this.sb.append(DB_CLOSE);
            return this.sb.toString();
        }
        void openTable(String tableName) {
            this.sb.append(TABLE_OPEN + tableName + CLOSE_WITH_TICK);
        }
        void closeTable() {
            this.sb.append(TABLE_CLOSE);
        }
        void openRow() {
            this.sb.append(ROW_OPEN);
        }
        void closeRow() {
            this.sb.append(ROW_CLOSE);
        }
        void addColumn(final String name, final String val) throws IOException {
            this.sb.append(COL_OPEN + name + CLOSE_WITH_TICK + val + COL_CLOSE);
        }
    }

    public boolean importData(Context ctx,String arqName,String tableName, String modo_op)
    {
        DaoCreateDB daoDB = null;
        DaoCreateDBP daoDBP = null;
        if (modo_op.equals("teste")){
            daoDB = new DaoCreateDB(ctx);
        } else {
            daoDBP = new DaoCreateDBP(ctx);
        }


        File file = new File( dirImportExport + "/" + arqName + ".xml");

        if (file.exists()){
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            DataInputStream dis = null;

            try {
                fis = new FileInputStream(file);

                bis = new BufferedInputStream(fis);
                dis = new DataInputStream(bis);
                String codPrduto = "";
                String _idTabela = "";
                String codUn = "";
                String codigoCli = "";
                //String qtdMin = "";
                String ordem = "";
                String numeroCReceber  = "";
                ContentValues values = new ContentValues();
                String _id = "";
                //ContentValues valuesComapar;
                boolean syncOk = false;
                while (dis.available() != 0) {

                    int inicio = 0;
                    String linha = dis.readLine();
                    linha = linha.replace("</col></row>","");
                    int posIni = linha.indexOf(COL_OPENImport,inicio);
                    int posFim = linha.indexOf(COL_CLOSE,inicio);

                    while (posIni > -1) {
                        syncOk = true;
                        inicio = posFim + 1;
                        String campo = linha.substring(posIni, posFim);
                        posIni = campo.indexOf(CLOSE_WITH_TICKImport) + CLOSE_WITH_TICKImport.length();
                        posFim = campo.length();
                        String valor = campo.substring(posIni, posFim);
                        posIni = campo.indexOf(COL_OPENImport) + COL_OPENImport.length() + 1;
                        posFim = campo.indexOf(CLOSE_WITH_TICKImport) - 1;
                        campo = campo.substring(posIni, posFim);

                        if (campo.equals("fimlinha")) {

                            break;
                        }
                        if (campo.equals("codPrduto")) {
                            codPrduto = valor;
                        }

                        if (campo.equals("_idTabela")) {
                            _idTabela = valor;
                        }
                        if (campo.equals("codUn")) {
                            codUn = valor;
                        }
                        if (campo.equals("codigo") && tableName.equals("clientes")) {
                            codigoCli = valor;
                        }
                        if (campo.equals("ordem") && tableName.equals("produtoInfo")) {
                            ordem = valor;
                        }
                        if (campo.equals("_id") && tableName.equals("creceb")) {
                            numeroCReceber = valor;
                        }

                        if (!campo.equals("fimlinha")) {
                            if (campo.equals("_id")) {
                                if (tableName.equals("produtoInfo")
                                        || tableName.equals("creceb")
                                        || tableName.equals("produtos")
                                        || tableName.equals("clientes")) {
                                    /************/
                                } else if (tableName.equals("operacao")
                                        || tableName.equals("condicaoDePgto")
                                        || tableName.equals("formaDePgto")
                                        || tableName.equals("filtroProduto")
                                        || tableName.equals("tbPrecoCliente")) {
                                    values.put(campo, valor);
                                    _id = valor;

                                }
                            } else {
                                values.put(campo, valor);


                            }
                        }
                        posIni = linha.indexOf(COL_OPENImport, inicio);
                        posFim = linha.indexOf(COL_CLOSE, inicio);
                    }
                    }
                    if (syncOk){
                        Cursor c = null;
                        if (tableName.equals("produtos")){
                            c = db.rawQuery("SELECT *  FROM " + tableName +" tb " +
                                    " WHERE tb.codPrduto = ? and tb.codUn = ? " ,  new String[]{""+codPrduto, ""+codUn});
                        }
                        else if (tableName.equals("clientes")){
                            c = db.rawQuery("SELECT *  FROM precadastro tb " +
                                    " WHERE tb.codigo = ? " ,  new String[]{""+codigoCli});
                            if (c.moveToFirst()){
                                c = db.rawQuery("DELETE FROM precadastro tb " +
                                        " WHERE tb.codigo = ? " ,  new String[]{""+codigoCli});
                            }
                            c = db.rawQuery("SELECT *  FROM " + tableName +" tb " +
                                    " WHERE tb.codigo = ? " ,  new String[]{""+codigoCli});
                        } else if (tableName.equals("produtoInfo")){
                            c = db.rawQuery("SELECT *  FROM " + tableName +" tb " +
                                    " WHERE tb.codPrduto = ? and tb._idTabela = ? and tb.codUn = ?  and tb.ordem = ? " ,  new String[]{""+codPrduto, ""+_idTabela, ""+codUn  , ""+ordem });
                        } else if (tableName.equals("creceb")){
                            c = db.rawQuery("SELECT *  FROM " + tableName +" tb " +
                                    " WHERE tb._id = ? " ,  new String[]{""+numeroCReceber});
                        }
                        if (syncOk){
                            if ((c != null) && (c.moveToFirst())){
                                if (modo_op.equals("teste")){
                                    daoDB.update(db,tableName, values, "_id = " + c.getString(c.getColumnIndex("_id")), null);
                                }else {
                                    daoDBP.update(db,tableName, values, "_id = " + c.getString(c.getColumnIndex("_id")), null);
                                }
                            } else {
                                if (modo_op.equals("teste")){
                                    daoDB.insert(db,tableName, "", values);
                                } else {
                                    try{
                                        if(values.get("emp").equals("jrc")){
                                            SharedPreferencesUtil sdu = new SharedPreferencesUtil(ctx);
                                            sdu.setGuestUser(true);
                                        }
                                        daoDBP.insert(db,tableName, "", values);
                                        Config config = ConfigVendedor.getConfig(this.db);
                                        int t=0;
                                    }catch(Exception e){
                                       System.out.println(e.getMessage());
                                    }
                                }

                            }
                        }
                    }


                fis.close();
                bis.close();
                dis.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Alertas a = new Alertas(ctx);
            a.AlertaSinc("Sem o arquivo de configuracao");
        }
        return  false;
    }


}
