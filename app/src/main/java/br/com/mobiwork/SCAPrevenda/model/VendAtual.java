package br.com.mobiwork.SCAPrevenda.model;

import android.database.Cursor;

/**
 * Created by LuisGustavo on 16/04/2015.
 */
public class VendAtual{
    private int _id;
    private int codigovend;
    private String nomevend;

    public void VendAtual(Cursor c){
            this._id = c.getInt(c.getColumnIndex("_id"));
            this.codigovend = c.getInt(c.getColumnIndex("codigovend"));
            this.nomevend = c.getString(c.getColumnIndex("nomevend"));

        }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getCodigovend() {
        return codigovend;
    }

    public void setCodigovend(int codigovend) {
        this.codigovend = codigovend;
    }

    public String getNomevend() {
        return nomevend;
    }

    public void setNomevend(String nomevend) {
        this.nomevend = nomevend;
    }
}
