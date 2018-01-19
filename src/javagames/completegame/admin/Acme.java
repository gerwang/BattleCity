package javagames.completegame.admin;

import java.awt.*;

import javagames.completegame.CompleteGame;
import javagames.util.*;

public class Acme {

    private CompleteGame app;
    private Sprite greenTank, redTank;

    public Acme(CompleteGame app) {
        this.app = app;
    }

    public void setGreenTank(Sprite greenTank) {
        this.greenTank = greenTank;
    }

    public void setRedTank(Sprite redTank) {
        this.redTank = redTank;
    }

    public void drawScore(Graphics2D g, int score) {
        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );
        String toShow = "" + score;
        while (toShow.length() < 3) {
            toShow = "0" + toShow;
        }
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.GREEN);
        Utility.drawCenteredString(g, app.getScreenWidth(), 0, toShow);
    }

    public void drawLives(Graphics2D g, Matrix3x3f view, int enemylives, int alliedlives) {
        final float x = 0.315f;
        for (int i = 0; i < enemylives; i++) {
            redTank.render(g, view, new Vector2f(0.05f + x * i, 12.4f), 0);
        }
        for (int i = 0; i < alliedlives; i++) {
            greenTank.render(g, view, new Vector2f(0.05f + x * i, 12.0f), 0);
        }
    }
}