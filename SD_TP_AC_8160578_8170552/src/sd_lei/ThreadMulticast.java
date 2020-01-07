/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd_lei;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

/**
 *
 * @author Utilizador
 */
public class ThreadMulticast extends Thread {
    MulticastSocket socketMulticastCidadao = null;
    DatagramPacket packetReceive = null;
    String received = null;

    public ThreadMulticast(MulticastSocket ms) {
        this.socketMulticastCidadao = ms;
    }

    @Override
    public void run() {

        // Recebe mensagens do grupo multicast
        while (!socketMulticastCidadao.isClosed()) {
            byte[] buf = new byte[256];

            packetReceive = new DatagramPacket(buf, buf.length);

            try {
                socketMulticastCidadao.receive(packetReceive);
            } catch (IOException ex) {
                socketMulticastCidadao.close();
            }

            received = new String(packetReceive.getData(), 0, packetReceive.getLength());

            System.out.println(received);
        }
    }
}
