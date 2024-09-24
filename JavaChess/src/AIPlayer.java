import java.util.ArrayList;

public class AIPlayer {
    Piece selectedPiece;
    BoardManager bm;

    // Constructor
    public AIPlayer(BoardManager bm) { this.bm = bm; }
    int[] res;

    // Method to decide what move the AI will make on their turn.
    public int[] decideMove() {
        smartGen();
        if(selectedPiece != null) { return res; }
        // No possible moves found via smartGen, so randomly generate a move instead.
        randomGen();
        for(int i=0; i<4; i++){ System.out.println(res[i]); }
        System.out.println("\n");
        return res;
    }

    // Method to search the board for the best move.
    public void smartGen() {
        res = new int[4];
        selectedPiece = null;
        int maxScore = -900;
        bm.setBoardScore();
        for (Piece p : bm.getPieceList(1)) {
            bm.refreshBoardData();
            p.possibleMoves(bm.boardData);
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if(bm.boardData[i][j] == 3 && bm.scoreData[i][j] > 0 && (bm.scoreData[i][j] - p.score) > maxScore) {
                        maxScore = bm.scoreData[i][j];
                        selectedPiece = p;
                        res[0] = p.x;
                        res[1] = p.y;
                        res[2] = i;
                        res[3] = j;
                    }
                }
            }
        }
    }

    // Method to randomly generate a space on the Board.
    public void randomGen() {
        while (bm.boardData[res[2]][res[3]] != 3) {
            bm.refreshBoardData();
            res = new int[4];
            Piece p = bm.getPieceList(1).get((int)(Math.random() * bm.getPieceList(1).size()));
            p.possibleMoves(bm.boardData);
            res[0] = p.x;
            res[1] = p.y;
            res[2] = (int)(Math.random() * 8);
            res[3] = (int)(Math.random() * 8);
        }
    }
}
