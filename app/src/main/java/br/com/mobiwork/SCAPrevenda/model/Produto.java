package br.com.mobiwork.SCAPrevenda.model;

import android.database.Cursor;

/**
 * Created by LuisGustavo on 06/08/14.
 */
public class Produto {
    private int _id;
    private String procodigo;
    private String prodigito;
    private String descricao;
    private String probarra;
    private double proprvenda1;
    private double proprvenda2;
    private String codun;
    private double desconto;
    private double qtdembal;
    private double proprcusto;
    private double proprvenda1F;
    private double ProQuantEmbal2;
    private double conversor;
    private double ProQuantEmbal;
    private int proqtddecimal;

    public void setProduto(Cursor c,Config config){
        this._id = c.getInt(c.getColumnIndex("_id"));
        this.procodigo = c.getString(c.getColumnIndex("procodigo"));
        this.prodigito = c.getString(c.getColumnIndex("prodigito"));
        this.descricao = c.getString(c.getColumnIndex("prodescri"));
        this.probarra = c.getString(c.getColumnIndex("probarra"));
        this.proprvenda1 = c.getDouble(c.getColumnIndex("proprvenda1"));
        this.proprvenda2 = c.getDouble(c.getColumnIndex("proprvenda2"));
        this.codun=c.getString(c.getColumnIndex("codun"));
        if(config.getEmp().equalsIgnoreCase("jrc")){
            this.desconto=c.getDouble(c.getColumnIndex("desconto"));
            this.qtdembal=c.getDouble(c.getColumnIndex("qtdembal"));
            this.proprcusto=c.getDouble(c.getColumnIndex("proprcusto"));
            this.proprvenda1F=c.getDouble(c.getColumnIndex("proprvenda1F"));
            this.ProQuantEmbal2=c.getDouble(c.getColumnIndex("ProQuantEmbal2"));
            this.conversor=c.getDouble(c.getColumnIndex("conversor"));
            this.ProQuantEmbal=c.getDouble(c.getColumnIndex("ProQuantEmbal"));
            this.proqtddecimal=c.getInt(c.getColumnIndex("proqtddecimal"));
        }
    }

    public int getProqtddecimal() {
        return proqtddecimal;
    }

    public void setProqtddecimal(int proqtddecimal) {
        this.proqtddecimal = proqtddecimal;
    }

    public double getProQuantEmbal() {
        return ProQuantEmbal;
    }

    public void setProQuantEmbal(double proQuantEmbal) {
        ProQuantEmbal = proQuantEmbal;
    }

    public double getConversor() {
        return conversor;
    }

    public void setConversor(double conversor) {
        this.conversor = conversor;
    }

    public double getProprvenda1F() {
        return proprvenda1F;
    }

    public void setProprvenda1F(double proprvenda1F) {
        this.proprvenda1F = proprvenda1F;
    }

    public double getProQuantEmbal2() {
        return ProQuantEmbal2;
    }

    public void setProQuantEmbal2(double proQuantEmbal2) {
        ProQuantEmbal2 = proQuantEmbal2;
    }

    public double getProprcusto() {
        return proprcusto;
    }

    public void setProprcusto(double proprcusto) {
        this.proprcusto = proprcusto;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }

    public double getQtdembal() {
        return qtdembal;
    }

    public void setQtdembal(double qtdembal) {
        this.qtdembal = qtdembal;
    }

    public String getCodun() {
        return codun;
    }

    public void setCodun(String codun) {
        this.codun = codun;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getProcodigo() {
        return procodigo;
    }

    public void setProcodigo(String procodigo) {
        this.procodigo = procodigo;
    }

    public String getProdigito() {
        return prodigito;
    }

    public void setProdigito(String prodigito) {
        this.prodigito = prodigito;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getProbarra() {
        return probarra;
    }

    public void setProbarra(String probarra) {
        this.probarra = probarra;
    }

    public double getProprvenda1() {
        return proprvenda1;
    }

    public void setProprvenda1(double proprvenda1) {
        this.proprvenda1 = proprvenda1;
    }

    public double getProprvenda2() {
        return proprvenda2;
    }

    public void setProprvenda2(double proprvenda2) {
        this.proprvenda2 = proprvenda2;
    }
}
