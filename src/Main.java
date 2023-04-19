
public class Main {
    public static void main(String[] args)
    {

        Human human = new Human(GameType.CvC);
        Human human2 = new Human(GameType.HvH);

        Server server = new Server();
//        server.serverConfig();

        server.addPlayer(human);
        server.addPlayer(human2);


        try {
            server.serverMain();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

