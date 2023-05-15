/*
INF1416 - Seguranca da Informacao
Nome: Gustavo Sampaio
Nome: Mariana Barreto - Matrícula: 1820673
*/
import java.io.*;
import java.security.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
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

    public static int validateArgs(String[] args){
        String[] valid_algorithms = {"MD5", "SHA-1", "SHA-256", "SHA-512"};
        if(args.length != 3){
            System.out.println("O número de argumentos não está de acordo com o especificado (3).");
            return -1;
        }
        String digest_type = args[0];
        boolean found = false;
        for (String element : valid_algorithms) {
            if (element.equals(digest_type)) {
                found = true;
                break;
            }
        }
        if(!found){
            System.out.println("O tipo do Digest não está dentre os aceitos pelo enunciado.");
            return -1;
        }
        return 0;
    }

    public static String[][] calculateDigestList(String file_path, String digest_type){
        try{
            MessageDigest digest = MessageDigest.getInstance(digest_type);
            File directory = new File(file_path);
            File[] files = directory.listFiles();
            String[][] digest_list = new String[files.length][3];
            int i = 0;
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
                    digest_list[i][0] = file.getName(); // Nome do Arquivo
                    digest_list[i][1] = file_digest_string; // Digest Hex
                    digest_list[i][2] = ""; // espaço para o status
                }
                i++;
            }
            return digest_list;
        }
        catch (NoSuchAlgorithmException e){
            System.out.println("Erro calculateDigest: nenhum algoritmo foi encontrado");
            System.exit(0);
        }
        catch (IOException e){
            System.out.println("Erro calculateDigest: não foi possível abrir o arquivo");
            System.exit(0);
        }
        return null;
    }

    public static void printDigestList(String[][] digest_list, String digest_type){
        for(int i = 0; i < digest_list.length; i++){
            System.out.println(digest_list[i][0] + " " + digest_type + " " + digest_list[i][1] + " " + "(" + digest_list[i][2] + ")");
        }
    }

    public static String[] compareDigestsToList(String[] digest_element, String[][] digest_list, String digest_type, int i){
        String[] new_element = digest_element;
        for(int j = 0; j < digest_list.length; j++){
            if(j != i){
                if(digest_list[j][1].equals(digest_element[1])){
                    digest_element[2] = "COLLISION";
                }
            }
        }
        return digest_element;
    }

    public static String[] compareDigestsToFile(String[] element, String path_to_digest_list_file, String element_digest_type){
        try {
            // Leitura do arquivo XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File(path_to_digest_list_file));
            doc.getDocumentElement().normalize();

            String[] new_element = element;
            String element_filename = element[0];
            String element_digest_hex = element[1];

            NodeList fileList = doc.getElementsByTagName("FILE_ENTRY");
            // Itera os arquivos na lista
            String status = "FILE NOT FOUND";
            for (int i = 0; i < fileList.getLength(); i++) {
                Node fileNode = fileList.item(i);
                if (fileNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element fileElement = (Element) fileNode;
                    // Pega o nome do arquivo e verifica se existe um arquivo com o mesmo nome
                    String fileName = fileElement.getElementsByTagName("FILE_NAME").item(0).getTextContent();
                    if (fileName.equals(element_filename)){
                        status = "NOT FOUND";
                    }

                    // Pega as entradas de digest desse arquivo
                    NodeList digestList = fileElement.getElementsByTagName("DIGEST_ENTRY");
                    
                    // Itera os digest_entry
                    for (int j = 0; j < digestList.getLength(); j++) {
                        Node digestNode = digestList.item(j);
                        if (digestNode.getNodeType() == Node.ELEMENT_NODE) {
                            // Extrai os dados do digest
                            Element digestElement = (Element) digestNode;
                            String digest_type = digestElement.getElementsByTagName("DIGEST_TYPE").item(0).getTextContent();
                            String digest_hex = digestElement.getElementsByTagName("DIGEST_HEX").item(0).getTextContent();
                            // Verifica se o digest procurado está no arquivo
                            if(element_digest_hex.equals(digest_hex) && element_digest_type.equals(digest_type) && element_filename.equals(fileName)){
                                // System.out.println("ENCONTROU O MESMO DIGEST NO MESMO ARQUIVO!");
                                if(!status.equals("COLLISION")){
                                    status = "OK";
                                }
                            }
                            else if(element_digest_hex.equals(digest_hex) && !element_filename.equals(fileName) && element_digest_type.equals(digest_type)){
                                // System.out.println("ENCONTROU O MESMO DIGEST EM OUTRO ARQUIVO!");
                                status = "COLLISION";
                            }
                            else if(!element_digest_hex.equals(digest_hex) && element_filename.equals(fileName) && element_digest_type.equals(digest_type)){
                                // System.out.println("ENCONTROU OUTRO DIGEST PARA O MESMO ARQUIVO");
                                status = "NOT OK";
                            }
                        }
                    }
                }
            }
            new_element[2] = status;
            return new_element;
        }
        catch (Exception e) {
            System.out.println("ERRO: Não foi possível fazer parsing do arquivo com a lista de digests. Por favor, verifique se ele está correto.");
            System.exit(0);
        }
        return null;
    }

    private static Node findFileEntryByName(Document doc, String fileName) {
        Node catalog = doc.getFirstChild();
        Node fileEntry = catalog.getFirstChild();
        while (fileEntry != null) {
            if (fileEntry.getNodeType() == Node.ELEMENT_NODE) {
                Element fileEntryElement = (Element) fileEntry;
                String name = fileEntryElement.getElementsByTagName("FILE_NAME").item(0).getTextContent();
                if (name.equals(fileName)) {
                    return fileEntry;
                }
            }
            fileEntry = fileEntry.getNextSibling();
        }
        return null;
    }
    
    private static void addFileEntry(String path_to_digest_list_file, String entry_digest_filename, String entry_digest_type, String entry_digest_hex) {
        try {
        // Criar um objeto DocumentBuilderFactory e usar este objeto para criar um novo documento XML
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(path_to_digest_list_file));

        // Criar um novo elemento FILE_ENTRY com os elementos FILE_NAME, DIGEST_ENTRY, DIGEST_TYPE e DIGEST_HEX
        Element fileEntry = doc.createElement("FILE_ENTRY");
        Element fileName = doc.createElement("FILE_NAME");
        Element digestEntry = doc.createElement("DIGEST_ENTRY");
        Element digestType = doc.createElement("DIGEST_TYPE");
        Element digestHex = doc.createElement("DIGEST_HEX");

        // Definir o valor do elemento FILE_NAME
        fileName.setTextContent(entry_digest_filename);

        // Definir o valor dos elementos DIGEST_TYPE e DIGEST_HEX
        digestType.setTextContent(entry_digest_type);
        digestHex.setTextContent(entry_digest_hex);

        // Anexar os elementos DIGEST_TYPE e DIGEST_HEX ao elemento DIGEST_ENTRY
        digestEntry.appendChild(digestType);
        digestEntry.appendChild(digestHex);

        // Anexar o elemento FILE_NAME e o elemento DIGEST_ENTRY ao elemento FILE_ENTRY
        fileEntry.appendChild(fileName);
        fileEntry.appendChild(digestEntry);

        // Anexar o elemento FILE_ENTRY ao elemento raiz CATALOG
        Element catalog = doc.getDocumentElement();
        catalog.appendChild(fileEntry);

        // Escrever o documento XML atualizado no arquivo XML usando a classe Transformer e TransformerFactory
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(path_to_digest_list_file));
        transformer.transform(source, result);

        //System.out.println("Nova entrada FILE_ENTRY adicionada com sucesso ao arquivo XML!");

        } catch (Exception e) {
        e.printStackTrace();
        }
    }

    private static void addDigestEntry(String path_to_digest_list_file, String entry_digest_filename, String entry_digest_type, String entry_digest_hex) {
        try {
            // Criar um objeto DocumentBuilderFactory e usar este objeto para criar um novo documento XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(path_to_digest_list_file));

            // encontrar o FILE_ENTRY desejado pelo nome do arquivo
            Node fileEntry = findFileEntryByName(doc, entry_digest_filename);
            if (fileEntry == null) {
                System.out.println("FILE_ENTRY não encontrado");
                return;
            }

            // criar o elemento DIGEST_ENTRY
            Element digestEntry = doc.createElement("DIGEST_ENTRY");

            Element digestType = doc.createElement("DIGEST_TYPE");
            Element digestHex = doc.createElement("DIGEST_HEX");

            digestType.setTextContent(entry_digest_type);
            digestHex.setTextContent(entry_digest_hex);

            // adicionar DIGEST_TYPE e DIGEST_HEX a DIGEST_ENTRY
            digestEntry.appendChild(digestType);
            digestEntry.appendChild(digestHex);

            // adicionar DIGEST_ENTRY a FILE_ENTRY
            Element fileEntryElement = (Element) fileEntry;
            fileEntryElement.appendChild(digestEntry);

            // Escrever o documento XML atualizado no arquivo XML usando a classe Transformer e TransformerFactory
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(path_to_digest_list_file));
            transformer.transform(source, result);

            //System.out.println("Entrada de digest adicionada com sucesso");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateXML(String path_to_digest_list_file, String[][] digest_list, String digest_type){
        for(int i = 0; i < digest_list.length; i++){
            if (digest_list[i][2] == "FILE NOT FOUND"){
                addFileEntry(path_to_digest_list_file, digest_list[i][0], digest_type, digest_list[i][1]);
                digest_list[i][2] = "NOT FOUND";
            }
            else if(digest_list[i][2] == "NOT FOUND"){
                addDigestEntry(path_to_digest_list_file, digest_list[i][0], digest_type, digest_list[i][1]);
            }
        }
    }

    public static void main(String[] args) {
        // Valida os argumentos
        if (validateArgs(args) == -1){
            return;
        }
        String digest_type = args[0];
        String path_to_file_folder = args[1];
        String path_to_digest_list_file = args[2];
        String[][] calculated_digest_list;

        // Calcula o digest solicitado do conteúdo de todos os arquivos presentes na pasta fornecida
        calculated_digest_list = calculateDigestList(path_to_file_folder, digest_type);

        // Compara os digests calculados com os respectivos digests registrados para cada arquivo no arquivo ArqListaDigest e na pasta fornecida
        for(int i = 0; i < calculated_digest_list.length; i++){
            calculated_digest_list[i] = compareDigestsToFile(calculated_digest_list[i], path_to_digest_list_file, digest_type);
            calculated_digest_list[i] = compareDigestsToList(calculated_digest_list[i], calculated_digest_list, digest_type, i);
        }

        updateXML(path_to_digest_list_file, calculated_digest_list, digest_type);
        printDigestList(calculated_digest_list, digest_type);

    }

}