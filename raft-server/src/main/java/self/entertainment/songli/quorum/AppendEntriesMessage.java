package self.entertainment.songli.quorum;

import java.io.Serializable;

public class AppendEntriesMessage implements Serializable {

   private long term;
   private long leaderId;
   private long prevLogIndex;
   private Entry[] entries;
   private long leaderCommit;


}
