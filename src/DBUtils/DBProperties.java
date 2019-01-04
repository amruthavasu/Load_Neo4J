package DBUtils;

import java.sql.Connection;

public interface DBProperties {
	public static final String CONNECTION_STRING = "jdbc:neo4j:http://localhost:7474";
	public static final String USERNAME = "neo4j";
	public static final String PASSWORD = "P@ssw0rd!23";
	
	//public static Connection createConnection();
}
