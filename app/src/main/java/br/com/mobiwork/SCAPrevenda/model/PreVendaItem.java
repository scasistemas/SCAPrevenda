package br.com.mobiwork.SCAPrevenda.model;

import android.database.Cursor;

/**
 * Created by LuisGustavo on 06/08/14.
 */
public class PreVendaItem {

    private String id;
    private String numpre;
    private String codprod;
    private String digprod;
    private double quantidade;
    private double valor;
    private double precoTb;
    private String descricao;
    private String loteenvio;
    private double desconto;
    private String tipo;
    private String unidade;


    public void setPreVendaItem(Cursor c){
        this.id = c.getString(c.getColumnIndex("_id"));
        this.numpre = c.getString(c.getColumnIndex("numpre"));
        this.codprod = c.getString(c.getColumnIndex("codprod"));
        this.digprod = c.getString(c.getColumnIndex("digprod"));
        this.quantidade = c.getDouble(c.getColumnIndex("quantidade"));
        this.valor = c.getDouble(c.getColumnIndex("valor"));
        this.loteenvio = c.getString(c.getColumnIndex("loteenvio"));
        this.descricao = c.getString(c.getColumnIndex("descricao"));
        this.precoTb=c.getDouble(c.getColumnIndex("precoTb"));
        this.desconto=c.getDouble(c.getColumnIndex("desconto"));
        this.tipo=c.getString(c.getColumnIndex("tipo"));
        this.unidade=c.getString(c.getColumnIndex("unidade"));
    }



    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getLoteenvio() {
        return loteenvio;
    }

    public void setLoteenvio(String loteenvio) {
        this.loteenvio = loteenvio;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getId() {
        return id;
    }

    public double getPrecoTb() {
        return precoTb;
    }

    public void setPrecoTb(double precoTb) {
        this.precoTb = precoTb;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumpre() {
        return numpre;
    }

    public void setNumpre(String numpre) {
        this.numpre = numpre;
    }

    public String getCodprod() {
        return codprod;
    }

    public void setCodprod(String codprod) {
        this.codprod = codprod;
    }

    public String getDigprod() {
        return digprod;
    }

    public void setDigprod(String digprod) {
        this.digprod = digprod;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }
}
