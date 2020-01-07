/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd_lei;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author Utilizador
 */
public class UserProtocol {

    String theUser = null;
    BufferedReader inSocket = null;

    public String processUser(Socket socket) throws IOException {
        inSocket = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()));

        theUser = inSocket.readLine();

        return theUser;
    }
}
