/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd_lei;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Utilizador
 */
public class Cidadao {

    private String nameCidadao;
    private String pass;
    private String localidade;

    public Cidadao() {
    }

    public Cidadao(String nameCidadao, String pass, String localidade) {
        this.nameCidadao = nameCidadao;
        this.pass = pass;
        this.localidade = localidade;
    }

    public Cidadao(String nameCidadao, String pass) {
        this.nameCidadao = nameCidadao;
        this.pass = pass;
    }

   

    public String getNameCidadao() {
        return nameCidadao;
    }

    public void setNameCidadao(String nameCidadao) {
        this.nameCidadao = nameCidadao;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Socket socketCidadao = null;
        Cidadao c =new Cidadao();
        Catastrofes cd= new Catastrofes();
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            socketCidadao = new Socket(Configuracoes.IP_UNICAST_CIDADAO, Configuracoes.PORT_UNICAST_CIDADAO);
            out = new PrintWriter(socketCidadao.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socketCidadao.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to.");
            System.exit(1);
        }
        new ThreadUnicastCidadao(in, out,c,cd).start();
    }

}
