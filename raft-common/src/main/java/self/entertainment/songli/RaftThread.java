package self.entertainment.songli;

import org.apache.log4j.Logger;

public class RaftThread extends Thread {

   private static final Logger LOGGER = Logger.getLogger(RaftThread.class);

   public RaftThread(String threadName) {
      super(threadName);

      setUncaughtExceptionHandler(
            (t, e) -> LOGGER
                  .warn("Exception occurred from thread " + t.getName() + ": " +
                        e));
   }
}
