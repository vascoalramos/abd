package abd;

import org.apache.commons.lang3.RandomStringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
	
	private final Random rand;
	private final Connection c;
	
	public Workload(Random rand, Connection c) throws Exception {
		this.rand = rand;
		this.c = c;
		
		//---- DEMO WORKLOAD ----
		// initialize connection, e.g. c.setAutoCommit(false);
		// or create prepared statements...
		//-----------------------
		this.c.setAutoCommit(false);
	}
	
	public static void populate(Random rand, Connection c) throws Exception {
		Statement s = c.createStatement();
		
		// drop tables, if they exist
		s.executeUpdate("DROP TABLE IF EXISTS client CASCADE");
		s.executeUpdate("DROP TABLE IF EXISTS product CASCADE");
		s.executeUpdate("DROP TABLE IF EXISTS invoice CASCADE");
		s.executeUpdate("DROP TABLE IF EXISTS invoice_line CASCADE");
		s.executeUpdate("DROP TABLE IF EXISTS \"order\" CASCADE");
		
		// create tables
		s.executeUpdate("CREATE TABLE client (id SERIAL PRIMARY KEY," +
				"name VARCHAR," +
				"address VARCHAR," +
				"data VARCHAR" +
				")");
		s.executeUpdate("CREATE TABLE product (id SERIAL PRIMARY KEY," +
				"description VARCHAR," +
				"stock INT," +
				"min INT," +
				"max INT," +
				"data VARCHAR" +
				")");
		s.executeUpdate("CREATE TABLE invoice (id SERIAL PRIMARY KEY," +
				"client_id INT REFERENCES client(id)," +
				"data VARCHAR" +
				")");
		s.executeUpdate("CREATE TABLE invoice_line (id SERIAL PRIMARY KEY," +
				"invoice_id INT REFERENCES invoice(id)," +
				"product_id INT REFERENCES product(id)" +
				")");
		s.executeUpdate("CREATE TABLE \"order\" (id SERIAL PRIMARY KEY," +
				"product_id INT REFERENCES product(id)," +
				"supplier VARCHAR," +
				"items INT" +
				")");
		
		// insert clients
		PreparedStatement insertClientSt = c.prepareStatement("INSERT INTO client (name, address, data) VALUES (?, ?, ?)");
		for (int i = 0; i < MAX; i++) {
			insertClientSt.setString(1, NAME);
			insertClientSt.setString(2, ADDRESS);
			insertClientSt.setString(3, DATA);
			insertClientSt.executeUpdate();
		}
		
		// insert products
		PreparedStatement insertProductSt = c.prepareStatement("INSERT INTO product (description, stock, min, max, data) VALUES (?, ?, ?, ?, ?)");
		for (int i = 0; i < MAX; i++) {
			insertProductSt.setString(1, DESCRIPTION);
			insertProductSt.setInt(2, rand.nextInt(5000 - 5) + 5);
			insertProductSt.setInt(3, rand.nextInt(200 - 10) + 10);
			insertProductSt.setInt(4, rand.nextInt(15000 - 7500) + 7500);
			insertProductSt.setString(5, DATA);
			insertProductSt.executeUpdate();
		}
		
		s.close();
	}
	
	public void transaction() throws Exception {
		Statement s = c.createStatement();
		
		// execute random operation
		int opt = rand.nextInt(3);
		
		switch (opt) {
			case 0:
				sell(s);
				break;
			case 1:
				account(s);
				break;
			case 2:
				// top10(s);
				break;
			default:
				System.out.println("Oops.... :)");
		}
		
		c.commit();
		s.close();
	}
	
	private void sell(Statement s) throws Exception {
		int clientId = generateId();
		int invoiceId;
		int productId;
		
		String sql = "INSERT INTO invoice (client_id, data) VALUES (" + clientId + ", '" + DATA + "')";
		int affectedRows = s.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
		
		if (affectedRows > 0) {
			ResultSet keys = s.getGeneratedKeys();
			
			if (keys.next()) {
				invoiceId = keys.getInt(1);
				
				for (int i = 0; i < (rand.nextInt(10) + 1); i++) {
					productId = generateId();
					
					// insert lines to the invoice
					s.executeUpdate("INSERT INTO invoice_line (invoice_id, product_id) VALUES (" + invoiceId + ", " + productId + ")");
					
					// update stock value
					s.executeUpdate("UPDATE product SET stock = stock - 1 WHERE id=" + productId);
				}
			}
			
		}
	}
	
	private void account(Statement s) throws Exception {
		int clientId = generateId();
		
		ResultSet rs = s.executeQuery("SELECT description " +
				"FROM (SELECT * FROM invoice WHERE client_id=" + clientId + ") as invoice_client" +
				"	INNER JOIN invoice_line on invoice_client.id=invoice_id" +
				"	INNER JOIN product on product_id=product.id"
		);
		
		while (rs.next()) {
		}
	}
	
	private void top10(Statement s) throws Exception {
		ResultSet rs = s.executeQuery("select product_id from mv_product_sales order by total_sales desc limit 10;");
		
		while (rs.next()) {
		}
	}
	
	private int generateId() {
		return rand.nextInt(MAX) | rand.nextInt(MAX);
	}
	
}