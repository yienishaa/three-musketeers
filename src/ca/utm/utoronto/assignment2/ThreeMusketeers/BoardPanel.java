package ca.utm.utoronto.assignment2.ThreeMusketeers;

import java.util.List;

import ca.utm.utoronto.assignment2.ThreeMusketeers.ThreeMusketeers.GameMode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

public class BoardPanel extends GridPane implements EventHandler<ActionEvent> {

    private final View view;
    private final Board board;
    private Cell from;
    

    /**
     * Constructs a new GridPane that contains a Cell for each position in the board
     *
     * Contains default alignment and styles which can be modified
     * @param view
     * @param board
     */
    public BoardPanel(View view, Board board) {
        this.view = view;
        this.board = board;

        // Can modify styling
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: #181a1b;");
        int size = 550;
        this.setPrefSize(size, size);
        this.setMinSize(size, size);
        this.setMaxSize(size, size);

        setupBoard();
        updateCells();
    }


    /**
     * Setup the BoardPanel with Cells
     */
    private void setupBoard(){ // TODO
    	
    	this.setVgap(2);
    	this.setHgap(2);
    	
    	List<Cell> playerList = this.board.getAllCells();
    	
    	
    	for(Cell player : playerList)
    	{
    		player.setOnAction(this);
    		super.setConstraints(player, player.getCoordinate().col,player.getCoordinate().row);
    	}
    	
    	super.getChildren().addAll(playerList);
    	

    }

    /**
     * Updates the BoardPanel to represent the board with the latest information
     *
     * If it's a computer move: disable all cells and disable all game controls in view
     *
     * If it's a human player turn and they are picking a piece to move:
     *      - disable all cells
     *      - enable cells containing valid pieces that the player can move
     * If it's a human player turn and they have picked a piece to move:
     *      - disable all cells
     *      - enable cells containing other valid pieces the player can move
     *      - enable cells containing the possible destinations for the currently selected piece
     *
     * If the game is over:
     *      - update view.messageLabel with the winner ('MUSKETEER' or 'GUARD')
     *      - disable all cells
     */
    protected void updateCells(){ // TODO
    	
    	List<Cell> allCells = this.board.getAllCells();
    
    	//If its a computer move
    	if(!this.view.model.isHumanTurn())
    	{	
    		if(this.view.undoButton != null)
    		{
    			this.view.undoButton.setDisable(true);
    		}
    		if(this.view.restartButton != null)
    		{
    			this.view.restartButton.setDisable(true);
    		}
    		if(this.view.saveButton != null)
    		{
    			this.view.saveButton.setDisable(true);
    		}
    		
    		for(Cell cell : allCells)
    		{
    			cell.setDisable(true);
    			cell.setDefaultColor();
    		}
    	}
    	else
    	{
    		//for all human moves
    		
    		//Human turn and they are picking a piece to move
    		if(this.from==null)
    		{
    			this.view.setUndoButton();
    			
    			//disabling all cells
        		for(Cell cell : allCells)
        		{
        			cell.setDisable(true);
        			cell.setDefaultColor();
        		}
        		
        		List<Cell> validCells = this.board.getPossibleCells();
        		
        		//enabling cells containing valid pieces that the player can move
        		for(Cell cell : validCells)
        		{
        			cell.setDisable(false);
        			cell.setAgentFromColor();
        		}	
    		}
    		else
    		{
    			//Human turn and they have picked a piece to move
    			
    			
    			//disabling all cells
        		for(Cell cell : allCells)
        		{
        			cell.setDisable(true);
        			cell.setDefaultColor();
        		}
        		
        		//enabling cells containing other valid pieces the player can move
        		List<Cell> validCells = this.board.getPossibleCells();
        		for(Cell cell : validCells)
        		{
        			cell.setDisable(false);
        			cell.setAgentFromColor();
        		}
        		
    		     //enable cells containing the possible destinations for the currently selected piece
    			List<Cell> g = this.board.getPossibleDestinations(from);
    			for(Cell cell : g)
        		{
        			cell.setDisable(false);
        			cell.setOptionsColor();
        		}
    		}
    	}
    	
    	//If the game is over
    	if (board.isGameOver()) 
    	{
    		//update view.messageLabel with the winner ('MUSKETEER' or 'GUARD')
    		view.setMessageLabel(board.getWinner().getType() + " is the Winner!");
    		
    		//disabling all cells
    		for(Cell cell : allCells)
    		{
    			cell.setDisable(true);
    			cell.setDefaultColor();
    		}
    	}
    }

    /**
     * Handles Cell clicks and updates the board accordingly
     * When a Cell gets clicked the following must be handled:
     *  - If it's a valid piece that the player can move, select the piece and update the board
     *  - If it's a destination for a selected piece to move, perform the move and update the board
     * @param actionEvent
     */
    @Override
    public void handle(ActionEvent actionEvent) { // TODO
    	
    	Cell clickedCell = (Cell) actionEvent.getSource();
    	
    	//Possibly a piece is chosen to move
    	if(from==null)
    	{
    		//If its the correct from piece chosen, set it as the from piece
    		if( clickedCell.hasPiece() && clickedCell.getPiece().getType() == this.board.getTurn())
    		{
    			this.from = clickedCell;
    		}
    		else
    		{
    			System.out.println("Wrong piece selected");
    		}
    		
    	}
    	else
    	{
    		//a destination is chosen
    		
    		//List<Move> moveList = this.board.getPossibleMoves();
    		
    		List<Cell> allCells = this.board.getAllCells();
    		
    		Move moving = new Move(this.from, clickedCell );
    		
    		if(this.board.isValidMove(moving))
    		{
    			this.view.model.move(moving);
    			this.from.setDefaultColor();
    			
    			for(Cell box : allCells)
    			{
    				box.setDefaultColor();
    			}
    			
    			this.from = null;
    		}
    		
    		/*for(Move move : moveList)
    		{
    			if(move.fromCell == this.from)
    			{
    				if(move.toCell== cell)
    				{
    					
    					Move movePiece = new Move(from, cell);
    					
    		    		this.board.move(movePiece);
    		    		this.from = null;
    				}
    			}
    			
    		}*/
    		
    	}
    	
    	this.view.runMove();
    	
    }
}
