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
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author lucas & Fabim & Kaleber
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
     * Método construtor usado para iniciar o servidor
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
     * Método run, metodo usado para fica escudo mensagens enviadas para o servidor,
     * é verificado se o cliente escolheu se desconectar ou enviar uma mensagem.
     * Caso tenha escolhido enviar mensagem então o servidor retorna a mensagem enviada para todos os clientes
     * conectados ao servidor. Caso contrario é desconectado do servidor. Para se o servidor envie a mensagem para
     * todos os clientes ele utiliza um Metodo chamado: #sendToAll.
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
                    sendToAll(bfw, msg);
                }
            } catch (IOException | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
                System.out.println(e);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Método sendToAll, metodo usado para recuperar o nome do cliente que enviou a mensagem e
     * a mensagem do cliente, feito isso é enviado a mensagem para todos os outros clientes respeitando a
     * estrutura de: { Nome_Do_Cliente -> Mensagem_Enviada }
     */
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
