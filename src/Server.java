package src;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Server extends JFrame{

    private JTextField userText;
    private JTextArea chatWindow;

    // when communicating between two computers an input and output stream are needed
    //output stream is packaged information that goes away from you
    // input stream is packaged info that comes to you

    private ObjectOutputStream output;
    private ObjectInputStream input;

    // server socket variable

    private ServerSocket server;

    // socket variable - connection between two computers

    private Socket connection;

    //constructor

    public Server(){
        super("My Instant Messaging Application");
        userText = new JTextField();

        //before connecting to anyone, we will block any potential of writing messages in the messaging box

        userText.setEditable(false);

        userText.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sendMessage(e.getActionCommand());
                        userText.setText("");
                    }
                }
        );

        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(400,200);
        setVisible(true);

    }

    // build and run server

    public void runServer(){
        try{
            server = new ServerSocket(6789, 100);
            while(true){

                try{
                    waitForConnection();

                    // setup input and output streams

                    setupStreams();

                    // allows us to send messages back and forth

                    whileChatting();

                }catch(EOFException eofException){
                    showMessage("\n server ended the connection.");
                }finally{
                    closeDown();
                }

            }

        }catch(IOException ioException){
            ioException.printStackTrace();
        }

    }


    // wait for connection method then display connection info

    private void waitForConnection() throws IOException{

        showMessage(" Waiting for someone to connect... \n");

        //connection is created between you and other computer
        connection = server.accept();
        // show that you are now connected
        showMessage(" Now connected to " + connection.getInetAddress().getHostName());
    }

    //build stream to send a receive data

    private void setupStreams() throws IOException{

        //setup output stream

        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();

        //setup input stream

        input = new ObjectInputStream(connection.getInputStream());

        showMessage("\n streams are now setup \n");
    }

    //when in the conversation

    private void whileChatting() throws IOException{
        String message = "You are now connected!";
        sendMessage(message);
        ableToType(true);

        do{
            //conversation is happening

            try{

                message = (String) input.readObject();
                showMessage("\n" +  message);

            }catch(ClassNotFoundException classNotFoundException){
                showMessage("\n Not sure what user sent?");
            }


        }while(!message.equals("CLIENT - END"));
    }

    //close down method - closes streams and sockets after chatting is complete

    private void closeDown(){
        showMessage("\n closing connections... \n");
        ableToType(false);

        try{
            output.close();
            input.close();
            connection.close();

        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    // send a message to client

    private void sendMessage(String message){
        try{

            output.writeObject("SERVER - " + message);
            output.flush();
            showMessage("\n SERVER - " + message);

        }catch(IOException ioException){
            chatWindow.append("\n Error, not able to send.");
        }
    }

    // updates the chat window

    private void showMessage(final String text){

        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {

                        //adds message to chat windows and updates
                        chatWindow.append(text);
                    }
                }
        );
    }

    // let user type

    private void ableToType(final boolean b){

        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        userText.setEditable(b);
                    }
                }
        );

    }

}
