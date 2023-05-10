import java.util.Scanner;

public class Cadastro{

    private static int valida_campo_string(String campo, int num_caracteres){
        if (campo.length() > num_caracteres) {
            System.out.println("ERRO: Valor digitado possui mais de " + num_caracteres + " caracteres. Por favor, digite novamente.");
            return -1;
        }
        return 0;
    }

    private static int valida_campo_grupo(String nome){
        if (!nome.equals("A") && !nome.equals("U")){
            System.out.println("ERRO: Não foi possível reconhecer o grupo digitado. Por favor, digite novamente.");
            return -1;
        }
        return 0;
    }

    private static int valida_senha(String senha) {
        int tam = senha.length();
        if (tam < 8 || tam > 10) {
            System.out.println("ERRO: A senha deve ter entre 8 a 10 caracteres.");
            return -1;
        }
        if (!senha.matches("\\d+")) {
            System.out.println("ERRO: A senha deve conter apenas dígitos.");
            return -1;
        }
        for (int i = 0; i < tam - 2; i++) {
            if (senha.charAt(i) == senha.charAt(i + 1) && senha.charAt(i + 1) == senha.charAt(i + 2)) {
                System.out.println("ERRO: A senha não aceita sequências de números repetidos.");
                return -1;
            }
        }
        return 0;
    }

    public static void executa_formulario(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Formulário de Cadastro:");
        System.out.print("Caminho do arquivo do certificado digital: ");
        String caminho_certificado = scanner.nextLine();
        while(valida_campo_string(caminho_certificado, 255) == -1){
            System.out.print("Caminho do arquivo do certificado digital: ");
            caminho_certificado = scanner.nextLine();
        }
        System.out.print("Caminho do arquivo da chave privada: ");
        String caminho_chave_privada = scanner.nextLine();
        while(valida_campo_string(caminho_chave_privada, 255) == -1){
            System.out.print("Caminho do arquivo da chave privada: ");
            caminho_chave_privada = scanner.nextLine();
        }
        System.out.print("Frase secreta: ");
        String frase_secreta = scanner.nextLine();
        while(valida_campo_string(frase_secreta, 255) == -1){
            System.out.print("Frase secreta: ");
            frase_secreta = scanner.nextLine();
        }
        System.out.print("Grupo ('A' para Administrador ou 'U' para Usuário): ");
        String grupo = scanner.nextLine();
        while(valida_campo_grupo(grupo) == -1){
            System.out.print("Grupo (Administrador ou Usuário): ");
            grupo = scanner.nextLine();
        }
        System.out.print("Senha pessoal: ");
        String senha_pessoal = scanner.nextLine();
        while(valida_senha(senha_pessoal) == -1){
            System.out.print("Senha pessoal: ");
            senha_pessoal = scanner.nextLine();
        }
        System.out.print("Confirmação senha pessoal: ");
        String confirmacao_senha_pessoal = scanner.nextLine(); // confirmacao apenas quando clicar no botao de cadastrar
    }
}