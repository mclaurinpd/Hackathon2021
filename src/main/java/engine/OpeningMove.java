package engine;

public class OpeningMove {
    String move;
    int score;

    public OpeningMove() {}

    public OpeningMove(String move, int score) {
        this.move = move;
        this.score = score;
    }

    public String getMove() {
        return move;
    }

    public int getScore() {
        return score;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setScore(String score) {
        this.score = Integer.parseInt(score);
    }
}
