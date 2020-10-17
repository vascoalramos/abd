package abd;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Random;

public class Workload {
    private static final int MAX = 1024; // n = 10 => MAX = 2^10 = 1024

    public static void populate(Random rand, Connection c) throws Exception {
        Statement s = c.createStatement();

        // create tables and insert values
        s.executeUpdate("create table client (id int, name varchar, address varchar, data varchar, primary key (id))");
        s.executeUpdate("create table product (id int, description varchar, data varchar, primary key (id))");
        s.executeUpdate("create table invoice (id int, productId int references product, clientId int references client, data varchar, primary key (id))");

        // insert clients
        s.executeUpdate("insert into client values ("+ generateId(rand) + ", 'vasco', 'ola', 'adeus')");

        s.close();
    }

    public static void transaction(Random rand, Connection c) throws Exception {
        Statement s = c.createStatement();

        // Executar operação

        s.close();
    }

    private static int generateId(Random rand) {
        return rand.nextInt(MAX)|rand.nextInt(MAX);
    }
}
