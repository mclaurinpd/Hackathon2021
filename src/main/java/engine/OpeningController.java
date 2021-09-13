package engine;

import com.github.bhlangonijr.chesslib.move.Move;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OpeningController {
    private final String url = "http://www.chessdb.cn/cdb.php?action=queryall&board=";

    public List<OpeningMove> getOpeningMoves(String fen) {

        HttpClient client = HttpClient.newHttpClient();

        try {
            String encodedFen = fen.replace(" ", "%20");
            HttpRequest request = HttpRequest.newBuilder(
                            URI.create(url + encodedFen))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return decodeOpening(response.body());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    private List<OpeningMove> decodeOpening(String body) {
        String[] moves = body.split("\\|");
        List<OpeningMove> openingMoves = new ArrayList<>();

        if (moves.length <= 0) {
            return openingMoves;
        }

        for (String move : moves) {
            openingMoves.add(decodeMove(move.split(",")));
        }

        return openingMoves;
    }

    private OpeningMove decodeMove(String[] moveDetails) {
        OpeningMove openingMove = new OpeningMove();

        for (String moveDetail : moveDetails) {
            if (moveDetail.contains("move:")) {
                openingMove.setMove(moveDetail.split(":")[1]);
            } else if (moveDetail.contains("score:")) {
                openingMove.setScore(moveDetail.split(":")[1]);
            }
        }

        return openingMove;
    }
}
