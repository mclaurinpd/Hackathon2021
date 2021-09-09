package test;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import engine.Controller;

import java.util.Scanner;

public class MattTest {
    private static Controller controller;
    private static Scanner reader = new Scanner(System.in);


    public static void main(String[] args) {
        Board board = new Board();

        //board.loadFromFen("k7/8/8/6p1/8/7N/8/K7 w - - 0 1");

        controller = new Controller();
        String userMove = "";

        userMove = getUserMove();
        board.doMove(userMove);

        while (!board.isMated()) {
            System.out.println("\nBoard before:");
            System.out.println(board);
            Move bestMove = controller.findBestMove(board);

            System.out.println("Moving " + board.getPiece(bestMove.getFrom()) + " from " + bestMove.getFrom() + " to " + bestMove.getTo());
            board.doMove(bestMove);

            System.out.println("\nBoard after:\n" + board);

            userMove = getUserMove();
            board.doMove(userMove);
        }

        reader.close();
    }

    private static String getUserMove() {
        System.out.println("Enter a move: ");
        String move = reader.next(); // Scans the next token of the input as an int.
        //once finished
        return move;
    }
}