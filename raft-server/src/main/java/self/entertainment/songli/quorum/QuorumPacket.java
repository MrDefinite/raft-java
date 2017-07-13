package self.entertainment.songli.quorum;

import java.io.Serializable;

public class QuorumPacket implements Serializable {

   static enum PacketType {
      APPEND_ENTRIES,
      REQUEST_VOTE,
      INSTALL_SNAPSHOT
   }

   private PacketType type;
   private long id;
   private byte[] data;

   public QuorumPacket(PacketType type, long id, byte[] data) {
      this.type = type;
      this.id = id;
      this.data = data;
   }

   public PacketType getType() {
      return type;
   }

   public void setType(PacketType type) {
      this.type = type;
   }

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public byte[] getData() {
      return data;
   }

   public void setData(byte[] data) {
      this.data = data;
   }
}
