package sd_lei;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Utilizador
 */
public class ProtecaoCivil {

    private String name_util;
    private String passprot;
    private String localidade;

    public ProtecaoCivil(String name_util, String passprot, String localidade) {
        this.name_util = name_util;
        this.passprot = passprot;
        this.localidade = localidade;
    }

    public ProtecaoCivil() {
    }

    

    public String getName_util() {
        return name_util;
    }

    public void setName_util(String name_util) {
        this.name_util = name_util;
    }

    public String getPassprot() {
        return passprot;
    }

    public void setPassprot(String passprot) {
        this.passprot = passprot;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public static void main(String[] args) {
          
        Socket socketPC = null;
        PrintWriter out = null;
        BufferedReader in = null;

        ProtecaoCivil pc = new ProtecaoCivil();
        Catastrofes c = new Catastrofes();

        try {
            socketPC = new Socket(Configuracoes.IP_UNICAST_PROTECAO, Configuracoes.PORT_UNICAST_PROTECAO);
            out = new PrintWriter(socketPC.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                    socketPC.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: site.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to: site.");
            System.exit(1);
        }

        new ThreadUnicastPC(in, out, pc).start();
    }

}
