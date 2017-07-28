package self.entertainment.songli.quorum;

import org.apache.log4j.Logger;
import self.entertainment.songli.RaftThread;
import self.entertainment.songli.util.CommonUtil;

import java.util.Set;
import java.util.concurrent.*;

public class QuorumPeer extends RaftThread {

   private static final Logger LOGGER = Logger.getLogger(QuorumPeer.class);
   private final String serverName;
   private final String serverAddress;
   private final String serverFullName;
   private final int messagePort;
   private final int electionPort;
   private Set<QuorumPeer> quorumPeers;
   private ScheduledExecutorService electionExecutor;
   private ScheduledFuture electionFuture = null;
   private Election election;
   public boolean online = false;

   public enum ServerState {
      FOLLOWER, CANDIDATE, LEADER
   }

   // Init server state as a follower
   private volatile ServerState state = ServerState.FOLLOWER;

   private long currentElectionTimeout;
   private long heartbeatInterval = CommonUtil.getHeartBeatInterval();


   public QuorumPeer(int messagePort, int electionPort,
                     String serverName, String serverAddress,
                     String serverFullName, Set<QuorumPeer> quorumPeers) {
      super("QuorumPeer");

      this.messagePort = messagePort;
      this.electionPort = electionPort;
      this.serverName = serverName;
      this.serverAddress = serverAddress;
      this.serverFullName = serverFullName;
      this.quorumPeers = quorumPeers;
      this.online = true;

      initElectionCom(quorumPeers, electionPort);
   }

   public QuorumPeer(int messagePort, int electionPort,
                     String serverName, String serverAddress,
                     String serverFullName) {
      this(messagePort, electionPort, serverName, serverAddress,
            serverFullName, null);
   }

   private void initElectionCom(Set<QuorumPeer> quorumPeers, int electionPort) {
      electionExecutor = Executors.newSingleThreadScheduledExecutor();
      election = Election.getInstance();
      election.setQuorumPeers(quorumPeers);
      election.setElectionPort(electionPort);
   }

   public void setQuorumPeers(Set<QuorumPeer> quorumPeers) {
      election.setQuorumPeers(quorumPeers);
      this.quorumPeers = quorumPeers;
   }

   volatile boolean running = true;
   public void setRunning(boolean running) {
      this.running = running;
   }

   public boolean isRunning() {
      return running;
   }

   private void startTimer() {
      LOGGER.debug("Start timer for election");
      electionFuture = electionExecutor.schedule(() -> {
         LOGGER.debug("Election timeout reached! Begin election!");

         election.beginElection();
      }, CommonUtil.getElectionTimeout(), TimeUnit.MILLISECONDS);

   }

   private void stopTimer() {
      if (electionFuture == null) {
         throw new NullPointerException("electionFuture is null!");
      }

      LOGGER.debug("Stopping election timer!");
      electionFuture.cancel(false);
      electionFuture = null;
   }

   private void runAsFollower() {
      while (true) {
         if (electionFuture == null) {
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

   public String getServerName() {
      return serverName;
   }

   public String getServerAddress() {
      return serverAddress;
   }

   public String getServerFullName() {
      return serverFullName;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      QuorumPeer that = (QuorumPeer) o;

      if (serverName != null ? !serverName.equals(that.serverName) :
            that.serverName != null) return false;
      if (serverAddress != null ? !serverAddress.equals(that.serverAddress) :
            that.serverAddress != null) return false;
      return serverFullName != null ?
            serverFullName.equals(that.serverFullName) :
            that.serverFullName == null;
   }

   @Override
   public int hashCode() {
      int result = serverName != null ? serverName.hashCode() : 0;
      result =
            31 * result +
                  (serverAddress != null ? serverAddress.hashCode() : 0);
      result =
            31 * result +
                  (serverFullName != null ? serverFullName.hashCode() : 0);
      return result;
   }
}
