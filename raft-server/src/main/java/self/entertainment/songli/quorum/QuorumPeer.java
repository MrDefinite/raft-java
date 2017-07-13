package self.entertainment.songli.quorum;

import org.apache.log4j.Logger;
import self.entertainment.songli.RaftThread;
import self.entertainment.songli.util.CommonUtil;
import self.entertainment.songli.util.Timer;

import java.net.InetSocketAddress;

public class QuorumPeer extends RaftThread {

   private static final Logger LOGGER = Logger.getLogger(QuorumPeer.class);
   private final String serverName;
   private final String serverAddress;
   private final String serverFullName;

   public enum ServerState {
      FOLLOWER, CANDIDATE, LEADER
   }

   // Init server state as a follower
   private volatile ServerState state = ServerState.FOLLOWER;

   private long currentElectionTimeout;
   private long heartbeatInterval = CommonUtil.getHeartBeatInterval();

   private Thread timerThread = null;

   public QuorumPeer(String serverName, String serverAddress,
                     String serverFullName) {
      super("QuorumPeer");

      this.serverName = serverName;
      this.serverAddress = serverAddress;
      this.serverFullName = serverFullName;
   }

   volatile boolean running = true;
   public void setRunning(boolean running) {
      this.running = running;
   }

   public boolean isRunning() {
      return running;
   }

   private void startTimer() {
      if (timerThread != null) {
         stopTimer();
      }

      timerThread = new Thread(new Timer(CommonUtil.getElectionTimeout()));
      timerThread.start();
   }

   private void stopTimer() {
      if (timerThread == null) {
         LOGGER.warn("There is no timer to stop");
         return;
      }

      timerThread.interrupt();
      timerThread = null;
   }

   private void runAsFollower() {
      while (true) {
         if (timerThread == null) {
            startTimer();
         }
      }
   }

   private void handleRPC() {

   }

   @Override
   public void run() {
      LOGGER.info("Starting quorum peer");

      while (running) {
         switch (state) {
            case LEADER:
               break;
            case CANDIDATE:
               break;
            case FOLLOWER:
               runAsFollower();

               break;
         }
      }

   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      QuorumPeer that = (QuorumPeer) o;

      if (heartbeatInterval != that.heartbeatInterval) return false;
      if (running != that.running) return false;
      return state == that.state;
   }

   @Override
   public int hashCode() {
      int result = state != null ? state.hashCode() : 0;
      result =
            31 * result +
                  (int) (heartbeatInterval ^ (heartbeatInterval >>> 32));
      result = 31 * result + (running ? 1 : 0);
      return result;
   }
}
