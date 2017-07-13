package self.entertainment.songli.quorum;

public class QuorumStats {

   public enum STATE {

   }


   /**
    * latest term server has seen (initialized to on first boot,
    * increases monotonically)
    */
   private long currentTerm = 0;

   /**
    * candidateId that received vote in current term (or null if none)
    */
   private String votedFor = null;

//   private Log


   private final Provider provider;

   public QuorumStats(
         Provider provider) {
      this.provider = provider;
   }

   public interface Provider {
      static public final String UNKNOWN_STATE = "unknown";
      static public final String LOOKING_STATE = "leaderelection";
      static public final String LEADING_STATE = "leading";
      static public final String FOLLOWING_STATE = "following";
      static public final String OBSERVING_STATE = "observing";

      public String[] getQuorumPeers();

      public String getServerState();
   }


   public String getServerState() {
      return provider.getServerState();
   }

   public String[] getQuorumPeers() {
      return provider.getQuorumPeers();
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(super.toString());
      String state = getServerState();
      if (state.equals(Provider.LEADING_STATE)) {
         sb.append("Followers:");
         for (String f : getQuorumPeers()) {
            sb.append(" ").append(f);
         }
         sb.append("\n");
      } else if (state.equals(Provider.FOLLOWING_STATE)
            || state.equals(Provider.OBSERVING_STATE)) {
         sb.append("Leader: ");
         String[] ldr = getQuorumPeers();
         if (ldr.length > 0)
            sb.append(ldr[0]);
         else
            sb.append("not connected");
         sb.append("\n");
      }
      return sb.toString();
   }

}
