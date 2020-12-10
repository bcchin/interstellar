public class GameLauncher{
  // main class to run the game

   private static AbstractGame game;

   public static void main(String[] args) {
     game = new CreativeGame(5, 10, 0);
     System.out.println("From GameLauncher main: Running the CreativeGame version ");
     game.play();
   }

}
