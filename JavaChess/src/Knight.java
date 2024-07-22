import javax.swing.*;

public class Knight extends Piece {

    public Knight(int team, int x, int y) {
        super(team, x, y);
        String workingDirectory = System.getProperty("user.dir");
        pieceImage = (new ImageIcon(workingDirectory + "\\assets\\pieces\\knight" + team + ".png")).getImage();
    }

    public int[][] possibleMoves(int[][] boardData) {
        if(checkRange(x+1, y+2) && boardData[x+1][y+2] != team) { boardData[x+1][y+2] = 3; }
        if(checkRange(x+2, y+1) && boardData[x+2][y+1] != team) { boardData[x+2][y+1] = 3; }

        if(checkRange(x+1, y-2) && boardData[x+1][y-2] != team) { boardData[x+1][y-2] = 3; }
        if(checkRange(x+2, y-1) && boardData[x+2][y-1] != team) { boardData[x+2][y-1] = 3; }

        if(checkRange(x-1, y+2) && boardData[x-1][y+2] != team) { boardData[x-1][y+2] = 3; }
        if(checkRange(x-2, y+1) && boardData[x-2][y+1] != team) { boardData[x-2][y+1] = 3; }

        if(checkRange(x-1, y-2) && boardData[x-1][y-2] != team) { boardData[x-1][y-2] = 3; }
        if(checkRange(x-2, y-1) && boardData[x-2][y-1] != team) { boardData[x-2][y-1] = 3; }
        return boardData;
    }

}
