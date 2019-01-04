package neo4jDemo;

import java.io.File;

public class MainClass {
	public static void main(String[] args) {
		System.out.println("Creating graph nodes");
		PolicyParser parser = new PolicyParser(); 
		File directory = new File("F:\\Eclipse\\Workspace\\balana-master-original\\modules\\balana-core\\src\\test\\resources\\performance\\suite6\\policies");
		for (File file : directory.listFiles()) {
			parser.parsePolicy(directory.getAbsolutePath());
		}
		System.out.println("Graph node creation complete");
	}
}
