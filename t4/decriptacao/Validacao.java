import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.io.ByteArrayInputStream;
import java.security.Signature;

public class Validacao{

    public static PublicKey restauraChavePublica(String nome_arquivo) throws Exception{
        byte[] dados_certificado = Files.readAllBytes(Paths.get(nome_arquivo));
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate certificado = cf.generateCertificate(new ByteArrayInputStream(dados_certificado));
        PublicKey chave_publica = certificado.getPublicKey();
        return chave_publica;
    }
    
    public static boolean verificaAssinatura(String nome_arquivo, byte[] texto_plano, PublicKey chave_publica) throws Exception{
        byte[] dados_assinatura = Files.readAllBytes(Paths.get(nome_arquivo + ".asd"));

        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initVerify(chave_publica);
        signature.update(texto_plano);

        return signature.verify(dados_assinatura);
    }

    public static void main(String args[]){
        PublicKey chave_publica = null;
        try{
            chave_publica = restauraChavePublica("Pacote-T4/Keys/admin-x509.crt");
        } catch (Exception e){
            System.out.println("[restauraChavePublica] Exceção: " + e);
            System.exit(0);
        }
        try{
            byte[] texto_plano = Decriptacao.recuperaTextoPlano("Pacote-T4/Files/index", "Pacote-T4/Keys/admin-pkcs8-des.key", "admin");
            boolean status = verificaAssinatura("Pacote-T4/Files/index", texto_plano, chave_publica);
            System.out.println("Verifica assinatura: " + status);
        } catch (Exception e){
            System.out.println("[verificaAssinatura] Exceção: " + e);
            System.exit(0);
        }
    }
}