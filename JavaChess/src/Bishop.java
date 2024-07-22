import javax.swing.*;

public class Bishop extends Piece {

    public Bishop(int team, int x, int y) {
        super(team, x, y);
        String workingDirectory = System.getProperty("user.dir");
        pieceImage = (new ImageIcon(workingDirectory + "\\assets\\pieces\\bishop" + team + ".png")).getImage();
    }

    public int[][] possibleMoves(int[][] boardData) {
        for(int z = 1; z <= 7; z++){ if(checkRange(x+z, y+z) && boardData[x+z][y+z] != team) { if (boardData[x+z][y+z] == opp) { boardData[x+z][y+z] = 3; break; } else { boardData[x+z][y+z] = 3; } } else { break; } }
        for(int z = 1; z <= 7; z++){ if(checkRange(x+z, y-z) && boardData[x+z][y-z] != team) { if (boardData[x+z][y-z] == opp) { boardData[x+z][y-z] = 3; break; } else { boardData[x+z][y-z] = 3; } } else { break; } }
        for(int z = 1; z <= 7; z++){ if(checkRange(x-z, y+z) && boardData[x-z][y+z] != team) { if (boardData[x-z][y+z] == opp) { boardData[x-z][y+z] = 3; break; } else { boardData[x-z][y+z] = 3; } } else { break; } }
        for(int z = 1; z <= 7; z++){ if(checkRange(x-z, y-z) && boardData[x-z][y-z] != team) { if (boardData[x-z][y-z] == opp) { boardData[x-z][y-z] = 3; break; } else { boardData[x-z][y-z] = 3; } } else { break; } }
        return boardData;
    }

}
