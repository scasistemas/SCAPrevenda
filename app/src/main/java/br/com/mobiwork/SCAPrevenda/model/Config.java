package br.com.mobiwork.SCAPrevenda.model;

import android.database.Cursor;

/**
 * Created by LuisGustavo on 06/08/14.
 */
public class Config {

    private Integer _id;
    private Integer vendid;
    private String nome;
    private String endereco;
    private String enderecoorc;
    private String login;
    private String senha;
    private String emp;
    private String atu;
    private String versaotabela;
    private String loginpre;
    private String senhapre;
    public static String guestpc="GUESTPC";
    public static String idsmart="IDSMART";
    public static String atuaut="ATUAUT";
    public static String versaodados="";

    public void setConfig(Cursor c ){
        this._id = c.getInt(c.getColumnIndex("_id"));
        this.nome=c.getString(c.getColumnIndex("nome"));
        this.vendid = c.getInt(c.getColumnIndex("vendid"));
        this.endereco = c.getString(c.getColumnIndex("endereco"));
        this.login = c.getString(c.getColumnIndex("login"));
        this.senha = c.getString(c.getColumnIndex("senha"));
        this.emp = c.getString(c.getColumnIndex("emp"));
        this.atu=c.getString(c.getColumnIndex("atu"));
        this.versaotabela=c.getString(c.getColumnIndex("versaotabela"));
        this.loginpre=c.getString(c.getColumnIndex("loginpre"));
        this.senhapre=c.getString(c.getColumnIndex("senhapre"));
        this.enderecoorc=c.getString(c.getColumnIndex("enderecoorc"));
    }



    public String getEnderecoorc() {
        return enderecoorc;
    }

    public void setEnderecoorc(String enderecoorc) {
        this.enderecoorc = enderecoorc;
    }

    public String getSenhapre() {
        return senhapre;
    }

    public void setSenhapre(String senhapre) {
        this.senhapre = senhapre;
    }

    public String getLoginpre() {
        return loginpre;
    }

    public void setLoginpre(String loginpre) {
        this.loginpre = loginpre;
    }

    public String getVersaotabela() {
        return versaotabela;
    }

    public void setVersaotabela(String versaotabela) {
        this.versaotabela = versaotabela;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public Integer getVendid() {
        return vendid;
    }

    public void setVendid(Integer vendid) {
        this.vendid = vendid;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmp() {
        return emp;
    }

    public void setEmp(String emp) {
        this.emp = emp;
    }

    public String getAtu() {
        return atu;
    }

    public void setAtu(String atu) {
        this.atu = atu;
    }
}
