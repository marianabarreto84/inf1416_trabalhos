public class Sistema{

    private static void inicializacao(){
        Database db = new Database();
        Cadastro cadastro = new Cadastro();
        db.cria_banco();
        cadastro.exibe_corpo1(0);
        cadastro.executa_formulario();
    }

    public static void main(String args[]){
        inicializacao();












    }
}