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
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Utilizador
 */
public class ServidorThreadCidadao extends Thread {

    Socket socketUnicastCidadao = null;
    MulticastSocket serverSocketMulticast = null;
    MulticastSocket serverSocketBroadcast = null;
    MulticastSocket serverSocketBroadcastl = null;
    String received = null;
    DatagramPacket packetGroupMulticast = null;
    DatagramPacket packetGroupMulticastl = null;
    InetAddress groupAddress = null;
    InetAddress groupAdressl = null;
    BufferedReader inSocketCidadao = null;
    PrintWriter outCidadao = null;
    ArrayCidadao<Cidadao> cidadao = null;
    ObjPartilhado objPartilhado;
    boolean login = false;
    boolean tentativa = false;
    boolean aceite = false;
    boolean existe = false;
    String buffer = null;
    String bufferNome = null;
    String bufferPassword = null;
    String bufferLocal = null;
    Integer numeroAlerta = null;
    String numeroAlertaPC = null;
    boolean existiu = false;
    boolean eParaRemover = false;
    ArrayList<Integer> pedidosRejeitados = null;
    String local = null;
    InetAddress groupAddressB = null;
    DatagramPacket packetBroadcast = null;

    public ServidorThreadCidadao(Socket client, ObjPartilhado objPartilhado, MulticastSocket ms, MulticastSocket l, MulticastSocket B, InetAddress ia, InetAddress br, InetAddress lisboa, ArrayCidadao ac) throws IOException {
        this.socketUnicastCidadao = client;
        this.objPartilhado = objPartilhado;
        this.serverSocketMulticast = ms;
        this.serverSocketBroadcast = B;
        this.serverSocketBroadcastl = l;
        this.groupAdressl = lisboa;
        this.groupAddressB = br;
        this.groupAddress = ia;
        outCidadao = new PrintWriter(socketUnicastCidadao.getOutputStream(), true);
        inSocketCidadao = new BufferedReader(
                new InputStreamReader(
                        socketUnicastCidadao.getInputStream()));
        this.cidadao = ac;
    }

    @Override
    public void run() {

        //Verificar login/registo do cidadao
        while (!login) {
            tentativa = false;
            try {
                buffer = inSocketCidadao.readLine();
                bufferNome = inSocketCidadao.readLine();
                bufferPassword = inSocketCidadao.readLine();
                bufferLocal = inSocketCidadao.readLine();
                if (buffer.equals("registo")) {
                    for (Object c : cidadao.getCidadao()) {

                        Cidadao cidadao = (Cidadao) c;
                        if (cidadao.getNameCidadao().equals(bufferNome)) {
                            outCidadao.println("LoginInvalido");
                            tentativa = true;
                        }
                    }
                    if (!tentativa) {
                        login = true;
                        Cidadao novoCidadao = new Cidadao(bufferNome, bufferPassword, bufferLocal);
                        cidadao.addCidadao(novoCidadao);
                    }
                } else if (buffer.equals("login")) {
                    if (cidadao.getCidadao().isEmpty()) {
                        outCidadao.println("LoginInvalido");
                    } else {
                        for (Object c : cidadao.getCidadao()) {

                            Cidadao cidad = (Cidadao) c;
                            if (cidad.getNameCidadao().equals(bufferNome)) {
                                if (cidad.getPass().equals(bufferPassword)) {
                                    if(cidad.getLocalidade().equalsIgnoreCase(bufferLocal))
                                    login = true;
                                }
                            }
                        }
                        if (login == false) {
                            outCidadao.println("LoginInvalido");
                        }
                    }
                }
            } catch (IOException ex) {
                System.out.println("Tentativa de login falhada.");
            }
        }
        System.out.println("...new client connected" + " UserName: " + bufferNome);
        outCidadao.println("LoginValido");

        // Recebe message
        while (!socketUnicastCidadao.isClosed()) {
            try {

                if ((received = inSocketCidadao.readLine()).equals("pedido")) {
                    received = inSocketCidadao.readLine();
                    local = received;
                    received = inSocketCidadao.readLine();
                    numeroAlerta = objPartilhado.addNumeroAlerta();
                    objPartilhado.getNumalerta().add(numeroAlerta);

                    if (received != null) {
                        received = "ID: " + numeroAlerta + "\t" + received;
                        System.out.println(received + "\nEnviando mensagem multicast para a proteção civil...");
                        //send
                        if (local.equalsIgnoreCase("lisboa")) {
                            packetGroupMulticastl = new DatagramPacket(received.getBytes(), received.getBytes().length, groupAdressl, Configuracoes.PORT_MULTICAST_LISBOA);
                            serverSocketMulticast.send(packetGroupMulticastl);

                        } else if (local.equalsIgnoreCase("porto")) {
                            packetGroupMulticast = new DatagramPacket(received.getBytes(), received.getBytes().length, groupAddress, Configuracoes.PORT_MULTICAST_PORTO);
                            serverSocketMulticast.send(packetGroupMulticast);
                        }

                        System.out.println("Aguardando resposta...");
                        // Recebe resposta do membros da proteção civil
                        // Aguarda 45 segundos pela resposta de algum mebro da proteção civil            
                        for (int i = 0; i < 30 && !aceite; i++) {
                            //Verifica a cada 1,5 segundos se o pedido foi aceite
                            Thread.sleep(1500);
                            existe = objPartilhado.getNumalerta().contains(numeroAlerta);
                            if (!existe) {
                                objPartilhado.getAlertas().add(received);
                                aceite = true;
                                System.out.println("Alerta " + numeroAlerta + " aceite!!");
                            }
                        }

                        // Responde ao cidadao
                        if (aceite) {
                            outCidadao.println("O seu pedido foi aceite.");

                            int contador = 0;
                            for (String alerta : objPartilhado.getAlertas()) {
                                if (alerta.contains(numeroAlerta.toString())) {
                                    break;
                                }
                                contador++;
                            }
                            if (contador > 0) {
                                System.out.println(contador);
                                String Alerta = objPartilhado.getAlertas().remove(contador);
                                objPartilhado.getAlertas().add(Alerta);
                            }
                        } else {
                            // Envia mensagem negativa para cidadao em caso de nenhum membro da proteção civil ter aceite o alerta
                            outCidadao.println("Ninguem aceitou o seu pedido.");
                        }

                    }

                } else if (received.equals("Alerta")) {

                    for (Integer pedido2 : objPartilhado.getNumalertaPC()) {
                        if (pedido2.toString().equals(numeroAlertaPC)) {

                            if (!(pedidosRejeitados.contains(Integer.parseInt(numeroAlertaPC)))) {
                                //if (pedido2 == Integer.parseInt(numeroAlerta)) {
                                eParaRemover = true;

                                // Responde a proteção civil que aceitou o pedido            
                                outCidadao.println(" confirmada.");
                            }

                        }
                    }

                } else if (received.equals("visualizar")) {
                    String historico = "Estimado cidadao, " + bufferNome + ", segue o seu historico:";
                    for (String string : objPartilhado.getAlertasPC()) {

                        historico = historico + "\n" + string;

                    }
                    if (historico.equals("Estimado cidadao, " + bufferNome + ", segue o seu historico: ")) {
                        outCidadao.println("Ainda nao possui historico...");
                        outCidadao.println("finished");
                    } else {
                        outCidadao.println(historico);
                        outCidadao.println("finished");
                    }
                } else if (received.equals("historico")) {
                    String historico = "Estimado cidadao, " + bufferNome + ", segue o seu historico:";
                    for (String string : objPartilhado.getAlertas()) {

                        historico = historico + "\n" + string;

                    }

                    if (historico.equals("Estimado cidadao, " + bufferNome + ", segue o seu historico:")) {
                        outCidadao.println("Ainda nao possui historico...");
                        outCidadao.println("finished");
                    } else {
                        outCidadao.println(historico);
                        outCidadao.println("finished");
                    }
                }

            } catch (IOException | InterruptedException ex) {
                try {
                    socketUnicastCidadao.close();
                } catch (IOException ex1) {
                    // detetar possíveis erros
                    Logger.getLogger(ServidorThreadCidadao.class.getName()).log(Level.SEVERE, null, ex1);
                    System.out.println("TCP/IP Port" + Configuracoes.PORT_UNICAST_CIDADAO + "is occupied.");
                    ex1.printStackTrace();
                }
                System.out.println("...citizen disconnected");
            }
        }
    }
}
