package engine;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {
    private int depth = 10;
    private int distance;
    private Map<Long, TranspositionNode> transpositionTable = new HashMap<>();

    public Move findBestMove(Board board) {
        double tempScore;
        double moveScore = Double.POSITIVE_INFINITY;
        Move bestMove = null;
        List<Move> moves = board.legalMoves();

        if (board.getMoveCounter() < 10) {
            OpeningController openingController = new OpeningController();
            List<OpeningMove> openingMoves = openingController.getOpeningMoves(board.getFen());

            if (!openingMoves.isEmpty()) {
                return new Move(openingMoves.get(0).getMove(), board.getSideToMove());
            }
        }

        long startTime = System.currentTimeMillis();

        for(distance = 1; distance < depth && (System.currentTimeMillis() - startTime) < 30000; distance++) {
            for (int i=0; i < moves.size(); i++) {
                board.doMove(moves.get(i));
                tempScore = alphabeta(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, distance, board, true);
                if (tempScore < moveScore) {
                    moveScore = tempScore;
                    bestMove = moves.get(i);
                    moves.remove(moves.get(i));
                    moves.add(0, bestMove);
                }
                board.undoMove();
            }
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
        if (transpositionTable.containsKey(board.getIncrementalHashKey()) && transpositionTable.get(board.getIncrementalHashKey()).depth > depthleft) {
            double score = transpositionTable.get(board.getIncrementalHashKey()).score;
            int type = transpositionTable.get(board.getIncrementalHashKey()).type;

            if (type == 0) {
                return score;
            }
            else if (type == -1 && score >= beta) {
                return score;
            }
            else if (type == 1 && score <= alpha) {
                return score;
            }
            else if (beta <= alpha) {
                return score;
            }
        }

        if (depthleft == 0) {
            double score = evaluateBoard(board);

            if (score <= alpha) {
                transpositionTable.put(board.getIncrementalHashKey(), new TranspositionNode(score, 1, distance));
            }
            else if (score >= beta) {
                transpositionTable.put(board.getIncrementalHashKey(), new TranspositionNode(score, -1, distance));
            }
            else {
                transpositionTable.put(board.getIncrementalHashKey(), new TranspositionNode(score, 0, distance));
            }

            return score;
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
