package self.entertainment.songli.quorum;

import org.apache.log4j.Logger;
import self.entertainment.songli.message.TestMessage;
import self.entertainment.songli.util.ByteUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Election {
   private static final Logger LOGGER = Logger.getLogger(Election.class);
   private static final int REQUEST_THREAD_NUM = 6;
   private static int electionPort;
   private static ConcurrentLinkedQueue<TestMessage> msgQueue;

   private static volatile Election instance = new Election();

   private Set<QuorumPeer> quorumPeers;
   private ExecutorService electionExecutorService;

   static class Sender implements Runnable {
      private static final Logger LOGGER = Logger.getLogger(Sender.class);
      private QuorumPeer q;
      private Selector selector;

      Sender(QuorumPeer q) {
         LOGGER.debug("Sender initializing");
         this.q = q;
      }

      @Override
      public void run() {
         LOGGER.debug("Sender send msg to peer: " + q.getServerName());

         SocketChannel channel;
         try {
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            selector = SelectorProvider.provider().openSelector();
            channel.connect(
                  new InetSocketAddress(q.getServerAddress(), electionPort));
            channel.register(selector, SelectionKey.OP_CONNECT);
         } catch (IOException e) {
            e.printStackTrace();
         }

         while (true) {
            if (!selector.isOpen()) {
               break;
            }

            try {
               selector.select();
               Iterator it = selector.selectedKeys().iterator();

               while (it.hasNext()) {
                  SelectionKey key = (SelectionKey) it.next();

                  if (key.isConnectable()) {
                     connectAndSend(key);
                  }

                  it.remove();
               }
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }

      private void connectAndSend(SelectionKey sk) {
         LOGGER.debug("Going to connect to server: " + q.getServerName());
         SocketChannel channel = (SocketChannel) sk.channel();
         if (channel.isConnectionPending()) {
            try {
               channel.finishConnect();

               channel.configureBlocking(false);

               LOGGER.debug("Send message to server: " + q.getServerName());
               channel
                     .write(ByteBuffer.wrap(new String("test!").getBytes()));

               LOGGER.debug("Message sent to " + q.getServerName() +
                     ". Close channel now");
               channel.close();
               sk.selector().close();

               q.online = true;
            } catch (IOException e) {
               e.printStackTrace();

               LOGGER.debug("Peer server " + q.getServerName() +
                     " cannot be contacted, wait for some time.");
               q.online = false;
            }
         }
      }
   }

   static class MessageProcessor implements Runnable {
      private int messageCount = 0;

      @Override
      public void run() {
         LOGGER.debug("Message processor begins to work.");
         while (true) {
            TestMessage t = msgQueue.poll();
            messageCount++;
         }
      }
   }

   static class Receiver implements Runnable {
      private static final Logger LOGGER = Logger.getLogger(Receiver.class);
      private Set<QuorumPeer> quorumPeers;
      private final int port;
      private Selector selector = null;
      private static final int READ_BUFFER_SIZE = 8192;

      Receiver(Set<QuorumPeer> quorumPeers, int port) {
         this.quorumPeers = quorumPeers;
         this.port = port;
      }

      @Override
      public void run() {
         LOGGER.debug("Election listen channel launched!");

         ServerSocketChannel server = null;
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
                     doAccept(key);
                  } else if (key.isValid() && key.isReadable()) {
                     doRead(key);
                  }
                  it.remove();
               }

            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }

      private void doAccept(SelectionKey sk) {
         ServerSocketChannel server = (ServerSocketChannel) sk.channel();
         SocketChannel clientChannel;

         try {
            clientChannel = server.accept();
            clientChannel.configureBlocking(false);

            SelectionKey clientKey =
                  clientChannel.register(selector, SelectionKey.OP_READ);

            InetAddress clientAddress = clientChannel.socket().getInetAddress();
            LOGGER.debug("Accepted connection from: " +
                  clientAddress.getHostAddress());
         } catch (IOException e) {
            e.printStackTrace();
         }

      }

      private void doRead(SelectionKey sk) {
         SocketChannel channel = (SocketChannel) sk.channel();
         int len;
         ByteBuffer byteBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);

         try {
            len = channel.read(byteBuffer);
            if (len < 0) {
               disconnect(sk);
               return;
            }

         } catch (IOException e) {
            e.printStackTrace();
            disconnect(sk);
            return;
         }
         byteBuffer.flip();
         try {
            InetSocketAddress inetSocketAddress =
                  (InetSocketAddress) channel.getLocalAddress();

            Object o = ByteUtil.getObject(byteBuffer);
            if (o instanceof TestMessage) {
               TestMessage t = (TestMessage) o;
               msgQueue.add(t);
            } else if (o instanceof String) {
               String s = (String) o;
               LOGGER.debug("Receive string: " + s + " from server " +
                     getNodeNameFromAddress(
                           inetSocketAddress.getAddress().getHostAddress()));
            }
         } catch (ClassNotFoundException e) {
            LOGGER.error("Cannot cast receiving buffer to test message");
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      private void disconnect(SelectionKey sk) {
         SocketChannel channel = (SocketChannel) sk.channel();

         InetAddress clientAddress = channel.socket().getInetAddress();
         LOGGER.debug(clientAddress.getHostAddress() + " disconnected.");

         try {
            channel.close();
         } catch (Exception e) {
            LOGGER.error("Failed to close client socket channel.");
            e.printStackTrace();
         }
      }

      private String getNodeNameFromAddress(String serverAddress) {
         for (QuorumPeer q : quorumPeers) {
            if (q.getServerAddress().equals(serverAddress)) {
               return q.getServerName();
            }
         }
         return "";
      }
   }

   private Election() {
      LOGGER.debug("New election is constructing now");

      msgQueue = new ConcurrentLinkedQueue<>();
      electionExecutorService =
            Executors.newFixedThreadPool(REQUEST_THREAD_NUM);
   }

   public void setQuorumPeers(Set<QuorumPeer> quorumPeers) {
      this.quorumPeers = quorumPeers;
   }

   public void setElectionPort(int electionPort) {
      this.electionPort = electionPort;
   }

   public static Election getInstance() {
      return instance;
   }

   void beginElection() {
      if (quorumPeers == null) {
         throw new NullPointerException("quorumPeers cannot be empty");
      }

      LOGGER.debug("Being new election now!");

      // open listen channel
      electionExecutorService.submit(new Receiver(quorumPeers, electionPort));

      for (QuorumPeer q : quorumPeers) {
         // send msg to peer
         electionExecutorService.submit(new Sender(q));
      }

      electionExecutorService.submit(new MessageProcessor());
   }

   void stopElection() {

   }
}
