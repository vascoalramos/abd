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
		
		// create tables and insert values
		s.executeUpdate("create table client (id serial, name varchar, address varchar, data varchar, primary key (id))");
		s.executeUpdate("create table product (id serial, description varchar, data varchar, primary key (id))");
		s.executeUpdate("create table invoice (id serial, productId int references product, clientId int references client, data varchar, primary key (id))");
		
		// insert clients
		for (int i = 0; i < MAX; i++) {
			s.executeUpdate("insert into client (name, address, data) values ('" + NAME + "', '" + ADDRESS + "', '" + DATA + "')");
		}
		
		// insert products
		for (int i = 0; i < MAX; i++) {
			s.executeUpdate("insert into product (description, data) values ('" + DESCRIPTION + "', '" + DATA + "')");
		}
		
		// insert invoices
		int clientId, productId;
		for (int i = 0; i < Math.pow(2, N + 5); i++) {
			clientId = generateId(rand);
			productId = generateId(rand);
			s.executeUpdate("insert into invoice (productId, clientId, data) values ('" + clientId + "', '" + productId + "', '" + DATA + "')");
		}
		
		s.close();
	}
	
	public static void transaction(Random rand, Connection c) throws SQLException {
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
	
	private static void sell(Random rand, Statement s) throws SQLException {
		int clientId = generateId(rand);
		int productId = generateId(rand);
		
		s.executeUpdate("insert into invoice (productId, clientId, data) values ('" + clientId + "', '" + productId + "', '" + DATA + "')");
		
		System.out.println("Product [" + productId + "] sold to client [" + clientId + "].");
	}
	
	private static void account(Random rand, Statement s) throws SQLException {
		int clientId = generateId(rand);
		
		ResultSet rs = s.executeQuery("select id, description from product inner join (select  distinct productId from invoice where clientId=" + clientId + ") as invoice on productId=product.id");
		
		System.out.println("List of products sold to client [" + clientId + "]:");
		
		// TODO: check this
		/*
		while (rs.next()) {
			System.out.println("  -> Name: " + rs.getString("description"));
		}
		*/
		
		System.out.println();
	}
	
	private static void top10(Statement s) throws SQLException {
		ResultSet rs = s.executeQuery("select productId from invoice group by productId order by count(productId) desc limit 10;");
		
		System.out.println("Top 10 products:");
		
		// TODO: check this
		/*
		while (rs.next()) {
			System.out.println("  -> ID: " + rs.getString("productid"));
		}
		*/
		
		System.out.println();
	}
	
	private static int generateId(Random rand) {
		return rand.nextInt(MAX) | rand.nextInt(MAX);
	}
}
