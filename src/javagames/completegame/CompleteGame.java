package javagames.completegame;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import javagames.completegame.admin.*;
import javagames.completegame.gui.Gui_BattleCity;
import javagames.completegame.object.BattleNet;
import javagames.completegame.state.*;
import javagames.util.*;

public class CompleteGame extends WindowFramework {

    private StateController controller;
    Socket socket;
    boolean isServer;
    Gui_BattleCity menu;

    public CompleteGame(Gui_BattleCity menu, Socket socket, boolean isServer) {
        this.menu = menu;
        this.socket = socket;
        this.isServer = isServer;

        appBorder = GameConstants.APP_BORDER;
        appWidth = GameConstants.APP_WIDTH;
        appHeight = GameConstants.APP_HEIGHT;
        appSleep = GameConstants.APP_SLEEP;
        appTitle = GameConstants.APP_TITLE;
        appWorldWidth = GameConstants.WORLD_WIDTH;
        appWorldHeight = GameConstants.WORLD_HEIGHT;
        appBorderScale = GameConstants.BORDER_SCALE;
        appDisableCursor = GameConstants.DISABLE_CURSOR;
        appMaintainRatio = GameConstants.MAINTAIN_RATIO;

    }

    @Override
    protected void initialize() {
        super.initialize();
        controller = new StateController();
        controller.setAttribute("app", this);
        controller.setAttribute("keys", keyboard);
        controller.setAttribute("ACME", new Acme(this));
        controller.setAttribute("viewport", getViewportTransform());
        controller.setAttribute("socket", socket);
        controller.setAttribute("is-server", isServer);
        controller.setAttribute("menu", menu);
        BattleNet net = new BattleNet(socket, controller);
        controller.setAttribute("net", net);
        keyboard.setNet(net);
        controller.setState(new GameLoading());
    }

    public void shutDownGame() {
        shutDown();
    }

    private void processCmd(ArrayList<String> cmd) {
        for (String line : cmd) {
            String[] parameters = line.split(" ");
            int keyCode = Integer.parseInt(parameters[0]);
            boolean status = Integer.parseInt(parameters[1]) == 1;
            keyboard.getKeys()[keyCode] = status;
        }
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
        BattleNet net = (BattleNet) controller.getAttribute("net");
        if (net != null) {
            if (net.isRunning()) {
                net.flushLocalInput();
                try {
                    ArrayList<String> localCmd = (ArrayList<String>) net.getLocal().take();
                    ArrayList<String> remoteCmd = (ArrayList<String>) net.getRemote().take();
                    processCmd(localCmd);
                    processCmd(remoteCmd);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        keyboard.poll();
        controller.processInput(delta);
    }

    @Override
    protected void updateObjects(float delta) {
        controller.updateObjects(delta);
    }

    @Override
    protected void render(Graphics g) {
        controller.render((Graphics2D) g, getViewportTransform());
        super.render(g);
    }

    @Override
    protected void terminate() {
        super.terminate();
        QuickRestart event =
                (QuickRestart) controller.getAttribute("fire-clip");
        if (event != null) {
            System.out.println("Sound: fire-clip");
            event.close();
            event.shutDown();
            System.out.println("Done: fire-clip");
        }
        QuickRestart[] explosions =
                (QuickRestart[]) controller.getAttribute("explosions");
        for (int i = 0; i < explosions.length; ++i) {
            System.out.println("Sound: explosions: " + i);
            explosions[i].close();
            explosions[i].shutDown();
            System.out.println("Done: explosions");
        }
        QuickRestart loadMusic =
                (QuickRestart) controller.getAttribute("load-music");
        if (loadMusic != null) {
            System.out.println("Sound: load-music");
            loadMusic.close();
            loadMusic.shutDown();
            System.out.println("Done: load-music");
        }
        QuickRestart winMusic =
                (QuickRestart) controller.getAttribute("win-music");
        if (winMusic != null) {
            System.out.println("Sound: win-music");
            winMusic.close();
            winMusic.shutDown();
            System.out.println("Done: win-music");
        }
        QuickRestart loseMusic =
                (QuickRestart) controller.getAttribute("lose-music");
        if (loseMusic != null) {
            System.out.println("Sound: lose-music");
            loseMusic.close();
            loseMusic.shutDown();
            System.out.println("Done: lose-music");
        }
        QuickRestart metalMusic =
                (QuickRestart) controller.getAttribute("metal-clip");
        if (metalMusic != null) {
            System.out.println("Sound: metal-clip");
            metalMusic.close();
            metalMusic.shutDown();
            System.out.println("Done: metal-clip");
        }
        QuickRestart stoneMusic =
                (QuickRestart) controller.getAttribute("stone-clip");
        if (stoneMusic != null) {
            System.out.println("Sound: stone-clip");
            stoneMusic.close();
            stoneMusic.shutDown();
            System.out.println("Done: stone-clip");
        }
        BattleNet net = (BattleNet) controller.getAttribute("net");
        if (net != null) {
            if (net.isRunning()) {
                net.stopRecv();
            }
            net.close();
        }
        menu.back2Menu();
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public static void launch(Gui_BattleCity menu, Socket socket, boolean isServer) {
        launchApp(new CompleteGame(menu, socket, isServer));
    }
}