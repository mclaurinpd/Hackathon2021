package engine;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;

public class Controller {
    private int depth = 2;

    public Controller(){

    }
    public Move findBestMove(Board board) {
        double tempScore;
        double moveScore = Double.NEGATIVE_INFINITY;
        Move bestMove = null;
        Board tempBoard = new Board();
        tempBoard.loadFromFen(board.getFen());


        for (Move move:board.legalMoves()) {
            tempBoard.doMove(move);
            tempBoard.setSideToMove(tempBoard.getSideToMove().flip());
            tempScore = alphaBetaMax(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth, tempBoard);
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
                        materialScore += negateMaterial * 45.0;
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
            tempBoard.loadFromFen(board.getFen());
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

        try {
            tempBoard.legalMoves();
        } catch (MoveGeneratorException e) {
            return alpha;
        }

        for (Move move: tempBoard.legalMoves()) {
            tempBoard.doMove(move);
            //tempBoard.setSideToMove(tempBoard.getSideToMove().flip());
            score = alphaBetaMax(alpha, beta, depthleft - 1, tempBoard);
            if(score <= alpha) {
                return alpha; // fail hard alpha-cutoff
            }
            if(score < beta) {
                beta = score; // beta acts like min in MiniMax
            }
            tempBoard.loadFromFen(board.getFen());
        }
        return beta;
    }
}
