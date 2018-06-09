/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import javax.swing.*;

/**
 *
 * @author lucas & Fabim & Kaleber
 */
public class Client extends JFrame implements ActionListener, KeyListener {

    private static final long serialVersionUID = 1L;
    private JTextArea texto;
    private JTextField txtMsg;
    private JButton btnSend;
    private JButton btnSair;
    private JLabel lblHistorico;
    private JLabel lblMsg;
    private JPanel pnlContent;
    private Socket socket;
    private OutputStream ou;
    private Writer ouw;
    private BufferedWriter bfw;
    private JTextField txtIP;
    private JTextField txtPorta;
    private JTextField txtNome;

    /**
     * Metodo para criar uma janela no browser para que o cliente informe o seu nome 
     * e possa participar do chat.
     * @throws IOException
     * 
     */
    public Client() throws IOException {
        JLabel lblMessage = new JLabel("Verificar!");
        txtIP = new JTextField("127.0.0.1");
        txtPorta = new JTextField("12345");
        txtNome = new JTextField("Cliente");
        Object[] texts = {lblMessage,txtNome};
        JOptionPane.showMessageDialog(null, texts);
        pnlContent = new JPanel();
        texto = new JTextArea(10, 20);
        texto.setEditable(false);
        texto.setBackground(new Color(240, 240, 240));
        txtMsg = new JTextField(20);
        lblHistorico = new JLabel("Histórico");
        lblMsg = new JLabel("Mensagem");
        btnSend = new JButton("Enviar");
        btnSend.setToolTipText("Enviar Mensagem");
        btnSair = new JButton("Sair");
        btnSair.setToolTipText("Sair do Chat");
        btnSend.addActionListener(this);
        btnSair.addActionListener(this);
        btnSend.addKeyListener(this);
        txtMsg.addKeyListener(this);
        JScrollPane scroll = new JScrollPane(texto);
        texto.setLineWrap(true);
        pnlContent.add(lblHistorico);
        pnlContent.add(scroll);
        pnlContent.add(lblMsg);
        pnlContent.add(txtMsg);
        pnlContent.add(btnSair);
        pnlContent.add(btnSend);
        pnlContent.setBackground(Color.LIGHT_GRAY);
        texto.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
        txtMsg.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
        setTitle(txtNome.getText());
        setContentPane(pnlContent);
        setLocationRelativeTo(null);
        setResizable(false);
        setSize(250, 300);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * *
     * Método usado para conectar no server socket, retorna IO Exception caso dê
     * algum erro.
     *
     * @throws IOException
     */
    public void conectar() throws IOException {
        socket = new Socket(txtIP.getText(), Integer.parseInt(txtPorta.getText()));
        ou = socket.getOutputStream();
        ouw = new OutputStreamWriter(ou);
        bfw = new BufferedWriter(ouw);
        bfw.write(txtNome.getText() + "\r\n");
        bfw.flush();
    }

    /**
     * *
     * Método usado para enviar mensagem criptografada para o server socket
     *
     * @param msg do tipo String
     * @throws IOException retorna IO Exception caso dê algum erro.
     */
    public void enviarMensagem(String msg) throws IOException {
        String msdDecipt= "";
        try {
            if (msg.equals("Sair")) {
                String msgEncript = Criptografar.encrypt("Desconectado");
                bfw.write(msgEncript+"\r\n");
                texto.append("Desconectado \r\n");
            } else {
                String msgCripto = Criptografar.encrypt(msg);
                bfw.write(msgCripto+ "\r\n");
                System.out.println("Enviado: Decript: " + msgCripto);
                
                texto.append(txtNome.getText() + " diz -> " + txtMsg.getText() + "\r\n");
            }
            bfw.flush();
            txtMsg.setText("");
        } catch (Exception e) {
        }

    }

    /**
     * Método usado para receber mensagem criptografada do servidor, em seguida o metodo executa
     * a descriptografia e exibir a mensagem para o cliente.
     *
     * @throws IOException retorna IO Exception caso dê algum erro.
     */
    public void escutar() throws IOException {
        InputStream in = socket.getInputStream();
        InputStreamReader inr = new InputStreamReader(in);
        BufferedReader bfr = new BufferedReader(inr);
        String msg = "";
        String msdDecipt= "";
        String txt[] = null;
        while (!"Sair".equalsIgnoreCase(msg)) {
            if (bfr.ready()) {
                msg = bfr.readLine();
                try{
                    txt = msg.split(" ");
                    msdDecipt = Criptografar.decrypt(txt[2]);
                    
                    System.out.println("Recebido: " + txt[1]);
                    System.out.println("Recebido Decript: " + msdDecipt);
                }catch(Exception e){
                    System.out.println(e);
                }
                if (msg.equals("Sair")) {
                    texto.append("Servidor caiu! \r\n");
                } else {
                    texto.append(txt[0] + txt[1] + msdDecipt + "\r\n");
                }
            }
        }
    }

    /**
     * *
     * Método usado quando o usuário clica em sair
     *
     * @throws IOException retorna IO Exception caso dê algum erro.
     */
    public void sair() throws IOException {

        enviarMensagem("Sair");
        bfw.close();
        ouw.close();
        ou.close();
        socket.close();
    }

    /**
     * *
     * Método usado para Eevento clicque, verifica se o cliente clicou em sair ou enviar mensagem.
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            if (e.getActionCommand().equals(btnSend.getActionCommand())) {
                enviarMensagem(txtMsg.getText());
            } else if (e.getActionCommand().equals(btnSair.getActionCommand())) {
                sair();
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block

        }
    }
    
    /**
     * *
     * Método usado para Eevento de pressionar a tecla ENTER, é enviado uma mensagem para o servidor.
     *
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                enviarMensagem(txtMsg.getText());
            } catch (IOException e1) {
                // TODO Auto-generated catch block

            }
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub               
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub               
    }

    /**
     *  Metodo Estatico para iniciar o cliente de forma manual.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        Client app = new Client();
        app.conectar();
        app.escutar();
    }
}
