import javax.swing.*;

public class Pawn extends Piece {

    public Pawn(int team, int x, int y) {
        super(team, x, y);
        String workingDirectory = System.getProperty("user.dir");
        pieceImage = (new ImageIcon(workingDirectory + "\\assets\\pieces\\pawn" + team + ".png")).getImage();
    }

    public int[][] possibleMoves(int[][] boardData) {
        if(y == 0 || y == 7) { return boardData; }
        if(team == 0) {
            if (boardData[x][y-1] == 0) { boardData[x][y-1] = 3; }
            if (x != 7 && boardData[x+1][y-1] == 2) { boardData[x+1][y-1] = 3; }
            if (x != 0 && boardData[x-1][y-1] == 2) { boardData[x-1][y-1] = 3; }
            if (moveCount == 0 && boardData[x][y-1] == 3 && boardData[x][y-2] == 0) { boardData[x][y-2] = 3; }
        } else {
            if (boardData[x][y+1] == 0) { boardData[x][y+1] = 3; }
            if (x < 7 && boardData[x+1][y+1] == 1) { boardData[x+1][y+1] = 3; }
            if (x > 0 && boardData[x-1][y+1] == 1) { boardData[x-1][y+1] = 3; }
            if (moveCount == 0 && boardData[x][y+1] == 3 && boardData[x][y+2] == 0) { boardData[x][y+2] = 3; }
        }
        return boardData;
    }
}
