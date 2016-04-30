package net.edudb.engine;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

	public static String generateUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	public static void main(String[] args) {
		System.out.println(generateUUID());
	}

	/**
	 * 
	 * @param string
	 *            String to be matched
	 * @param regex
	 *            Regular expression to be matched against
	 * @return
	 */
	public static Matcher getMatcher(String string, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(string);
		return matcher;
	}

}
