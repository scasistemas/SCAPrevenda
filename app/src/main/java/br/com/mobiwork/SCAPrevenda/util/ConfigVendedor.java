package br.com.mobiwork.SCAPrevenda.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;

import br.com.mobiwork.SCAPrevenda.model.Config;

/**
 * Created by LuisGustavo on 07/08/14.
 */
public final class ConfigVendedor {
    public static Config config;
    private static SQLiteDatabase db;

    public static Config getConfig(SQLiteDatabase db){

            config = new Config();
            Cursor c = db.rawQuery("SELECT * FROM config tb " +
                    " WHERE tb._id = ?",  new String[]{"1"});
            if (c.moveToFirst()) {
                config.setConfig(c);
            }


        return config;
    }



    public static String getExternalStorageDirectoryVs(){
        String dirMercadorName = Environment.getExternalStorageDirectory().toString() + "//SCAPrevenda/Vs" ;
        File file = new File(dirMercadorName);
        if(!file.isDirectory()){
            file.mkdir();
            if(!file.isDirectory()){
                dirMercadorName = "/mnt/sdcard" + "//SCAPrevenda/Vs" ;  // Juares
                file = new File(dirMercadorName);
                if(!file.isDirectory()){
                    file.mkdir();
                    if(!file.isDirectory()){
                        dirMercadorName = "/sdcard" + "//SCAPrevenda/Vs" ;  // Juares
                        file = new File(dirMercadorName);
                        if(!file.isDirectory()){
                            file.mkdir();
                        }
                    }
                }
            }
        }
        return dirMercadorName;

    }


    public static void setConfig(Config config) {
        ConfigVendedor.config = config;
    }

    public static String getExternalStorageDirectory(){
        String dirMercadorName = Environment.getExternalStorageDirectory().toString() + "//SCAPrevenda" ;
        File file = new File(dirMercadorName);
        if(!file.isDirectory()){
            file.mkdir();
            if(!file.isDirectory()){
                dirMercadorName = "/mnt/sdcard" + "//SCAPrevenda" ;  // Juares
                file = new File(dirMercadorName);
                if(!file.isDirectory()){
                    file.mkdir();
                    if(!file.isDirectory()){
                        dirMercadorName = "/sdcard" + "//SCAPrevenda" ;  // Juares
                        file = new File(dirMercadorName);
                        if(!file.isDirectory()){
                            file.mkdir();
                        }
                    }
                }
            }
        }
        return dirMercadorName;

    }
}
