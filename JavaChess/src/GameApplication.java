import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.util.ArrayList;

// TODO: Implement Bonus Tactics (Pawn Upgrades, Castling)

public class GameApplication extends JFrame implements Runnable, MouseListener {

    private boolean isInitialised = false;
    private final BufferStrategy strategy;
    private final Graphics offscreenBuffer;
    private int gameState = 0;
    private String winner;

    private Image titleImage;
    private Image boardImage;
    private Image backdropImage;
    private Image highlightImage;
    private int boardIndent = 280;

    public int[][] boardData = new int[8][8];
    private Piece selectedPiece;
    private final ArrayList<Piece> whitePieces = new ArrayList<>();
    private final ArrayList<Piece> blackPieces = new ArrayList<>();
    private boolean whiteTurn = true;

    public GameApplication() {

        //Create and Display JFrame Window.
        this.setTitle("Java Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screensize.width/2 - 600), (screensize.height/2 - 400), 1200, 800);
        setVisible(true);

        // Start Animation Thread.
        Thread t = new Thread(this);
        t.start();

        // Implement Double-Buffering.
        createBufferStrategy(2);
        strategy = getBufferStrategy();
        offscreenBuffer = strategy.getDrawGraphics();

        // Add Input Listener.
        addMouseListener(this);

        String workingDirectory = System.getProperty("user.dir");
        titleImage = (new ImageIcon(workingDirectory + "\\assets\\UI\\menuTitle.png")).getImage();
        backdropImage = (new ImageIcon(workingDirectory + "\\assets\\backdrops\\backdrop.png")).getImage();
        boardImage = (new ImageIcon(workingDirectory + "\\assets\\boards\\board.png")).getImage();
        highlightImage = (new ImageIcon(workingDirectory + "\\assets\\UI\\tileHighlight.png")).getImage();

        boardData = new int[8][8];

        // Game can now be Initialised.
        isInitialised = true;
    }

    public static void main(String[] args) { GameApplication game = new GameApplication(); }

    public void run() {
        while(true) {
            try { Thread.sleep(200); } catch (InterruptedException e) { }
            this.repaint();
        }
    }

    // MouseListener Events
    public void mouseClicked(MouseEvent e) {
        int clickX = e.getX();
        int clickY = e.getY();
        switch (gameState) {
            case 0:
                startGame();
                break;
            case 1:
                // Mouse Clicked somewhere on the Board.
                if(clickX > boardIndent && clickX < 920 && clickY > 55 && clickY < 695) {
                    pieceInteraction(((clickX - boardIndent) / 80), ((clickY - 55) / 80));
                } else { refreshBoardData(); }
                break;
            case 2:
                reset();
                break;
        }
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public void startGame() {
        for(int i=0; i<8; i++) { whitePieces.add(new Pawn(0, i, 6)); }
        whitePieces.add(new Rook(0,0  ,7));
        whitePieces.add(new Rook(0,7  ,7));
        whitePieces.add(new Knight(0,1  ,7));
        whitePieces.add(new Knight(0,6  ,7));
        whitePieces.add(new Bishop(0,2  ,7));
        whitePieces.add(new Bishop(0,5  ,7));
        whitePieces.add(new Queen(0,4  ,7));
        whitePieces.add(new King(0,3  ,7));

        for(int i=0; i<8; i++) { blackPieces.add(new Pawn(1, i, 1)); }
        blackPieces.add(new Rook(1,0  ,0));
        blackPieces.add(new Rook(1,7  ,0));
        blackPieces.add(new Knight(1,1  ,0));
        blackPieces.add(new Knight(1,6  ,0));
        blackPieces.add(new Bishop(1,2  ,0));
        blackPieces.add(new Bishop(1,5  ,0));
        blackPieces.add(new Queen(1,3  ,0));
        blackPieces.add(new King(1,4 ,0));
        refreshBoardData();
        gameState = 1;
    }

    public void refreshBoardData() {
        selectedPiece = null;
        boardData = new int[8][8];
        for (Piece p : whitePieces) { boardData[p.x][p.y] = 1; }
        for (Piece p : blackPieces) { boardData[p.x][p.y] = 2; }
    }

    public void pieceInteraction(int tileX, int tileY) {
        // If there is a Piece Selected, and the Tile Clicked is a valid space, then move.
        if (boardData[tileX][tileY] == 3 && selectedPiece != null) {
            selectedPiece.move(tileX, tileY);
            capturePiece(tileX, tileY);
            refreshBoardData();
            whiteTurn = !whiteTurn;
        } else {
            if(whiteTurn) {
                for (Piece p : whitePieces) {
                    if (p.x == tileX && p.y == tileY) {
                        selectedPiece = p;
                        boardData = selectedPiece.possibleMoves(boardData);
                        break;
                    } else { refreshBoardData(); }
                }
            } else {
                for (Piece p : blackPieces) {
                    if (p.x == tileX && p.y == tileY) {
                        selectedPiece = p;
                        boardData = selectedPiece.possibleMoves(boardData);
                        break;
                    } else { refreshBoardData(); }
                }
            }
        }
    }

    public void capturePiece(int targetX, int targetY) {
        if(!whiteTurn) {
            for (Piece p : whitePieces) {
                if(p.x == targetX && p.y == targetY) {
                    whitePieces.remove(p);
                    if(p instanceof King) { checkmate(1); }
                    return;
                }
            }
        } else {
            for (Piece p : blackPieces) {
                if(p.x == targetX && p.y == targetY) {
                    blackPieces.remove(p);
                    if(p instanceof King) { checkmate(0); }
                    return;
                }
            }
        }
    }

    public void checkmate(int result) {
        switch (result) {
            case 0:
                winner = "White Wins!";
                break;
            case 1:
                winner = "Black Wins!";
                break;
        }

        gameState = 2;
    }

    public void reset() {
        blackPieces.clear();
        whitePieces.clear();
        gameState = 0;
    }

    // Paint Method
    public void paint(Graphics g) {
        // Only Paint if game is Initialised.
        if(!isInitialised) { return; }
        g = offscreenBuffer;
        g.setFont(new Font("Serif", Font.BOLD, 32));
        switch (gameState) {
            case 0:
                g.drawImage(titleImage, 0, -25, null);
                break;
            default:
                g.drawImage(backdropImage, 0, -25, null);
                g.drawImage(boardImage, boardIndent, 55, null);
                for (Piece p : whitePieces) { p.paint(g); }
                for (Piece p : blackPieces) { p.paint(g); }
                for (int i=0; i<8; i++) { for (int j=0; j<8; j++) { if(boardData[i][j] == 3) { g.drawImage(highlightImage, boardIndent+(80*i), 55+(80*j), null); } } }
                if(gameState == 2) { g.drawString(winner, 515, 385); }
                break;
        }
        strategy.show();
    }
}