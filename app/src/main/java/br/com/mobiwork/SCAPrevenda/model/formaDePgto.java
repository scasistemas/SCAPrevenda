package br.com.mobiwork.SCAPrevenda.model;

import android.database.Cursor;

/**
 * Created by LuisGustavo on 03/09/14.
 */
public class formaDePgto {

    public int _id;
    public String descricao;


    public void setPreVenda(Cursor c)  {
        this._id=c.getInt(c.getColumnIndex("_id"));
        this.descricao=c.getString(c.getColumnIndex("descricao"));
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
