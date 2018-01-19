package javagames.completegame.gui;

import javagames.completegame.CompleteGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

/**
 * Created by Gerwa on 2017/4/27.
 */
public class Gui_BattleCity implements ActionListener {
    JFrame frame;
    TTTMenu MenuInstance;

    void Init() {
        frame = new JFrame("Java Battle City");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MenuInstance = new TTTMenu();

        frame.setContentPane(MenuInstance.getMenuPanel());
        frame.pack();
        MenuInstance.getStatus().setText("Status: Ready");
        frame.setVisible(true);

        MenuInstance.getLaunchButton().addActionListener(this);
        MenuInstance.getConnectButton().addActionListener(this);
        MenuInstance.getMapDesigner().addActionListener(this);
    }

    public void disableButton() {
        MenuInstance.getLaunchButton().setEnabled(false);
        MenuInstance.getConnectButton().setEnabled(false);
        MenuInstance.getMapDesigner().setEnabled(false);
    }


    public void resumeButton() {
        MenuInstance.getLaunchButton().setEnabled(true);
        MenuInstance.getConnectButton().setEnabled(true);
        MenuInstance.getMapDesigner().setEnabled(true);
    }

    public void back2Menu() {
        resumeButton();
        MenuInstance.getStatus().setText("Status: Ready");
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Gui_BattleCity gui_ttt = new Gui_BattleCity();
        gui_ttt.Init();
    }

    private void serverPrepareSocket() {
        MenuInstance.getStatus().setText("Status: waiting for a client to connect");
        ServerSocket serverSocket = null;
        try {
            int port = Integer.parseInt(MenuInstance.getPortText().getText());
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            MenuInstance.getStatus().setText("error: open serversocket fail!");
        }
        Socket socket = null;
        try {
            socket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
            MenuInstance.getStatus().setText("error: get client socket fail!");
        }
        frame.setVisible(false);
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        CompleteGame.launch(this, socket, true);
    }

    private void clientPrepareSocket() {
        MenuInstance.getStatus().setText("Status: connecting to a server");
        String host = MenuInstance.getIpAddress().getText();
        Socket socket = null;
        try {
            int port = Integer.parseInt(MenuInstance.getPortText().getText());
            socket = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
            MenuInstance.getStatus().setText("error: Connection failed!");
            resumeButton();
            return;
        }
        frame.setVisible(false);
        CompleteGame.launch(this, socket, false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == MenuInstance.getMapDesigner()) {
            frame.setVisible(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new MapDesigner(frame).launch();
                }
            }, "map-designer").start();
        } else if (e.getSource() == MenuInstance.getLaunchButton()) {
            //Launch as a server
            disableButton();
            /*
            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
            Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
            for(Thread thread:threadArray) {
                System.out.println(thread.getName());
            }
            */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    serverPrepareSocket();
                }
            }).start();
        } else if (e.getSource() == MenuInstance.getConnectButton()) {
            //Connect to a server
            disableButton();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    clientPrepareSocket();
                }
            }).start();
        }
    }
}
