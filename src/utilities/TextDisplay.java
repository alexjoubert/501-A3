package utilities;

import java.util.List;

public class TextDisplay {
	private static int DEFAULT_TEXT_WRAP = 2500; 		// The default value to wrap
														// around display text.
	
	private static String DEFAULT_DELIMINATOR = "\n"; 	// The default value to
														// wrap around display
														// text.
	
	private static int DEFAULT_DEPTH = 0;; 				// The default depth to indent
														// display text.

	public static void display() {
		display("", DEFAULT_DELIMINATOR, DEFAULT_TEXT_WRAP, DEFAULT_DEPTH);
	}

	public static void display(String message) {
		display(message, DEFAULT_DELIMINATOR, DEFAULT_TEXT_WRAP, DEFAULT_DEPTH);
	}

	public static void display(List<String> messageList) {
		for (String line : messageList)
			display(line);
	}

	public static void display(String message, int depth) {
		display(message, DEFAULT_DELIMINATOR, DEFAULT_TEXT_WRAP, depth);
	}

	public static String repeatChar(String c, int repetitions) {
		if (repetitions == 0)
			return "";
		else
			return String.format(String.format("%%0%dd", repetitions), 0).replaceAll("0", c);
	}
	
	private static void display(String message, String deliminator, int length, int depth) {
		System.out.println(repeatChar("\t", depth) + wrapString(message, deliminator, length));
	}
	
	private static String wrapString(String s, String deliminator, int length) {
		StringBuilder sb = new StringBuilder(s);

		int i = 0;
		while (i + length < sb.length() && (i = sb.lastIndexOf(" ", i + length)) != -1) {
			sb.replace(i, i + 1, deliminator);
		}
		return sb.toString();
	}
}