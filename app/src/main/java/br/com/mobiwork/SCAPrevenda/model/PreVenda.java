package br.com.mobiwork.SCAPrevenda.model;

import android.database.Cursor;

/**
 * Created by LuisGustavo on 06/08/14.
 */
public class PreVenda {
    private String _id;
    private String numero;
    private String data;
    private String hora;
    private String condicao;
    private double valorbruto,desc,valortotal;
    private int numped;
    private String loteenvio;
    private int status;
    private int imp;
    private String obs;
    private double parc;
    private int nparc;
    private int forma;
    private int prstatus;
    private int idsmart;
    private String nomeCliente;


    public void setValores(){
        this.parc=0;
        this.desc=0;
        this.valortotal=0;
        this.valorbruto=0;
        this.imp=1;

    }

    public void setPreVenda(Cursor c)  {
        this._id=c.getString(c.getColumnIndex("_id"));
        this.numped=c.getInt(c.getColumnIndex("numped"));
        this.numero = c.getString(c.getColumnIndex("numero"));
        this.data = c.getString(c.getColumnIndex("data"));
        this.hora = c.getString(c.getColumnIndex("hora"));
        this.condicao = c.getString(c.getColumnIndex("condicao"));
        this.loteenvio = c.getString(c.getColumnIndex("loteenvio"));
        this.valorbruto=c.getDouble(c.getColumnIndex("valorbruto"));
        this.valortotal=c.getDouble(c.getColumnIndex("valortotal"));
        this.status = c.getInt(c.getColumnIndex("status"));
        this.imp = c.getInt(c.getColumnIndex("imp"));
        this.obs = c.getString(c.getColumnIndex("obs"));
        this.parc=c.getDouble(c.getColumnIndex("parc"));
        this.nparc=c.getInt(c.getColumnIndex("nparc"));
        this.forma=c.getInt(c.getColumnIndex("forma"));
        this.nomeCliente=c.getString(c.getColumnIndex("nomeCliente"));
        this.prstatus=c.getInt(c.getColumnIndex("prstatus"));
        this.idsmart=c.getInt(c.getColumnIndex("idsmart"));

    }

    public int getIdsmart() {
        return idsmart;
    }

    public void setIdsmart(int idsmart) {
        this.idsmart = idsmart;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public int getPrstatus() {
        return prstatus;
    }

    public void setPrstatus(int prstatus) {
        this.prstatus = prstatus;
    }

    public int getForma() {
        return forma;
    }

    public void setForma(int operacao) {
        this.forma = operacao;
    }

    public int getNparc() {
        return nparc;
    }

    public void setNparc(int nparc) {
        this.nparc = nparc;
    }

    public double getParc() {
        return parc;
    }

    public void setParc(double parc) {
        this.parc = parc;
    }

    public double getValorbruto() {
        return valorbruto;
    }

    public void setValorbruto(double valorbruto) {
        this.valorbruto = valorbruto;
    }

    public double getDesc() {
        return desc;
    }

    public void setDesc(double desc) {
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getImp() {
        return imp;
    }

    public void setImp(int imp) {
        this.imp = imp;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getLoteenvio() {
        return loteenvio;
    }

    public void setLoteenvio(String loteenvio) {
        this.loteenvio = loteenvio;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public double getValortotal() {
        return valortotal;
    }

    public void setValortotal(double valortotal) {
        this.valortotal = valortotal;
    }

    public int getNumped() {
        return numped;
    }

    public void setNumped(int numped) {
        this.numped = numped;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getCondicao() {
        return condicao;
    }

    public void setCondicao(String condicao) {
        this.condicao = condicao;
    }
}
