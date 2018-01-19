package javagames.util;


import javagames.completegame.object.BattleNet;

import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class KeyboardInput implements KeyListener {

    private boolean[] keys;
    private int[] polled;

    public static final Map<Integer, Integer> keyMapping = new HashMap<Integer, Integer>() {{
        put(KeyEvent.VK_A, KeyEvent.VK_LEFT);
        put(KeyEvent.VK_W, KeyEvent.VK_UP);
        put(KeyEvent.VK_D, KeyEvent.VK_RIGHT);
        put(KeyEvent.VK_S, KeyEvent.VK_DOWN);
        put(KeyEvent.VK_ENTER, KeyEvent.VK_SPACE);
        put(KeyEvent.VK_ESCAPE, KeyEvent.VK_ESCAPE);
    }};

    private BattleNet net;

    public boolean[] getKeys() {
        return keys;
    }

    public void setNet(BattleNet net) {
        this.net = net;
    }

    public KeyboardInput() {
        keys = new boolean[256];
        polled = new int[256];
    }

    public boolean keyDown(int keyCode) {
        return polled[keyCode] > 0;
    }

    public boolean keyDownOnce(int keyCode) {
        return polled[keyCode] == 1;
    }

    public synchronized void poll() {
        for (int i = 0; i < keys.length; ++i) {
            if (keys[i]) {
                polled[i]++;
            } else {
                polled[i] = 0;
            }
        }
    }

    public synchronized void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyMapping.containsKey(keyCode)) {
            if (net != null && net.isRunning()) {
                net.processLocalInput(keyCode,1);
            } else {
                keys[keyCode] = true;
            }
        }
    }

    public synchronized void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyMapping.containsKey(keyCode)) {
            if (net != null && net.isRunning()) {
                net.processLocalInput(keyCode,0);
            } else {
                keys[keyCode] = false;
            }
        }
    }

    public void keyTyped(KeyEvent e) {
        // Not needed
    }
}
