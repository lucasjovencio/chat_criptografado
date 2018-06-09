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
 * @author lucas & Fabim & Kaleber
 */
public class Main {

    public static void main(String[] args) throws IOException {
        String porta = "12345";
        
        /*
            * Inicia uma Thread para Startar o servidor de mensagens
        */
        new Thread() {
            @Override
            public void run() {
                try {
                    Server.setServer(new ServerSocket(Integer.parseInt(porta)));
                    Server.setClientes(new ArrayList<>());
                    while (true) {
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
        }.start();
        /*
            * Inicia uma Thread iniciar um cliente
        */
        new Thread() {
            @Override
            public void run() {
                try {
                    Client app = new Client();
                    app.conectar();
                    app.escutar();
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        }.start();
        
        /*
            * Inicia uma Thread iniciar um cliente
        */
        new Thread() {
            @Override
            public void run() {
                try {
                    Client app = new Client();
                    app.conectar();
                    app.escutar();
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        }.start();
    }
}
