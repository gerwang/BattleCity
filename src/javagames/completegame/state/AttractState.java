package javagames.completegame.state;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.*;

import javagames.completegame.admin.*;
import javagames.completegame.object.*;
import javagames.util.*;

public abstract class AttractState extends State {

    private float time;
    private Sprite background;
    protected Acme acme;
    protected KeyboardInput keys;
    protected HighScoreMgr highScoreMgr;

    public AttractState() {

    }

    @Override
    public void enter() {
        highScoreMgr = (HighScoreMgr) controller.getAttribute("score");
        keys = (KeyboardInput) controller.getAttribute("keys");
        background = (Sprite) controller.getAttribute("background");
        acme = (Acme) controller.getAttribute("ACME");
        time = 0.0f;
    }

    @Override
    public void updateObjects(float delta) {
        time += delta;
        if (shouldChangeState()) {
            AttractState state = getState();
            getController().setState(state);
            return;
        }
    }

    protected boolean shouldChangeState() {
        return time > getWaitTime();
    }

    protected float getWaitTime() {
        return 5.0f;
    }

    protected abstract AttractState getState();

    @Override
    public void processInput(float delta) {
        if (keys.keyDownOnce(KeyEvent.VK_ESCAPE)) {
            app.shutDownGame();
        }
    }

    @Override
    public void render(Graphics2D g, Matrix3x3f view) {
        background.render(g, view);
    }
}