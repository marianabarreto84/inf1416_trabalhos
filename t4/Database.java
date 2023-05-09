import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringJoiner;
import org.sqlite.JDBC;

public class Database{

    private static void cria_tabela(String sql, String table_name){
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:banco.db");
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            // System.out.println("[cria_tabela] Tabela " + table_name +  " criada com sucesso.");
        } catch (Exception e) {
            System.out.println("[cria_tabela] Exceção ao criar tabela " + table_name + ": " + e);
            System.exit(0);
        } finally {
            try {
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println("[cria_tabela] Exceção ao fechar conexão e statement: " + e);
            }
        }
    }

    private static void popula_mensagens(String nome_arquivo){
        Connection conn = null;
        String sql = "INSERT INTO Mensagens (MID, texto) VALUES (?, ?)";
        try{
            conn = DriverManager.getConnection("jdbc:sqlite:banco.db");
            BufferedReader leitor = new BufferedReader(new FileReader(nome_arquivo));
            PreparedStatement stmt = conn.prepareStatement(sql);
            String linha;
            while ((linha = leitor.readLine()) != null) {
                String[] partes = linha.split(" ");
                StringJoiner sj = new StringJoiner(" ");
                for (int i = 1; i < partes.length; i++) {
                    sj.add(partes[i]);
                }
                String texto = sj.toString();
                int mid = Integer.parseInt(partes[0]);
                stmt.setInt(1, mid);
                stmt.setString(2, texto);
                stmt.executeUpdate();
            }
            // System.out.println("[popula_mensagens] Tabela de mensagens populada com sucesso.");
        } catch (Exception e) {
            System.out.println("[popula_mensagens] Exceção ao popular mensagens: " + e);
        }
    }

    private static void popula_grupos(String nome_grupo){
        Connection conn = null;
        String sql = "INSERT INTO Grupos (nome) VALUES (?)";
        try{
            conn = DriverManager.getConnection("jdbc:sqlite:banco.db");
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nome_grupo);
            stmt.executeUpdate();
        }
        catch (Exception e){
            System.out.println("[popula_grupos] Exceção ao popular grupos: " + e);
        }
    }

    public static void cria_banco(){
        String tabela_usuarios = "CREATE TABLE Usuarios (UID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, nome TEXT NOT NULL, senha TEXT NOT NULL, email TEXT NOT NULL, certificado TEXT NOT NULL, chave_privada TEXT NOT NULL, GID INTEGER, FOREIGN KEY (GID) REFERENCES Grupos (GID) ON DELETE CASCADE);";
        String tabela_chaveiro = "CREATE TABLE Chaveiro (KID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, UID INTEGER NOT NULL, certificado TEXT NOT NULL, chave_privada TEXT NOT NULL, FOREIGN KEY (UID) REFERENCES Usuarios(UID));";
        String tabela_grupos = "CREATE TABLE Grupos (GID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, nome TEXT NOT NULL);";
        String tabela_mensagens = "CREATE TABLE Mensagens (MID INTEGER PRIMARY KEY, texto TEXT NOT NULL);";
        String tabela_registros = "CREATE TABLE Registros (RID INTEGER PRIMARY KEY, MID INTEGER NOT NULL, UID INTEGER, data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (MID) REFERENCES Mensagens (MID) ON DELETE CASCADE, FOREIGN KEY (UID) REFERENCES Usuarios (UID) ON DELETE SET NULL);";

        cria_tabela(tabela_usuarios, "Usuarios");
        cria_tabela(tabela_chaveiro, "Chaveiro");
        cria_tabela(tabela_grupos, "Grupos");
        cria_tabela(tabela_mensagens, "Mensagens");
        cria_tabela(tabela_registros, "Registros");
        popula_mensagens("tabela_mensagens.txt");
        popula_grupos("Administrador");
        popula_grupos("Usuário");
    }

}