
public class Engine implements Player {

    private final GameLogic gameLogic;

    private GameLogic.GameState myGameState;

    private int[] result;

    private int countOfVisitedNodes = 0;

    private int strategy = 0;

    private boolean useStrategyManager = false;

    private int lastDepth = 0;

    private int currentPlayer = 0;


    Engine(boolean useStrategyManager)
    {
        /** This function is the constructor of the Engine class
         *
         * args:
         *   useStrategyManager - if true, the engine will use the strategy manager in games
         *
         * returns:
         *   void
         *  **/
        gameLogic = new GameLogic();
        myGameState = null;
        result = null;
        this.useStrategyManager = useStrategyManager;
    }
    @Override
    public int[] returnMove() {
        /** This function returns the move that the engine has found
         *
         *  returns:
         *     the move that the engine has found
         *  **/
        System.out.println("Visited nodes: "+countOfVisitedNodes + " Depth: "+lastDepth);
        int[] returnResult = result;
        //reset the result, because if don't do it, the next time we call returnMove, we will return the same result
        result = null;
        return returnResult;
    }

    @Override
    public void prepareMove(GameLogic.GameState gameState) {
        /** This function finds the best move for the engine
         *
         * args:
         *   gameState - the current game state
         *
         * returns:
         *   void
         *  **/

        // function dig deeper and deeper until it finds time is finished,
        // it dig to a certain depth, and then increment the depth and search again
        int depth = 1;
        currentPlayer = gameState.getCurrentPlayer();
        while(depth <= gameLogic.getBOARD_SIZE()*gameLogic.getBOARD_SIZE())
        {
            int[] bestMove = findBestMove(gameState, depth);
            result = bestMove;
            lastDepth = depth;
            depth+=2;
        }
    }

    private int[] findBestMove(GameLogic.GameState gameState,  int depth)
    {
        /** This function finds the best move for the engine for certain depth using the alpha-beta algorithm
         *  It also uses the strategy manager or the strategy from engine
         *  This function start the tree search
         *
         * args:
         *   gameState - the current game state
         *   depth - the depth of the search
         *
         * returns:
         *   the best move for the engine
         *  **/
        //set variables
        int[] bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        countOfVisitedNodes = 0;

        //get the strategy
        int currentStrategy = useStrategyManager ? gameLogic.strategyManager(gameState, lastDepth) : strategy;

        //find the best move
        for (int[] move:  gameLogic.getAllAvailableMoves(gameState))
        {
            //make a copy of the game state and make the move
            GameLogic.GameState newGameState = gameState.copy();
            gameLogic.makeMove(newGameState,move);

            //find the score of the move
            int score = alphaBetaMinMax(newGameState,depth,alpha,beta,false,currentStrategy);

            //if the score is better than the best score, update the best score and the best move
            if(score>bestScore)
            {
                bestScore = score;
                bestMove = move;
            }
//            System.out.println("Move: "+move[0]+" "+move[1]+" Score: "+bestScore+" Strategy: "+currentStrategy);

        }
//
        return bestMove;
    }


    private int alphaBetaMinMax(GameLogic.GameState  gameState, int depth, int alpha, int beta, boolean isMaxLevel, int strategy)
    {
        /** This function finds the best score for the engine for certain depth using the alpha-beta algorithm
         *  It also uses the strategy manager or the strategy from engine
         *
         * args:
         *   gameState - the current game state
         *   depth - the depth of the search
         *   alpha - the alpha value
         *   beta - the beta value
         *   isMaxLevel - if true, we serach for max value, else we search for min value
         *   strategy - the strategy that the engine uses
         *
         * returns:
         *   the best score for the engine
         *  **/
        //set variables
        int bestScoreOnLevel = isMaxLevel ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        //if the depth is 0 or the game is over, return the score of the game state
        if(depth==0 || gameLogic.isOver(gameState))
        {
            if (currentPlayer != gameState.getCurrentPlayer())
            {
                return  isMaxLevel ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            }
            return  gameLogic.getScoreForGameState(gameState,strategy);
        }

            //find the best score
            for (int[] move:  gameLogic.getAllAvailableMoves(gameState))
            {
                //make a copy of the game state and make the move
                GameLogic.GameState newGameState = gameState.copy();
                gameLogic.makeMove(newGameState,move);

                //count the visited nodes
                countOfVisitedNodes++;

                //find the score of the move
                int score = alphaBetaMinMax(newGameState,depth-1,alpha,beta,!isMaxLevel,strategy);

                //if the score is better than the best score, update the best score
                if(isMaxLevel) {
                    if (score > bestScoreOnLevel)
                    {
                        bestScoreOnLevel = score;
                    }

                    alpha = Math.max(alpha, score);

                    //if alpha is bigger than beta, we can stop the search
                    if (alpha >= beta)
                    {
                        break;
                    }
                }
                else
                {
                    if (score < bestScoreOnLevel)
                    {
                        bestScoreOnLevel = score;
                    }

                    beta = Math.min(beta, score);

                    //if beta is bigger than alpha, we can stop the search
                    if(alpha<=beta)
                    {
                        break;
                    }

                }
            }

        return bestScoreOnLevel;
    }

    @Override
    public boolean searchingNeed() {
        /** This function returns if the engine is searching for a move
         *
         * args:
         *   none
         *
         * returns:
         *   if the engine is searching for a move
         *  **/
        // engine will always search for a move beacuse he always dig deeper and deeper
        return true;
    }

    @Override
    public void setMyGameState(GameLogic.GameState gameState) {
        /** This function sets the current game state
         *
         * args:
         *   gameState - the current game state
         *
         * returns:
         *   none
         *  **/
        myGameState = gameState;
    }

    @Override
    public void run() {
        /** This function runs the engine, it is used by thread when that thread is starting
         *
         * args:
         *   none
         *
         * returns:
         *   none
         *  **/
        prepareMove(myGameState);
    }


    public void setStrategy(int strategy) {
        /** This function sets the strategy of the engine
         *
         * args:
         *   strategy - the strategy of the engine
         *
         * returns:
         *   none
         *  **/
        this.strategy = strategy;
    }

    public void setUseStrategyManager(boolean useStrategyManager) {
        /** This function sets if the engine will use the strategy manager
         *
         * args:
         *   useStrategyManager - if the engine will use the strategy manager
         *
         * returns:
         *   none
         *  **/
        this.useStrategyManager = useStrategyManager;
    }

    public boolean isUseStrategyManager() {
        /** This function returns if the engine will use the strategy manager
         *
         * args:
         *   none
         *
         * returns:
         *   if the engine will use the strategy manager
         *  **/
        return useStrategyManager;
    }
}
