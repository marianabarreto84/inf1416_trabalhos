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
                DigestCalculator.calculateDigest(directory, algorithm);
            } catch (Exception e) {
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

}
