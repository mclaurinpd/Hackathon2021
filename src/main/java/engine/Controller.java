package engine;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;

public class Controller {
    private int depth = 20;

    public Controller(){

    }
    public Move findBestMove(Board board) {
        double tempScore;
        double moveScore = -100000;
        Move bestMove = null;
        Board tempBoard = new Board();
        tempBoard.loadFromFen(board.getFen());


        for (Move move:board.legalMoves()) {
            tempBoard.doMove(move);
            tempScore = alphaBetaMax(100000, -99999, depth, tempBoard);
            if (tempScore > moveScore) {
                moveScore = tempScore;
                bestMove = move;
            }
            tempBoard.loadFromFen(board.getFen());
        }
        System.out.println("\nBest move: " + bestMove);
        return bestMove;
    }

    private double evaluateBoard(Board board) {
        return evaluateMobilityScore(board) + evaluateMaterialScore(board);
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

    private double alphaBetaMax(double alpha, double beta, int depthleft, Board board) {
        double score;
        Board tempBoard = new Board();
        tempBoard.loadFromFen(board.getFen());

        if (depthleft == 0) {
            return evaluateBoard(tempBoard);
        }
        for (Move move: tempBoard.legalMoves()) {
            tempBoard.doMove(move);
            score = alphaBetaMin(alpha, beta, depthleft - 1, tempBoard);
            if(score >= beta) {
                return beta;   // fail hard beta-cutoff
            }
            if(score > alpha) {
                alpha = score; // alpha acts like max in MiniMax
            }
        }
        return alpha;
    }

    private double alphaBetaMin(double alpha, double beta, int depthleft, Board board) {
        double score;
        Board tempBoard = new Board();
        tempBoard.loadFromFen(board.getFen());

        if (depthleft == 0) {
            return -1 * evaluateBoard(tempBoard);
        }
        for (Move move: tempBoard.legalMoves()) {
            tempBoard.doMove(move);
            score = alphaBetaMax(alpha, beta, depthleft - 1, tempBoard);
            if(score <= alpha) {
                return alpha; // fail hard alpha-cutoff
            }
            if(score < beta) {
                beta = score; // beta acts like min in MiniMax
            }
        }
        return beta;
    }
}
