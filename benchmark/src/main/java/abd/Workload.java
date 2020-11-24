package abd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;

public class Workload {
	public static void populate(Random rand, Connection c) throws Exception {
		Statement s = c.createStatement();
		
		//---- DEMO WORKLOAD ----
		// replace with your workload!
		s.executeUpdate("create table demo (a int, b varchar, c int)");
		s.executeUpdate("insert into demo values (1, 'one',0)");
		//-----------------------
		
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