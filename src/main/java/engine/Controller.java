package engine;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;

public class Controller {
    private int depth = 4;

    public Controller(){

    }
    public Move findBestMove(Board board) {
        double tempScore;
        double moveScore = Double.POSITIVE_INFINITY;
        Move bestMove = null;

        for (Move move:board.legalMoves()) {
            board.doMove(move);
            tempScore = alphabeta(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth, board, true);
            if (tempScore < moveScore) {
                moveScore = tempScore;
                bestMove = move;
            }
            board.undoMove();
        }
        System.out.println("\nBest move: " + bestMove);
        return bestMove;
    }

    private double evaluateBoard(Board board) {
        return (evaluateMobilityScore(board) + evaluateMaterialScore(board)) * (board.getSideToMove() == Side.BLACK ? -1.0 : 1.0);
    }

    private double evaluateMobilityScore(Board board) {
        Board boardCopy = new Board();
        double mobilityScore = 0;
        boardCopy.loadFromFen(board.getFen());

        mobilityScore += boardCopy.legalMoves().size() * 0.1;
        boardCopy.setSideToMove(boardCopy.getSideToMove().flip());
        mobilityScore -= boardCopy.legalMoves().size() * 0.1;

        return mobilityScore;
    }

    private double evaluateMaterialScore(Board board) {
        double materialScore = 0.0;
        double negateMaterial;

        for (Piece piece:board.boardToArray()) {
            if (!piece.toString().equalsIgnoreCase("none")) {
                if (!piece.getPieceSide().equals(board.getSideToMove())) {
                    negateMaterial = -1.0;
                }
                else {
                    negateMaterial = 1.0;
                }

                switch (piece.getPieceType().toString()) {
                    case ("ROOK"):
                        materialScore += negateMaterial * 5.0;
                        break;
                    case("KNIGHT"):
                    case("BISHOP"):
                        materialScore += negateMaterial * 3.0;
                        break;
                    case("QUEEN"):
                        materialScore += negateMaterial * 9.0;
                        break;
                    case("KING"):
                        materialScore += negateMaterial * 200.0;
                        break;
                    case("PAWN"):
                        materialScore += negateMaterial * 0.5;
                        break;
                    default:
                        System.out.println("Found a piece not defined in switch statement!!");
                        break;
                }
            }
        }
        return materialScore;
    }

    private double alphabeta(double alpha, double beta, int depthleft, Board board, boolean maximizingPlayer) {
        if (depthleft == 0) {
            return evaluateBoard(board);
        }

        if (maximizingPlayer) {
            double maxVal = Double.NEGATIVE_INFINITY;

            for (Move move : board.legalMoves()) {

                board.doMove(move);
                double val = Math.max(maxVal, alphabeta(alpha, beta, depthleft-1, board, false));
                board.undoMove();
                maxVal = Math.max(maxVal, val);
                alpha = Math.max(alpha, maxVal);

                if (beta <= alpha) {
                    break;
                }
            }
            return maxVal;
        }
        else {
            double minVal = Double.POSITIVE_INFINITY;

            for(Move move : board.legalMoves()) {

                board.doMove(move);
                double val = Math.min(minVal, alphabeta(alpha, beta, depthleft-1, board, true));
                board.undoMove();
                minVal = Math.min(minVal, val);
                beta = Math.min(beta, minVal);

                if (beta <= alpha) {
                    break;
                }
            }
            return minVal;
        }
    }

}
