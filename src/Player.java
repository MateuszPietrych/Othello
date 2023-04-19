public interface Player extends Runnable {
    public int[] returnMove();
    public void prepareMove(GameLogic.GameState gameState);
    public boolean searchingNeed();
    public void setMyGameState(GameLogic.GameState myGameState);
}
