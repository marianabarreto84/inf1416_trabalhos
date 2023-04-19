import java.io.*;
import java.security.*;

public class DigestCalculatorTest {

    public static void main(String[] args) {
        
        // Valida os argumentos
        if (DigestCalculator.validateArgs(args) == -1){
            return;
        }


        // Criar uma pasta temporária com alguns arquivos de teste
        File directory = new File(System.getProperty("user.dir"), "testdir");
        directory.mkdir();

        File file1 = new File(directory, "file1.txt");
        File file2 = new File(directory, "file2.txt");

        try {
            FileWriter writer1 = new FileWriter(file1);
            FileWriter writer2 = new FileWriter(file2);

            // Escreve o conteúdo nos arquivos temporários
            writer1.write("conteúdo do arquivo 1");
            writer1.close();
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
