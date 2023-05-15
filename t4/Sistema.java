public class Sistema{

    private static void exibe_cabecalho(String login_do_usuario, String grupo_do_usuario, String nome_do_usuario){
        System.out.println("Login: " + login_do_usuario);
        System.out.println("Grupo: " + grupo_do_usuario);
        System.out.println("Nome: " + nome_do_usuario);
    }

    private static void exibe_corpo1(int total_usuarios){
        System.out.println("Total de usuários do sistema: " + total_usuarios);
    }

    private static void inicializacao(){
        Database.cria_banco();
    }

    private static Usuario cadastro_admin(){
        exibe_corpo1(0);
        Usuario admin = new Usuario();
        admin = Cadastro.executa_formulario();
        admin = Certificado.recuperaDadosUsuario(caminho_certificado);
    }

    public static void main(String args[]){
        inicializacao();
        Usuario admin = cadastro_admin();

    }
}