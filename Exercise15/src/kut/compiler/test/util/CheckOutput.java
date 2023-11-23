package kut.compiler.test.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import difflib.*;

public class CheckOutput {


	/**
	 * @param filename1
	 * @param filename2
	 * @return
	 */
	public static boolean getDiffString(String testname, String filename1, String filename2) 
	{
		List<String>  c1 = fileToLines(filename1);
		List<String>  c2 = fileToLines(filename2);

		Patch patch = DiffUtils.diff(c1,  c2);

		if (patch.getDeltas().size() == 0) {
			return true;
		}

		System.out.println("----------------------------------------");
		System.out.println("compiler output:");
		System.out.println("wrong code generation - test name:" + testname);
		System.out.println("----------------------------------------");
		for (Delta delta: patch.getDeltas()) {
			System.out.println(delta);
		}

		return false;
	}


	// Helper method for get the file content
	private static List<String> fileToLines(String filename) 
	{
		List<String> lines = new LinkedList<String>();
		String line = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return lines;
	}
}
