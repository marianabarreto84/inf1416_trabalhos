
public class Sistema{

    private static void cadastro_inicial(){
        String status_desvio;

        Database.cria_banco();
        Tela.exibe_corpo1(0);
        Cadastro.executa_formulario();
        
        status_desvio = Tela.executa_desvio("cadastro_inicial");
    }

    public static void main(String args[]){
        cadastro_inicial();

    }
}