package self.entertainment.songli.channels;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class MessageChannel implements Runnable {
   private static final Logger LOGGER = Logger.getLogger(MessageChannel.class);

   private final int port;

   public MessageChannel(final int port) {
      this.port = port;
   }

   @Override
   public void run() {
      LOGGER.debug("Message channel begins now.");

      ServerSocketChannel server = null;
      Selector selector = null;
      try {
         server = ServerSocketChannel.open();

         server.configureBlocking(false);

         server.bind(new InetSocketAddress(port));

         selector = Selector.open();
         server.register(selector, server.validOps());

      } catch (IOException e) {
         e.printStackTrace();
      }

      while (true) {
         LOGGER.debug("Message channel begins to receive messages.");
         try {
            selector.select();
            Iterator it = selector.selectedKeys().iterator();

            while (it.hasNext()) {
               SelectionKey key = (SelectionKey) it.next();

               if (key.isAcceptable()) {
                  ServerSocketChannel serverSocketChannel =
                        (ServerSocketChannel) key.channel();

                  SocketChannel client = serverSocketChannel.accept();
//                  client.

               } else if (key.isReadable()) {

               }

               it.remove();
            }

         } catch (IOException e) {
            e.printStackTrace();
         }
      }


   }
}
