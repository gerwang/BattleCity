package javagames.completegame.state;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javagames.completegame.admin.QuickRestart;
import javagames.completegame.object.*;
import javagames.util.Matrix3x3f;
import javagames.util.Utility;

public class GameOver extends AttractState {

    GameState state;
    GameMap gameMap;
    ArrayList<Tank> tanks;

    public GameOver(GameMap gameMap, ArrayList<Tank> tanks, GameState state) {
        this.state = state;
        this.tanks = tanks;
        this.gameMap = gameMap;
    }

    @Override
    protected float getWaitTime() {
        return 3.0f;
    }

    @Override
    public void enter() {
        super.enter();
        QuickRestart loseMusic = (QuickRestart) getController().getAttribute("lose-music");
        loseMusic.fire();
        QuickRestart loadMusic = (QuickRestart) getController().getAttribute("load-music");
        loadMusic.close();
        loadMusic.open();
        BattleNet net = (BattleNet) getController().getAttribute("net");
        controller.setShouldSync(false);
        net.sendLine("over");
        net.stopRecv();
    }

    @Override
    protected AttractState getState() {
        if (highScoreMgr.newHighScore(state)) {
            return new EnterHighScoreName(state);
        } else {
            return new HighScore();
        }
    }

    @Override
    public void render(Graphics2D g, Matrix3x3f view) {
        super.render(g, view);
        gameMap.draw(g, view);
        for (Tank tank : tanks) {
            tank.draw(g, view);
        }
        acme.drawScore(g, state.getScore());
        Utility.drawCenteredString(g, app.getScreenWidth(),
                app.getScreenHeight() / 3, "G A M E O V E R");
    }
}