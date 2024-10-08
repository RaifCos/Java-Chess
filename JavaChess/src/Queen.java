import javax.swing.*;

public class Queen extends Piece {

    public Queen(int team, int x, int y) {
        super(team, x, y);
        String workingDirectory = System.getProperty("user.dir");
        pieceImage = (new ImageIcon(workingDirectory + "\\assets\\pieces\\queen" + team + ".png")).getImage();
    }

    public int[][] possibleMoves(int[][] boardData) {
        for (int z = x-1; z >= 0; z--) { if(boardData[z][y] != team + 1) { if(boardData[z][y] == opp + 1) { boardData[z][y] = 3; break; } else { boardData[z][y] = 3; } } else break; }
        for (int z = x+1; z <= 7; z++) { if(boardData[z][y] != team + 1) { if(boardData[z][y] == opp + 1) { boardData[z][y] = 3; break; } else { boardData[z][y] = 3; } } else break; }
        for (int z = y-1; z >= 0; z--) { if(boardData[x][z] != team + 1) { if(boardData[x][z] == opp + 1) { boardData[x][z] = 3; break; } else { boardData[x][z] = 3; } } else break; }
        for (int z = y+1; z <= 7; z++) { if(boardData[x][z] != team + 1) { if(boardData[x][z] == opp + 1) { boardData[x][z] = 3; break; } else { boardData[x][z] = 3; } } else break; }
        for(int z = 1; z <= 7; z++){ if(checkRange(x+z, y+z) && boardData[x+z][y+z] != team + 1) { if (boardData[x+z][y+z] == opp + 1) { boardData[x+z][y+z] = 3; break; } else { boardData[x+z][y+z] = 3; } } else { break; } }
        for(int z = 1; z <= 7; z++){ if(checkRange(x+z, y-z) && boardData[x+z][y-z] != team + 1) { if (boardData[x+z][y-z] == opp + 1) { boardData[x+z][y-z] = 3; break; } else { boardData[x+z][y-z] = 3; } } else { break; } }
        for(int z = 1; z <= 7; z++){ if(checkRange(x-z, y+z) && boardData[x-z][y+z] != team + 1) { if (boardData[x-z][y+z] == opp + 1) { boardData[x-z][y+z] = 3; break; } else { boardData[x-z][y+z] = 3; } } else { break; } }
        for(int z = 1; z <= 7; z++){ if(checkRange(x-z, y-z) && boardData[x-z][y-z] != team + 1) { if (boardData[x-z][y-z] == opp + 1) { boardData[x-z][y-z] = 3; break; } else { boardData[x-z][y-z] = 3; } } else { break; } }
        return boardData;
    }

}
