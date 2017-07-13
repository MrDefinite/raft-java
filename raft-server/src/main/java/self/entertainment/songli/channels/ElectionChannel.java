package self.entertainment.songli.channels;

import org.apache.log4j.Logger;

public class ElectionChannel implements Runnable {
   private static final Logger LOGGER = Logger.getLogger(MessageChannel.class);

   private final String peerAddress;
   private final String port;

   public ElectionChannel(final String peerAddress, final String port) {
      this.peerAddress = peerAddress;
      this.port = port;
   }

   @Override
   public void run() {

      // Try to connect to peer first



      // PeerServer already connect to me!
   }
}
