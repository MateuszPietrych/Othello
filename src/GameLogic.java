import java.util.ArrayList;
import java.util.HashMap;

public class GameLogic {

    private final int BOARD_SIZE = 8;

    private HashMap<String, Integer> translateDict;

    public GameLogic()
    {
        translateDict = new HashMap<>(3);
        translateDict.put("Empty",0);
        translateDict.put("Black",1);
        translateDict.put("White",2);
    }

    public GameState makeMove(GameState gameState, int[] move)
    {
        /** This function gets a game state and a move and returns a new game state
        *
        *   args:
        *      gameState - the current game state
        *      move - the move to make
        *
        *  returns:
        *     new game state
        *  **/


        // copy the game state
        int[][] board = gameState.getBoard();
        int currentPlayer = gameState.getCurrentPlayer();

        // separate row and column from move
        int row = move[0];
        int column = move[1];

        // check if given move is placed on empty field
        if (board[row][column] != translateDict.get("Empty")) {
            System.out.println("Invalid move.  (MakeMove1))");
            return null;
        }

        // place piece on board
        board[row][column] = currentPlayer;

        // flip pieces
        int count = flipPieces(gameState, row, column, true);

        // check if some pieces were fliped if not move is incorrect
        if (count == 0) {
            board[row][column] = translateDict.get("Empty");
            System.out.println("Invalid move. (MakeMove2)");
            return null;
        }

        // set new values for black and white pieces
        if (currentPlayer == translateDict.get("Black")) {
            gameState.setBlackCount(gameState.getBlackCount() + count + 1);
            gameState.setWhiteCount(gameState.getWhiteCount() - count);
        } else {
            gameState.setWhiteCount(gameState.getWhiteCount() + count + 1);
            gameState.setBlackCount(gameState.getBlackCount() - count);
        }

        //change current player, if player can't move change player again
        int i = 0;
        do
        {
            if (currentPlayer == translateDict.get("Black")) {
                currentPlayer = translateDict.get("White");
            } else {
                currentPlayer = translateDict.get("Black");
            }
            gameState.setCurrentPlayer(currentPlayer);
            i++;
        }
        while(!checkIfPlayerCanMove(gameState) && i < 2);

        // update game state
        gameState.setBoard(board);

        return gameState;
    }

    public boolean isOver(GameState gameState)
    {
        /** This function checks if the game is over
         *
         * args:
         *   gameState - the current game state
         *
         * returns:
         *   true if the game is over
         *
         * **/

        int blackCount = gameState.getBlackCount();
        int whiteCount = gameState.getWhiteCount();

        // check if all fields are occupied
        if (blackCount + whiteCount == 64)
        {
            return true;
        }
        return false;
    }


    public String getWinner(GameState gameState)
    {
        /** This function gets a game state and returns the winner
         *
         * args:
         *   gameState - the current game state
         *
         * returns:
         *   the winner
         *
         * **/
        int blackCount = gameState.getBlackCount();
        int whiteCount = gameState.getWhiteCount();

        // check who has more pieces
        if (blackCount > whiteCount)
        {
            return "Black";
        }
        else if (whiteCount > blackCount)
        {
            return "White";
        }
        else
        {
            return "Draw";
        }
    }


    public int[][] generateStartingMap()
    {
        /** This function generates the starting map
         *
         * returns:
         *   the starting map
         *
         * **/
        int[][] board = new int[BOARD_SIZE][BOARD_SIZE];

        // set all fields to empty
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = translateDict.get("Empty");
            }
        }

        // set starting pieces
        board[3][3] = translateDict.get("White");
        board[4][4] = translateDict.get("White");
        board[3][4] = translateDict.get("Black");
        board[4][3] = translateDict.get("Black");

        return board;
    }

    public String getBoardToDisplay(GameState gameState)
    {
        /** This function gets a game state and returns a string representation of the board
         *
         * args:
         *   gameState - the current game state
         *
         * returns:
         *   string representation of the board
         *
         * **/


        int[][] board = gameState.getBoard();
        String boardInString = "  0 1 2 3 4 5 6 7\n";

        for (int i = 0; i < BOARD_SIZE; i++) {
            boardInString+= i + "|";
            for (int j = 0; j < BOARD_SIZE; j++) {
                switch (board[i][j])
                {
                    case 0:
                        boardInString += " |";
                        break;
                    case 1:
                        boardInString += "B|";
                        break;
                    case 2:
                        boardInString += "W|";
                        break;
                }
            }
            boardInString+="\n";
        }
        return  boardInString;
    }


    private int flipPieces(GameState gameState, int x, int y, boolean flipingMode) {
        /** This function flips pieces on the board when a move is made
         *
         * args:
         *   gameState - the current game state
         *   x - x coordinate of the move
         *   y - y coordinate of the move
         *   flipingMode - if true pieces will be fliped
         *
         * returns:
         *   number of pieces fliped
         *
         * **/

        // set board from game state
        int[][] board = gameState.getBoard();
        // set current player from game state
        int currentPlayer = gameState.getCurrentPlayer();

        int count = 0;
        // check all directions
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                int currX = x + i;
                int currY = y + j;
                int flipCount = 0;
                // check if there are pieces to flip, and how many of them
                while (currX >= 0 && currX < BOARD_SIZE && currY >= 0 && currY < BOARD_SIZE &&
                        board[currX][currY] != translateDict.get("Empty") && board[currX][currY] != currentPlayer) {
                    currX += i;
                    currY += j;
                    flipCount++;
                }

                // flip pieces
                if(flipingMode)
                {
                    // if there are pieces to flip, flip them
                    if (flipCount > 0 && currX >= 0 && currX < BOARD_SIZE && currY >= 0 && currY < BOARD_SIZE &&
                            board[currX][currY] == currentPlayer) {
                        currX = x + i;
                        currY = y + j;
                        while (currX >= 0 && currX < BOARD_SIZE && currY >= 0 && currY < BOARD_SIZE &&
                                board[currX][currY] != translateDict.get("Empty") && board[currX][currY] != currentPlayer) {
                            board[currX][currY] = currentPlayer;
                            currX += i;
                            currY += j;
                            count++;
                        }
                    }
                }
                else
                {
                    // if there are pieces to flip, return the number of pieces to flip
                    if(flipCount > 0 && currX >= 0 && currX < BOARD_SIZE && currY >= 0 && currY < BOARD_SIZE &&
                            board[currX][currY] == currentPlayer)
                    {
                        return flipCount;
                    }
                }

            }
        }
        //update game state
        gameState.setBoard(board);

        return count;
    }

    public boolean checkIfPlayerCanMove(GameState gameState)
    {
        /** This function checks if the current player can make a move
         *
         * args:
         *   gameState - the current game state
         *
         * returns:
         *   true if the current player can make a move, false otherwise
         *
         * **/

        //set board from game state
        int[][] board = gameState.getBoard();

        // check board field by field, if there is posible move from that field
        for(int i=0; i<BOARD_SIZE; i++)
        {
            for(int j=0; j<BOARD_SIZE; j++)
            {
                if(board[i][j] == translateDict.get("Empty") )
                {
                    // get flip from position
                    int filipCounter = flipPieces(gameState,i,j,false);
                    if(filipCounter != 0)
                    {
                      return true;
                    }
                }
            }
        }
        return false;
    }

    public int getBOARD_SIZE() {
        /** This function returns the board size
         *
         * args:
         *   none
         *
         * returns:
         *   board size
         *
         * **/
        return BOARD_SIZE;
    }

    public boolean checkMove(GameState gameState, int x, int y)
    {
        /** This function checks if a move is valid
         *
         * args:
         *   gameState - the current game state
         *   x - x coordinate of the move
         *   y - y coordinate of the move
         *
         * returns:
         *   true if the move is valid, false otherwise
         *
         * **/

        //set board from game state
        int[][] board = gameState.getBoard();

        //set current player from game state
        int currentPlayer = gameState.getCurrentPlayer();

        // check if the move is in the board
        if(x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE)
        {
            // check if field is empty
            if(board[x][y] == translateDict.get("Empty"))
            {
                board[x][y] = currentPlayer;
                int count = flipPieces(gameState, x, y, false);

                // if there are no pieces to flip, the move is not valid
                if (count == 0) {
                    board[x][y] = translateDict.get("Empty");
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public ArrayList<int[]> getAllAvailableMoves(GameState gameState)
    {
        /** This function returns all available moves for the current player
         *
         * args:
         *   gameState - the current game state
         *
         * returns:
         *   array list of all available moves
         *
         * **/

        // setup board and available moves array
        int[][] board = gameState.getBoard();
        ArrayList<int[]> availableMoves = new ArrayList<>();

        // check all fields on the board
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                // if the field is empty, check if there are pieces to flip
                if (board[i][j] == translateDict.get("Empty") && flipPieces(gameState, i, j, false) != 0)
                {
                    availableMoves.add(new int[]{i,j});
                }
            }
        }
        return availableMoves;
    }

    public String translatePlayer(int player)
    {
        /** This function translates player represent by number to player represent by string
         *
         * args:
         *   player - player represent by number
         *
         * returns:
         *   player represent by string
         *
         * **/
        switch (player)
        {
            case 0:
                return " ";
            case 1:
                return "Black";
            case 2:
                return "White";
        }
        return null;
    }

    public int[] getRandomMove(GameState gameState)
    {
        /** This function returns a random move for the current player
         *
         * args:
         *   gameState - the current game state
         *
         * returns:
         *   array of the random move
         *
         * **/

        //set board from game state
        int[][] board = gameState.getBoard();
        //set current player from game state
        int currentPlayer = gameState.getCurrentPlayer();

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == translateDict.get("Empty") && flipPieces(gameState, i, j, false) != 0)
                {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }




    public int getScoreForGameState(GameLogic.GameState  gameState, int strategy)
    {
        /** This function returns the score of the game state
         *  It uses the strategy manager or the strategy from player
         *
         * args:
         *   gameState - the current game state
         *   strategy - the strategy that the player uses
         *
         * returns:
         *   the score of the game state
         *  **/
        switch (strategy)
        {
            case 0:
                return getScoreByDiffrenceBetweenBlackAndWhite(gameState);

            case 1:
                return getScoreByCloseToSides(gameState);

            case 2:
                return getScoreByCorners(gameState);

            case 3:
                return getScoreByStability(gameState) + getScoreByCorners(gameState)*15;

            case 4:
                return getScoreByMobility(gameState) + getScoreByCorners(gameState)*30;

            case 5:
                return getScoreByStability(gameState)*2 + getScoreByCorners(gameState)*30 +getScoreByMobility(gameState);

            default:
                return 0;
        }
    }

    public int strategyManager(GameLogic.GameState  gameState)
    {
        /** This function returns the strategy that the player should use
         *
         * args:
         *   gameState - the current game state
         *
         * returns:
         *   the strategy that the player should use
         *  **/
       return strategyManager(gameState, 0);
    }

    public int strategyManager(GameLogic.GameState  gameState,  int lastDepth)
    {
        /** This function returns the strategy that the player should use
         *
         * args:
         *   gameState - the current game state
         *
         * returns:
         *   the strategy that the player should use
         *  **/

        int boardSize = BOARD_SIZE;
        int MIN_MOBILITY_NEEDED = 2;
        int MAX_MOBILITY_NEEDED = 8;

        //if the game is almost over
        if( boardSize*boardSize - (gameState.getBlackCount() + gameState.getWhiteCount()) < lastDepth - 1)
        {
            return 0;
        }
        //if player mobility is very low
        else if(getScoreByMobility(gameState)<=MIN_MOBILITY_NEEDED)
        {
            return 4;
        }
        //if player mobility is very high
        else if(getScoreByMobility(gameState)>=MAX_MOBILITY_NEEDED)
        {
            return 3;
        }
        //neutral strategy
        else
        {
            return 5;
        }
    }

    private int getScoreByDiffrenceBetweenBlackAndWhite(GameLogic.GameState gameState)
    {
        /** This function returns the score of the game state by the diffrence between black and white
         *
         * args:
         *   gameState - the current game state
         *
         * returns:
         *   the score of the game state by the diffrence between black and white
         *  **/
        if(this.translatePlayer(gameState.getCurrentPlayer()) == "Black")
        {
            return gameState.getBlackCount()- gameState.getWhiteCount();
        }
        else
        {
            return gameState.getWhiteCount()- gameState.getBlackCount();
        }
    }

    private int getScoreByCloseToSides(GameLogic.GameState gameState)
    {
        /** This function returns the score of the game state by the number of pieces that are close to the sides
         *
         * args:
         *   gameState - the current game state
         *
         * returns:
         *   the score of the game state by the number of pieces that are close to the sides
         *  **/
        int counterBySide = 0;
        for (int i = 0; i < this.getBOARD_SIZE(); i++) {
            for (int j = 0; j < this.getBOARD_SIZE(); j++)
            {
                //if the piece is close to the sides
                if (this.translatePlayer(gameState.getBoard()[i][j]) == this.translatePlayer(gameState.getCurrentPlayer())
                        && (i == 0 || i == this.getBOARD_SIZE() - 1 || j == 0 || j == this.getBOARD_SIZE() - 1))
                {
                    counterBySide++;
                }
            }
        }
        return counterBySide;
    }

    private int getScoreByCorners(GameLogic.GameState gameState)
    {
        /** This function returns the score of the game state by the number of pieces that are in the corners
         *
         * args:
         *   gameState - the current game state
         *
         * returns:
         *   the score of the game state by the number of pieces that are in the corners
         *  **/
        int counterInCorner = 0;
        counterInCorner += gameState.getBoard()[0][0] == gameState.getCurrentPlayer() ? 1 : 0;
        counterInCorner += gameState.getBoard()[0][BOARD_SIZE - 1] == gameState.getCurrentPlayer() ? 1 : 0;
        counterInCorner += gameState.getBoard()[BOARD_SIZE - 1][0] == gameState.getCurrentPlayer() ? 1 : 0;
        counterInCorner += gameState.getBoard()[BOARD_SIZE - 1][BOARD_SIZE - 1] == gameState.getCurrentPlayer() ? 1 : 0;

        int otherPlayer = gameState.getCurrentPlayer() == 1 ? 2 : 1;
        counterInCorner += gameState.getBoard()[0][0] == otherPlayer ? -1 : 0;
        counterInCorner += gameState.getBoard()[0][BOARD_SIZE - 1] == otherPlayer ? -1 : 0;
        counterInCorner += gameState.getBoard()[BOARD_SIZE - 1][0] == otherPlayer ? -1 : 0;
        counterInCorner += gameState.getBoard()[BOARD_SIZE - 1][BOARD_SIZE - 1] == otherPlayer ? -1 : 0;
        return counterInCorner;
    }

    private int getScoreByMobility(GameLogic.GameState gameState)
    {
        /** This function returns the score of the game state by the number of available moves
         *
         * args:
         *   gameState - the current game state
         *
         * returns:
         *   the score of the game state by the number of available moves
         *  **/
        return this.getAllAvailableMoves(gameState).size();
    }

    public int getScoreByStability(GameLogic.GameState gameState)
    {
        /** This function returns the score of the game state by the number of stable pieces
         *
         * args:
         *   gameState - the current game state
         *
         * returns:
         *   the score of the game state by the number of stable pieces
         *  **/

        // get 4 corners
        int[][] cornersInfo = {{0,0,1, 1}, {0,BOARD_SIZE-1,1, -1}, {BOARD_SIZE-1,0, -1, 1}, {BOARD_SIZE-1,BOARD_SIZE-1,-1, -1}};

        // create a list of all stable pieces
        int[][] stablePieces = new int[BOARD_SIZE][BOARD_SIZE];
        int stablePiecesCounter = 0;

        for (int[] rowFields: stablePieces
        ) {
            for (int field: rowFields
            ) {
                field = 0;
            }
        }

        // go from corners to find stable pieces
        for (int[] corner : cornersInfo) {

            // check if corner same color as current player and stable
            if(gameState.getBoard()[corner[0]][corner[1]] == gameState.getCurrentPlayer())
            {
                stablePieces[corner[0]][corner[1]] = 1;
                stablePiecesCounter++;
            }
            boolean ifMoreStable = true;

            // go from the corner to the sides to find stable pieces correctly
            for (int i=0; i<BOARD_SIZE && ifMoreStable; i++ )
            {
                ifMoreStable = false;
                int x = 0;
                int y = 0;
                for (int j=0; j<BOARD_SIZE-i; j++)
                {
                    x = corner[0] + (i+j)*corner[2];
                    y = corner[1] + i*corner[3];

                    // if the piece is the same color as the current player and is stable
//                  if (  this.translatePlayer(gameState.getBoard()[x][y]) == this.translatePlayer(gameState.getCurrentPlayer()) && isStable(x, y, stablePieces))

                    if ( gameState.getBoard()[x][y] != 0 && isStable(x, y, stablePieces))
                    {
                        // add the piece to the list of stable pieces
                        stablePieces[x][y] = 1;
                        stablePiecesCounter += gameState.getBoard()[x][y]==gameState.getCurrentPlayer() ? 1 : -1;
                        ifMoreStable = true;
                    }

                    x = corner[0] + i*corner[2];
                    y = corner[1] + (i+j)*corner[3];

                    // if the piece is the same color as the current player and is stable
                    if (gameState.getBoard()[x][y] != 0 && isStable(x, y, stablePieces))
                    {
                        // add the piece to the list of stable pieces
                        stablePieces[x][y] = 1;
                        stablePiecesCounter += gameState.getBoard()[x][y]==gameState.getCurrentPlayer() ? 1 : -1;
                        ifMoreStable = true;
                    }
                }
            }
        }

        // return the number of stable pieces
        return stablePiecesCounter;
    }

    public boolean isStable( int row, int column, int[][] stablePieces)
    {
        /** This function returns if the piece is stable
         *
         * args:
         *   gameState - the current game state
         *   row - the row of the piece
         *   column - the column of the piece
         *   stablePieces - the list of stable pieces
         *
         * returns:
         *   if the piece is stable
         *  **/

        // if the piece is in the corners
        if ((row == 0 && column == 0) || (row == 0 && column == BOARD_SIZE - 1) || (row == BOARD_SIZE - 1 && column == 0) || (row == BOARD_SIZE - 1 && column == BOARD_SIZE - 1))
        {
            return true;
        }

        // create a list of all directions
        int[][]  directions = new int[4][2];
        directions[0][0] = 1;directions[0][1] = 1;
        directions[1][0] = 1;directions[1][1] = -1;
        directions[2][0] = 1;directions[2][1] = 0;
        directions[3][0] = 0;directions[3][1] = 1;

        // check if the piece is stable, by checking if the piece is surrounded by stable pieces from all directions
        for (int[] direction: directions)
        {
            int rowPlus = row + direction[0];
            int rowMinus = row - direction[0];
            int columnPlus = column + direction[1];
            int columnMinus = column - direction[1];

            // if the piece is outside the board we know that it is stable
            if(0<=rowPlus && rowPlus < BOARD_SIZE && 0 <= rowMinus && rowMinus < BOARD_SIZE &&
                    0 <= columnPlus && columnPlus < BOARD_SIZE && 0 <= columnMinus  && columnMinus < BOARD_SIZE)
            {
                //if piece is not stable in this direction return false
                if(stablePieces[rowPlus][columnPlus] != 1 && stablePieces[rowMinus][columnMinus] != 1)
                {
                    return false;
                }
            }
        }
        return true;
    }

    public class GameState
    {
        /** This class represents the game state
         *
         * **/
        private int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
        private int currentPlayer;
        private int blackCount = 2;
        private int whiteCount = 2;
        public int[][] getBoard() {
            return board;
        }

        public void setBoard(int[][] board) {
            this.board = board;
        }

        public int getCurrentPlayer() {
            return currentPlayer;
        }

        public void setCurrentPlayer(int currentPlayer) {
            this.currentPlayer = currentPlayer;
        }

        public int getBlackCount() {
            return blackCount;
        }

        public void setBlackCount(int blackCount) {
            this.blackCount = blackCount;
        }

        public int getWhiteCount() {
            return whiteCount;
        }

        public void setWhiteCount(int whiteCount) {
            this.whiteCount = whiteCount;
        }

        public GameState copy()
        {
            /** This function returns a copy of the game state
             *
             * args:
             *   none
             *
             * returns:
             *   copy of the game state
             *
             * **/
            GameState newGameState = new GameState();
            int[][] newBoard = new int[BOARD_SIZE][BOARD_SIZE];
            for (int i = 0; i < BOARD_SIZE; i++)
            {
                for (int j = 0; j < BOARD_SIZE; j++)
                {
                    newBoard[i][j] = this.board[i][j];
                }
            }
            newGameState.setBoard(newBoard);
            newGameState.setCurrentPlayer(currentPlayer);
            newGameState.setBlackCount(blackCount);
            newGameState.setWhiteCount(whiteCount);
            return newGameState;
        }

    }

}
