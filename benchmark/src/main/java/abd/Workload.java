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
		s.executeUpdate("drop table if exists client cascade");
		s.executeUpdate("drop table if exists product cascade");
		s.executeUpdate("drop table if exists invoice cascade");
		s.executeUpdate("drop table if exists mv_product_sales cascade");
		
		// drop triggers, if they exist
		s.executeUpdate("drop trigger if exists update_product_sales on abd.invoice");
		
		// drop functions, if they exist
		s.executeUpdate("drop function if exists update_product_sales");
		
		// create tables
		s.executeUpdate("create table client (id serial primary key, name varchar, address varchar, data varchar)");
		s.executeUpdate("create table product (id serial primary key, description varchar, data varchar)");
		s.executeUpdate("create table invoice (id serial primary key, product_id int, client_id int, data varchar)");
		
		// create indexes to account operation
		s.executeUpdate("create index idx_invoice_client_id on invoice(client_id)");
		s.executeUpdate("create index idx_invoice_product_id on invoice(product_id)");
		
		// insert clients
		for (int i = 0; i < MAX; i++) {
			s.executeUpdate("insert into client (name, address, data) values ('" + NAME + "', '" + ADDRESS + "', '" + DATA + "')");
		}
		
		// insert products
		for (int i = 0; i < MAX; i++) {
			s.executeUpdate("insert into product (description, data) values ('" + DESCRIPTION + "', '" + DATA + "')");
		}
		
		// create materialized view (plus insert trigger and function) to top10 operation
		s.executeUpdate("select id as product_id, 0 as total_sales into mv_product_sales from product group by product_id");
		
		s.executeUpdate("create function update_product_sales() returns trigger as '" +
				"	BEGIN" +
				"		update mv_product_sales set total_sales = total_sales + 1 where product_id = new.product_id;" +
				"		return new;" +
				"	END" +
				"' language 'plpgsql'"
		);
		
		s.executeUpdate("create trigger update_product_sales" +
				"	after insert on invoice" +
				"	for each row execute procedure update_product_sales();"
		);
		
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
		
		s.executeUpdate("insert into invoice (product_id, client_id, data) values ('" + clientId + "', '" + productId + "', '" + DATA + "')");
	}
	
	private static void account(Random rand, Statement s) throws Exception {
		int clientId = generateId(rand);
		
		ResultSet rs = s.executeQuery("select description from invoice inner join product on product_id=product.id where client_id=" + clientId);
		
		while (rs.next()) {
		}
	}
	
	private static void top10(Statement s) throws Exception {
		ResultSet rs = s.executeQuery("select product_id from mv_product_sales order by total_sales desc limit 10;");
		
		while (rs.next()) {
		}
	}
	
	private static int generateId(Random rand) {
		return rand.nextInt(MAX) | rand.nextInt(MAX);
	}
}
