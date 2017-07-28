package self.entertainment.songli.util;

import java.io.*;
import java.nio.ByteBuffer;

public class ByteUtil {

   public static byte[] getBytes(Serializable obj) throws IOException {
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(bout);
      out.writeObject(obj);
      out.flush();
      byte[] bytes = bout.toByteArray();
      bout.close();
      out.close();
      return bytes;
   }

   public static int sizeof(Serializable obj) throws IOException {
      return getBytes(obj).length;
   }

   public static Object getObject(byte[] bytes) throws IOException, ClassNotFoundException {
      ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
      ObjectInputStream oi = new ObjectInputStream(bi);
      Object obj = oi.readObject();
      bi.close();
      oi.close();
      return obj;
   }

   public static Object getObject(ByteBuffer byteBuffer) throws ClassNotFoundException, IOException {
      InputStream input = new ByteArrayInputStream(byteBuffer.array());
      ObjectInputStream oi = new ObjectInputStream(input);
      Object obj = oi.readObject();
      input.close();
      oi.close();
      byteBuffer.clear();
      return obj;
   }

   public static ByteBuffer getByteBuffer(Serializable obj) throws IOException {
      byte[] bytes = ByteUtil.getBytes(obj);
      ByteBuffer buff = ByteBuffer.wrap(bytes);
      return buff;
   }

}
