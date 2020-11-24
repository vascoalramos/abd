package abd;

import org.apache.commons.lang3.RandomStringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
	
	private final PreparedStatement increaseProductStockSt;
	private final PreparedStatement decreaseProductStockSt;
	
	private final PreparedStatement insertOrderSt;
	private final PreparedStatement deleteOrderSt;
	
	private final PreparedStatement insertInvoiceLineSt;
	
	public Workload(Random rand, Connection c) throws Exception {
		this.rand = rand;
		this.c = c;
		
		this.c.setAutoCommit(false);
		
		this.increaseProductStockSt = c.prepareStatement("UPDATE product SET stock = stock + ? WHERE id = ?");
		this.decreaseProductStockSt = c.prepareStatement("UPDATE product SET stock = stock - ? WHERE id = ?");
		
		this.insertOrderSt = c.prepareStatement("INSERT INTO \"order\" (product_id, supplier, items) VALUES (?, ?, ?)");
		this.deleteOrderSt = c.prepareStatement("DELETE FROM \"order\" WHERE id = ?");
		
		this.insertInvoiceLineSt = c.prepareStatement("INSERT INTO invoice_line (invoice_id, product_id) VALUES (?, ?)");
	}
	
	public static void populate(Random rand, Connection c) throws Exception {
		Statement s = c.createStatement();
		
		// drop tables, if they exist
		s.executeUpdate("DROP TABLE IF EXISTS client CASCADE");
		s.executeUpdate("DROP TABLE IF EXISTS product CASCADE");
		s.executeUpdate("DROP TABLE IF EXISTS invoice CASCADE");
		s.executeUpdate("DROP TABLE IF EXISTS invoice_line CASCADE");
		s.executeUpdate("DROP TABLE IF EXISTS \"order\" CASCADE");
		s.executeUpdate("DROP TABLE IF EXISTS mv_product_sales CASCADE");
		
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
		
		// drop triggers, if they exist
		s.executeUpdate("DROP TRIGGER IF EXISTS update_product_sales ON abd.invoice");
		
		// drop functions, if they exist
		s.executeUpdate("DROP FUNCTION IF EXISTS update_product_sales");
		
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
			insertProductSt.setInt(2, rand.nextInt(3000 - 2250) + 2250);
			insertProductSt.setInt(3, rand.nextInt(2250 - 1850) + 1850);
			insertProductSt.setInt(4, rand.nextInt(3250 - 3000) + 3000);
			insertProductSt.setString(5, DATA);
			insertProductSt.executeUpdate();
		}
		
		/*
		// create materialized view (plus insert trigger and function) to top10 operation
		s.executeUpdate("SELECT id AS product_id, 0 AS total_sales INTO mv_product_sales FROM product GROUP BY product_id");
		
		s.executeUpdate("CREATE FUNCTION update_product_sales() RETURNS TRIGGER AS '" +
				"	BEGIN" +
				"		update mv_product_sales set total_sales = total_sales + 1 where product_id = new.product_id;" +
				"		return new;" +
				"	END" +
				"' language 'plpgsql'"
		);
		
		s.executeUpdate("CREATE TRIGGER update_product_sales" +
				"	AFTER INSERT ON invoice_line" +
				"	FOR EACH ROW EXECUTE PROCEDURE update_product_sales();"
		);
		*/
		
		s.close();
	}
	
	public void transaction() throws Exception {
		Statement s = c.createStatement();
		
		// execute random operation
		int opt = rand.nextInt(5);
		
		switch (opt) {
			case 0:
				sell(s);
				break;
			case 1:
				account(s);
				break;
			case 2:
				top10(s);
				break;
			case 3:
				if (rand.nextInt(4) == 1) {
					order(s);
				}
				break;
			case 4:
				if (rand.nextInt(4) == 1) {
					delivery(s);
				}
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
				
				for (int i = 0; i < (rand.nextInt(5) + 1); i++) {
					productId = generateId();
					
					// insert lines to the invoice
					insertInvoiceLineSt.setInt(1, invoiceId);
					insertInvoiceLineSt.setInt(2, productId);
					insertInvoiceLineSt.executeUpdate();
					
					// update stock value
					decreaseProductStockSt.setInt(1, 1);
					decreaseProductStockSt.setInt(2, productId);
					decreaseProductStockSt.executeUpdate();
				}
			}
			
		}
	}
	
	private void account(Statement s) throws Exception {
		int clientId = generateId();
		
		ResultSet rs = s.executeQuery("SELECT description " +
				"FROM (SELECT * FROM invoice WHERE client_id=" + clientId + ") AS invoice_client" +
				"	INNER JOIN invoice_line ON invoice_client.id=invoice_id" +
				"	INNER JOIN product ON product_id=product.id"
		);
		
		while (rs.next()) {
		}
	}
	
	private void top10(Statement s) throws Exception {
		ResultSet rs = s.executeQuery("SELECT product_id " +
				"FROM invoice_line " +
				"GROUP BY product_id " +
				"ORDER BY sum(product_id) DESC LIMIT 10;");
		
		while (rs.next()) {
		}
	}
	
	private void order(Statement s) throws Exception {
		ResultSet rs = s.executeQuery("SELECT id, stock, max FROM product WHERE stock < min");
		
		List<int[]> entries = new ArrayList<>();
		
		while (rs.next()) {
			entries.add(new int[]{rs.getInt(1), rs.getInt(2), rs.getInt(3)});
		}
		
		for (int[] entry: entries) {
			insertOrderSt.setInt(1, entry[0]);
			insertOrderSt.setString(2, DATA);
			insertOrderSt.setInt(3, entry[2] - entry[1]);
			insertOrderSt.executeUpdate();
		}
	}
	
	private void delivery(Statement s) throws Exception {
		ResultSet rs = s.executeQuery("SELECT items, product_id, id FROM \"order\"");
		
		List<int[]> entries = new ArrayList<>();
		
		while (rs.next()) {
			entries.add(new int[]{rs.getInt(1), rs.getInt(2), rs.getInt(3)});
		}
		
		for (int[] entry: entries) {
			increaseProductStockSt.setInt(1, entry[0]);
			increaseProductStockSt.setInt(2, entry[1]);
			increaseProductStockSt.executeUpdate();
			
			deleteOrderSt.setInt(1, entry[2]);
			deleteOrderSt.executeUpdate();
		}
	}
	
	private int generateId() {
		int res = rand.nextInt(MAX) | rand.nextInt(MAX);
		return res > 0 ? res : 1;
	}
	
}