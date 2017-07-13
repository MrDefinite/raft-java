package self.entertainment.songli.util;

import java.util.Random;

public class CommonUtil {

   private static final long DEFAULT_HEARTBEAT_INTERVAL = 50;

   /**
    * timeout is between 150ms and 300ms
    * @return the random election timeout
    */
   public static long getElectionTimeout() {
      Random r = new Random();
      return r.nextInt(150) + 150;
   }

   public static long getHeartBeatInterval() {
      return DEFAULT_HEARTBEAT_INTERVAL;
   }
}
