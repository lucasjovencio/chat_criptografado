/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.awt.HeadlessException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucas
 */
public class Main {
    public static void main(String[] args)  throws IOException {
        String porta = "12345";
        try{
            Server.setServer(new ServerSocket(Integer.parseInt(porta)));
            Server.setClientes(new ArrayList<>());
            while(true){
                System.out.println("Aguardando conexão...");
                Socket con = Server.getServer().accept();
                System.out.println("Cliente conectado...");
                Thread t = new Server(con);
                t.start();   
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    
    

}
