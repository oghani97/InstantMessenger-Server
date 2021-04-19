package src;

import javax.swing.JFrame;

public class MainClass{

        public static void main(String[] args){

            Server test = new Server();
            test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            test.runServer();
        }
    }
