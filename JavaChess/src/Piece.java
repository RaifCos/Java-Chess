import java.awt.*;

public class Piece {

    protected int x,y;
    protected int team;
    protected int opp;
    protected int moveCount;
    protected Image pieceImage;

    public Piece(int team, int x, int y) {
        this.team = team + 1;
        opp = 2 - team;
        this.x = x;
        this.y = y;
    }

    public int[][] possibleMoves(int[][] boardData) { return null; }

    public boolean checkRange(int posX, int posY){ return posX >= 0 && posX <= 7 && posY >= 0 && posY <= 7; }

    public void move(int targX, int targY) {
        x = targX;
        y = targY;
        moveCount++;
    }

    public void paint(Graphics g) { g.drawImage(pieceImage, 280+((x)*80), 55+((y)*80), null); }

}
