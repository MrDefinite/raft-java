package self.entertainment.songli.util;

import org.apache.log4j.Logger;

public class Timer implements Runnable {
   private static final Logger LOGGER = Logger.getLogger(Timer.class);

   private long ElectionTimeout;

   public Timer(long ElectionTimeout) {
      this.ElectionTimeout = ElectionTimeout;
   }

   @Override
   public void run() {
      try {
         LOGGER.info("Begin to count election timeout!");
         Thread.sleep(ElectionTimeout);

         LOGGER.info("Election timeout reached! Begin new election.");
         // TODO begin new election
      } catch (InterruptedException e) {
         LOGGER.debug(
               "Stop to count election timeout, already receive heartbeat" +
                     " from leader.");
      }
   }
}
