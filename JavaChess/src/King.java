import javax.swing.*;

public class King extends Piece {

    public King(int team, int x, int y) {
        super(team, x, y);
        String workingDirectory = System.getProperty("user.dir");
        pieceImage = (new ImageIcon(workingDirectory + "\\assets\\pieces\\king" + team + ".png")).getImage();
    }

    public int[][] possibleMoves(int[][] boardData) {
        for(int r = -1; r <= 1; r++ ) {
            for(int c = -1; c <= 1; c++ ) {
                if(checkRange(x+r, y+c) && boardData[x+r][y+c] != team) { boardData[x+r][y+c] = 3; }
            }
        }
        boardData[x][y] = 0;
        return boardData;
    }

}
