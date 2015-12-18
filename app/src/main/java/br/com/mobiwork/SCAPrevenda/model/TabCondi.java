package br.com.mobiwork.SCAPrevenda.model;

import android.database.Cursor;

/**
 * Created by LuisGustavo on 06/08/14.
 */
public class TabCondi {
    private int _id;
    private String tabcodigo;
    private String tabdescri;
    private int tabnvenc;
    private int tabcondpag;
    private int tconrvenc;


    private void setTabCondi(Cursor c){
        this._id = c.getInt(c.getColumnIndex("_id"));
        this.tabcodigo = c.getString(c.getColumnIndex("tabcodigo"));
        this.tabdescri = c.getString(c.getColumnIndex("tabdescri"));
        this.tabnvenc = c.getInt(c.getColumnIndex("tconrvenc"));
        this.tabnvenc = c.getInt(c.getColumnIndex("tabcondpag"));

    }

    public int getconrvenc() {
        return tconrvenc;
    }

    public void setconrvenc(int tconrvenc) {
        this.tconrvenc = tconrvenc;
    }


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTabcodigo() {
        return tabcodigo;
    }

    public void setTabcodigo(String tabcodigo) {
        this.tabcodigo = tabcodigo;
    }

    public String getTabdescri() {
        return tabdescri;
    }

    public void setTabdescri(String tabdescri) {
        this.tabdescri = tabdescri;
    }

    public int getTabnvenc() {
        return tabnvenc;
    }

    public void setTabnvenc(int tabnvenc) {
        this.tabnvenc = tabnvenc;
    }

    public int getTabcondpag() {
        return tabcondpag;
    }

    public void setTabcondpag(int tabcondpag) {
        this.tabcondpag = tabcondpag;
    }
}
