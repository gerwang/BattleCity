package javagames.completegame.state;

public class GameState {

    private int level;
    private int lives;
    private int enemylives;
    private int score;

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        return score;
    }

    public int getEnemylives() {
        return enemylives;
    }

    public void setEnemylives(int enemylives) {
        this.enemylives = enemylives;
    }

    public void updateScore(int delta) {
        score += delta;
    }
}