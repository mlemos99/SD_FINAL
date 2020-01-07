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

/**
 *
 * @author Utilizador
 */
public class ThreadUnicastPC extends Thread {

    String local = null;
    String incidencia = null;
    String Grau_perigo = null;
    String localidade = null;
    String Evento = null;
    SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    String pedido = null;
    private String send = null;
    private String received = null;
    private String buffer = null;
    private BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
    private BufferedReader in = null;
    private PrintWriter out = null;
    private ProtecaoCivil pc;
    private Catastrofes c;
    private boolean login = false;
    private boolean listening = true;
    private String escolha = null;

    public ThreadUnicastPC(BufferedReader b, PrintWriter p, ProtecaoCivil pc) {
        super("Threadprotecaocivil");
        this.in = b;
        this.out = p;
        this.pc = pc;
        this.c = new Catastrofes();

    }

    @Override
    public void run() {

        try {
            out.println("Proteção civil!");

            while (!login) {
                System.out.println("Login Proteção civil!!!");
                System.out.println("Já possui conta? s/n");

                if (teclado.readLine().equalsIgnoreCase("n")) {
                    out.println("registo");
                    System.out.println("******** REGISTO PC********");
                    System.out.print("Username: ");
                    out.println(buffer = teclado.readLine());
                    pc.setName_util(buffer);
                    System.out.print("Password: ");
                    out.println(buffer = teclado.readLine());
                    pc.setPassprot(buffer);
                    System.out.println("Meta porto ou lisboa");
                    System.out.print("Localidade: ");
                    out.println(buffer = teclado.readLine());
                    localidade = buffer;
                    pc.setLocalidade(buffer);

                } else {
                    out.println("login");
                    System.out.println("******** LOGIN ********");
                    System.out.print("Username: ");
                    out.println(buffer = teclado.readLine());
                    pc.setName_util(buffer);
                    System.out.print("Password: ");
                    out.println(buffer = teclado.readLine());
                    pc.setPassprot(buffer);
                    System.out.print("Localidade: ");
                    out.println(buffer = teclado.readLine());
                    pc.setLocalidade(buffer);
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

        System.out.println("Bem vindo " + pc.getName_util() + "!!");

        try {
            //para aceder ao grupo broadcast
            MulticastSocket socketBroadcastpc = new MulticastSocket(Configuracoes.PORT_Broadcat);
            InetAddress groupAddressB = InetAddress.getByName(Configuracoes.IP_Broadcast);
            socketBroadcastpc.joinGroup(groupAddressB);
            Thread Broadcast = new Thread(new ThreadMulticast(socketBroadcastpc));
            Broadcast.start();
            Thread multicast = null;
            if (pc.getLocalidade().equalsIgnoreCase("lisboa")) {

                // Para aceder ao grupo multicast lisboa
                MulticastSocket socketMulticastL = new MulticastSocket(Configuracoes.PORT_MULTICAST_LISBOA);
                InetAddress groupAddressL = InetAddress.getByName(Configuracoes.IP_MULTICAST_LISBOA);
                socketMulticastL.joinGroup(groupAddressL);
                multicast = new Thread(new ThreadMulticast(socketMulticastL));
                multicast.start();
            } else if (pc.getLocalidade().equalsIgnoreCase("porto")) {
                // Para aceder ao grupo multicast porto
                MulticastSocket socketMulticast = new MulticastSocket(Configuracoes.PORT_MULTICAST_PORTO);
                InetAddress groupAddress = InetAddress.getByName(Configuracoes.IP_MULTICAST_PORTO);
                socketMulticast.joinGroup(groupAddress);
                multicast = new Thread(new ThreadMulticast(socketMulticast));
                multicast.start();
            }

            while (listening) {

                System.out.println("1- Aceitar" + "\n" + "2- Visualizar alertas PC" + "\n" + "3- Visualizar alertas cidadões" + "\n" + "4- Fazer um alerta" + "\n" + "0 -Sair" + "\n");

                escolha = teclado.readLine();
                switch (escolha) {
                    case "1":
                        out.println("pedido");
                        System.out.println("******** Aceitar Pedido ********");
                        System.out.println("Escreva o id do pedido: ");

                        // accept request
                        send = teclado.readLine();
                        out.println(send);
                        // get response
                        received = in.readLine();

                        // display response
                        System.out.println(received);

                        if (received.equals("Digite: 'aceitar' / 'rejeitar'")) {
                            send = teclado.readLine();
                            out.println(send);
                            received = in.readLine();
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
                        out.println("historico");
                        while (!(received = in.readLine()).equals("finished")) {
                            System.out.println(received);
                        }
                        break;
                    case "4":
                        out.println("Alerta");
                        //Evento
                        System.out.println("******** Criar Alerta ********");
                        System.out.println("Evento:");
                        Evento = teclado.readLine();
                        c.setEvento(Evento);

                        //Grau de perigo
                        System.out.println("Grau de perigo: [1-3] ");
                        Grau_perigo = teclado.readLine();
                        c.setGrau_perigo(Integer.parseInt(Grau_perigo));

                        // local request
                        System.out.print("Local: ");
                        local = teclado.readLine();
                        c.setLocal(local);

                        //incidencia
                        System.out.println("Ponha local ou nacional ");
                        System.out.println("Incidencia:");
                        incidencia = teclado.readLine();
                        c.setIncidencia(incidencia);

                        Date dt = new Date();
                        out.println(local);
                        out.println(incidencia);
                        pedido = formato.format(dt) + "\tProteção civil|operador: " + pc.getName_util() + " | Evento: " + c.getEvento() + " | Grau de perigo: " + c.getGrau_perigo() + " |Incidência: " + c.getIncidencia() + "| Local:" + c.getLocal();
                        out.println(pedido);

                        break;
                    case "0":
                        System.out.println("Sessão terminada. Até já " + pc.getName_util() + "!!");
                        listening = false;
                        System.exit(0);
                        break;
                }

            }

            multicast.stop();
            multicast.join();
            Broadcast.stop();;
            Broadcast.join();
        } catch (IOException | InterruptedException ex) {

        }
    }

}
