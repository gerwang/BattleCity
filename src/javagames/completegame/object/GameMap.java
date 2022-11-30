package javagames.completegame.object;

import javagames.completegame.admin.GameConstants;
import javagames.util.Matrix3x3f;
import javagames.util.ResourceLoader;
import javagames.util.Sprite;
import javagames.util.Vector2f;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Gerwa on 2017/5/14.
 */
public class GameMap {
    private Sprite brickWall;
    private Sprite steel;
    private Sprite[] river;
    private Sprite[] base;
    private char[][] map = new char[26][26];
    private int currentFrame;
    private int destroyState;
    private int[][] dist = new int[26][26];
    private boolean[][] canGo = new boolean[26][26];
    private int[][] prev = new int[26][26];
    private final static String[] FILE_NAME = {"battlemap_client.dat", "battlemap_server.dat"};

    Pair<Integer, Integer> transferFromAxis(float x, float y) {
        int intX = (int) (x * 2);
        int intY = (int) (y * 2);
        if (intX < 0 || intX > 25 || intY < 0 || intY > 25) {
            return new Pair<>(-1, -1);
        }
        return new Pair<>(25 - intY, intX);
    }

    public Sprite getBrickWall() {
        return brickWall;
    }

    public Sprite getSteel() {
        return steel;
    }

    public Sprite[] getRiver() {
        return river;
    }

    private void dyeColor(Tank tank, boolean color) {
        Pair<Integer, Integer> pair = transferFromAxis(tank.getPosition().x, tank.getPosition().y);
        if (pair.getKey() != -1) {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    canGo[pair.getKey() - i][pair.getValue() + j] = color;
                }
            }
        }
    }

    public void initBFS(ArrayList<Tank> tanks, Tank[] pTank) {
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 26; j++) {
                canGo[i][j] = true;
                if (map[i][j] != 'F') {
                    canGo[i][j] = false;
                }
            }
        }
        for (Tank tank : tanks) {
            if (tank == pTank[0] || tank == pTank[1]) {
                continue;
            }
            dyeColor(tank, false);
        }
    }

    private boolean getCanAt(int row, int col) {
        boolean res = true;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (row - i < 0 || col + j >= 26) {
                    return false;
                }
                res &= canGo[row - i][col + j];
            }
        }
        return res;
    }

    public int getDestroyState() {
        return destroyState;
    }

    public void setDestroyState(int destroyState) {
        this.destroyState = destroyState;
    }

    public char getBrick(float x, float y) {
        Pair<Integer, Integer> pair = transferFromAxis(x, y);
        if (pair.getKey() == -1) {
            return 'F';
        }
        return map[pair.getKey()][pair.getValue()];
    }

    public void setBrick(float x, float y, char brick) {
        Pair<Integer, Integer> pair = transferFromAxis(x, y);
        if (pair.getKey() == -1) {
            return;
        }
        map[pair.getKey()][pair.getValue()] = brick;
    }

    public boolean hitBase(float x, float y) {
        Pair<Integer, Integer> pair = transferFromAxis(x, y);
        if (pair.getKey() == -1) {
            return false;
        }
        int row = pair.getKey();
        int col = pair.getValue();
        if (row >= 24 && row <= 25 && col >= 12 && col <= 13) {
            return true;
        } else {
            return false;
        }
    }

    public void loadMap() {
        currentFrame = 0;
        destroyState = 0;
        brickWall = loadWall("brickwall2.png");
        steel = loadWall("steelwall2.png");
        river = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            river[i] = loadWall("river3-" + (i + 1) + ".png");
        }
        base = new Sprite[2];
        for (int i = 0; i < 2; i++) {
            BufferedImage image = loadBrick("base" + (i + 3) + ".png");
            base[i] = new Sprite(image, new Vector2f(0, 1f), new Vector2f(1f, 0));
        }
    }


    private void bfs(int row, int col) {
        LinkedList<Pair<Integer, Integer>> queue = new LinkedList<>();
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 26; j++) {
                dist[i][j] = -1;
            }
        }
        dist[row][col] = 0;
        queue.offer(new Pair<>(row, col));
        while (!queue.isEmpty()) {
            Pair<Integer, Integer> pair = queue.remove();
            row = pair.getKey();
            col = pair.getValue();
            final int[][] dir = {{0, -1}, {-1, 0}, {0, 1}, {1, 0}};
            for (int i = 0; i < 4; i++) {
                int nextRow = row + dir[i][0];
                int nextCol = col + dir[i][1];
                if (nextRow >= 0 && nextRow < 26
                        && nextCol >= 0 && nextCol < 26
                        && getCanAt(nextRow, nextCol) && dist[nextRow][nextCol] == -1) {
                    dist[nextRow][nextCol] = dist[row][col] + 1;
                    prev[nextRow][nextCol] = i;
                    queue.offer(new Pair<>(nextRow, nextCol));
                }
            }
        }
    }

    private int getLastDirection(int sRow, int sCol, int row, int col) {
        int res = 3;
        final int[][] dir = {{0, -1}, {-1, 0}, {0, 1}, {1, 0}};
        while (row != sRow || col != sCol) {
            res = prev[row][col];
            row -= dir[res][0];
            col -= dir[res][1];
        }
        return res;
    }

    public void decideDirection(Tank tank, boolean[] keys, Tank[] pTank) {
        if (tank.getPosition().x * 2 != (float) ((int) (tank.getPosition().x * 2))
                || tank.getPosition().y * 2 != (float) ((int) (tank.getPosition().y * 2))) {
            return;
        }
        dyeColor(tank, true);
        Pair<Integer, Integer> tmp = transferFromAxis(tank.getPosition().x, tank.getPosition().y);
        int row = tmp.getKey();
        int col = tmp.getValue();
        bfs(row, col);
        dyeColor(tank, false);
        for (int i = 0; i < 2; i++) {
            if (pTank[i].isAlive()) {
                Pair<Integer, Integer> pair = transferFromAxis(pTank[i].getPosition().x, pTank[i].getPosition().y);
                if (dist[pair.getKey()][pair.getValue()] != -1) {
                    keys[getLastDirection(row, col, pair.getKey(), pair.getValue())] = true;
                    return;
                }
            }
        }
        if (dist[25][12] != -1) {
            //base
            keys[getLastDirection(row, col, 25, 12)] = true;
            return;
        }
        for (int x = 25; x >= 0; x--) {
            for (int y = 0; y < 26; y++) {
                if (dist[x][y] != -1) {
                    keys[getLastDirection(row, col, x, y)] = true;
                    return;
                }
            }
        }
    }

    public char[][] getMap() {
        return map;
    }

    private File getTerranFile(int isServer) {
        return new File(System.getProperty("user.home"), FILE_NAME[isServer]);
    }

    private void createDefaultFile(File file) throws IOException {
        if (!file.createNewFile()) {
            throw new IOException();
        }
        String map = "FFBBFFBBFFBBFFBBFFBBFFBBFF\n" +
                "FFBBFFBBFFBBFFBBFFBBFFBBFF\n" +
                "FFBBFFBBFFBBFFBBFFBBFFBBFF\n" +
                "FFBBFFBBFFBBFFBBFFBBFFBBFF\n" +
                "FFBBFFBBFFBBEEBBFFBBFFBBFF\n" +
                "FFBBFFBBFFBBEEBBFFBBFFBBFF\n" +
                "FFBBFFBBFFBBFFBBFFBBFFBBFF\n" +
                "FFBBFFBBFFFFFFFFFFBBFFBBFF\n" +
                "FFBBFFBBFFFFFFFFFFBBFFBBFF\n" +
                "FFFFFFFFFFWWFFWWFFFFFFFFFF\n" +
                "FFFFFFFFFFWWFFWWFFFFFFFFFF\n" +
                "FFFFBBBBFFFFFFFFFFBBBBFFFF\n" +
                "EEFFBBBBFFFFFFFFFFBBBBFFEE\n" +
                "FFFFFFFFFFBBFFBBFFFFFFFFFF\n" +
                "FFFFFFFFFFBBBBBBFFFFFFFFFF\n" +
                "FFBBFFBBFFBBBBBBFFBBFFBBFF\n" +
                "FFBBFFBBFFBBFFBBFFBBFFBBFF\n" +
                "FFBBFFBBFFBBFFBBFFBBFFBBFF\n" +
                "FFBBFFBBFFBBFFBBFFBBFFBBFF\n" +
                "FFBBFFBBFFBBFFBBFFBBFFBBFF\n" +
                "FFBBFFBBFFBBFFBBFFBBFFBBFF\n" +
                "FFBBFFBBFFFFFFFFFFBBFFBBFF\n" +
                "FFBBFFBBFFFFFFFFFFBBFFBBFF\n" +
                "FFBBFFBBFFFBBBBFFFBBFFBBFF\n" +
                "FFFFFFFFFFFBFFBFFFFFFFFFFF\n" +
                "FFFFFFFFFFFBFFBFFFFFFFFFFF\n";
        PrintWriter out = new PrintWriter(new FileWriter(file));
        out.write(map);
        out.flush();
        out.close();
    }

    public void parseTerran(File file) throws FileNotFoundException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file)
                ));
        for (int i = 0; i < 26; i++) {
            try {
                String line = reader.readLine();
                for (int j = 0; j < 26; j++) {
                    map[i][j] = line.charAt(j);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        validateTerran();
    }


    public void sendTerran(BattleNet net) {
        for (int i = 0; i < 26; i++) {
            String line = "";
            for (int j = 0; j < 26; j++) {
                line += map[i][j];
            }
            net.sendLine(line);
        }
    }

    public void recvTerran(BattleNet net) {
        for (int i = 0; i < 26; i++) {
            String line = net.recvLine();
            for (int j = 0; j < 26; j++) {
                map[i][j] = line.charAt(j);
            }
        }
    }

    private void handleFileError() {
        JOptionPane.showMessageDialog(null, "Could not create Map File"
                        + "\nWill not be able to load map", "Error",
                JOptionPane.ERROR_MESSAGE);
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 26; j++) {
                map[i][j] = 'F';
            }
        }
    }

    public void saveTerran(int isServer) {
        try {
            File file = getTerranFile(isServer);
            FileWriter out = new FileWriter(file);
            for (int i = 0; i < 26; i++) {
                for (int j = 0; j < 26; j++) {
                    out.write(map[i][j]);
                }
                out.write('\n');
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadTerran(int isServer) {
        try {
            File file = getTerranFile(isServer);
            if (!file.exists()) {
                createDefaultFile(file);
            }
            parseTerran(file);
        } catch (IOException e) {
            e.printStackTrace();
            handleFileError();
        }
    }

    public void clearArea(int row, int col, int width, int height) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                map[row + i][col + j] = 'F';
            }
        }
    }

    public void validateTerran() {
        clearArea(24, 8, 2, 2);
        clearArea(24, 16, 2, 2);
        clearArea(0, 0, 2, 2);
        clearArea(0, 12, 2, 2);
        clearArea(0, 24, 2, 2);
        clearArea(24, 12, 2, 2);
    }

    private Sprite loadWall(String path) {
        BufferedImage image = loadBrick(path);
        return new Sprite(image, new Vector2f(0f, 0.5f), new Vector2f(0.5f, 0f));
    }

    private BufferedImage loadBrick(String path) {
        InputStream stream = ResourceLoader.load(
                GameMap.class, "res/assets/images/wall/" + path, "/images/wall/" + path
        );
        try {
            return ImageIO.read(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Vector2f getCoord(int row, int col) {
        return new Vector2f(col * 0.5f, GameConstants.WORLD_HEIGHT - (row + 1) * 0.5f);
    }

    public void draw(Graphics2D g, Matrix3x3f view) {
        base[destroyState].render(g, view, getCoord(25, 12), 0f);
        final int interval = 20;
        currentFrame = (currentFrame + 1) % (interval * 3);
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 26; j++) {
                Vector2f position = getCoord(i, j);
                switch (map[i][j]) {
                    case 'F':
                        break;
                    case 'B':
                        brickWall.render(g, view, position, 0f);
                        break;
                    case 'E':
                        steel.render(g, view, position, 0f);
                        break;
                    case 'W':
                        river[currentFrame / interval].render(g, view, position, 0f);
                        break;
                }
            }
        }
    }
}
