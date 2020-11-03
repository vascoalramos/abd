package abd;

import org.apache.commons.lang3.RandomStringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class Workload {
	private static final int N = 10;
	private static final int MAX = (int) Math.pow(2, N);
	
	// fields
	private static final String NAME = RandomStringUtils.randomAlphabetic(10, 15);
	private static final String ADDRESS = RandomStringUtils.randomAlphabetic(15, 25);
	private static final String DATA = RandomStringUtils.randomAlphanumeric(1000, 1500);
	private static final String DESCRIPTION = RandomStringUtils.randomAlphabetic(15, 100);
	
	public static void populate(Random rand, Connection c) throws SQLException {
		Statement s = c.createStatement();
		
		// drop tables, if they exist
		s.executeUpdate("drop table if exists client cascade;");
		
		s.executeUpdate("drop table if exists product cascade");
		
		s.executeUpdate("drop table if exists Invoice cascade");
		
		// create tables and insert values
		s.executeUpdate("create table client (id int primary key, name varchar, address varchar, data varchar)");
		
		s.executeUpdate("create table product (id int primary key, description varchar, data varchar)");
		
		s.executeUpdate("create table invoice (id serial primary key, productId int, clientId int, data varchar)");

		// create indexes
		s.executeUpdate("create index idx_invoice_client_id on invoice(clientId)");
		s.executeUpdate("create index idx_invoice_product_id on invoice(productId)");
		s.executeUpdate("cluster invoice using idx_invoice_product_id");
		
		// insert clients
		for (int i = 0; i < MAX; i++) {
			s.executeUpdate("insert into client (id, name, address, data) values ('" + i + "', '" + NAME + "', '" + ADDRESS + "', '" + DATA + "')");
		}
		
		// insert products
		for (int i = 0; i < MAX; i++) {
			s.executeUpdate("insert into product (id, description, data) values ('" + i + "', '" + DESCRIPTION + "', '" + DATA + "')");
		}
		
		s.close();
	}
	
	public static void transaction(Random rand, Connection c) throws Exception {
		Statement s = c.createStatement();
		
		// execute random operation
		int opt = rand.nextInt(3);
		
		switch (opt) {
			case 0:
				sell(rand, s);
				break;
			case 1:
				account(rand, s);
				break;
			case 2:
				top10(s);
				break;
			default:
				System.out.println("Oops.... :)");
		}
		
		s.close();
	}
	
	private static void sell(Random rand, Statement s) throws Exception {
		int clientId = generateId(rand);
		int productId = generateId(rand);
		
		ResultSet rs = s.executeQuery("select count(id) as total from invoice;");
		rs.next();
		
		s.executeUpdate("insert into invoice (productId, clientId, data) values ('" + clientId + "', '" + productId + "', '" + DATA + "')");
	}
	
	private static void account(Random rand, Statement s) throws Exception {
		int clientId = generateId(rand);

		ResultSet rs = s.executeQuery("select description from invoice inner join product on productId=product.id where clientId=" + clientId);
		
		while (rs.next()) {
			;
		}
	}
	
	private static void top10(Statement s) throws Exception {
		ResultSet rs = s.executeQuery("select productId from invoice group by productId order by count(productId) desc limit 10;");
		
		while (rs.next()) {
			;
		}
	}
	
	private static int generateId(Random rand) {
		return rand.nextInt(MAX) | rand.nextInt(MAX);
	}
}
