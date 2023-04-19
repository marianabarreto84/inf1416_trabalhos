import java.io.*;
import java.security.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

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

    public static void readXML(String listaDigest) {
       //Leitura dos arquivos xml
        try {
            File fileDirectory = new File(listaDigest);
            for (final File fileEntry : fileDirectory.listFiles()) {
                
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fileEntry.getName());
                doc.getDocumentElement().normalize();

                NodeList fileList = doc.getElementsByTagName("FILE_ENTRY");
                // itera os arquvios na lista
                for (int i = 0; i < fileList.getLength(); i++) {
                    Node fileNode = fileList.item(i);
                    if (fileNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element fileElement = (Element) fileNode;
                        // pega o nome do arquivo
                        String fileName = fileElement.getElementsByTagName("FILE_NAME").item(0).getTextContent();

                        NodeList digestList = fileElement.getElementsByTagName("DIGEST_ENTRY");
                        
                        // ITERA OS DIGEST_ENTRY
                        for (int j = 0; j < digestList.getLength(); j++) {
                            Node digestNode = digestList.item(j);
                            if (digestNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element digestElement = (Element) digestNode;
                                
                                // Extrai os dados do digest
                                String digestType = digestElement.getElementsByTagName("DIGEST_TYPE").item(0).getTextContent();
                                String digestHex = digestElement.getElementsByTagName("DIGEST_HEX").item(0).getTextContent();
                                
                                // Output em uma string
                                System.out.println("FILE_NAME: " + fileName + ", DIGEST_TYPE: " + digestType + ", DIGEST_HEX: ");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}