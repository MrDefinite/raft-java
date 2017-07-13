package self.entertainment.songli.model;

import self.entertainment.songli.channels.ElectionChannel;
import self.entertainment.songli.channels.MessageChannel;

public final class Channels {

   private final ElectionChannel electionChannel;
   private final MessageChannel messageChannel;

   public Channels(
         ElectionChannel electionChannel,
         MessageChannel messageChannel) {
      this.electionChannel = electionChannel;
      this.messageChannel = messageChannel;
   }

   public ElectionChannel getElectionChannel() {
      return electionChannel;
   }

   public MessageChannel getMessageChannel() {
      return messageChannel;
   }
}
