package self.entertainment.songli;

import org.apache.log4j.Logger;
import self.entertainment.songli.quorum.QuorumPeer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class Server {
   private static final String CONFIG_FOLDER = "/Users/songlil/raft-config/";
   private static final String CONFIG_FILE = "server.properties";
   private static final String CONFIG_FILE_PARAM_PREFIX = "-propertyFile=";
   private static final Logger LOGGER = Logger.getLogger(Server.class);

   private static Server ourInstance = new Server();

   private String messagePort;
   private String electionPort;
   private Set<QuorumPeer> quorumPeers;
   private QuorumPeer self;


   public static Server getInstance() {
      return ourInstance;
   }

   private Server() {
      LOGGER.debug("Server is initializing!");

   }

   private void start() {
      quorumPeers = new HashSet<>();
      readConfig();
      self.start();
   }

   private void start(String propertyFile) {
      quorumPeers = new HashSet<>();
      readConfigFromFile(propertyFile);
      self.start();
   }


   private String getServerName(final String fullName) {
      int dot = fullName.indexOf('.');
      return fullName.substring(dot + 1);
   }

   private void readConfig() {
      readConfigFromFile(CONFIG_FOLDER + CONFIG_FILE);
   }

   private void readConfigFromFile(String file) {
      LOGGER.info("Reading server.properties now.");

      Properties p = new Properties();

      InputStream resourceStream = null;
      try {
         resourceStream = new FileInputStream(file);
      } catch (FileNotFoundException e) {
         LOGGER.warn("Failed to read config from file!");
         e.printStackTrace();
         resourceStream =
               Thread.currentThread().getContextClassLoader()
                     .getResourceAsStream(CONFIG_FILE);
      }

      try {
         p.load(resourceStream);
         messagePort = p.getProperty("message.port");
         electionPort = p.getProperty("election.port");

         LOGGER.debug("Server port is: " + messagePort);
         LOGGER.debug("Election port is: " + electionPort);

         LOGGER.debug("Begin to read peer server info");
         for (Object key : p.keySet()) {
            final String fullName = (String) key;
            if (fullName.startsWith("servername.")) {
               final String address = p.getProperty(fullName);
               final String serverName = getServerName(fullName);

               LOGGER.debug("Peer server found in config.");
               LOGGER.debug("Peer server full name: " + fullName);
               LOGGER.debug("Peer server name: " + serverName);
               LOGGER.debug("Peer server address: " + address);

               if (!address.equals("127.0.0.1")) {
                  LOGGER.debug("Add peer server to set.");
                  QuorumPeer q = new QuorumPeer(Integer.parseInt(messagePort),
                        Integer.parseInt(electionPort), serverName, address, fullName);
                  quorumPeers.add(q);
               } else {
                  LOGGER.debug("Init self peer server.");
                  self = new QuorumPeer(Integer.parseInt(messagePort),
                        Integer.parseInt(electionPort),serverName, address, fullName);
               }
            }
         }
         self.setQuorumPeers(quorumPeers);

      } catch (IOException e) {
         LOGGER.error("Failed to load server config!");
         e.printStackTrace();
      }
   }

   public static void main(String[] args) {
      Server s = Server.getInstance();

      if (args.length == 1 && args[0].startsWith(CONFIG_FILE_PARAM_PREFIX)) {
         LOGGER.debug("The properties file has been passed in.");

         final String filePath = args[0].split("=")[1];
         s.start(filePath);
      } else {
         s.start();
      }
   }
}
