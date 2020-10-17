package abd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;

public class Workload {
    public static void populate(Random rand, Connection c) throws Exception {
        Statement s = c.createStatement();

        // create tables and insert values
        s.executeUpdate("create table client (id int, name varchar, address varchar, data varchar)");
        s.executeUpdate("create table product (id int, description varchar, data varchar)");
        s.executeUpdate("create table invoice (id int, productId varchar, clientId varchar, data varchar)");

        s.close();
    }

    public static void transaction(Random rand, Connection c) throws Exception {
        Statement s = c.createStatement();

        // Executar operação

        s.close();
    }
}
