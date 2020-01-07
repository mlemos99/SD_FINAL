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
public class ServidorThreadPC extends Thread {

    Socket socketUnicastPC = null;
    MulticastSocket serverSocketMulticast = null;
    MulticastSocket serverSocketMulticastl = null;
    MulticastSocket serverSocketBroadcast = null;
    String received = null;
    String numeroAlerta = null;
    Integer numeroAlertaPC = 0;
    BufferedReader inSocketPC = null;
    PrintWriter outPC = null;
    ArrayProtecaoCivil<ProtecaoCivil> protecaoC = null;
    ObjPartilhado objPartilhado;
    DatagramPacket packetGroupMulticast = null;
    DatagramPacket packetGroupBroadcast = null;
    DatagramPacket packetGroupMulticastl = null;
    InetAddress groupAddressB = null;
    InetAddress groupAddressl = null;
    InetAddress groupAddress = null;
    boolean login = false;
    boolean tentativa = false;
    boolean existiu = false;
    boolean eParaRemover = false;
    String buffer;
    String bufferNome;
    String bufferPassword;
    ArrayList<Integer> pedidosRejeitados = null;
    String local = null;
    String incidencia = null;
    String bufferLocalidade =null;

    public ServidorThreadPC(Socket s, ObjPartilhado objSharing, MulticastSocket ms, MulticastSocket l, MulticastSocket B, InetAddress ia, InetAddress br, InetAddress lisboa, ArrayProtecaoCivil ac) throws IOException {
        this.socketUnicastPC = s;
        this.objPartilhado = objSharing;
        this.protecaoC = ac;
        this.serverSocketMulticastl = l;
        this.serverSocketMulticast = ms;
        this.serverSocketBroadcast = B;
        this.groupAddressB = br;
        this.groupAddress = ia;
        this.groupAddressl = lisboa;
        
        outPC = new PrintWriter(socketUnicastPC.getOutputStream(), true);
        inSocketPC = new BufferedReader(
                new InputStreamReader(
                        socketUnicastPC.getInputStream()));
        pedidosRejeitados = new ArrayList();
    }

    @Override
    public void run() {

        //Verificar login/registo do membro da proteção civil
        try {
            while (!login) {
                tentativa = false;
                buffer = inSocketPC.readLine();
                bufferNome = inSocketPC.readLine();
                bufferPassword = inSocketPC.readLine();
                bufferLocalidade=inSocketPC.readLine();
                if (buffer.equals("registo")) {
                    for (Object c : protecaoC.getPC()) {

                        ProtecaoCivil pc = (ProtecaoCivil) c;
                        if (pc.getName_util().equals(bufferNome)) {
                            outPC.println("LoginInvalido");
                            tentativa = true;
                        }
                    }
                    if (!tentativa) {
                        login = true;

                        ProtecaoCivil novoPC = new ProtecaoCivil(bufferNome, bufferPassword,bufferLocalidade);
                        protecaoC.addPC(novoPC);
                    }
                } else if (buffer.equals("login")) {
                    if (protecaoC.getPC().isEmpty()) {
                        outPC.println("LoginInvalido");
                    } else {
                        for(Object c : protecaoC.getPC()) {

                            ProtecaoCivil p = (ProtecaoCivil) c;
                            if (p.getName_util().equals(bufferNome)) {
                                if (p.getPassprot().equals(bufferPassword)) {
                                 if(p.getLocalidade().equalsIgnoreCase(bufferLocalidade))   
                                    login = true;
                                 
                                }
                            }
                        }
                        if (login == false) {
                            outPC.println("LoginInvalido");
                        }
                    }
                }
            }
            System.out.println("...new PC connected" + " UserName: " + bufferNome);
            outPC.println("LoginValido");

            // Recebe message
            while (!socketUnicastPC.isClosed()) {

                eParaRemover = false;
                existiu = false;
                if ((received = inSocketPC.readLine()).equals("pedido")) {

                    numeroAlerta = inSocketPC.readLine();

                    if (numeroAlerta != null && !objPartilhado.getNumalerta().isEmpty()) {

                        if (objPartilhado.getNumalerta().contains(Integer.parseInt(numeroAlerta))) {

                            existiu = true;

                            outPC.println("Digite: 'aceitar' / 'rejeitar'");

                            received = inSocketPC.readLine();

                            if (received.equalsIgnoreCase("aceitar")) {
                                for (Integer pedido2 : objPartilhado.getNumalerta()) {
                                    if (pedido2.toString().equals(numeroAlerta)) {

                                        if (!(pedidosRejeitados.contains(Integer.parseInt(numeroAlerta)))) {

                                            eParaRemover = true;

                                            // Responde ao membro da proteção civil que aceitou o pedido            
                                            outPC.println("Alerta confirmado.");
                                        }

                                    }
                                }
                            } else if (received.equalsIgnoreCase("rejeitar")) {

                                for (Integer pedido2 : objPartilhado.getNumalerta()) {
                                    if (pedido2.toString().equals(numeroAlerta)) {

                                        pedidosRejeitados.add(Integer.parseInt(numeroAlerta));

                                        // Responde ao membro da proteção civil que rejeitou o pedido            
                                        outPC.println("Alerta " + numeroAlerta + " rejeitado.");
                                    }
                                }
                            }

                        } else {
                            outPC.println("Alerta não existe");
                        }
                        if (eParaRemover) {
                            int index = objPartilhado.getNumalerta().indexOf(Integer.parseInt(numeroAlerta));
                            if (objPartilhado.getAlertas() != null) {
                                objPartilhado.getNumalerta().remove(index);
                            }

                            int contador = 0;
                            for (String alertas : objPartilhado.getAlertas()) {
                                if (alertas.contains(numeroAlerta)) {
                                    break;
                                }
                                contador++;
                            }
                            if (contador > 0) {
                                System.out.println(contador);
                                String Alertas = objPartilhado.getAlertas().remove(contador);
                                objPartilhado.getAlertas().add(Alertas + "\tPC: " + bufferNome);
                            } else {
                                System.out.println("Já foi aceite!!!");
                            }
                        } else if (existiu) {
                            outPC.println("O alerta já foi aceite");
                        } else {
                            outPC.println("alerta não existe");
                        }
                    } else {
                        outPC.println("alerta não existe");
                    }

                } else if (received.equals("Alerta")) {
                    received = inSocketPC.readLine();
                    local = received;
                    received=inSocketPC.readLine();
                    incidencia=received;
                    received = inSocketPC.readLine();
                    numeroAlertaPC = objPartilhado.addNumeroAlerta();
                    objPartilhado.getNumalertaPC().add(numeroAlertaPC);

                    if (received != null) {
                        received = "ID: " + numeroAlertaPC + "\t" + received;
                        System.out.println(received + "\nEnviando mensagem multicast para o cidadao...");
                        if (incidencia.equalsIgnoreCase("local")) {
                            if (local.equalsIgnoreCase("lisboa")) {
                                packetGroupMulticastl = new DatagramPacket(received.getBytes(), received.getBytes().length, groupAddressl, Configuracoes.PORT_MULTICAST_LISBOA);
                                serverSocketMulticast.send(packetGroupMulticastl);

                            }else if(local.equalsIgnoreCase("porto")){
                                packetGroupMulticast = new DatagramPacket(received.getBytes(), received.getBytes().length, groupAddress, Configuracoes.PORT_MULTICAST_PORTO);
                                serverSocketMulticast.send(packetGroupMulticast);
                            }
                               
                        } else if(incidencia.equalsIgnoreCase("nacional")) {
                            packetGroupBroadcast = new DatagramPacket(received.getBytes(), received.getBytes().length, groupAddressB, Configuracoes.PORT_Broadcat);
                            serverSocketBroadcast.send(packetGroupBroadcast);
                        }
                        objPartilhado.getAlertasPC().add(received);

                        // Responde ao cidadao
                        int contador = 0;
                        for (String alerta : objPartilhado.getAlertasPC()) {
                            if (alerta.contains(numeroAlertaPC.toString())) {
                                break;
                            }
                            contador++;
                        }
                        String Alerta = objPartilhado.getAlertasPC().remove(contador);

                        objPartilhado.getAlertasPC().add(Alerta);

                    }
                } else if (received.equals("historico")) {
                    String historico = "Estimado PC, " + bufferNome + ", segue o seu historico:";
                    for (String string : objPartilhado.getAlertas()) {

                        historico = historico + "\n" + string;

                    }
                    if (historico.equals("Estimado PC, " + bufferNome + ", segue o seu historico:")) {
                        outPC.println("Ainda nao possui historico...");
                        outPC.println("finished");
                    } else {
                        outPC.println(historico);
                        outPC.println("finished");
                    }
                } else if (received.equals("visualizar")) {
                    String historico = "Estimado PC, " + bufferNome + ", segue o seu historico:";
                    for (String string : objPartilhado.getAlertasPC()) {

                        historico = historico + "\n" + string;

                    }
                    if (historico.equals("Estimado PC, " + bufferNome + ", segue o seu historico:")) {
                        outPC.println("Ainda nao possui historico...");
                        outPC.println("finished");
                    } else {
                        outPC.println(historico);
                        outPC.println("finished");
                    }
                } 
            }
        } catch (IOException ex) {
            try {
                socketUnicastPC.close();
            } catch (IOException ex1) {
                // detetar possíveis erros
                Logger.getLogger(ServidorThreadCidadao.class.getName()).log(Level.SEVERE, null, ex1);
                System.out.println("TCP/IP Port" + Configuracoes.PORT_UNICAST_PROTECAO + "is occupied.");
                ex1.printStackTrace();
            }
            System.out.println("...Proteção civil disconnected");
        }
    }
}
