package javagames.completegame.object;

import javagames.completegame.admin.Acme;
import javagames.completegame.admin.QuickRestart;
import javagames.completegame.state.StateController;
import javagames.util.ResourceLoader;
import javagames.util.Sprite;
import javagames.util.Vector2f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Random;

/**
 * Created by Gerwa on 2017/5/3.
 */
public class TankFactory {
    private Sprite p1Tank[][];
    private Sprite p2Tank[][];
    private Sprite enemyTank[][];
    private Sprite ironTank[][];
    private Sprite brokenTank[][];
    private Sprite explosions[];
    private Sprite redTank, greenTank;
    private StateController controller;
    Random rand;

    public TankFactory(Random rand, StateController controller) {
        this.rand = rand;
        this.controller = controller;
    }

    public void handleAcme(Acme acme) {
        float width = 0.3f;
        redTank = new Sprite(ironTank[0][0].getImage(), new Vector2f(0, width), new Vector2f(width, 0));
        greenTank = new Sprite(p2Tank[0][0].getImage(), new Vector2f(0, width), new Vector2f(width, 0));
        acme.setRedTank(redTank);
        acme.setGreenTank(greenTank);
    }

    Sprite[][] loadTankSprite(String folder, String prefix) {
        Sprite[][] res = new Sprite[4][2];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                String thispath = "tank/" + folder + "/"
                        + prefix + "-" + String.valueOf(i + 1) + "-" + String.valueOf(j + 1) + ".png";
                BufferedImage image = loadSprite(thispath);
                Vector2f topLeft = new Vector2f(0f, 1f);
                Vector2f buttomRight = new Vector2f(1f, 0f);
                res[i][j] = new Sprite(image, topLeft, buttomRight);
            }
        }
        return res;
    }

    public void loadFactory() {
        p1Tank = loadTankSprite("p1", "m2");
        p2Tank = loadTankSprite("p2", "m22");
        enemyTank = loadTankSprite("enemy", "1");
        ironTank = loadTankSprite("iron", "dj3");
        brokenTank = loadTankSprite("broken", "3");
        explosions = loadExplosionSprite();
    }

    Sprite[] loadExplosionSprite() {
        Sprite[] res = new Sprite[8];
        for (int i = 0; i < 8; i++) {
            String thispath = "tank/blast/blast" + (i + 1) + ".gif";
            BufferedImage image = loadSprite(thispath);
            Vector2f topLeft = new Vector2f(0f, 1f);
            Vector2f buttomRight = new Vector2f(1f, 0f);
            res[i] = new Sprite(image, topLeft, buttomRight);
        }
        return res;
    }

    public TankExplosion createTankExplosion(Vector2f position) {
        TankExplosion tankExplosion = new TankExplosion(position, explosions);
        QuickRestart[] explosions = (QuickRestart[]) controller.getAttribute("explosions");
        explosions[rand.nextInt(explosions.length)].fire();
        return tankExplosion;
    }

    public void breakTank(Tank tank) {
        if (tank.getSprites() == ironTank) {
            tank.setSprites(brokenTank);
            QuickRestart metalClip = (QuickRestart) controller.getAttribute("metal-clip");
            metalClip.fire();
        }
    }

    public String getTankType(Tank tank) {
        if (tank.getSprites() == p1Tank) {
            return "p1";
        } else if (tank.getSprites() == p2Tank) {
            return "p2";
        } else if (tank.getSprites() == enemyTank) {
            return "enemy";
        } else if (tank.getSprites() == ironTank) {
            return "iron";
        } else if (tank.getSprites() == brokenTank) {
            return "broken";
        }
        return null;
    }

    public Tank createTank(String type) {
        // 在生成坦克的过程中，特别设定了坦克的初始位置和精灵
        Tank tank = new Tank(0);
        if (type.equals("p1")) {
            tank.setTankSprite(p1Tank);
            tank.setInitposition(new Vector2f(4f, 0f));
            tank.setPosition(tank.getInitposition());
        } else if (type.equals("p2")) {
            tank.setTankSprite(p2Tank);
            tank.setInitposition(new Vector2f(8f, 0f));
            tank.setPosition(tank.getInitposition());
        } else {
            tank.setTeam(1);
            if (type.equals("enemy")) {
                tank.setTankSprite(enemyTank);
            } else if (type.equals("iron")) {
                tank.setTankSprite(ironTank);
            } else if (type.equals("broken")) {
                tank.setTankSprite(brokenTank);
            }
            final float[] initX = {0f, 6f, 12f};
            final float[] initY = {12f, 12f, 12f};
            int index = rand.nextInt(3);
            tank.setInitposition(new Vector2f(initX[index], initY[index]));
            //tank.setInitposition(new Vector2f(0f, 12f));
            tank.setPosition(tank.getInitposition());
        }
        tank.reset();
        return tank;
    }

    private BufferedImage loadSprite(String path) {
        InputStream stream = ResourceLoader.load(
                TankFactory.class, "res/assets/images/" + path, "/images/" + path
        );
        try {
            return ImageIO.read(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
