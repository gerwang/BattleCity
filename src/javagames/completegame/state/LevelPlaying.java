package javagames.completegame.state;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.*;

import javagames.completegame.admin.*;
import javagames.completegame.object.*;
import javagames.util.*;

public class LevelPlaying extends State {

    private ArrayList<Bullet> bullets;
    private Sprite background;
    private Acme acme;
    private QuickRestart laser;
    private Random rand;
    private TankFactory tankFactory;
    private Tank[] pTank = new Tank[2];
    private ArrayList<Tank> tanks;
    private TankExplosion[] pBlast = new TankExplosion[2];
    private ArrayList<TankExplosion> blasts;
    private GameState state;
    private KeyboardInput keys;
    boolean paused;
    private float spawnTime;
    private GameMap gameMap;
    private float destroyTimeWait;
    private int localPlayerIndex;

    public LevelPlaying(GameState state) {
        this.state = state;
    }

    @Override
    public void enter() {
        background = (Sprite) controller.getAttribute("background");
        keys = (KeyboardInput) controller.getAttribute("keys");
        laser = (QuickRestart) controller.getAttribute("fire-clip");
        tankFactory = (TankFactory) controller.getAttribute("tank-factory");
        gameMap = (GameMap) controller.getAttribute("map");
        acme = (Acme) controller.getAttribute("ACME");
        rand = (Random) controller.getAttribute("rng");
        tanks = new ArrayList<>();
        bullets = new ArrayList<Bullet>();
        blasts = new ArrayList<>();
        pBlast[0] = pBlast[1] = null;
        spawnTime = 6f;
        pTank[0] = tankFactory.createTank("p1");
        addTank(pTank[0]);
        pTank[1] = tankFactory.createTank("p2");
        addTank(pTank[1]);
        boolean isServer = (boolean) controller.getAttribute("is-server");
        localPlayerIndex = isServer ? 0 : 1;
    }

    private void addTank(Tank tank) {
        tanks.add(tank);
        makeRoomFor(tank);
    }

    private void processTankInput(float delta, boolean EnterKey, boolean AKey, boolean WKey, boolean DKey, boolean SKey, Tank tank) {

        tank.setLaunchCD(tank.getLaunchCD() + delta);
        if (tank.isAlive()) {
            if (EnterKey) {
                // 发射子弹
                if (tank.getLaunchCD() > 1f) {
                    tank.setLaunchCD(0f);
                    bullets.add(tank.launchBullet());
                    laser.fire();
                }
            }


            float tankX = tank.getPosition().x;
            float tankY = tank.getPosition().y;
            float upTankX = (float) Math.ceil(tankX * 2) / 2;
            float downTankX = (float) Math.floor(tankX * 2) / 2;
            float upTankY = (float) Math.ceil(tankY * 2) / 2;
            float downTankY = (float) Math.floor(tankY * 2) / 2;

            final float tankDelta = 0.05f;
            final boolean[] arrowKeys = {AKey, WKey, DKey, SKey};
            boolean power = false;
            for (int i = 0; i < 4; i++) {
                if (arrowKeys[i]) {
                    Vector2f direction = Tank.getDirectionVector(i);
                    if (direction.x == 0f) {
                        if (Math.abs(tankX - downTankX) < tankDelta) {
                            tank.getPosition().x = downTankX;
                            tank.setDirection(i);
                        } else if (Math.abs(tankX - upTankX) < tankDelta) {
                            tank.getPosition().x = upTankX;
                            tank.setDirection(i);
                        }
                    }
                    if (direction.y == 0f) {
                        if (Math.abs(tankY - downTankY) < tankDelta) {
                            tank.getPosition().y = downTankY;
                            tank.setDirection(i);
                        } else if (Math.abs(tankY - upTankY) < tankDelta) {
                            tank.getPosition().y = upTankY;
                            tank.setDirection(i);
                        }
                    }
                    power = true;
                }
            }
            Vector2f direct = Tank.getDirectionVector(tank.getDirection());
            boolean in = false;
            if (!power) {
                if (direct.x == 1f) {
                    if (Math.abs(tankX - upTankX) < tankDelta) {
                        tank.getPosition().x = upTankX;
                        in = true;
                    }
                } else if (direct.x == -1f) {
                    if (Math.abs(tankX - downTankX) < tankDelta) {
                        tank.getPosition().x = downTankX;
                        in = true;
                    }
                } else if (direct.y == 1f) {
                    if (Math.abs(tankY - upTankY) < tankDelta) {
                        tank.getPosition().y = upTankY;
                        in = true;
                    }
                } else if (direct.y == -1f) {
                    if (Math.abs(tankY - downTankY) < tankDelta) {
                        tank.getPosition().y = downTankY;
                        in = true;
                    }
                }
            }
            tank.setPower(power || !in);
        }
    }

    @Override
    public void processInput(float delta) {
        if (keys.keyDownOnce(KeyEvent.VK_ESCAPE)) {
            paused = !paused;
        }
        if (paused) {
            return;
        }
        processTankInput(delta, keys.keyDownOnce(KeyEvent.VK_ENTER),
                keys.keyDown(KeyEvent.VK_A), keys.keyDown(KeyEvent.VK_W),
                keys.keyDown(KeyEvent.VK_D), keys.keyDown(KeyEvent.VK_S), pTank[localPlayerIndex]);
        processTankInput(delta, keys.keyDownOnce(KeyEvent.VK_SPACE),
                keys.keyDown(KeyEvent.VK_LEFT), keys.keyDown(KeyEvent.VK_UP),
                keys.keyDown(KeyEvent.VK_RIGHT), keys.keyDown(KeyEvent.VK_DOWN), pTank[localPlayerIndex ^ 1]);
        processAI(delta);
    }

    @Override
    public void updateObjects(float delta) {
        if (paused) {
            return;
        }
        updateBullets(delta);
        updateTanks(delta);
        updateBlasts(delta);
        checkForLevelWon(delta);
    }

    private void processAITank(float delta, Tank tank) {
        boolean[] keys = new boolean[4];
        if (rand.nextInt(100) < 90) {
            gameMap.decideDirection(tank, keys, pTank);
        } else {
            keys[rand.nextInt(4)] = true;
        }
        processTankInput(delta, rand.nextInt(100) < 4, keys[0], keys[1], keys[2], keys[3], tank);
    }

    private void processAI(float delta) {
        spawnTime += delta;
        if (spawnTime > 6f && state.getEnemylives() > 0) {
            state.setEnemylives(state.getEnemylives() - 1);
            spawnTime = 0f;
            Tank newTank;
            if (rand.nextInt(100) < 70) {
                newTank = tankFactory.createTank("enemy");
            } else {
                newTank = tankFactory.createTank("iron");
            }
            addTank(newTank);
        }
        gameMap.initBFS(tanks, pTank);
        for (Tank tank : tanks) {
            if (tank != pTank[0] && tank != pTank[1]) {
                processAITank(delta, tank);
            }
        }
    }

    private void updateTank(Tank tank, float delta) {
        Vector2f nextPos = tank.getPosition().add(tank.getVelocity().mul(delta));
        boolean okay = true;
        if (GameConstants.hasLeftWorld(
                nextPos.add(new Vector2f(0f, 1f)),
                nextPos.add(new Vector2f(1f, 0f))
        )) {
            okay = false;
        }
        if (okay) {
            Vector2f midPos = nextPos.add(new Vector2f(0.5f, 0.5f));
            final Vector2f[] directionVector = {new Vector2f(1f, 1f), new Vector2f(1f, -1f),
                    new Vector2f(-1f, 1f), new Vector2f(-1f, -1f)};
            for (int i = 0; i < 4; i++) {
                Vector2f anglePos = midPos.add(directionVector[i].mul(0.49f));
                char brick = gameMap.getBrick(anglePos.x, anglePos.y);
                if (brick == 'E' || brick == 'W' || brick == 'B') {
                    okay = false;
                    break;
                }
            }
        }
        if (okay) {
            for (Tank otherTank : tanks) {
                if (otherTank != tank) {
                    if (otherTank.intersectsWith(nextPos)) {
                        okay = false;
                        break;
                    }
                }
            }
        }
        if (okay) {
            tank.setPosition(nextPos);
        }
    }

    private void updateBlasts(float delta) {
        for (int i = 0; i < 2; i++) {
            if (pBlast[i] != null && pBlast[i].isFinished()) {
                pBlast[i] = null;
            }
        }
        ArrayList<TankExplosion> copy = new ArrayList<>(blasts);
        for (TankExplosion ex : copy) {
            ex.update(delta);
            if (ex.isFinished()) {
                blasts.remove(ex);
            }
        }
    }

    private void removeTank(Tank tank) {
        tanks.remove(tank);
        for (int i = 0; i < 2; i++) {
            if (pTank[i].isAlive() && pTank[i] == tank) {
                pBlast[i] = tankFactory.createTankExplosion(tank.getPosition());
                blasts.add(pBlast[i]);
                pTank[i].setAlive(false);
                return;
            }
        }
        blasts.add(tankFactory.createTankExplosion(tank.getPosition()));
    }

    private void makeRoomFor(Tank tank) {
        ArrayList<Tank> copy = new ArrayList<>(tanks);
        for (Tank other : copy) {
            if (tank != other && other.intersectsWith(tank)) {
                removeTank(other);
            }
        }
    }

    private void processTankRespawn() {
        for (int i = 0; i < 2; i++) {
            if (!pTank[i].isAlive() && pBlast[i] == null) {
                if (state.getLives() > 0) {
                    state.setLives(state.getLives() - 1);
                    pTank[i].reset();
                    addTank(pTank[i]);
                } else {
                    getController().setState(new GameOver(gameMap, tanks, state));
                }
            }
        }
    }

    private void updateTanks(float delta) {
        ArrayList<Tank> copy = new ArrayList<>(tanks);
        for (Tank tank : copy) {
            updateTank(tank, delta);
        }
        processTankRespawn();
    }


    private void updateBullets(float delta) {
        ArrayList<Bullet> copy = new ArrayList<Bullet>(bullets);
        for (Bullet bullet : copy) {
            updateBullet(delta, bullet);
        }
    }

    private void updateBullet(float delta, Bullet bullet) {
        bullet.update(delta);
        if (GameConstants.hasLeftWorld(bullet.getPosition(), bullet.getPosition())) {
            bullets.remove(bullet);
        } else {
            boolean removed = false;

            //update bricks
            final Vector2f[] directionVector = {new Vector2f(1f, 1f), new Vector2f(1f, -1f),
                    new Vector2f(-1f, 1f), new Vector2f(-1f, -1f)};
            for (int i = 0; i < 4; i++) {
                Vector2f pos = bullet.getPosition().add(
                        directionVector[i].mul(bullet.getRadius()
                        ));
                char brick = gameMap.getBrick(pos.x, pos.y);
                if (brick == 'E') {
                    removed = true;
                } else if (brick == 'B') {
                    removed = true;
                    gameMap.setBrick(pos.x, pos.y, 'F');
                    QuickRestart stoneClip = (QuickRestart) getController().getAttribute("stone-clip");
                    stoneClip.fire();
                }
                if (gameMap.hitBase(pos.x, pos.y)) {
                    gameMap.setDestroyState(1);
                    removed = true;
                }
            }

            //update tanks
            ArrayList<Tank> copy;
            copy = new ArrayList<>(tanks);
            for (Tank tank : copy) {
                if (tank.pointInTank(bullet.getPosition())) {
                    removed = true;
                    if (bullet.getColor() != Tank.teamColor[tank.getTeam()]) {
                        String type = tankFactory.getTankType(tank);
                        if (type.equals("enemy")) {
                            state.updateScore(1);
                            removeTank(tank);
                        } else if (type.equals("broken")) {
                            state.updateScore(3);
                            removeTank(tank);
                        } else if (type.equals("iron")) {
                            tankFactory.breakTank(tank);
                        } else if (type.equals("p1") || type.equals("p2")) {
                            removeTank(tank);
                        }
                    }
                }
            }

            //update bullets
            ArrayList<Bullet> otherbullets = new ArrayList<>(bullets);
            for (Bullet other : otherbullets) {
                if (other.getColor() != bullet.getColor()
                        && other.getPosition().sub(bullet.getPosition()).len()
                        < other.getRadius() + bullet.getRadius()) {
                    removed = true;
                    bullets.remove(other);
                }
            }

            if (removed) {
                bullets.remove(bullet);
            }
        }
    }


    private void checkForLevelWon(float delta) {
        if (gameMap.getDestroyState() == 1) {
            destroyTimeWait += delta;
            if (destroyTimeWait > 1f) {
                getController().setState(new GameOver(gameMap, tanks, state));
                return;
            }
        }
        if (state.getEnemylives() == 0) {
            for (Tank tank : tanks) {
                if (tank != pTank[0] && tank != pTank[1]) {
                    return;
                }
            }
            if (blasts.isEmpty()) {
                state.setLevel(state.getLevel() + 1);
                state.setEnemylives(state.getLevel() + 5);
                getController().setState(new LevelStarting(state));
                QuickRestart winMusic = (QuickRestart) getController().getAttribute("win-music");
                winMusic.fire();
                QuickRestart loadMusic = (QuickRestart) getController().getAttribute("load-music");
                loadMusic.close();
                loadMusic.open();
            }
        }
    }

    @Override
    public void render(Graphics2D g, Matrix3x3f view) {
        background.render(g, view);
        gameMap.draw(g, view);
        for (Tank tank : tanks) {
            tank.draw(g, view);
        }
        for (TankExplosion ex : blasts) {
            ex.draw(g, view);
        }
        for (Bullet b : bullets) {
            b.draw(g, view);
        }
        acme.drawLives(g, view, state.getEnemylives(), state.getLives());
        acme.drawScore(g, state.getScore());
        if (paused) {
            Utility.drawCenteredString(g, app.getScreenWidth(),
                    app.getScreenHeight() / 3, "G A M E   P A U S E D");
        }
    }
}