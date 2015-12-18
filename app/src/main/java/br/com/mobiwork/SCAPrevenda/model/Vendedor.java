package br.com.mobiwork.SCAPrevenda.model;

import android.database.Cursor;

/**
 * Created by LuisGustavo on 16/04/2015.
 */
public class Vendedor {
    private int _id;
    private int vencodigo;
    private String vennome;
    private String vensenha;



    private void setTabCondi(Cursor c){
        this._id = c.getInt(c.getColumnIndex("_id"));
        this.vencodigo = c.getInt(c.getColumnIndex("vencodigo"));
        this.vennome = c.getString(c.getColumnIndex("vennome"));
        this.vensenha = c.getString(c.getColumnIndex("vensenha"));

    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getVencodigo() {
        return vencodigo;
    }

    public void setVencodigo(int vencodigo) {
        this.vencodigo = vencodigo;
    }

    public String getVennome() {
        return vennome;
    }

    public void setVennome(String vennome) {
        this.vennome = vennome;
    }

    public String getVensenha() {
        return vensenha;
    }

    public void setVensenha(String vensenha) {
        this.vensenha = vensenha;
    }
}
