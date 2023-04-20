import java.util.ArrayList;
import java.util.Scanner;

public class Server {

    private final Engine engine1;
    private final Engine engine2;
    private ArrayList<Human> players;

    private final GameLogic gameLogic;

    private final int MAX_ROUND_TIME = 5000;

    public Server() {
        /** This function is the constructor of the Server class
         *  **/
        engine1 = new Engine(true);
        engine2 = new Engine(false);
        players = new ArrayList<>();
        gameLogic = new GameLogic();

        engine1.setStrategy(0);
        engine2.setStrategy(0);
    }
    public Server(Engine engine1, Engine engine2) {
        /** This function is the constructor of the Server class
         *  **/
        this.engine1 = engine1;
        this.engine2 = engine2;
        players = new ArrayList<>();
        gameLogic = new GameLogic();
    }

    public void serverConfig()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("If engine1 (engine players play against) shloud use strategy manager, enter 1, else enter 0");
        while (true)
        {
            int input = Integer.parseInt(scanner.nextLine());
            if (input == 1)
            {
                engine1.setUseStrategyManager(true);
                break;
            }
            else if (input == 0)
            {
                engine1.setUseStrategyManager(false);
                break;
            }
            else
            {
                System.out.println("Invalid input, try again");
            }
        }
        System.out.println("If engine2 (used if two engines play) shloud use strategy manager, enter 1, else enter 0");
        while (true)
        {
            int input = Integer.parseInt(scanner.nextLine());
            if (input == 1)
            {
                engine1.setUseStrategyManager(true);
                break;
            }
            else if (input == 0)
            {
                engine1.setUseStrategyManager(false);
                break;
            }
            else
            {
                System.out.println("Invalid input, try again");
            }
        }
        if(!engine1.isUseStrategyManager())
        {
            System.out.println("Enter the strategy for engine1");
            while (true)
            {
                int input = Integer.parseInt(scanner.nextLine());
                if (input >= 0)
                {
                    engine1.setStrategy(input);
                    break;
                }
                else
                {
                    System.out.println("Invalid input, try again");
                }
            }
        }
        if(!engine2.isUseStrategyManager())
        {
            System.out.println("Enter the strategy for engine1");
            while (true)
            {
                int input = Integer.parseInt(scanner.nextLine());
                if (input >= 0)
                {
                    engine2.setStrategy(input);
                    break;
                }
                else
                {
                    System.out.println("Invalid input, try again");
                }
            }
        }

    }

    public void serverMain() throws InterruptedException {
        /** This function is the main function of the server it manages diffrent players and starts the games
         *
         *  returns:
         *     void
         *  **/
        //value for the first player for HvH, used for pairing human players
        Human firstPlayerForHvH = null;

        //main loop
        while(true)
        {
            Thread.sleep(100);
            // go for each player
            for (Human player : players)
            {
                // check if the player is ready to play, and what type of game he wants to start
                switch (player.getGameType())
                {
                    case HvH:
                        if (firstPlayerForHvH == null)
                        {
                            firstPlayerForHvH = player;
                        }
                        else
                        {
                            System.out.println("Game started");
                            // start game HvH
                            play(firstPlayerForHvH, player);
                            firstPlayerForHvH = null;
                        }
                        break;
                    case HvC:
                        // start game HvC
                        play(player, engine1);
                        break;
                    case CvC:
                        // start game CvC
                        play(engine1, engine2);
                        break;
                    default:
                        break;
                }
            }

        }
    }

    public void addPlayer(Human player) {
        /** This function adds a player to the server
         *
         *  args:
         *     player - the player to add
         *  **/
        players.add(player);
    }

    public void play(Player player1, Player player2) throws InterruptedException {
        /** This function starts a game between two players, enigne or human
         *
         *  args:
         *     player1 - the first player
         *     player2 - the second player
         *  **/
        // init game state
        GameLogic.GameState gameState = gameLogic.new GameState();
        gameState.setBoard(gameLogic.generateStartingMap());
        gameState.setCurrentPlayer(1);

        // set variables
        Player currentPlayer = player1;
        boolean errorExit = false;

        // main game loop
        while (!gameLogic.isOver(gameState) && !errorExit)
        {
            // set the game state
            currentPlayer.setMyGameState(gameState.copy());

            // print the board,the current player
            System.out.println(gameLogic.getBoardToDisplay(gameState));
            System.out.println("Current Player: "+gameLogic.translatePlayer(gameState.getCurrentPlayer()));
            System.out.println("Black: "+gameState.getBlackCount()+" White: "+gameState.getWhiteCount());
            System.out.println("Strategy: "+gameLogic.strategyManager(gameState,8));

            // start the thread for the current player
            Thread thread1 = new Thread(currentPlayer);
            long startTime = System.currentTimeMillis();
            long endTime = System.currentTimeMillis();
            thread1.start();

            // wait for the player to return a move and stop the thread if it takes too long
            while(MAX_ROUND_TIME > (endTime - startTime) && currentPlayer.searchingNeed())
            {
                endTime = System.currentTimeMillis();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            // get the move from the player
            int[] playedMove = currentPlayer.returnMove();

            // stop the thread if it is still running
            if (thread1.isAlive())
            {
                thread1.stop();
                System.out.println("Stopped thread");
            }

            //get random move if player didn't return any
            if (playedMove == null)
            {
                if(gameLogic.checkIfPlayerCanMove(gameState))
                {
                    if(gameLogic.getRandomMove(gameState) != null)
                    {
                        playedMove = gameLogic.getRandomMove(gameState);
                    }
                }
                else
                {
                    errorExit = true;
                }
            }

            // make the move
            if (playedMove != null && !errorExit)
            {
                gameLogic.makeMove(gameState,playedMove);
            }

            // switch the current player
            if (gameState.getCurrentPlayer() == 2)
            {
                currentPlayer = player2;
            }
            else
            {
                currentPlayer = player1;
            }


        }

        // print the winner
        System.out.println("Game over");
        System.out.println(gameLogic.getBoardToDisplay(gameState));
        System.out.println("Winner: "+ gameLogic.getWinner(gameState));

    }

}

