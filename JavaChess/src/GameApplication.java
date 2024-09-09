import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.util.ArrayList;

// TODO: Fix Pawns (0,Y) Glitch
// TODO: Implement "Check" Mode (Players can only make moves to protect their King).
// TODO: Implement Pawn Upgrades

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
    private Image castlingImage;
    private final Image backsplashImage;
    private final Image whiteTurnImage;
    private final Image blackTurnImage;
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

        // Retrieve Images
        String workingDirectory = System.getProperty("user.dir");
        titleImage = (new ImageIcon(workingDirectory + "\\assets\\UI\\menuTitle.png")).getImage();
        backdropImage = (new ImageIcon(workingDirectory + "\\assets\\backdrops\\backdrop.png")).getImage();
        boardImage = (new ImageIcon(workingDirectory + "\\assets\\boards\\board.png")).getImage();
        highlightImage = (new ImageIcon(workingDirectory + "\\assets\\UI\\tileHighlight.png")).getImage();
        castlingImage = (new ImageIcon(workingDirectory + "\\assets\\UI\\tileCastling.png")).getImage();
        backsplashImage = (new ImageIcon(workingDirectory + "\\assets\\backdrops\\backsplash.png")).getImage();
        whiteTurnImage = (new ImageIcon(workingDirectory + "\\assets\\UI\\turn0.png")).getImage();
        blackTurnImage = (new ImageIcon(workingDirectory + "\\assets\\UI\\turn1.png")).getImage();

        // Initialise Board Data
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

    // MouseListener Event for when the Mouse is Clicked.
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

    // Other MouseListener Events
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    // Method to start the Game by assembling all Pieces on the board.
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

    // Refresh the State of each Piece and the Board.
    public void refreshBoardData() {
        selectedPiece = null;
        boardData = new int[8][8];
        for (Piece p : whitePieces) { boardData[p.x][p.y] = 1; }
        for (Piece p : blackPieces) { boardData[p.x][p.y] = 2; }
    }

    // Method that handles the main Gameplay mechanics.
    public void pieceInteraction(int tileX, int tileY) {
        // If the King is selected and "Castling" is possible, then execute the move.
        if (boardData[tileX][tileY] == 4 && selectedPiece instanceof King) {
            castling(selectedPiece.team);
            refreshBoardData();
            whiteTurn = !whiteTurn;
        } // If a Piece is selected and a valid Move Target is clicked, then move.
        else if (boardData[tileX][tileY] == 3 && selectedPiece != null) {
            selectedPiece.move(tileX, tileY);
            capturePiece(tileX, tileY);
            refreshBoardData();
            whiteTurn = !whiteTurn;
        } // No Piece is Selected, so check if the Player is clicking on a Piece to select it.
        else {
            // If it is White's Turn, check for a White Piece to select.
            if(whiteTurn) {
                for (Piece p : whitePieces) {
                    if (p.x == tileX && p.y == tileY) {
                        selectedPiece = p;
                        boardData = selectedPiece.possibleMoves(boardData);
                        // If a King is selected, check if "Castling" is possible.
                        if(p instanceof King && p.moveCount == 0) { castleCheck(1); }
                        break;
                    } else { refreshBoardData(); }
                }
            } // If it is not White's Turn, check for a Black Piece to select.
            else {
                for (Piece p : blackPieces) {
                    if (p.x == tileX && p.y == tileY) {
                        selectedPiece = p;
                        boardData = selectedPiece.possibleMoves(boardData);
                        // If a King is selected, check if "Castling" is possible.
                        if(p instanceof King && p.moveCount == 0) { castleCheck(2); }
                        break;
                    } else { refreshBoardData(); }
                }
            }
        }
    }

    // Method that checks if a Moving Piece is "Capturing" another, removing it from the Game.
    public void capturePiece(int targetX, int targetY) {
        // If it is not White's Turn, check for a White Piece to capture.
        if(!whiteTurn) {
            for (Piece p : whitePieces) {
                if(p.x == targetX && p.y == targetY) {
                    whitePieces.remove(p);
                    // If the removed Piece is the King, end the game.
                    if(p instanceof King) { checkmate(1); }
                    return;
                }
            } // If it is White's Turn, check for a Black Piece to capture.
        } else {
            for (Piece p : blackPieces) {
                if(p.x == targetX && p.y == targetY) {
                    blackPieces.remove(p);
                    // If the removed Piece is the King, end the game.
                    if(p instanceof King) { checkmate(0); }
                    return;
                }
            }
        }
    }

    // Method that ends the Game when a Player's King is captured.
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

    // Method that carries out the "Castling" technique.
    public void castling(int team) {
        if (team == 1) {
            for (Piece p : whitePieces) {
                if(p instanceof King) { p.move(1, 7); }
                if(p instanceof Rook && p.x == 0 && p.y == 7) { p.move(2, 7); }
            }
        } else if (team == 2) {
            for (Piece p : blackPieces) {
                if(p instanceof King) { p.move(6, 0); }
                if(p instanceof Rook && p.x == 7 && p.y == 0) { p.move(5, 0); }
            }
        }
    }

    // Method to Check if the Player can execute the "Castling" technique.
    public void castleCheck(int team) {
        if (team == 1) {
            for (Piece p : whitePieces) {
                if (p instanceof Rook && p.moveCount == 0 && p.x == 0 && boardData[1][7] != 1 && boardData[2][7] != 1 && boardData[1][7] != 2 && boardData[2][7] != 2) { boardData[1][7] = 4; }
            }
        } else if (team == 2) {
            for (Piece p : blackPieces) {
                if (p instanceof Rook && p.moveCount == 0 && p.x == 7 && boardData[6][0] != 1 && boardData[5][0] != 1 && boardData[6][0] != 2 && boardData[5][0] != 2) { boardData[6][0] = 4; }
            }
        }
    }

    // Reset all Game Pieces and gameState.
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
        g.setColor(Color.WHITE);
        switch (gameState) {
            // Paint the Title Screen
            case 0:
                g.drawImage(titleImage, 0, -25, null);
                break;
            // Paint the Board, Pieces, and Game UI.
            default:
                g.drawImage(backdropImage, 0, -25, null);
                g.drawImage(boardImage, boardIndent, 55, null);
                if(whiteTurn) { g.drawImage(whiteTurnImage, 90, 330, null); }
                else { g.drawImage(blackTurnImage, 90, 330, null); }
                for (Piece p : whitePieces) { p.paint(g); }
                for (Piece p : blackPieces) { p.paint(g); }
                for (int i=0; i<8; i++) {
                    for (int j=0; j<8; j++) {
                        if(boardData[i][j] == 3) { g.drawImage(highlightImage, boardIndent+(80*i), 55+(80*j), null); }
                        else if(boardData[i][j] == 4) { g.drawImage(castlingImage, boardIndent+(80*i), 55+(80*j), null); }
                    }
                }

                // If Game is Over, Paint Results Screen.
                if (gameState == 2) {
                    g.drawImage(backsplashImage, 0, -25, null);
                    g.drawString(winner, 515, 385);
                }
                break;
        }
        strategy.show();
    }
}
