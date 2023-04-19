import java.io.*;
import java.security.*;

public class DigestCalculatorTest {

    public static void main(String[] args) {
        // Criar uma pasta temporária com alguns arquivos de teste
        File directory = new File(System.getProperty("java.io.tmpdir"), "testdir");
        directory.mkdir();
        File file1 = new File(directory, "file1.txt");
        FileWriter writer1;
        try {
            writer1 = new FileWriter(file1);
            writer1.write("conteúdo do arquivo 1");
            writer1.close();
            File file2 = new File(directory, "file2.txt");
            FileWriter writer2 = new FileWriter(file2);
            writer2.write("conteúdo do arquivo 2");
            writer2.close();
        
            // Calcular o digest dos arquivos na pasta temporária usando o algoritmo MD5
            String algorithm = "MD5";
            try {
                MessageDigest digest = MessageDigest.getInstance(algorithm);
                File[] files = directory.listFiles();
                for (File file : files) {
                    if (file.isFile()) {
                        FileInputStream fis = new FileInputStream(file);
                        byte[] buffer = new byte[1024];
                        int n = 0;
                        while ((n = fis.read(buffer)) != -1) {
                            digest.update(buffer, 0, n);
                        }
                        fis.close();
                        byte[] fileDigest = digest.digest();
                        String fileDigestString = bytesToHex(fileDigest);
                        System.out.println(file.getName() + ": " + fileDigestString);
                    }
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        
            // Deletar a pasta temporária e seus arquivos de teste
            file1.delete();
            file2.delete();
            directory.delete();
            
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}
