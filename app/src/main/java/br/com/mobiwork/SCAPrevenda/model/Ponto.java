package br.com.mobiwork.SCAPrevenda.model;

/**
 * Created by LuisGustavo on 20/03/2015.
 */
public class Ponto {
    private int _id;
    private int vendedor;
    private String datainicio;
    private String horainicio;
    private String horafim;
    private String horasaialm ;
    private String horavoltalm ;
    private String enviado;


    public Ponto (){
        _id=0;
        vendedor=0;
        datainicio="";
        horainicio="";
        horafim="";
        horasaialm="";
        horavoltalm="";
        enviado="";
    }

    public String getEnviado() {
        return enviado;
    }

    public void setEnviado(String enviado) {
        this.enviado = enviado;
    }

    public String getHorasaialm() {
        return horasaialm;
    }

    public void setHorasaialm(String horasaialm) {
        this.horasaialm = horasaialm;
    }

    public String getHoravoltalm() {
        return horavoltalm;
    }

    public void setHoravoltalm(String horavoltalm) {
        this.horavoltalm = horavoltalm;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getVendedor() {
        return vendedor;
    }

    public void setVendedor(int vendedor) {
        this.vendedor = vendedor;
    }

    public String getDatainicio() {
        return datainicio;
    }

    public void setDatainicio(String datainicio) {
        this.datainicio = datainicio;
    }

    public String getHorainicio() {
        return horainicio;
    }

    public void setHorainicio(String horainicio) {
        this.horainicio = horainicio;
    }

    public String getHorafim() {
        return horafim;
    }

    public void setHorafim(String horafim) {
        this.horafim = horafim;
    }
}
