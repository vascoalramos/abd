package abd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;

public class Workload {
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