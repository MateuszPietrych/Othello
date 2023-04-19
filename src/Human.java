import java.util.Scanner;
import java.util.function.Supplier;

public class Human implements Player{

    private GameType gameType;

    private int[] result;
    private int[][] board;
    private int player;
    Scanner input;
    GameLogic gameLogic;

    GameLogic.GameState gameState;


    Human(GameType gameType){
        /** This function is the constructor of the Human class
         *
         * args:
         *   gameType - the type of the game
         *
         * returns:
         *   void
         *  **/
        this.gameType = gameType;
        this.input = new Scanner(System.in);
        this.gameLogic = new GameLogic();
        this.gameState = gameLogic.new GameState();
    }


    @Override
    public int[] returnMove()
    {
        /** This function returns the move that the player has found
         *
         *  returns:
         *     the move that the player has found
         *  **/

        int[] returnResult = result;
        //reset the result, because if don't do it, the next time we call returnMove, we will return the same result
        result = null;
        return returnResult;
    }

    @Override
    public void prepareMove(GameLogic.GameState gameState)
    {
        /** This function gets a move from the user and checks if it is valid, if not, it asks the user to enter a valid move
         *
         * args:
         *   gameState - the current game state
         *
         * returns:
         *   void
         *  **/
        // set the game state
        setMyGameState(gameState.copy());
        result = null;

        int[] move = getMove();
        Supplier<int[]> getMove = () -> move;
        // check if the move is valid, use a lazy parameter becasue we want to check if move is null first, and only then check if it is valid
        while (getMove.get() == null || !gameLogic.checkMove(this.gameState, getMove.get()[0], getMove.get()[1])) {
            System.out.println("Invalid move. (PrepareMove)) ");
            int[] secondMove = getMove();
            getMove = () -> secondMove;
        }

        // set the result to the move that the user entered
        result = getMove.get();
    }

    @Override
    public boolean searchingNeed()
    {
        /** This function checks if the player is searching for a move
         *
         *  returns:
         *     true if the player is searching for a move
         *     false if the player is not searching for a move
         *  **/
        // if user still don't made a move, that mean that he is searching for a move
        if (result == null) {
            return true;
        }
        return false;
    }

    private int[] getMove() {
        /** This function gets a move from the user
         *
         *  returns:
         *     user move
         *  **/

        input = new Scanner(System.in);

        int[] move = new int[2];
        try {
            //Get row and column for move
            System.out.print("Enter move (row):    ");
            move[0] = input.nextInt();
            System.out.print("Enter move (column): ");
            move[1] = input.nextInt();
        } catch (Exception e) {
            System.out.println("Invalid move. (getMove) ");
            return null;
        }
        //check if move is valid
        if (move[0] < 0 || move[0] >= gameLogic.getBOARD_SIZE()  || move[1] < 0 || move[1] >= gameLogic.getBOARD_SIZE() ) {
            System.out.println("Invalid move. (getMove) ");
            return null;
        }
        return move;
    }

    public GameType getGameType() {
        /** This function returns the game type
         *
         *  returns:
         *     game type
         *  **/
        return gameType;
    }

    public void setMyGameState(GameLogic.GameState myGameState){
        /** This function sets the game state
         *
         *  parameters:
         *     myGameState - the game state
         *  **/
        this.gameState = myGameState.copy();
    }

    @Override
    public void run() {
        /** This function is used by thread when that thread is starting */
        prepareMove(this.gameState);
    }
}
