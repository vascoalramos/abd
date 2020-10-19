package abd;

import org.apache.commons.lang3.RandomStringUtils;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Random;

public class Workload {
    private static final int N = 10;
    private static final int MAX = (int) Math.pow(2, N);

    public static void populate(Random rand, Connection c) throws Exception {
        Statement s = c.createStatement();

        // create tables and insert values
        s.executeUpdate("create table client (id serial, name varchar, address varchar, data varchar, primary key (id))");
        s.executeUpdate("create table product (id serial, description varchar, data varchar, primary key (id))");
        s.executeUpdate("create table invoice (id serial, productId int references product, clientId int references client, data varchar, primary key (id))");

        String name = RandomStringUtils.randomAlphabetic(10, 15);
        String address = RandomStringUtils.randomAlphabetic(15, 25);
        String data = RandomStringUtils.randomAlphanumeric(1000, 1500);
        String description = RandomStringUtils.randomAlphabetic(15, 100);

        // insert clients
        for(int i = 0; i < MAX; i++) {
            s.executeUpdate("insert into client (name, address, data) values ('" + name + "', '" + address + "', '" + data + "')");
        }

        // insert products
        for(int i = 0; i < MAX; i++) {
            s.executeUpdate("insert into product (description, data) values ('" + description + "', '" + data + "')");
        }

        // insert invoices
        int clientId, productId;
        for(int i = 0; i < Math.pow(2,N+5); i++) {
            clientId = generateId(rand);
            productId = generateId(rand);
            s.executeUpdate("insert into invoice (productId, clientId, data) values ('" + clientId + "', '" + productId + "', '" + data + "')");
        }

        s.close();
    }

    public static void transaction(Random rand, Connection c) throws Exception {
        Statement s = c.createStatement();

        // Executar operação
        // workload generator

        s.close();
    }

    private static int generateId(Random rand) {
        return rand.nextInt(MAX)|rand.nextInt(MAX);
    }
}
