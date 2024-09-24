import javax.swing.*;
import java.util.ArrayList;
import java.awt.*;

public class BoardManager {

    protected int[][] boardData;
    public int[][] scoreData = new int[8][8];
    private final ArrayList<Piece> whitePieces = new ArrayList<>();
    private final ArrayList<Piece> blackPieces = new ArrayList<>();
    private final Image[] highlightImage = new Image[2];

    public BoardManager() {
        // Retrieve Images
        String workingDirectory = System.getProperty("user.dir");
        highlightImage[0] = (new ImageIcon(workingDirectory + "\\assets\\UI\\tileHighlight.png")).getImage();
        highlightImage[1] = (new ImageIcon(workingDirectory + "\\assets\\UI\\tileCastling.png")).getImage();
    }

    public void setupBoard() {
        boardData = new int[8][8];
        // Assemble the White Pieces.
        for(int i=0; i<8; i++) { whitePieces.add(new Pawn(0, i, 6)); }
        whitePieces.add(new Rook(0,0,7));
        whitePieces.add(new Rook(0,7,7));
        whitePieces.add(new Knight(0,1,7));
        whitePieces.add(new Knight(0,6,7));
        whitePieces.add(new Bishop(0,2,7));
        whitePieces.add(new Bishop(0,5,7));
        whitePieces.add(new Queen(0,4,7));
        whitePieces.add(new King(0,3,7));
        // Assemble the Black Pieces.
        for(int i=0; i<8; i++) { blackPieces.add(new Pawn(1, i, 1)); }
        blackPieces.add(new Rook(1,0,0));
        blackPieces.add(new Rook(1,7,0));
        blackPieces.add(new Knight(1,1,0));
        blackPieces.add(new Knight(1,6,0));
        blackPieces.add(new Bishop(1,2,0));
        blackPieces.add(new Bishop(1,5,0));
        blackPieces.add(new Queen(1,3,0));
        blackPieces.add(new King(1,4,0));
    }

    // Method to reset the Board and Pieces once the game ends.
    public void resetBoard(){
        boardData = new int[8][8];
        whitePieces.clear();
        blackPieces.clear();
    }

    // Method to assign each White Piece a Score which the AI uses for Routing.
    public void setBoardScore() {
        scoreData = new int[8][8];
        for (Piece p : whitePieces) { scoreData[p.x][p.y] = p.score; }
    }

    // Refresh the State of each Piece and the Board.
    public void refreshBoardData() {
        boardData = new int[8][8];
        for (Piece p : whitePieces) { boardData[p.x][p.y] = 1; }
        for (Piece p : blackPieces) { boardData[p.x][p.y] = 2; }
    }

    // Get all the Pieces of one team on the Board.
    public ArrayList<Piece> getPieceList(int team) {
        if(team == 0) { return whitePieces; }
        else if(team == 1) { return blackPieces; }
        else { return null; }
    }

    public void paint(Graphics g) {
        // Paint all Pieces.
        for (Piece p : whitePieces) { p.paint(g); }
        for (Piece p : blackPieces) { p.paint(g); }

        // Paint any Highlighted Spaces on the Board.
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (boardData[i][j] == 3) { g.drawImage(highlightImage[0], 280 + (80 * i), 55 + (80 * j), null); }
                if (boardData[i][j] == 4) { g.drawImage(highlightImage[1], 280 + (80 * i), 55 + (80 * j), null); }
            }
        }
    }
}
