import javax.swing.*;

public class Rook extends Piece {

    public Rook(int team, int x, int y) {
        super(team, x, y);
        String workingDirectory = System.getProperty("user.dir");
        pieceImage = (new ImageIcon(workingDirectory + "\\assets\\pieces\\rook" + team + ".png")).getImage();
    }

    public int[][] possibleMoves(int[][] boardData) {
        for (int z = x-1; z >= 0; z--) { if(boardData[z][y] != team) { if(boardData[z][y] == opp) { boardData[z][y] = 3; break; } else { boardData[z][y] = 3; } } else break; }
        for (int z = x+1; z <= 7; z++) { if(boardData[z][y] != team) { if(boardData[z][y] == opp) { boardData[z][y] = 3; break; } else { boardData[z][y] = 3; } } else break; }
        for (int z = y-1; z >= 0; z--) { if(boardData[x][z] != team) { if(boardData[x][z] == opp) { boardData[x][z] = 3; break; } else { boardData[x][z] = 3; } } else break; }
        for (int z = y+1; z <= 7; z++) { if(boardData[x][z] != team) { if(boardData[x][z] == opp) { boardData[x][z] = 3; break; } else { boardData[x][z] = 3; } } else break; }
        return boardData;
    }

}
