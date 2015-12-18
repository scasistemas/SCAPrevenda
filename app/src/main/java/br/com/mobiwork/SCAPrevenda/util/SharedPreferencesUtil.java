package br.com.mobiwork.SCAPrevenda.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import br.com.mobiwork.SCAPrevenda.model.Config;

/**
 * Created by LuisGustavo on 02/11/2015.
 */
public class SharedPreferencesUtil {

    private Context ctx ;
    android.content.SharedPreferences sharedPreferences ;

    public SharedPreferencesUtil(Context ctx){
        this.ctx=ctx;
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public void setGuestUser(boolean guest){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Config.guestpc,guest);
        editor.commit();
    }

    public boolean getGuestUser(){
       SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
       return sharedPreferences.getBoolean(Config.guestpc, false);
    }

    public void setAtuAut(boolean guest){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Config.atuaut,guest);
        editor.commit();
    }

    public boolean getAtuAut(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPreferences.getBoolean(Config.atuaut, false);
    }

    public void setIdSmart(int id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Config.idsmart, id);
        editor.commit();
    }

    public int getIdSmart(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPreferences.getInt(Config.idsmart, 0);
    }

    public void setversaodados(String versao){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Config.versaodados,versao);
        editor.commit();
    }

    public String getversaodados(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPreferences.getString(Config.versaodados,"");
    }
}
