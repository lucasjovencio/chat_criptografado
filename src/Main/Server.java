/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author lucas
 */
public class Server extends Thread {

    private static ArrayList<BufferedWriter> clientes;
    private static ServerSocket server;

    public static ServerSocket getServer() {
        return server;
    }

    public static void setClientes(ArrayList<BufferedWriter> clientes) {
        Server.clientes = clientes;
    }

    public static void setServer(ServerSocket server) {
        Server.server = server;
    }
    private String nome;
    private Socket con;
    private InputStream in;
    private InputStreamReader inr;
    private BufferedReader bfr;

    /**
     * Método construtor
     *
     * @param con do tipo Socket
     */
    public Server(Socket con) {
        this.con = con;
        try {
            in = con.getInputStream();
            inr = new InputStreamReader(in);
            bfr = new BufferedReader(inr);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Método run
     */
    @Override
    public void run() {
        try {
            String msg;
            OutputStream ou = this.con.getOutputStream();
            Writer ouw = new OutputStreamWriter(ou);
            BufferedWriter bfw = new BufferedWriter(ouw);
            clientes.add(bfw);
            nome = msg = bfr.readLine();
            try {
                String sairEncript = Criptografar.encrypt("Desconectado");
                while (!sairEncript.equalsIgnoreCase(msg) && msg != null) {
                    msg = bfr.readLine();
                    String msdDecipt = Criptografar.decrypt(msg);
                    sendToAll(bfw, msdDecipt);
                    System.out.println("Encript: " + msg);
                    System.out.println("Decript: " + msdDecipt);
                }
            } catch (Exception e) {
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void sendToAll(BufferedWriter bwSaida, String msg) throws IOException {
        BufferedWriter bwS;
        for (BufferedWriter bw : clientes) {
            bwS = (BufferedWriter) bw;
            if (!(bwSaida == bwS)) {
                bw.write(nome + " -> " + msg + "\r\n");
                bw.flush();
            }
        }
    }

}
