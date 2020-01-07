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
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Utilizador
 */
public class ThreadUnicastCidadao extends Thread {

    String local = null;
    String Grau_perigo = null;
    String descricao = null;
    SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    String pedido = null;
    String received = null;
    String buffer = null;
    Catastrofes c = null;
    BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
    BufferedReader in = null;
    PrintWriter out = null;
    boolean listening = true;
    boolean login = false;
    Cidadao cidadao = null;
    String escolha = null;

    public ThreadUnicastCidadao(BufferedReader b, PrintWriter p, Cidadao cd, Catastrofes cdd) {
        super("ThreadCidadao");
        this.cidadao = cd;
        this.c = cdd;
        in = b;
        out = p;
    }

    @Override
    public void run() {
        super.run();
        try {
            out.println("Cidadão");
            System.out.println("Login Cidadão!!!");

            //para aceder ao grupo broadcast
            MulticastSocket socketBroadcastcidadao = new MulticastSocket(Configuracoes.PORT_Broadcat);
            InetAddress groupAddressB = InetAddress.getByName(Configuracoes.IP_Broadcast);
            socketBroadcastcidadao.joinGroup(groupAddressB);
            Thread Broadcast = new Thread(new ThreadMulticast(socketBroadcastcidadao));
            Broadcast.start();

            while (!login) {

                System.out.println("Já possui conta? s/n");

                if (teclado.readLine().equalsIgnoreCase("n")) {
                    out.println("registo");
                    System.out.println("******** REGISTO CIDADAO********");
                    System.out.print("Username: ");
                    out.println(buffer = teclado.readLine());
                    cidadao.setNameCidadao(buffer);
                    System.out.print("Password: ");
                    out.println(buffer = teclado.readLine());
                    cidadao.setPass(buffer);
                    System.out.println("Meta porto ou lisboa");
                    System.out.println("Localidade:");
                    out.println(buffer = teclado.readLine());
                    cidadao.setLocalidade(buffer);

                } else {
                    out.println("login");
                    System.out.println("******** LOGIN ********");
                    System.out.print("Username: ");
                    out.println(buffer = teclado.readLine());
                    cidadao.setNameCidadao(buffer);
                    System.out.print("Password: ");
                    out.println(buffer = teclado.readLine());
                    cidadao.setPass(buffer);
                    System.out.println("Localidade:");
                    out.println(buffer = teclado.readLine());
                    cidadao.setLocalidade(buffer);
                }
                login = true;
                if (in.readLine().equals("LoginInvalido")) {
                    login = false;
                    System.out.println("Tente novamente.");
                }
            }
        } catch (IOException ex) {
            System.out.println("Tentativa de login falhada.");
        }

        System.out.println("Bem vindo " + cidadao.getNameCidadao() + "!!");
        try {
            if (cidadao.getLocalidade().equalsIgnoreCase("lisboa")) {

                // Para aceder ao grupo multicast de lisboa
                MulticastSocket socketMulticastL = new MulticastSocket(Configuracoes.PORT_MULTICAST_LISBOA);
                InetAddress groupAddressL = InetAddress.getByName(Configuracoes.IP_MULTICAST_LISBOA);
                socketMulticastL.joinGroup(groupAddressL);
                Thread multicastL = new Thread(new ThreadMulticast(socketMulticastL));
                multicastL.start();
            } else if (cidadao.getLocalidade().equalsIgnoreCase("porto")) {
                // Para aceder ao grupo multicast porto
                MulticastSocket socketMulticast = new MulticastSocket(Configuracoes.PORT_MULTICAST_PORTO);
                InetAddress groupAddress = InetAddress.getByName(Configuracoes.IP_MULTICAST_PORTO);
                socketMulticast.joinGroup(groupAddress);
                Thread multicast = new Thread(new ThreadMulticast(socketMulticast));
                multicast.start();
            }
        } catch (IOException ex) {
            System.out.println("tentativa de aceder ao grupo invalida!!");
        }

        while (listening) {
            try {
                System.out.println("1- Visualizar alertas de cidadões" + "\n" + "2- Visualizar alertas PC" + "\n" + "3- Fazer um alerta" + "\n" + "0 -Sair" + "\n");
                escolha = teclado.readLine();

                switch (escolha) {
                    case "1":
                        out.println("historico");
                        while (!(received = in.readLine()).equals("finished")) {
                            System.out.println(received);
                        }
                        break;
                    case "2":
                        out.println("visualizar");
                        while (!(received = in.readLine()).equals("finished")) {
                            System.out.println(received);
                        }
                        break;
                    case "3":
                        out.println("pedido");

                        // local request
                        System.out.println("******** Criar Alerta ********");
                        System.out.print("Local: ");
                        local = teclado.readLine();
                        c.setLocal(local);

                        //Grau de perigo
                        System.out.println("Grau de perigo: [1-3] ");
                        Grau_perigo = teclado.readLine();
                        c.setGrau_perigo(Integer.parseInt(Grau_perigo));

                        //descrição
                        System.out.println("Descrição:");
                        descricao = teclado.readLine();
                        c.setEvento(descricao);

                        Date dt = new Date();
                        out.println(c.getLocal());
                        pedido = formato.format(dt) + "\tCidadao: " + cidadao.getNameCidadao() + " | Evento: " + c.getEvento() + "  Grau de perigo: " + c.getGrau_perigo() + "Local:" + c.getLocal();
                        out.println(pedido);

                        break;

                    case "0":

                        System.out.println("Sessão terminada, Ate já " + cidadao.getNameCidadao() + "!!");
                        Thread.sleep(1000);
                        listening = false;
                        System.exit(0);
                }

            } catch (IOException ex1) {

            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadUnicastCidadao.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
