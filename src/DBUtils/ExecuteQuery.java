package DBUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ExecuteQuery implements DBProperties {
	public static Connection createConnection() {
		Connection con = null;
		try {
			con = DriverManager.getConnection(DBProperties.CONNECTION_STRING, DBProperties.USERNAME, DBProperties.PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	} 
	
	public static List<String> execute(Map<String, String> attributes) {
		List<String> policyIds = new ArrayList<String>();
		String query = "";
		for (String str : attributes.keySet()) {
			if (!query.equals("")) {
				query += " UNION ";
			}
			query += "MATCH (policy)-[:ASSOCIATED_WITH]->(target)-[:DEFINED_BY]->(" + str + ") WHERE " + str
				+ ".AttributeValue=\'" + attributes.get(str) + "\' RETURN policy.filename";
		}
		try {
			ResultSet resultSet = createConnection().createStatement().executeQuery(query);
			while (resultSet.next()) {
				policyIds.add(resultSet.getString("policy.filename"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return policyIds;
	}
	
	public static void main (String args[]) {
		Map<String, String> attributes = new HashMap<String, String> ();
		attributes.put("action", "Read");
		attributes.put("action", "Write");
		attributes.put("action", "Execute");
		List<String> results = execute(attributes);
		for (String str : results) {
			System.out.println(str);
		}
	}
}
