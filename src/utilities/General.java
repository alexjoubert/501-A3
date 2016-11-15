package utilities;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class General {
	public static List<String> readToList(String filename)
	{
		System.out.println("Reading file: " + filename);
		Scanner sc = null;
		try
		{
			sc = new Scanner(new File(filename));
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		List<String> lines = new ArrayList<String>();
		while (sc.hasNextLine())
		{
			lines.add(sc.nextLine());
		}
		sc.close();

		return lines;
	}
	
	public static List<File> getListOfFiles(String folderPath)
	{
		return Arrays.asList((new File(folderPath)).listFiles());
	}
}
