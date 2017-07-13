package self.entertainment.songli;

public class Client {
   private static Client ourInstance = new Client();

   public static Client getInstance() {
      return ourInstance;
   }

   private Client() {

      

   }

   public boolean sendMsg() {
      return false;
   }


}
