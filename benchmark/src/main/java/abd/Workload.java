package abd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;

public class Workload {
	public static void populate(Random rand, Connection c) throws Exception {
		Statement s = c.createStatement();
		
		// drop tables, if they exist
		s.executeUpdate("drop table if exists client cascade");
		s.executeUpdate("drop table if exists product cascade");
		s.executeUpdate("drop table if exists invoice cascade");
		s.executeUpdate("drop table if exists mv_product_sales cascade");
		
		// create tables
		s.executeUpdate("create table client (id serial primary key, name varchar, address varchar, data varchar)");
		s.executeUpdate("create table product (id serial primary key, description varchar, data varchar)");
		s.executeUpdate("create table invoice (id serial primary key, product_id int, client_id int, data varchar)");
		
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