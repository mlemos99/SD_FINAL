/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd_lei;

import java.util.ArrayList;

/**
 *
 * @author Utilizador
 */
public class ObjPartilhado {

    private ArrayList<Integer> numalerta = new ArrayList<>();
    private Integer numeroAlerta = 0;
    private Integer numeroAlertaPC=0;
    private ArrayList<Integer> numalertaPC = new ArrayList<>();
    private volatile ArrayList<String> alertas = new ArrayList<>();
    private volatile ArrayList<String> alertasPC= new ArrayList<>();
    
    private volatile ArrayList<String> relatoriosP = new ArrayList<>();

    public ArrayList<String> getRelatoriosP() {
        return relatoriosP;
    }

    public void setRelatoriosP(ArrayList<String> relatoriosP) {
        this.relatoriosP = relatoriosP;
    }

    
   public Integer getNumeroAlerta() {
        return numeroAlerta;
    }

    public Integer addNumeroAlerta() {
        return ++this.numeroAlerta;
    }
     public Integer addNumeroAlertaPC() {
        return ++this.numeroAlertaPC;
    }

    public ArrayList<Integer> getNumalertaPC() {
        return numalertaPC;
    }

    public void setNumalertaPC(ArrayList<Integer> numalertaPC) {
        this.numalertaPC = numalertaPC;
    }
    

    public ArrayList<Integer> getNumalerta() {
        return numalerta;
    }

    public void setNumalerta(ArrayList<Integer> numalerta) {
        this.numalerta = numalerta;
    }

    public ArrayList<String> getAlertas() {
        return alertas;
    }

    public Integer getNumeroAlertaPC() {
        return numeroAlertaPC;
    }

    public ArrayList<String> getAlertasPC() {
        return alertasPC;
    }

    public void setAlertas(ArrayList<String> alertas) {
        this.alertas = alertas;
    }
    public void setAlertasPC(ArrayList<String> aletasPC){
        this.alertasPC=aletasPC;
    }

}
