import java.io.*;
import java.security.*;

public class DigestCalculator{

    private static String convertBytesToHex(byte[] bytes){
        char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
        char[] hex_characters = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hex_characters[i * 2] = HEX_ARRAY[v >>> 4];
            hex_characters[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hex_characters);
    }

    public static void calculateDigest(File directory, String algorithm) throws Exception{
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        File[] files = directory.listFiles();
        for(File file : files){
            if(file.isFile()){
                FileInputStream file_stream = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int n = 0;
                while((n = file_stream.read(buffer)) != -1){
                    digest.update(buffer, 0, n);
                }
                file_stream.close();
                byte[] file_digest = digest.digest();
                String file_digest_string = convertBytesToHex(file_digest);
                System.out.println(file.getName() + ":" + file_digest_string);
            }
        }

    }

}