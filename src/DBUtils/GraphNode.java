package DBUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Map;


public class GraphNode implements DBProperties {
	public static Connection createConnection() {
		Connection con = null;
		try {
			con = DriverManager.getConnection(DBProperties.CONNECTION_STRING, DBProperties.USERNAME, DBProperties.PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}
	public static int createNode(String label, Map<String, String> properties) {
		String query = "MERGE (n:" + label;
		int size = properties.size();
		if (size > 0) {
			query += " { ";
			for (String prop : properties.keySet()) {
				query = query + prop + ": \'" + properties.get(prop) + "\'";
				if (--size > 0) {
					query = query + ", ";
				}
			}
			query = query + "})";
		}
		
		try {
			createConnection().createStatement().executeQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static ResultSet executeQuery (String query) {
		ResultSet rs = null;
		try {
			rs = createConnection().createStatement().executeQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
}
