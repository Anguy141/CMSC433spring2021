package tests;

import java.io.*;

public class MyFileOutputWriter {
	
	public synchronized void writeToFile(String filename, StringBuilder str) {
		try {
			FileWriter myWriter = new FileWriter(filename);
			myWriter.append(str);
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
