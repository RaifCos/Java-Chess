import java.util.ArrayList;

public class AIPlayer {
    BoardManager bm;

    public AIPlayer(BoardManager bm) { this.bm = bm; }

    int[] res = new int[4];

    // Method to decide what move the AI will make on their turn.
    public int[] decideMove() {
        while (bm.boardData[res[2]][res[3]] != 3) {
            bm.refreshBoardData();
            randomGen();
        }
        return res;
    }

    // Method to randomly generate a space on the Board.
    public void randomGen() {
        Piece p = bm.getPieceList(1).get((int)(Math.random() * bm.getPieceList(1).size()));
        p.possibleMoves(bm.boardData);
        res[0] = p.x;
        res[1] = p.y;
        res[2] = (int)(Math.random() * 8);
        res[3] = (int)(Math.random() * 8);
    }
}
