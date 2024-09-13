import java.util.ArrayList;
import java.util.Random;

public class AIPlayer {

    Piece selectedPiece;
    public int[][] boardData = new int[8][8];
    private final ArrayList<Piece> pieceList = new ArrayList<>();

    // No Constructor Needed

    // Method to decide what move the AI will make on their turn.
    public int[] decideMove(Piece p, int[][] boardData) {
        int[] res = new int[2];

        // Check all Possible Moves for the selected Piece
        p.possibleMoves(boardData);
        // To avoid an infinite Loop during Generation, check there is at least one available move.
        boolean failSafe = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (boardData[i][j] == 3) { failSafe = true; break; }
            }
        }
        if(!failSafe) { return null; }

        // Generate random Spaces until an available space is found.
        randomGen(res);
        while(boardData[res[0]][res[1]] != 3) { randomGen(res); }
        return res;
    }

    // Method to randomly generate a space on the Board.
    public int[] randomGen(int[] res) {
        res[0] = (int) Math.floor(Math.random() * 8);
        res[1] = (int) Math.floor(Math.random() * 8);
        return res;
    }
}
