import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    public static int randomInt(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static byte[] getBytesFromWav(String filename)throws Exception{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        AudioInputStream in = AudioSystem.getAudioInputStream(new File(filename));
        int read;
        byte[] buff = new byte[1024];
        while ((read = in.read(buff)) > 0) {out.write(buff, 0, read);}
        out.flush();
        return out.toByteArray();
    }

    public static byte[] mixBuffers(byte[] bufferA, byte[] bufferB) {
        byte[] array = new byte[bufferA.length];
        for (int i=0; i<bufferA.length; i+=2) {
            short buf1A = bufferA[i+1];
            short buf2A = bufferA[i];
            buf1A = (short) ((buf1A & 0xff) << 8);
            buf2A = (short) (buf2A & 0xff);
            short buf1B = bufferB[i+1];
            short buf2B = bufferB[i];
            buf1B = (short) ((buf1B & 0xff) << 8);
            buf2B = (short) (buf2B & 0xff);
            short buf1C = (short) (buf1A + buf1B);
            short buf2C = (short) (buf2A + buf2B);
            short res = (short) (buf1C | buf2C);
            array[i] = (byte) res;
            array[i+1] = (byte) (res >> 8);
        }
        return array;
    }

    public static byte[] combineArr(byte[] a, byte[] b){
        int length = a.length + b.length;
        byte[] result = new byte[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

}