package abd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;

public class Workload {
    public static void populate(Random rand, Connection c) throws Exception {
        Statement s = c.createStatement();

        // Criar tabelas e inserir valores

        s.close();
    }

    public static void transaction(Random rand, Connection c) throws Exception {
        Statement s = c.createStatement();

        // Executar operação

        s.close();
    }
}
