/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd_lei;

/**
 *
 * @author Utilizador
 */
public class Catastrofes {
    private String evento;
    private int grau_perigo;
    private String incidencia;
    private String local;
    private String descricao;

    public Catastrofes() {
    }
    
    

    public Catastrofes(String evento, int grau_perigo, String incidencia,String local, String descricao) {
        this.evento = evento;
        this.grau_perigo = grau_perigo;
        this.incidencia = incidencia;
        this.local=local;
        this.descricao=descricao;
        
    }

    public String getEvento() {
        return evento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public int getGrau_perigo() {
        return grau_perigo;
    }

    public void setGrau_perigo(int grau_perigo) {
        this.grau_perigo = grau_perigo;
    }

    public String getIncidencia() {
        return incidencia;
    }

    public void setIncidencia(String incidencia) {
        this.incidencia = incidencia;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }
    
    
    
    
    
    
}
