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
		PreparedStatement insertClientSt = c.prepareStatement("insert into client (name, address, data) values (?, ?, ?)");
		for (int i = 0; i < MAX; i++) {
			insertClientSt.setString(1, NAME);
			insertClientSt.setString(2, ADDRESS);
			insertClientSt.setString(3, DATA);
			insertClientSt.executeUpdate();
		}
		
		// insert products
		PreparedStatement insertProductSt = c.prepareStatement("insert into product (description, stock, min, max, data) values (?, ?, ?, ?, ?)");
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
	
	public void transaction() throws Exception {
		Statement s = c.createStatement();
		
		//---- DEMO WORKLOAD ----
		// replace with your workload!
		switch(rand.nextInt(2)) {
			case 0:
				s.executeUpdate("update demo set c=c+1 where a=1");
				break;
			case 1:
				ResultSet rs = s.executeQuery("select * from demo");
				while(rs.next())
					;
				break;
		}
		//-----------------------
		
		s.close();
	}
}