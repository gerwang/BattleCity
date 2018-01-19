package javagames.completegame.object;

import javagames.util.Matrix3x3f;
import javagames.util.Sprite;
import javagames.util.Vector2f;

import java.awt.*;

/**
 * Created by Gerwa on 2017/5/14.
 */
public class TankExplosion {
    public static final double TimePerFrame = 0.06;
    Vector2f position;
    Sprite[] explosions;
    double timePassed;

    public TankExplosion(Vector2f position, Sprite[] explosions) {
        this.position = position;
        this.explosions = explosions;
        this.timePassed = 0;
    }

    public double getTimePassed() {
        return timePassed;
    }

    public boolean isFinished() {
        return getTimePassed() > TimePerFrame * explosions.length;
    }

    public void update(double delta) {
        timePassed += delta;
    }

    public void draw(Graphics2D g, Matrix3x3f view) {
        int currentFrame = (int) (timePassed / TimePerFrame);
        if (currentFrame < explosions.length) {
            explosions[currentFrame].render(g, view, position, 0f);
        }
    }
}
