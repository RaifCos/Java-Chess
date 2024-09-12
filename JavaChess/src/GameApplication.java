/**
 * JavaChess - A Multiplayer 2D Chess Game.
 * @Author - Raif Costello (https://github.com/RaifCos)
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.util.ArrayList;

// TODO: Implement "Check" Mode (Players can only make moves to protect their King).
// TODO: Tidy Code (it's a mess at the moment)

public class GameApplication extends JFrame implements Runnable, MouseListener {

    private boolean isInitialised = false;
    private final BufferStrategy strategy;
    private final Graphics offscreenBuffer;
    private int gameState = 0;
    private String winner;

    private final Image titleImage;
    private final Image boardImage;
    private final Image backdropImage;
    private final Image highlightImage;
    private final Image castlingImage;
    private final Image backsplashImage;
    private final Image[] TurnImage = new Image[2];
    private final Image[] promoImage = new Image[2];
    private final int boardIndent = 280;

    public int[][] boardData = new int[8][8];
    private Piece selectedPiece;
    private Pawn upgradePawn;
    private final ArrayList<Piece> whitePieces = new ArrayList<>();
    private final ArrayList<Piece> blackPieces = new ArrayList<>();

    // Boolean which states if it is White's or Black's Turn.
    private boolean whiteTurn;

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
        TurnImage[0] = (new ImageIcon(workingDirectory + "\\assets\\UI\\turn0.png")).getImage();
        TurnImage[1] = (new ImageIcon(workingDirectory + "\\assets\\UI\\turn1.png")).getImage();
        promoImage[0] = (new ImageIcon(workingDirectory + "\\assets\\UI\\promotion0.png")).getImage();
        promoImage[1] = (new ImageIcon(workingDirectory + "\\assets\\UI\\promotion1.png")).getImage();

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
                // The mouse is clicked during the game, so check if a location on the board was clicked.
                if(clickX > boardIndent && clickX < 920 && clickY > 55 && clickY < 695) {
                    pieceInteraction(((clickX - boardIndent) / 80), ((clickY - 55) / 80));
                } else { refreshBoardData(); }
                break;
            case 2:
                reset();
                break;
            case 3:
                pawnUpgrade(clickX, clickY);
                break;
        }
    }

    // Other MouseListener Events
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public boolean checkButton(int clickX, int clickY, int xPos, int yPos, int width, int height) {
        return (clickX >= xPos) && (clickX <= xPos + width) && (clickY >= yPos) && (clickY <= yPos + height);

    }

    // Method to set up and start the Game
    public void startGame() {
        // White always goes first.
        whiteTurn = true;
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
        // Refresh the Board to "place" all the Pieces, then start the game.
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
            if (selectedPiece instanceof Pawn && (selectedPiece.y == 0 || selectedPiece.y == 7)) { pawnUpgradeTransition(selectedPiece.x, selectedPiece.team); }
            capturePiece(tileX, tileY);
            refreshBoardData();
            if (gameState != 3) { whiteTurn = !whiteTurn; }
        } // No Piece is Selected, so check if the Player is clicking on a Piece to select it.
        else {
            refreshBoardData();
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
        if(result == 0) { winner = "White Wins!"; }
        else if(result == 1) { winner = "Black Wins!"; }
        gameState = 2;
    }

    // Method that carries out the "Castling" technique.
    public void castling(int team) {
        // Selected Piece is a White King, so Castle on the White Side.
        if (team == 0) {
            for (Piece p : whitePieces) {
                if(p instanceof King) { p.move(1, 7); }
                if(p instanceof Rook && p.x == 0 && p.y == 7) { p.move(2, 7); }
            }
        } // Selected Piece is a Black King, so Castle on the Black Side.
        else if (team == 1) {
            for (Piece p : blackPieces) {
                if(p instanceof King) { p.move(6, 0); }
                if(p instanceof Rook && p.x == 7 && p.y == 0) { p.move(5, 0); }
            }
        }
    }

    // Method to Check if the Player can execute the "Castling" technique.
    public void castleCheck(int team) {
        // Selected Piece is a White King, so check if the White Team can Castle.
        if (team == 0) {
            for (Piece p : whitePieces) {
                if (p instanceof Rook && p.moveCount == 0 && p.x == 0 && boardData[1][7] != 1 && boardData[2][7] != 1 && boardData[1][7] != 2 && boardData[2][7] != 2) { boardData[1][7] = 4; break; }
            }
        } // Selected Piece is a Black King, so check if the Black Team can Castle.
        else if (team == 1) {
            for (Piece p : blackPieces) {
                if (p instanceof Rook && p.moveCount == 0 && p.x == 7 && boardData[6][0] != 1 && boardData[5][0] != 1 && boardData[6][0] != 2 && boardData[5][0] != 2) { boardData[6][0] = 4; break; }
            }
        }
    }

    // Method to Promote Pawns when they reach the opposite end of the board.
    public void pawnUpgradeTransition(int xPos, int team) {
        System.out.println(team);
        if(team == 0) {
            for (Piece p : whitePieces) {
                if(p.x == xPos && p.y == 0) {
                    upgradePawn = (Pawn) p;
                    whitePieces.remove(p);
                    gameState = 3;
                    return;
                }
            }
        } if(team == 1) {
            for (Piece p : blackPieces) {
                if(p.x == xPos && p.y == 7) {
                    upgradePawn = (Pawn) p;
                    blackPieces.remove(p);
                    gameState = 3;
                    return;
                }
            }
        }
    }

    public void pawnUpgrade(int xClick, int yClick) {
        if(upgradePawn == null) { return; }
        boolean upgradeSuccess = false;

        if (checkButton(xClick, yClick, 414, 330, 90, 90)) {
            if(whiteTurn) { whitePieces.add(new Rook(0, upgradePawn.x, upgradePawn.y)); }
            else { blackPieces.add(new Rook(1, upgradePawn.x, upgradePawn.y)); }
            upgradeSuccess = true;
        }

        if (checkButton(xClick, yClick, 508, 330, 90, 90)) {
            if(whiteTurn) { whitePieces.add(new Knight(0, upgradePawn.x, upgradePawn.y)); }
            else { blackPieces.add(new Knight(1, upgradePawn.x, upgradePawn.y)); }
            upgradeSuccess = true;
        }

        if (checkButton(xClick, yClick, 602, 330, 90, 90)) {
            if(whiteTurn) { whitePieces.add(new Bishop(0, upgradePawn.x, upgradePawn.y)); }
            else { blackPieces.add(new Bishop(1, upgradePawn.x, upgradePawn.y)); }
            upgradeSuccess = true;
        }

        if (checkButton(xClick, yClick, 696, 330, 90, 90)) {
            if(whiteTurn) { whitePieces.add(new Queen(0, upgradePawn.x, upgradePawn.y)); }
            else { blackPieces.add(new Queen(1, upgradePawn.x, upgradePawn.y)); }
            upgradeSuccess = true;
        }

        if(upgradeSuccess) {
            gameState = 1;
            upgradePawn = null;
            whiteTurn = !whiteTurn;
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
            case 0: // Paint the Title Screen.
                g.drawImage(titleImage, 0, -25, null);
                break;
            default: // Paint the Board, Pieces, and Game UI.
                g.drawImage(backdropImage, 0, -25, null);
                g.drawImage(boardImage, boardIndent, 55, null);
                if(whiteTurn) { g.drawImage(TurnImage[0], 90, 330, null); }
                else { g.drawImage(TurnImage[1], 90, 330, null); }
                for (Piece p : whitePieces) { p.paint(g); }
                for (Piece p : blackPieces) { p.paint(g); }
                for (int i=0; i<8; i++) {
                    for (int j=0; j<8; j++) {
                        if(boardData[i][j] == 3) { g.drawImage(highlightImage, boardIndent+(80*i), 55+(80*j), null); }
                        else if(boardData[i][j] == 4) { g.drawImage(castlingImage, boardIndent+(80*i), 55+(80*j), null); }
                    }
                }
                if(gameState == 2) {
                    // Paint the Results Screen over the Game.
                    g.drawImage(backsplashImage, 0, -25, null);
                    g.drawString(winner, 515, 385);
                }

                if(gameState == 3) {
                    // Paint the Promotion Menu
                    g.drawImage(backsplashImage, 0, -25, null);
                    if(whiteTurn) { g.drawImage(promoImage[0], 412, 330, null); }
                    if(!whiteTurn) { g.drawImage(promoImage[1], 412, 330, null); }
                }
                break;
        }
        strategy.show();
    }
}
