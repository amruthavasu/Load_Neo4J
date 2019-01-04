package DBUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

public class Relationship implements DBProperties {
	public static Connection createConnection() {
		Connection con = null;
		try {
			con = DriverManager.getConnection(DBProperties.CONNECTION_STRING, DBProperties.USERNAME, DBProperties.PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}
	
	public static void createRelationship(String relType, String label1, String label2, Map<String, String> map1, Map<String, String> map2) {
		String query = "MATCH (a:" + label1 + "), (b:" + label2 + ")\n" + 
				" WHERE ";
		for (String elementA : map1.keySet()) {
			query += "a." + elementA + " = \'" + map1.get(elementA) + "\' AND "; 
		}
		for (String elementB : map2.keySet()) {
			query += "b." + elementB + " = \'" + map2.get(elementB) + "\' AND "; 
		}
		query += "\n 1=1 CREATE UNIQUE (a)-[r:" + relType + "]->(b)";
		try {
			createConnection().createStatement().executeQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
