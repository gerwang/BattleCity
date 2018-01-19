package javagames.completegame.object;

import javagames.util.Matrix3x3f;
import javagames.util.Sprite;
import javagames.util.Vector2f;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Gerwa on 2017/5/3.
 */

public class Tank {

    public static final int TankDirections[][] = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
    public static final Color teamColor[] = {Color.GREEN, Color.RED};

    public ArrayList<Bullet> bullets;
    private Vector2f position;
    private Vector2f initposition;
    private final float velocity;
    private Sprite[][] sprites;
    private boolean alive;
    private boolean power;
    private int direction;
    private int currentFrame;
    private int team;
    private float launchCD;

    public float getLaunchCD() {
        return launchCD;
    }

    public void setLaunchCD(float launchCD) {
        this.launchCD = launchCD;
    }

    public static Vector2f getDirectionVector(int direction) {
        return new Vector2f(TankDirections[direction][0], TankDirections[direction][1]);
    }

    public Sprite[][] getSprites() {
        return sprites;
    }

    public void setSprites(Sprite[][] sprites) {
        this.sprites = sprites;
    }

    public Vector2f getVelocity() {
        if (isPower()) {
            Vector2f directionVector = getDirectionVector(direction);
            return directionVector.mul(velocity);
        } else {
            return new Vector2f();
        }
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public Tank(int team) {
        velocity = 2f;
        bullets = new ArrayList<>();
        this.team = team;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public Vector2f getInitposition() {
        return initposition;
    }

    public void setInitposition(Vector2f initposition) {
        this.initposition = initposition;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public boolean isPower() {
        return power;
    }

    public void setPower(boolean power) {
        this.power = power;
    }

    public void reset() {
        setAlive(true);
        setPosition(new Vector2f(initposition));
        setDirection(1);
        bullets.clear();
        setPower(false);
        currentFrame = 0;
    }

    public void setTankSprite(Sprite[][] tank) {
        this.sprites = tank;
    }

    public Bullet launchBullet() {
        Vector2f bulletPos = position.add(new Vector2f(0.5f, 0.5f));
        final float bulletVelocity = 5f;
        return new Bullet(bulletPos.add(getDirectionVector(direction).mul(0.6f)),
                getDirectionVector(direction).mul(bulletVelocity), teamColor[team]);
    }

    public void draw(Graphics2D g, Matrix3x3f view) {
        if (isAlive()) {
            sprites[direction][currentFrame].render(g, view, position, 0f);
            if (isPower()) {
                currentFrame = (currentFrame + 1) % 2;
            }
        }
    }

    public boolean intersectsWith(Tank otherTank) {
        return Math.abs(getPosition().x - otherTank.getPosition().x) < 1f
                && Math.abs(getPosition().y - otherTank.getPosition().y) < 1f;
    }

    public boolean intersectsWith(Vector2f otherPos) {
        return Math.abs(getPosition().x - otherPos.x) < 1f
                && Math.abs(getPosition().y - otherPos.y) < 1f;
    }

    public boolean pointInTank(Vector2f point) {
        return point.x > position.x && point.x < position.x + 1f && point.y > position.y && point.y < position.y + 1f;
    }
}
