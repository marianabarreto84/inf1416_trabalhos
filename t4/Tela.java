import java.util.Scanner;

public class Tela{

    public static void exibe_cabecalho(String login_do_usuario, String grupo_do_usuario, String nome_do_usuario){
        System.out.println("Login: " + login_do_usuario);
        System.out.println("Grupo: " + grupo_do_usuario);
        System.out.println("Nome: " + nome_do_usuario);
    }

    public static void exibe_corpo1(int total_usuarios){
        System.out.println("Total de usuários do sistema: " + total_usuarios);
    }

    private static int valida_desvio(String desvio, String[] lista_comandos){
        for (int i = 0; i < lista_comandos.length(); i++){
            if (desvio.equals(lista_comandos[i])){
                break;
            }
        }
        if (!desvio.equals("V") && !desvio.equals("C")){
            System.out.println("ERRO: Não foi possível reconhecer o comando digitado. Por favor, digite novamente.");
            return -1;
        }
        return 0;
    }

    public static String executa_desvio(String tipo){
        Scanner scanner = new Scanner(System.in);
        if (tipo == "cadastro_normal"){
            System.out.println("Aperte 'C' para concluir o cadastro ou 'V' para voltar para o Menu: ");
            String concluir_cadastro = scanner.nextLine();
            String[] lista_comandos = {"C", "V"};
            while(valida_desvio(concluir_cadastro, lista_comandos) == -1){
                System.out.println("Aperte 'C' para concluir o cadastro ou 'V' para voltar para o Menu: ");
                concluir_cadastro = scanner.nextLine();
            }
            return concluir_cadastro;
        }
        else if (tipo == "cadastro_inicial"){
            System.out.println("Aperte 'C' para concluir o cadastro: ");
            String concluir_cadastro = scanner.nextLine();
            String[] lista_comandos = {"C"};
            while(valida_desvio(concluir_cadastro, lista_comandos) == -1){
                System.out.println("Aperte 'C' para concluir o cadastro: ");
                concluir_cadastro = scanner.nextLine();
            }
            return concluir_cadastro;
        }
        else{
            System.out.println("ERRO: Tipo não reconhecido.");
            System.exit(0);
        }
        return null;
    }
}