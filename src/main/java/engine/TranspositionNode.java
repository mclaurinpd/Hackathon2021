package engine;

public class TranspositionNode {
    double score;
    int type;
    int depth;

    public TranspositionNode(double score, int type, int depth) {
        this.score = score;
        this.type = type;
        this.depth = depth;
    }
}