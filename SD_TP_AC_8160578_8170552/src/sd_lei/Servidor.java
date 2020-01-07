/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd_lei;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Utilizador
 */
public class Servidor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        boolean listening = true;
        ServerSocket serverSocketUnicastCidadao = null;
        MulticastSocket serverSocketBroadcast =null;
        InetAddress groupAddressB = null;
        MulticastSocket serverSocketMulticast = null;
        InetAddress groupAddress = null;
        InetAddress groupAddressL = null;
        ArrayCidadao<Cidadao> cidadao = new ArrayCidadao<>();
        ArrayProtecaoCivil<ProtecaoCivil> PC = new ArrayProtecaoCivil<>();
        ObjPartilhado objSharing = new ObjPartilhado();
        MulticastSocket serverSocketMulticastL = null;
        

        try { // Inicializar ServerSocket para os Cidadãos
            serverSocketUnicastCidadao = new ServerSocket(Configuracoes.PORT_UNICAST_CIDADAO);
            System.out.println("Server running on port: " + Configuracoes.PORT_UNICAST_CIDADAO);

        } catch (IOException e) {
            System.out.println("Could not listen on port: " + Configuracoes.PORT_UNICAST_CIDADAO);
            System.exit(-1);
        }


        try { // Inicializar MulticastSocket para enviar os alertas recebidos para os do Porto
            serverSocketMulticast = new MulticastSocket(Configuracoes.PORT_MULTICAST_PORTO);
            groupAddress = InetAddress.getByName(Configuracoes.IP_MULTICAST_PORTO);
            serverSocketMulticast.joinGroup(groupAddress);
            System.out.println("Server join the group multicast on port: " + Configuracoes.PORT_MULTICAST_PORTO);
        } catch (IOException e) {
            System.out.println("Could not listen on port: " + Configuracoes.PORT_MULTICAST_PORTO);
            System.exit(-1);
        }
         try { // Inicializar MulticastSocket para enviar os alertas recebidos para os do Lisboa
            serverSocketMulticastL = new MulticastSocket(Configuracoes.PORT_MULTICAST_LISBOA);
            groupAddressL = InetAddress.getByName(Configuracoes.IP_MULTICAST_LISBOA);
            serverSocketMulticastL.joinGroup(groupAddressL);
            System.out.println("Server join the group multicast on port: " + Configuracoes.PORT_MULTICAST_LISBOA);
        } catch (IOException e) {
            System.out.println("Could not listen on port: " + Configuracoes.PORT_MULTICAST_LISBOA);
            System.exit(-1);
        }
        try{// Inicializar Broadcast para enviar alertas
            serverSocketBroadcast = new MulticastSocket(Configuracoes.PORT_Broadcat);
            groupAddressB = InetAddress.getByName(Configuracoes.IP_Broadcast);
            serverSocketBroadcast.joinGroup(groupAddressB);
            System.out.println("Server join the group multicast on port: " + Configuracoes.PORT_Broadcat);
        }catch (IOException e) {
            System.out.println("Could not listen on port: " + Configuracoes.PORT_Broadcat);
            System.exit(-1);
        }


        System.out.println("Server wainting for connection...");

        UserProtocol protocol = new UserProtocol();
         
        while (listening) {
            Socket socket = serverSocketUnicastCidadao.accept();
            String user = protocol.processUser(socket);
            
            switch (user) {
                case "Cidadão":
                    new ServidorThreadCidadao(socket, objSharing, serverSocketMulticast,serverSocketMulticastL,serverSocketBroadcast,groupAddress, groupAddressB,groupAddressL ,cidadao).start();
                    break;
                case "Proteção civil!":
                    new ServidorThreadPC(socket, objSharing, serverSocketMulticast,serverSocketMulticastL,serverSocketBroadcast, groupAddress,groupAddressB,groupAddressL, PC).start();
                    break;
                default:
                    System.out.println("Tentativa de conexão falhada.");
                    break;
            }
        }
        serverSocketBroadcast.leaveGroup(groupAddressB);
        serverSocketBroadcast.close();
        serverSocketMulticast.leaveGroup(groupAddress);
        serverSocketMulticast.close();
        serverSocketMulticastL.leaveGroup(groupAddressL);
        serverSocketMulticastL.close();
        
    }
}
