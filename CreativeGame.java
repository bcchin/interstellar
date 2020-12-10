import java.awt.event.KeyEvent;
import java.awt.Color;

public class CreativeGame extends AbstractGame {

    private static final int INTRO = 0;
    private static final int RULES = 1;
    private static final int GAME = 2;
    private static final int LEVEL = 4;

    private String PLAYER_IMG = "images/rocket.gif";
    private String SPLASH_IMG = "images/title.png";
    private String RULES_IMG = "images/instructions.png";
    private String SCREEN_IMG = "images/background.png";
    private String WIN_IMG = "images/you_win.png";
    private String LOSE_IMG = "images/you_lose.png";
    private String LVL_1_IMG = "images/level_one.png";
    private String LVL_2_IMG = "images/level_two.png";

    private String STAR_IMG = "images/star.gif";
    private String ROCK_IMG = "images/meteorite.gif";

    private int gameLevel = 1;
    private int score;
    private int currentRow;
    private int currentCol;
    private int numRows;
    private int numCols;
    private boolean isPlaying = true;
    private boolean loseGame = false;
    private boolean winGame = false;
    private String collisionImage;
    private String background;

    // default number of vertical/horizontal cells: height/width of grid
    private static final int DEFAULT_GRID_H = 5;
    private static final int DEFAULT_GRID_W = 10;

    private static final int DEFAULT_TIMER_DELAY = 100;

    protected static final int STARTING_FACTOR = 10100;

    protected int factor = STARTING_FACTOR;
    protected int level_up = 50;

    protected Location player;

    protected int screen = INTRO;

    protected GameGrid grid;


    public CreativeGame() {
        this(DEFAULT_GRID_H, DEFAULT_GRID_W);
    }

    public CreativeGame(int grid_h, int grid_w){
         this(grid_h, grid_w, DEFAULT_TIMER_DELAY);
    }



    public CreativeGame(int hdim, int wdim, int init_delay_ms) {
        super(init_delay_ms);
        grid = new GameGrid(hdim, wdim);

    }

    /******************** Methods **********************/

    protected void initGame(){
         // store and initialize user position
         numRows = grid.getNumRows();
         numCols = grid.getNumCols();
         currentCol = (numCols - 1) / 2; // middle of grid
         currentRow = numRows - 1;	// last row
         player = new Location(currentRow, currentCol);
         grid.setCellImage(player, PLAYER_IMG);
         if (gameLevel == 1)
         	 grid.setTitle("Interstellar: A Journey Through Space");
    }

    // Displays intro screen.
    protected void displayIntro(){
       grid.setSplash(SPLASH_IMG);
       background = SPLASH_IMG;
       while (screen == INTRO) {
          super.sleep(timerDelay);
          handleKeyPress();
       }
       displayRules();
       displayGame();
    }

    // Updates screen to display rules.
    protected void displayRules(){
    	grid.setSplash(RULES_IMG);
    	background = RULES_IMG;
    	while (screen == RULES) {
    		super.sleep(timerDelay);
    		handleKeyPress();
    	}
    }

    // Updates screen to display game background.
    protected void displayGame(){
    	grid.setSplash(null);
    	grid.setGameBackground(SCREEN_IMG);
    	background = SCREEN_IMG;
    }

    // Updates game while game is running
    protected void updateGameLoop() {
    	handleKeyPress();
        if (background.equals(SCREEN_IMG) && turnsElapsed % factor == 0 && isPlaying && !isGameOver()) {
            scrollLeft();
            populateRightEdge();
            updateLevel();
        }
        updateTitle();
    }

    // Updates game state to reflect adding in new cells in the right-most column
    private void populateRightEdge() {
    	for (int row = 0; row < numRows; row ++) {
    		int cellContent = rand.nextInt(100);
    		Location loc = new Location(row, numCols - 1);
    		if (cellContent < 6)
    			grid.setCellImage(loc, ROCK_IMG);
    		else if (cellContent < 12)
    			grid.setCellImage(loc, STAR_IMG);
    		else
    			grid.setCellImage(loc, null);
    	}
    }

    // Updates the game state to reflect scrolling left by one column
    private void scrollLeft() {
    	for (int row = 0; row < numRows; row ++) {
    		for (int col = 1; col < numCols; col ++) {
    			Location currentLoc = new Location(row, col);
    			Location newLoc = new Location(row, col - 1);
    			String cellImage = grid.getCellImage(currentLoc);

    			collisionImage = grid.getCellImage(newLoc);
    			if (collisionImage == PLAYER_IMG)
    				collisionImage = grid.getCellImage(currentLoc);

    			if (newLoc.equals(player)) {
    				handleCollision();
    			}
    			else if (!newLoc.equals(player) && !currentLoc.equals(player)) {
    				grid.setCellImage(currentLoc, null);
    				grid.setCellImage(newLoc, cellImage);
    			}
    		}
    	}
    }

   // Updates level of game.
   protected void updateLevel() {
   	   if (currentRow == 0 && gameLevel <= 2) {
   	   	   gameLevel += 1;
   	   	   factor -= 50;
   	   	   displayLevel();
   	   	   resetGame();
   	   }
   	   else if (currentRow == 0 && gameLevel == 3)
   	   	  winGame = true;
   }

   // Displays the level that was just completed.
    protected void displayLevel() {
    	if (gameLevel == 2)
    		grid.setSplash(LVL_1_IMG);
    	else if (gameLevel == 3)
    		grid.setSplash(LVL_2_IMG);
    	background = "level screen";
    	screen = LEVEL;
    }

   // Resets game screen for next level.
   protected void resetGame() {
   	   while (screen == LEVEL) {
   	   	   super.sleep(timerDelay);
   	   	   handleKeyPress();
   	   }
   	   screen = GAME;
   	   resetGrid();
   	   displayGame();
   	   initGame();
    }

    // Resets cell contents for next level.
    protected void resetGrid() {
    	for (int row = 0; row < numRows; row ++) {
    		for (int col = 0; col < numCols; col ++) {
    			Location cell = new Location(row, col);
    			grid.setCellImage(cell, null);
    		}
    	}
    }

    private void handleCollision() {
    	// if collision with a Star
    	if (collisionImage == STAR_IMG)
    		score += 10;
    	// if collision with a Rock
    	else if (collisionImage == ROCK_IMG)
    		loseGame = true;
    }

    // Handles actions upon key press in game
    protected void handleKeyPress() {

        int key = grid.checkLastKeyPressed();

        // Q for quit
        if (key == KeyEvent.VK_Q)
            System.exit(0);

        // S for screenshot
        else if (key == KeyEvent.VK_S)
        	grid.save("screenshot.jpg");

        // SPACEBAR to change screen
        else if (key == KeyEvent.VK_SPACE)
          screen += 1;

       // P for pause/resume game
       else if (key == KeyEvent.VK_P) {
       	   if (isPlaying == false)
       	   	   isPlaying = true;
       	   else if (isPlaying == true)
       	   	   isPlaying = false;
       }

       // rocket moves up a row
       else if (key == KeyEvent.VK_UP && currentRow != 0) {
       	   currentRow -= 1;

       	   Location nextMove = new Location(currentRow, currentCol);
       	   collisionImage = grid.getCellImage(nextMove);
       	   handleCollision();

       	   grid.setCellImage(player, null);
       	   player = new Location(currentRow, currentCol);
       	   grid.setCellImage(player, PLAYER_IMG);
       }

       // rocket moves down a row
       else if (key == KeyEvent.VK_DOWN && currentRow != grid.getNumRows() - 1) {
       	   currentRow += 1;

       	   Location nextMove = new Location(currentRow, currentCol);
       	   collisionImage = grid.getCellImage(nextMove);
       	   handleCollision();

       	   grid.setCellImage(player, null);
       	   player = new Location(currentRow, currentCol);
       	   grid.setCellImage(player, PLAYER_IMG);
       }

       // rocket moves a column to the right
       else if (key == KeyEvent.VK_RIGHT && currentCol != grid.getNumCols() -1 ) {
       	   currentCol += 1;

       	   Location nextMove = new Location(currentRow, currentCol);
       	   collisionImage = grid.getCellImage(nextMove);
       	   handleCollision();

       	   grid.setCellImage(player, null);
       	   player = new Location(currentRow, currentCol);
       	   grid.setCellImage(player, PLAYER_IMG);
       }

       // rocket moves a column to the left
       else if (key == KeyEvent.VK_LEFT && currentCol != 0) {
       	   currentCol -= 1;
           Location nextMove = new Location(currentRow, currentCol);
       	   collisionImage = grid.getCellImage(nextMove);
           handleCollision();

       	   grid.setCellImage(player, null);
       	   player = new Location(currentRow, currentCol);
       	   grid.setCellImage(player, PLAYER_IMG);
       }

    }


    // Returns the "score" of the game
    private int getScore() {
        return score;
    }


    // Updates the title bar of the game window
    private void updateTitle() {
        grid.setTitle("Current Score: " + getScore());
    }

    // Checks if game is over based on if player has won or lost game.
    // post: returns true if game is over
    protected boolean isGameOver() {
    	return (loseGame || winGame);
    }

    // Displays game outcome at the end.
    // Quits game if player loses
    protected void displayEndScreen() {
    	if (loseGame) {
    		grid.setSplash(LOSE_IMG);
    		background = LOSE_IMG;
        super.sleep(2000);
        System.exit(0);
    	}
    	else if (winGame) {
    		grid.setSplash(WIN_IMG);
    		background = WIN_IMG;
    	}
    }

    // Displays player's final score
    protected void displayOutcome() {
    	displayEndScreen();
    	if (loseGame)
    		grid.setTitle("Final score: " + score);
    	else if (winGame)
    		grid.setTitle("Final score: " + score);
    }
}
