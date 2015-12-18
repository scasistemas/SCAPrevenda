package br.com.mobiwork.SCAPrevenda.util;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import br.com.mobiwork.SCAPrevenda.model.Config;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

/**
 * Created by LuisGustavo on 06/08/14.
 */
public class SincDadosSmb {

    public Config config;
    public static String user = "";
    public static String pass ="";
    public static String sharedFolder="";
    public static String pathdownup="";
    public static String arquivoenvi;
    public static String arquivoreceb;
    NtlmPasswordAuthentication auth ;
    SmbFileInputStream mFStream=null;
    SmbFile sc=null;
    SmbFile mFile=null;
    private SharedPreferencesUtil sdu;

    public SincDadosSmb(Config c,int paramcaminho,Context ctx){
        this.config=c;
        arquivoenvi="";
        arquivoreceb="";
        user=c.getLogin();
        pass=c.getSenha();
        sdu = new SharedPreferencesUtil(ctx);
        if(sdu.getGuestUser()){
            auth = new NtlmPasswordAuthentication("","GUEST","");
        }else{
            auth = new NtlmPasswordAuthentication("",user,pass);
        }

        if(paramcaminho==2){
            pathdownup=c.getEndereco()+"/vs";

        }else if(paramcaminho==1){
             pathdownup=c.getEndereco();
        }else{
             pathdownup=c.getEnderecoorc();
        }


    }


    public String downloadFilePc(String nome,String ext,int caminho ) throws IOException {
       String erro="";
        boolean ok=false;
        InputStream is = null;
        OutputStream mFileOutputStream = null;

        File mLocalFile;
        try {
            String urltemp=pathdownup+"/"+nome+ext;
          //  String urltemp=pathdownup+"/orcamento";
            SmbFile mFile = new SmbFile(urltemp,auth);

            mFStream = new SmbFileInputStream(mFile);


            if(caminho==1) {
                mLocalFile = new File(ConfigVendedor.getExternalStorageDirectoryVs(),
                        mFile.getName());
            }else{
                mLocalFile = new File(ConfigVendedor.getExternalStorageDirectory(),
                        mFile.getName());
            }
            mFileOutputStream = new FileOutputStream(
                    mLocalFile);
            is = mFile.getInputStream();
            try {
                mFile.listFiles();
            } catch (SmbException e) {
                e.printStackTrace();
            }

            int bufferSize = 5096;
            byte[] b = new byte[bufferSize];
            int noOfBytes = 0;
            while( (noOfBytes = is.read(b)) != -1 )
            {
                mFileOutputStream.write(b, 0, noOfBytes);
            }

            mFileOutputStream.close();
            mFStream.close();
            is.close();
            ok=true;
        } catch (MalformedURLException e) {
            erro=e.getMessage();
            mFileOutputStream.close();
            is.close();
            mFStream.close();

        } catch (SmbException e) {
            erro=erro(e.getMessage(),nome,ext);
            mFileOutputStream.close();
            is.close();
            mFStream.close();
        } catch (Exception e) {
            erro=e.getMessage();
        }finally {
            if(ok){
            mFileOutputStream.close();
            is.close();
            mFStream.close();
            }
            return erro;

        }
    }

    public List<HashMap<String, String>>  smbfile( )  {

        SmbFile[] domains ;
        List<HashMap<String, String>>  fillMaps = new ArrayList<HashMap<String, String>>();
        fillMaps.clear();


        try {
             sc = (new SmbFile(pathdownup+"/", auth));
            if(!sc.isDirectory()){
                sc.mkdir();
            }
            domains=sc.listFiles();
            for (int i = 0; i < domains.length; i++) {
                System.out.println(domains[i]);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("_id", "" +domains[i].getName());
                map.put("hora", String.valueOf(domains[i].getUncPath()));
                fillMaps.add(map);
           /*     SmbFile[] servers = domains[i].listFiles();
                for (int j = 0; j < servers.length; j++) {
                    System.out.println("\t" + servers[j]);
                }*/
            }
        } catch (SmbException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return fillMaps;
    }

    public void  criarpastaSmb()  {


    }



    public boolean testar(){

        //---------START SMB WORKS-------------------------
        String url = "smb://192.168.2.156/Users/LuisGustavo/";
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "LuisGustavo", "lg982110");
        String[] shares;
        String[] files;
        SmbFile dir = null;
        try {
            dir = new SmbFile(url, auth);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i("MyHome", "Authenication problem");
        }
        try {
            for (SmbFile f : dir.listFiles()) {
                System.out.println(f.getName());
                shares = dir.list();
            }
        } catch (SmbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i("MyHome", "Directory listing problem");
        }
        //-------END SMB WORKS---------------------

        SmbFile server = null;
        try {
            server = new SmbFile("smb://192.168.2.134/Users/LuisGustavo/");
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            files = server.list();
        } catch (SmbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;

    }



        public String sendFileFromPeerToSdcard(String file, String ext) throws IOException {

            String erro="1";
            boolean ok=false;
            String pathserv=pathdownup+"/"+file+ext;
            SmbFile smbFile = null;
            BufferedOutputStream out=null;
            InputStream in = null;
            FileInputStream f=null;
            f = null;
            try{
                try {
                    smbFile = new SmbFile(pathserv,auth);
                    f = new FileInputStream(Environment.getExternalStorageDirectory()+"/SCAPrevenda/"+file+ext);
                    in  = new BufferedInputStream(f);
                    out = new BufferedOutputStream(smbFile.getOutputStream());

                    byte[] buffer = new byte[10240];
                    int len = 0;
                    while((len = in.read(buffer))>0){
                        out.write(buffer,0,len);
                    }
                    ok=true;
                } catch (MalformedURLException e) {
                    erro=e.getMessage();
                    e.printStackTrace();


                } catch (FileNotFoundException e) {
                    erro=e.getMessage();
                    e.printStackTrace();
                    erro=erro(e.getMessage(),"","");
                }

               catch (IOException e) {
                  erro=erro(e.getMessage(),"","");
                }

           }catch (Exception e){
                erro=e.getMessage();

           }finally {
                if(ok){
                    out.close();
                    in.close();
                    f.close();
                }
            return  erro;
           }
    }
    public String erro(String mensagem,String nome,String ext){
       String e="";
        if(mensagem.equalsIgnoreCase("The system cannot find the path specified.")){
            e="Não foi possivel encontrar a pasta no servidor";
        }else if(mensagem.equalsIgnoreCase("The system cannot find the file specified.")){
            e="Não foi encontrado os arquivos de configuração do servidor: "+nome+ext;
        }else if(mensagem.equalsIgnoreCase("Logon failure: unknown user name or bad password.")){
            e="Usuário ou senha incorretos";
        }else{
            e=mensagem;
        }
        return e;
    }
}
