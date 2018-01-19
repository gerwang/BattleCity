package javagames.completegame.object;

import javagames.completegame.CompleteGame;
import javagames.completegame.admin.GameConstants;
import javagames.completegame.state.StateController;
import javagames.util.KeyboardInput;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

/**
 * Created by Gerwa on 2017/5/16.
 */
public class BattleNet implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private boolean running;
    private StateController controller;
    private Thread netThread;
    private BlockingQueue local, remote;
    private ArrayList<String> localMsg;
    public final int lantency = 5;
    private long appSleep;

    public boolean isRunning() {
        return running;
    }

    synchronized public void processLocalInput(int keyCode, int status) {
        synchronized (localMsg) {
            localMsg.add(keyCode + " " + status);
        }
        sendLine(KeyboardInput.keyMapping.get(keyCode) + " " + status);
    }

    public void flushLocalInput() {
        synchronized (local) {
            synchronized (localMsg) {
                try {
                    local.put(localMsg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                localMsg = new ArrayList<>();
            }
            sendLine("end");
            synchronized (remote) {
                if (local.size() > remote.size() + lantency / 2) {
                    if (appSleep < GameConstants.APP_SLEEP) {
                        appSleep++;
                    }
                } else {
                    if (appSleep > 0) {
                        appSleep--;
                    }
                }
            }
        }
        try {
            Thread.sleep(appSleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public BlockingQueue getLocal() {
        return local;
    }

    public BlockingQueue getRemote() {
        return remote;
    }

    public BattleNet(Socket socket, StateController controller) {
        appSleep = 0;
        this.socket = socket;
        this.controller = controller;
        try {
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        local = new LinkedBlockingQueue();
        remote = new LinkedBlockingQueue();
        for (int i = 0; i < lantency; i++) {
            try {
                local.put(new ArrayList<String>());
                remote.put(new ArrayList<String>());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        System.out.println("Socket closing");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Socket closed");
    }

    public void OnError() {
        CompleteGame app = (CompleteGame) controller.getAttribute("app");
        JOptionPane.showMessageDialog(app,
                "BattleNet error! Game will be shut down!",
                "Network Error", JOptionPane.ERROR_MESSAGE);
        app.shutDownGame();
    }

    synchronized public void sendLine(String line) {
        try {
            out.write(line + "\n");
            out.flush();
        } catch (IOException e) {
            OnError();
            return;
        }
    }

    synchronized public String recvLine() {
        try {
            return in.readLine();
        } catch (IOException e) {
            OnError();
            return null;
        }
    }

    public void startRecv() {
        netThread = new Thread(this, "net-thread");
        netThread.start();
    }

    public void stopRecv() {
        System.out.println("stop receiving");
        running = false;
        sendLine("bye");
        try {
            netThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("stopped receiving");
    }

    @Override
    public void run() {
        running = true;
        ArrayList<String> remoteMsg = new ArrayList<>();
        localMsg = new ArrayList<>();
        while (running) {
            try {
                String line = in.readLine();
                if (line.equals("over")) {
                    controller.setShouldSync(false);
                } else if (line.equals("bye")) {
                    if (running) {
                        sendLine("bye");
                        running = false;
                        if (controller.isShouldSync()) {
                            OnError();
                            return;
                        }
                    }
                    return;
                } else if (line.equals("end")) {
                    remote.put(remoteMsg);
                    remoteMsg = new ArrayList<>();
                } else {
                    remoteMsg.add(line);
                }
            } catch (IOException e) {
                OnError();
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
