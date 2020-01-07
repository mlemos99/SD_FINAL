/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd_lei;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;

/**
 *
 * @author Utilizador
 */
public class BroadcastingPC extends Thread {

    public InetAddress address;
    public byte[] buffer;
    public String str = null;

    MulticastSocket socket = null;
    DatagramPacket packetReceive = null;
    String received = null;

    public BroadcastingPC(MulticastSocket ms) throws SocketException, IOException {
        socket = new MulticastSocket(Configuracoes.PORT_Broadcat);
        this.socket = ms;
        address = InetAddress.getByName(Configuracoes.IP_Broadcast);

    }

    @Override
    public void run() {
        super.run(); //To change body of generated methods, choose Tools | Templates.
        while (!socket.isClosed()) {
            byte[] buffer = new byte[256];
            packetReceive = new DatagramPacket(buffer, buffer.length);
            try {

                socket.receive(packetReceive);

            } catch (IOException ex) {
                socket.close();

            }
            received=new String(packetReceive.getData(),0,packetReceive.getLength());
            System.out.println(received);
        }
    }

}
