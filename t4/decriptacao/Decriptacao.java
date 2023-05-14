import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Base64;


public class Decriptacao{

    public static PrivateKey restauraChavePrivada(String nome_arquivo, String frase_secreta) throws Exception{
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        KeyGenerator chave = KeyGenerator.getInstance("DES");
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

        byte[] dados_chave_privada = Files.readAllBytes(Paths.get(nome_arquivo));

        random.setSeed(frase_secreta.getBytes()); // utiliza a frase secreta como semente para o PRNG
        chave.init(56, random);
        SecretKey chave_simetrica = chave.generateKey();

        cipher.init(Cipher.DECRYPT_MODE, chave_simetrica);
        byte[] chave_privada_bytes = cipher.doFinal(dados_chave_privada); // decrypt
        String chave_privada_string = new String(chave_privada_bytes);
        chave_privada_string = chave_privada_string.replace("\n", "");
        chave_privada_string = chave_privada_string.replace("-----BEGIN PRIVATE KEY-----", "");
        chave_privada_string = chave_privada_string.replace("-----END PRIVATE KEY-----", "");
        byte[] chave_privada_bytes_decodificada = Base64.getDecoder().decode(chave_privada_string); // decode base-64
        PKCS8EncodedKeySpec user_keyspec = new PKCS8EncodedKeySpec(chave_privada_bytes_decodificada); // encoded key spec
        KeyFactory kf = KeyFactory.getInstance("RSA"); // key factory
        PrivateKey chave_privada = kf.generatePrivate(user_keyspec); // objeto PrivateKey
        
        return chave_privada;
    }

    public static SecretKey decriptaEnvelope(String nome_arquivo, PrivateKey chave_privada) throws Exception{
        byte[] chave_secreta_bytes = new byte[8];
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        KeyGenerator chave = KeyGenerator.getInstance("DES");

        byte[] dados_envelope = Files.readAllBytes(Paths.get(nome_arquivo + ".env")); // lê arquivo do envelope digital
    
        cipher.init(Cipher.DECRYPT_MODE, chave_privada);
        byte[] semente = cipher.doFinal(dados_envelope);

        random.setSeed(semente);
        chave.init(56, random);
        SecretKey chave_secreta = chave.generateKey();

        return chave_secreta;
    }

    public static byte[] decriptaArquivo(String nome_arquivo, SecretKey chave_secreta) throws Exception{
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

        cipher.init(Cipher.DECRYPT_MODE, chave_secreta);
        byte[] dados_encriptados = Files.readAllBytes(Paths.get(nome_arquivo + ".enc"));
        byte[] dados_decriptados = cipher.doFinal(dados_encriptados);

        return dados_decriptados;
    }

    public static byte[] recuperaTextoPlano(String nome_arquivo, String caminho_chave_privada, String frase_secreta) throws Exception{
        PrivateKey chave_privada = restauraChavePrivada(caminho_chave_privada, frase_secreta);
        SecretKey chave_secreta = decriptaEnvelope(nome_arquivo, chave_privada);
        byte[] dados_arquivo = decriptaArquivo(nome_arquivo, chave_secreta);
        return dados_arquivo;
    }

    public static void main(String args[]){
        PrivateKey chave_privada = null;
        SecretKey chave_secreta = null;
        byte[] dados_arquivo = null;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Frase secreta: ");
        String frase_secreta = scanner.nextLine();
        String caminho_arquivo = "Pacote-T4/Files/XXYYZZ11";
        String caminho_chave_privada = "Pacote-T4/Keys/user01-pkcs8-des.key";
        scanner.close();

        try{
            chave_privada = restauraChavePrivada(caminho_chave_privada, frase_secreta);
        } catch(Exception e){
            System.out.println("[restauraChavePrivada] Exceção: " + e);
        }
        try{
            chave_secreta = decriptaEnvelope(caminho_arquivo, chave_privada);
        } catch(Exception e){
            System.out.println("[decriptaEnvelope] Exceção: " + e);
        }
        try{
            dados_arquivo = decriptaArquivo(caminho_arquivo, chave_secreta);
        } catch(Exception e){
            System.out.println("[decriptaArquivo] Exceção: " + e);
        }

        try {
            Files.write(Paths.get("validacao_rotina.docx"), dados_arquivo);
        } catch (Exception e) {
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
        }
    }

}