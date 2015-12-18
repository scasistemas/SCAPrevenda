package br.com.mobiwork.SCAPrevenda.model;

import android.database.Cursor;

/**
 * Created by LuisGustavo on 24/09/14.
 */
public class ParamCondi {
    private int _id;
    private double pcValor;
    private int pcNumero;


    public void setPreVenda(Cursor c)  {
        this._id=c.getInt(c.getColumnIndex("_id"));
        this.pcValor=c.getDouble(c.getColumnIndex("pcvalor"));
        this.pcNumero = c.getInt(c.getColumnIndex("pcnumero"));

    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public double getPcValor() {
        return pcValor;
    }

    public void setPcValor(double pcValor) {
        this.pcValor = pcValor;
    }

    public int getPcNumero() {
        return pcNumero;
    }

    public void setPcNumero(int pcNumero) {
        this.pcNumero = pcNumero;
    }
}
