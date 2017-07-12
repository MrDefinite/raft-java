package self.entertainment.songli.message;


public class Server {
   private static Server ourInstance = new Server();

   public static Server getInstance() {
      return ourInstance;
   }

   private Server() {


   }

   public boolean sendMsg() {
      return false;
   }


}
