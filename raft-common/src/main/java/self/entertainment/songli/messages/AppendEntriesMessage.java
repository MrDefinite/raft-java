package self.entertainment.songli.messages;

import self.entertainment.songli.Entry;

import java.io.Serializable;

public class AppendEntriesMessage implements Serializable {

   private long term;
   private long leaderId;
   private long prevLogIndex;
   private Entry[] entries;
   private long leaderCommit;


}
