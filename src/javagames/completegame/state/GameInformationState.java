package javagames.completegame.state;

import java.awt.*;

import javagames.util.Matrix3x3f;
import javagames.util.Utility;

public class GameInformationState extends AttractState {

    private static final String[] gameInfo = {
            "Battle City - version 1.0",
            "Programmed by: gerw",
            "",
            "Special thanks to:",
            "Tim Wright",
            "Michaela Wright",
            "Destiny Tamboer",
            "Jimmi Wright",
    };

    @Override
    protected AttractState getState() {
        return new HighScore();
    }

    @Override
    public void render(Graphics2D g, Matrix3x3f view) {
        super.render(g, view);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.setColor(Color.GREEN);
        Utility.drawCenteredString(g, app.getScreenWidth(),
                app.getScreenHeight() / 3, gameInfo);
    }
}